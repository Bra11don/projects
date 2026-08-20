//Brandon C Dickson
// CS4012
// Parallel Permutations

using System.Diagnostics;

class Permute
{

    //JKVStep is adopted from the Original JKVStep function from the lab
    //- the function returns the next higher permutation from the given input string
    private static string? JKVStep(string start)
    {

        char[] digits = start.ToArray();

        void Swap(int to, int with)
        {
            char tmp = digits[to];
            digits[to] = digits[with];
            digits[with] = tmp;
        }
        char maxRight = digits[digits.Length - 1];
        for (var i = digits.Length - 2; i >= 0; i--)
        {
            char digit = digits[i];
            if (digit < maxRight)
            {
                var lessSignificant = i + 1;
                var repNdx = -1;
                for (var j = lessSignificant; j < digits.Length; j++)
                    if (digits[j] > digit &&
                        (repNdx == -1 || digits[j] < digits[repNdx]))
                        repNdx = j;
                Swap(repNdx, i);

                Array.Sort(digits, lessSignificant, digits.Length - lessSignificant);
                return new String(digits);

            }

            maxRight = digit;
        }
        return null;
    }

    //The following methods; GetPermutationsUsingList and GetPermutationsUsingArray were adopted from the GetPermutations functions in the permutations lab.
    //-  They generate all permutations of a given string using the JKVStep function

    //GetPermutationsUsingList is going to combine the results from JKVStep and put them into a list
    // It uses multiple threads to generate permutations in parallel.
    private static List<string> GetPermutationsUsingList(string permuteThis)
    {
        var permutations = new List<string>(); //to collect the final permutations

        //define a list thats going to store the local permutations
        var permutationsList = new List<List<string>>();

        //list to store the manual reset events
        var mres = new List<ManualResetEvent>();

        //iterating over the string
        for (var i = 0; i < permuteThis.Length; i++)
        {
            //given the character at index i (the head) this list will store all the local permutations for the character, then a
            var locals = new List<string>();
            permutationsList.Add(locals);

            //the head and tail of the string
            var head = permuteThis[i];
            var tail = permuteThis.Substring(0, i) + permuteThis.Substring(i + 1);

            var mre = new ManualResetEvent(false);
            mres.Add(mre);

            // Queue a work item to generate the local permutations for this character.
            ThreadPool.QueueUserWorkItem((object? args) => // using QUWI queue a lambda-define procedure
            {
                //unpacking the tuple of input parameters
                (var head, var tail, var mre, var locals) = (ValueTuple<char, string, ManualResetEvent, List<string>>)args!;

                lock (locals)
                //ensures that only one thread can modify the permutations array at a time, preventing potential race conditions where multiple threads might try to add elements to the array simultaneously.
                {
                    while (tail != null)
                    {
                        locals.Add(head.ToString() + tail);
                        tail = JKVStep(tail);
                    }
                } ((ManualResetEvent)mre)!.Set(); //signal that the thread has finished

            }, (head, tail, mre, locals));

        }
        WaitHandle.WaitAll(mres.ToArray());

        //moving the permutations from the list of lists to the permutations list that is returned by the function
        foreach (List<string> item in permutationsList)
        {
            permutations.AddRange(item);
        }

        return permutations;
    }

    //GetPermutationsUsingArray is going to combine the results from JKVStep and put them into an array
    private static string[] GetPermutationsUsingArray(string permuteThis)
    {
        var permutations = new string[Factorial(permuteThis.Length)]; // to store the final permutations

        var mres = new List<ManualResetEvent>();

        for (int i = 0; i < permuteThis.Length; i++)
        {
            var head = permuteThis[i];
            var tail = permuteThis.Substring(0, i) + permuteThis.Substring(i + 1);

            var mre = new ManualResetEvent(false);
            mres.Add(mre);

            //currentIndex is the starting index in permutations array for the block of permutations in the ith block since a tail will produce fact(tail.Length) "tails" to each of which you will prepend the head.
            int currentIndex = i * Factorial(tail.Length);
            ThreadPool.QueueUserWorkItem((object? args) => // using QUWI queue a lambda-define procedure
            {
                (var head, var tail, var mre, string[] locals) = (ValueTuple<char, string, ManualResetEvent, string[]>)args!; //unpacking the tuple

                lock (locals)
                //ensures that only one thread can modify the permutations array at a time, preventing potential race conditions where multiple threads might try to add elements to the array simultaneously.
                {
                    while (tail != null)
                    {

                        permutations[currentIndex] = (head.ToString() + tail);
                        currentIndex++;
                        tail = JKVStep(tail);
                    }
                } ((ManualResetEvent)mre)!.Set(); //setting the flag to signal that the thread has finished

            }, (head, tail, mre, permutations));
        }
        WaitHandle.WaitAll(mres.ToArray());

        return permutations;
    }


    // the Factorial function is used to calculate the total number of possible permutations of a given string. For example, the factorial of 4 is 4 x 3 x 2 x 1 = 24, which means there are 24 possible permutations of a string with 4 characters.
    //we are going to use the results from this to be the size of the array in the GetPermutationsusingArray function
    private static int Factorial(int length)
    {
        int factorial = 1;
        for (int i = length; i > 1; i--)
        {
            factorial = factorial * i;
        }
        return factorial;
    }



    static void Main(string[] args)
    {
        if (args.Length < 2 || args.Length > 3)
        {
            Console.WriteLine("User entry should be in the form : permutation <string> <list|array> [show]");
            return;
        }

        string permuteThis = args[0];
        string method = args[1];
        bool showResults = args.Length == 3 && args[2] == "show";

        //Sort the strings
        // since the algorithm works going forward we need to sort the strings to get the lowest configuration possible then, pass it through
        char[] aword = permuteThis.ToCharArray();
        Array.Sort(aword);
        permuteThis = new string(aword);
        //------

        //validating the string to be permuted
        if (string.IsNullOrEmpty(permuteThis))
        {
            return;
        }

        //validating method input
        if (method != "list" && method != "array")
        {
            Console.WriteLine("Second argument must be 'list' or 'array'");
            return;
        }

        Console.WriteLine($"Generating permutations for string: {permuteThis}");

        var stopwatch = Stopwatch.StartNew();

        List<string> permutationsList = new List<string>();
        string[] permutationsArray = new string[0]; //initializing the array to 0 to ensure that it has a value even if GetPermutationsArray doesnt return permutations (and also prevent any errors)

        // if user chooses list then call the GetPermutationsUsingList function
        if (method == "list")
        {
            permutationsList = GetPermutationsUsingList(permuteThis);
            if(!showResults){
            Console.WriteLine("Number of permutations: " + permutationsList.Count);
            }
        }
        else //this means that the user chose the array method so we call the GetPermutationsUsingArray function
        {
            permutationsArray = GetPermutationsUsingArray(permuteThis);

            if(!showResults){
            Console.WriteLine("Number of permutations: " + permutationsArray.Length);
            }
        }

        stopwatch.Stop();

        //if the user chooses the show option then the individual permutations will be printed
        if (showResults)
        {
            Console.WriteLine("Permutations:");
            if (method == "list")
            {
                foreach (var result in permutationsList)
                {
                    Console.WriteLine(result);
                }
                Console.WriteLine("Number of permutations: " + permutationsList.Count);
            }
            else
            {
                foreach (var result in permutationsArray)
                {
                    Console.WriteLine(result);
                }
                Console.WriteLine("Number of permutations: " + permutationsArray.Length);
            }
        }

        Console.WriteLine($"Execution time: " + stopwatch.Elapsed);

    }

}