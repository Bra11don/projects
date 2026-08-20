using System.Collections.Concurrent;
using System.Diagnostics;

class Intersection
{
    public Stopwatch time;
    public double stop; //using this later to mark the time that the vehicle has crossed the intersection

    // Enumeration for vehicle direction: NorthSouth or EastWest
    enum Direction
    {
        NorthSouth,
        EastWest
    }

    // Vehicle class with properties for vehicle number and direction
    // Represents a single vehicle
    class Vehicle
    {
        public int Number { get; }
        public Direction Direction { get; }

        public Vehicle(int number, Direction direction)
        {
            Number = number;
            Direction = direction;
        }
    }

    //the ParkingLot class contains the concurrent queue of vehicles not yet picked up.
    class ParkingLot
    {
        public ConcurrentQueue<Vehicle> VehicleQueue { get; }

        public ParkingLot(int N)
        {
            VehicleQueue = CreateVehicleQueue(N);
        }

        // Creating a queue of vehicles with random direction
        // Takes an integer N as the number of vehicles to create
        // Returns a ConcurrentQueue of Vehicle objects
        private ConcurrentQueue<Vehicle> CreateVehicleQueue(int N)
        {
            Random random = new Random();
            ConcurrentQueue<Vehicle> queue = new ConcurrentQueue<Vehicle>();

            for (int i = 0; i < N; i++)
            {
                int number = i + 1;

                //The following line creates a new instance of the Random class, generates a random integer between 0 and 1, and then casts the generated integer to the Direction enum type.
                Direction direction = (Direction)random.Next(2);

                queue.Enqueue(new Vehicle(number, direction));
            }

            Console.WriteLine("Vehicle queue:");
            foreach (Vehicle vehicle in queue)
            {
                string directionString = vehicle.Direction == Direction.NorthSouth ? "North/South" : "East/West";
                Console.WriteLine($"Vehicle {vehicle.Number}: {directionString}");
            }

            return queue;
        }
    }

    // Method to change traffic signal direction
    // Simulates the behavior of a traffic signal by periodically changing the direction (NorthSouth or EastWest)
    //the stopSignalThread is going to be useful in stopping the traffic signal change and ending the program
    private static Direction? CurrentDirection = null;
    static bool stopSignalThread = false;

    public static void ChangeDirection(object lockObj, Stopwatch time)
    {
        while (!stopSignalThread)
        {
            Thread.Sleep(1000);//simulated 100seconds, to change the traffic light signal
            lock (lockObj)
            {
                CurrentDirection = CurrentDirection == Direction.NorthSouth ? Direction.EastWest : Direction.NorthSouth; //abbreviated if/else

                Console.WriteLine($"Changed flow direction to {CurrentDirection} at {time.Elapsed.TotalSeconds:F2}s");

                //This line releases all threads that are waiting on the lockObj object, allowing them to continue their execution. In this case, it informs the Pilot threads that the traffic signal direction has changed, and they may need to reevaluate whether their vehicles can now cross the intersection. (The pilots are in the direction queues at this point)

                //I implemented this logic to solve the asleep at the wheel problem
                Monitor.PulseAll(lockObj);
            }

        }
    }

    //The Roads class is going to be the part between parking lot and the intersection
    // The individual directions roads are symbolized by the NS and EW queues respectively
    //The Pilot class uses this lock object to guard the movement from the parking lot into the respective roads (Critical section) ensuring that vehicles are added and removed in the proper order.
    class Roads
    {
        public Queue<Vehicle> NorthSouthQueue { get; }
        public Queue<Vehicle> EastWestQueue { get; }
        public object LockObj { get; }

        public Roads()
        {
            NorthSouthQueue = new Queue<Vehicle>();
            EastWestQueue = new Queue<Vehicle>();
            LockObj = new object();
        }
    }


    public object IntersectionLock { get; } //synchronization object (lock) used to ensure that only one vehicle can enter the intersection at a time.

    //intersection constructor
    public Intersection()
    {
        IntersectionLock = new object();
        time = Stopwatch.StartNew();
    }

    // private void EnterIntersection(Vehicle vehicle)
    // {
    //     lock (IntersectionLock)
    //     {
    //         Thread.Sleep(100); // Simulate 10 seconds in the intersection
    //     }
    // }

    private int crossedcount=0; //keep count of the number of vehicles that have crossed the intersection

    private void EnterIntersection(Vehicle vehicle)
    {
        Monitor.Enter(IntersectionLock); // acquires the lock on the IntersectionLock
        try
        {
            Thread.Sleep(100); // Simulated 10 seconds in the intersection
        }
        finally
        {
            Monitor.Exit(IntersectionLock);
            crossedcount++;
        }
    }

    // Pilot class to manage vehicle movement from the parking lot all the way through the intersection
    // Each Pilot instance is responsible for dequeuing vehicles from the concurrent queue in the parking lot, adding them to the respective direction queues which are our roads from parking lot to intersection, and managing their crossing through the intersection when the traffic signal allows
    //in Pilot is where Im doing the work from picking up to crossing over
    class Pilot
    {
        private int pilot_Num;
        private ParkingLot parking_Lot;
        private object lock_Obj;
        private Intersection intersection;
        private Roads directions;

        public Pilot(int pilotNumber, ParkingLot parkingLot, object lockObj, Intersection intersection, Roads roads)
        {
            pilot_Num = pilotNumber;
            parking_Lot = parkingLot;
            lock_Obj = lockObj;
            directions = roads;
            this.intersection = intersection;
        }

        // Route method for the Pilot class to manage vehicle movement (ie. the route that a pilot takes from the moment it picks a vehicle from parking lot to when it crosses the intersection)
        public void Route()
        {

            Vehicle vehicle; //instance of the vehicle

            double leftQueueTime = 0; //keep track of the time vehicle left queue

            //The while loop will continue to iterate until there are no more vehicles in the concurrent queue (in the parking lot) i.e all vehicles have been picked up by a pilot.
            while (parking_Lot.VehicleQueue.Count > 0)
            {

                //this first part is the process of the pilots picking up the vehicles from the concurrent queue and takes them out into the road (the direction-wise queues)
                //we're synchronizing it specifically to prevent race condition and protect the order of the cars being picked up.
                lock (directions.LockObj)
                {
                    if (!parking_Lot.VehicleQueue.TryDequeue(out vehicle!))
                    {
                        // If TryDequeue returns false, there was no vehicle in the queue.
                        continue;
                    }

                    if (vehicle.Direction == Direction.NorthSouth)
                    {
                        directions.NorthSouthQueue.Enqueue(vehicle);
                        Console.WriteLine($"Pilot {pilot_Num,2} Vehicle {vehicle.Number,2} entered queue NorthSouth ");
                    }
                    else
                    {
                        directions.EastWestQueue.Enqueue(vehicle);
                        Console.WriteLine($"Pilot {pilot_Num,2} Vehicle {vehicle.Number,2} entered queue EastWest");
                    }

                }

                //this second part is the process of the pilots trying to get into the intersection
                lock (lock_Obj)
                {
                    // Wait until the vehicle is allowed to cross the intersection i.e the traffic signal is pointing at the vehicle direction and the vehicle is infront of the queue
                    while (vehicle.Direction != CurrentDirection ||
                           (vehicle.Direction == Direction.NorthSouth && directions.NorthSouthQueue.Peek() != vehicle) ||
                           (vehicle.Direction == Direction.EastWest && directions.EastWestQueue.Peek() != vehicle))
                    {
                        Monitor.Wait(lock_Obj);
                    }
                }

                //at this point it means that, the vehicle is at the front of the queue and that the traffic signal is pointing to the respective direction

                //we can dequeue it i.e move it from the road to start getting through the intersection

                lock (intersection.IntersectionLock)
                {
                    if (vehicle.Direction == Direction.NorthSouth)
                    {
                        directions.NorthSouthQueue.Dequeue();
                    }
                    else
                    {
                        directions.EastWestQueue.Dequeue();
                    }
                    leftQueueTime = intersection.time.Elapsed.TotalSeconds;//recording the time it starts getting on its way towards the intersection

                    //Now the vehicle can enter the intersection,
                    //Once in the intersection, no other vehicle is allowed to enter until after the current vehicle is done crossing the intersection; then the next vehicle can go in.
                    //This is made possible through the use of a monitor
                    intersection.EnterIntersection(vehicle);
                    intersection.stop = intersection.time.Elapsed.TotalSeconds; //the time that vehicle crossed intersection

                    //formatting the output as instructed in the pdf
                    Console.WriteLine($"Pilot {pilot_Num,2} Vehicle {vehicle.Number,2} left queue at {leftQueueTime,6:F2}s crossed {vehicle.Direction,-7} at {intersection.stop,6:F2}s");

                }

            }
        }
    }


    static void Main(string[] args)
    {
        //validating command line arguments
        if (args.Length != 2)
        {
            Console.WriteLine("Usage: dotnet run <number of vehicles> <number of pilots>");
            return;
        }

        //The following IF statements try to parse the first and second argument as integers using the int.TryParse() method. If the parsing fails, then they print error messages to the console indicating that the first and second arguments are invalid and exits the program using the return statement. If the parsing succeeds, then the value of the parsed integer is stored in the N and P variables RESPECTIVELY.
        if (!int.TryParse(args[0], out int N))
        {
            Console.WriteLine($"Invalid number of vehicles: {args[0]}");
            return;
        }

        if (!int.TryParse(args[1], out int P))
        {
            Console.WriteLine($"Invalid number of pilots: {args[1]}");
            return;
        }

        //instantiating our lock object,parkinglot,intersection and Roads classes
        object lockObj = new object();
        ParkingLot parkingLot = new ParkingLot(N);
        Intersection intersection = new Intersection();
        Roads roads = new Roads();

        //This thread is responsible for periodically changing the direction of the traffic signal, simulating a real-world traffic light.
        Thread signalThread = new Thread(() => ChangeDirection(lockObj, intersection.time));
        signalThread.Start();

        var mres = new List<ManualResetEvent>();

        for (int i = 1; i <= P; i++)
        {
            var mre = new ManualResetEvent(false);
            mres.Add(mre);

            ThreadPool.QueueUserWorkItem((object? args) =>
            {
                int pilotNumber = (int)args!;
                Pilot pilot = new Pilot(pilotNumber, parkingLot, lockObj, intersection, roads);
                pilot.Route();
                mre.Set();
            }, i);
        }
        WaitHandle.WaitAll(mres.ToArray()); // This ensures that all pilot threads have finished executing their tasks before the program proceeds.

        // Signal the traffic light thread to stop and wait for it to finish
        stopSignalThread = true;
        signalThread.Join(); //This ensures that the traffic signal thread is terminated before the program proceeds.

        // Check if all vehicles have passed the intersection
        if (intersection.crossedcount != N)
        {
            Console.WriteLine("ERROR: Not all vehicles have passed the intersection, only "+intersection.crossedcount+" vehicles crossed instead of "+N+" vehicles.");
            Environment.Exit(1);
        }
        else
        {
            Console.WriteLine($"All {N} vehicles have passed the intersection.");
            Environment.Exit(0);
        }
    }
}
