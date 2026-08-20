//GATHER
//the first Gather algorithm starts from the nodes with the least significant bit difference (first fold) and then moves to the nodes with the most significant bit difference (third fold). The second Gather algorithm starts from the nodes with the most significant bit difference (third fold) and then moves to the nodes with the least significant bit difference (first fold). The output will display the sender and receiver nodes for each round and the perspective of the specified node during the Gather operation.

using System;
using System.Collections.Generic;

class HypercubeGather
{
    static void Main(string[] args)
    {
        int numNodes = 8;
        int destinationNode = 0;
        int perspectiveNode = 3;

        PerformGatherFirstToThird(numNodes, destinationNode, perspectiveNode);

        PerformGatherThirdToFirst(numNodes, destinationNode, perspectiveNode);
    }


    static void PerformGatherFirstToThird(int numNodes, int destinationNode, int perspectiveNode)
    {
        int dimensions = (int)Math.Log(numNodes, 2);
        List<int>[] rounds = new List<int>[dimensions];

        for (int i = 0; i < dimensions; i++)
        {
            rounds[i] = new List<int>();
            int mask = 1 << i;
            int node = destinationNode ^ mask;

            if (node < numNodes)
            {
                rounds[i].Add(destinationNode);
                rounds[i].Add(node);
            }
        }

        Console.WriteLine("Gather Operation (First to Third Fold):");

        for (int round = 0; round < dimensions; round++)
        {
            Console.WriteLine($"Round {round + 1}:");
            for (int i = 0; i < rounds[round].Count; i += 2)
            {
                int receiver = rounds[round][i];
                int sender = rounds[round][i + 1];

                Console.WriteLine($"  Node {sender} sends to Node {receiver}");
            }
        }

        Console.WriteLine($"\nPerspective of Node {perspectiveNode}:");
        for (int round = 0; round < dimensions; round++)
        {
            for (int i = 0; i < rounds[round].Count; i += 2)
            {
                int receiver = rounds[round][i];
                int sender = rounds[round][i + 1];

                if (perspectiveNode == sender)
                {
                    Console.WriteLine($"  Round {round + 1}: Sent to Node {receiver}");
                }
            }
        }
    }

    static void PerformGatherThirdToFirst(int numNodes, int destinationNode, int perspectiveNode)
    {
        int dimensions = (int)Math.Log(numNodes, 2);
        List<int>[] rounds = new List<int>[dimensions];

        for (int i = dimensions - 1; i >= 0; i--)
        {
            rounds[i] = new List<int>();
            int mask = 1 << i;
            int node = destinationNode ^ mask;

            if (node < numNodes)
            {
                rounds[i].Add(destinationNode);
                rounds[i].Add(node);
            }
        }

        Console.WriteLine("\nGather Operation (Third to First Fold):");

        for (int round = dimensions - 1; round >= 0; round--)
        {
            Console.WriteLine($"Round {dimensions - round}:");
            for (int i = 0; i < rounds[round].Count; i += 2)
            {
                int receiver = rounds[round][i];
                int sender = rounds[round][i + 1];

                Console.WriteLine($"  Node {sender} sends to Node {receiver}");
            }
        }

        Console.WriteLine($"\nPerspective of Node {perspectiveNode}:");
        for (int round = dimensions - 1; round >= 0; round--)
        {
            for (int i = 0; i < rounds[round].Count; i += 2)
            {
                int receiver = rounds[round][i];
                int sender = rounds[round][i + 1];

                if (perspectiveNode == sender)
                {
                    Console.WriteLine($"  Round {dimensions - round}: Sent to Node {receiver}");
                }
            }
        }
    }
}
