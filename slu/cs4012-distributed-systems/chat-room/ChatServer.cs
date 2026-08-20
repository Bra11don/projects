//BRANDON C DICKSON
//CS4012
//CHATSERVER PROJECT

using System.Net;
using System.Net.Sockets;

namespace ChatServer
{
    class Program
    {
        static void Main(string[] args)
        {
            //command line argument validation and parsing
            if (args.Length < 2)
            {
                Console.WriteLine("Usage: server <port> {show}");
                return;
            }
            int port = Int32.Parse(args[1]);

            if (args.Length == 3 && args[2].ToLower() == "show")
            {
                MessageQueue.Show = true;
            }

            if (args[0].ToLower() == "server")
            {
                BecomeServer(port);
            }
        }

        // Set up and start the Chat Server
        private static void BecomeServer(int port)
        {
            // Creating a TcpListener to listen for incoming connections on the specified port
            TcpListener server = new TcpListener(IPAddress.Any, port);
            server.Start();
            Console.WriteLine($"Server listening on port {port}");

            // Continuously accept incoming client connections
            while (true)
            {
                TcpClient client = server.AcceptTcpClient();

                // Create and start a new Task for each connected client to handle communication
                var u = new Task(obj => PCCS((TcpClient)obj!), client);
                u.Start();
                // u.Wait();
            }
        }

        // clientId is the Unique identifier for connected clients
        static int clientId = 0; //id 0 is assigned to the server and every one after is assigned to each of the connected clients

        // Process communication between the server and the connected client
        private static void PCCS(TcpClient client)
        {
            // Increment the client ID counter and assign the current client ID
            var currentClientId = Interlocked.Increment(ref clientId);//threadsafe


            // Set up the StreamReader and StreamWriter for the client's communication stream
            var stream = client.GetStream();
            var reader = new StreamReader(stream);
            var writer = new StreamWriter(stream) { AutoFlush = true };

            // Send a welcome message to the client
            writer.WriteLine("Welcome to the Chat Room!");

            // Enqueue a message announcing the client's entry into the chat
            MessageQueue.Enqueue($"Client {currentClientId} has joined the chat.", 0);

            // Initialize the timestamp and bailOut flag
            long timestamp = MessageQueue.Now;
            bool bailOut = false;

            // Task to send messages from the server to the client
            // “To Client” task dequeues messages from the MessageQueue (server) and writes those messages to the client’s user.
            //If a signal is set by the “From Client” task to bail out, then we exit the task—but not before cancelling any active Dequeue requests!
            var toClientTask = Task.Run(() =>
            {
                while (!bailOut)
                {
                    var message = MessageQueue.Dequeue(ref timestamp, currentClientId, in bailOut);
                    if (message != null)
                    {
                        writer.WriteLineAsync(message);
                    }
                    else
                    {
                        break;
                    }
                }
            });

            // Task to receive messages from the client and process them
            //“From Client” task reads messages from the client's user (via the ReadLineAsync) and enqueues those messages in the MessageQueue. Also includes a check for whether the client has disconnected or the client has stopped typing within the 20 second time limit
            var fromClientTask = Task.Run(() =>
            {
                while (!bailOut)
                {
                    var readLineTask = reader.ReadLineAsync();

                    // Wait for the readLineTask to complete or time out after 20 seconds
                    //if the completed Task receives a value of 0 then we know the user did something before the timeout. Once we get -1 then the nothing was entered within the 20seconds
                    var completedTask = Task.WaitAny(new[] { readLineTask }, 20000);

                    // Process the received message or handle the timeout
                    if (completedTask == 0) //we first check if the client's user did infact press something before the timeout
                    {
                        var message = readLineTask.Result;

                        //then we determine if it was a valid input, or the client's user disconnected
                        if (message == null) //true -- user disconnected
                        {
                            bailOut = true;
                            toClientTask.Wait();//Signal the “To Client task to complete and wait for it to do so;
                            break;
                        }

                        // Enqueue the received message to the MessageQueue
                        MessageQueue.Enqueue(message, currentClientId);
                    }
                    else
                    {
                        // Timeout occurred
                        bailOut = true;
                        writer.WriteLineAsync("Your session will be terminated due to inactivity.");

                        // Enqueue a message announcing the client's termination
                        MessageQueue.Enqueue($"Client {currentClientId} will be terminated.", 0);
                        toClientTask.Wait();//Signal the “To Client task to complete and wait for it to do so;
                        break;
                    }
                }
                // Enqueue a message announcing the client's exit from the chat
                MessageQueue.Enqueue($"Client {currentClientId} has left the chat.", 0);
            });
            //wait for both tasks to be complete before ending the program.
            Task.WaitAll(fromClientTask,toClientTask);
        }
    }
}