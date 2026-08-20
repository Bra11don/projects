//REDUCE
//We first calculate the dimensions of the hypercube based on the given number of nodes. We then create a list for each dimension to store the sender and receiver pairs for each round of the Reduce operation. After initializing the lists, we start from the first fold and go to the third fold, updating the sender and receiver pairs for each round, as well as the node values. Finally, we output the perspective of the given perspective node during the Reduce operation.

using System;
using System.Collections.Generic;

class HypercubeReduce
{
    static void Main(string[] args)
    {
        int numNodes = 8;
        int sourceNode = 4;
        int perspectiveNode = 3;
        int[] nodeValues = {1, 2, 3, 4, 5, 6, 7, 8};

        PerformReduce(numNodes, sourceNode, perspectiveNode, nodeValues);
    }

    static void PerformReduce(int numNodes, int sourceNode, int perspectiveNode, int[] nodeValues)
    {
        int dimensions = (int)Math.Log(numNodes, 2);
        List<int>[] rounds = new List<int>[dimensions];

        for (int i = 0; i < dimensions; i++)
        {
            rounds[i] = new List<int>();
            int mask = 1 << i;
            int node = sourceNode ^ mask;

            if (node < numNodes)
            {
                rounds[i].Add(sourceNode);
                rounds[i].Add(node);
            }
        }

        Console.WriteLine("Reduce Operation:");

        for (int round = 0; round < dimensions; round++)
        {
            Console.WriteLine($"Round {round + 1}:");
            for (int i = 0; i < rounds[round].Count; i += 2)
            {
                int sender = rounds[round][i];
                int receiver = rounds[round][i + 1];
                nodeValues[receiver] *= nodeValues[sender];

                Console.WriteLine($"  Node {sender} sends to Node {receiver} (Value updated to {nodeValues[receiver]})");

                for (int nextRound = round + 1; nextRound < dimensions; nextRound++)
                {
                    int mask = 1 << nextRound;
                    int newNode = receiver ^ mask;

                    if (newNode < numNodes && !rounds[nextRound].Contains(newNode))
                    {
                        rounds[nextRound].Add(receiver);
                        rounds[nextRound].Add(newNode);
                    }
                }
            }
        }

        Console.WriteLine($"\nPerspective of Node {perspectiveNode}:");
        for (int round = 0; round < dimensions; round++)
        {
            for (int i = 0; i < rounds[round].Count; i += 2)
            {
                int sender = rounds[round][i];
                int receiver = rounds[round][i + 1];

                if (perspectiveNode == receiver)
                {
                    Console.WriteLine($"  Round {round + 1}: Received from Node {sender} (Value updated to {nodeValues[perspectiveNode]})");
                }
            }
        }
    }
}
