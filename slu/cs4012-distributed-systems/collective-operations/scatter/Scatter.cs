//SCATTER

using System;
using System.Collections.Generic;

class HypercubeScatter
{
    static void Main(string[] args)
    {
        int numNodes = 8;
        int sourceNode = 0;
        int perspectiveNode = 3;

        PerformScatterFirstToThird(numNodes, sourceNode, perspectiveNode);

        PerformScatterThirdToFirst(numNodes, sourceNode, perspectiveNode);
    }

    static void PerformScatterFirstToThird(int numNodes, int sourceNode, int perspectiveNode)
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

        Console.WriteLine("Scatter Operation (First to Third Fold):");

        for (int round = 0; round < dimensions; round++)
        {
            Console.WriteLine($"Round {round + 1}:");
            for (int i = 0; i < rounds[round].Count; i += 2)
            {
                int sender = rounds[round][i];
                int receiver = rounds[round][i + 1];

                Console.WriteLine($"  Node {sender} sends to Node {receiver}");
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
                    Console.WriteLine($"  Round {round + 1}: Received from Node {sender}");
                }
            }
        }
    }

    // ... (Scatter First to Third Fold code remains the same as above)

static void PerformScatterThirdToFirst(int numNodes, int sourceNode, int perspectiveNode)
{
    int dimensions = (int)Math.Log(numNodes, 2);
    List<int>[] rounds = new List<int>[dimensions];

    for (int i = dimensions - 1; i >= 0; i--)
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

    Console.WriteLine("\nScatter Operation (Third to First Fold):");

    for (int round = dimensions - 1; round >= 0; round--)
    {
        Console.WriteLine($"Round {dimensions - round}:");
        for (int i = 0; i < rounds[round].Count; i += 2)
        {
            int sender = rounds[round][i];
            int receiver = rounds[round][i + 1];

            Console.WriteLine($"  Node {sender} sends to Node {receiver}");
        }
    }

    Console.WriteLine($"\nPerspective of Node {perspectiveNode}:");
    for (int round = dimensions - 1; round >= 0; round--)
    {
        for (int i = 0; i < rounds[round].Count; i += 2)
        {
            int sender = rounds[round][i];
            int receiver = rounds[round][i + 1];

            if (perspectiveNode == receiver)
            {
                Console.WriteLine($"  Round {dimensions - round}: Received from Node {sender}");
            }
        }
    }
}

// ... (Main method remains the same as above)
