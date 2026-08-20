//BROADCAST
//we first calculate the dimensions of the hypercube based on the given number of nodes. We then create a list for each dimension to store the sender and receiver pairs for each round of broadcasting. After initializing the lists, we start from the third fold (the highest dimension) and go back to the first fold, updating the sender and receiver pairs for each round. Finally, we output the perspective of the given perspective node during the broadcast process.

using System;
using System.Collections.Generic;

class HypercubeBroadcast
{
    static void Main(string[] args)
    {
        int numNodes = 8;
        int sourceNode = 1;
        int perspectiveNode = 3;

        PerformBroadcast(numNodes, sourceNode, perspectiveNode);
    }

    static void PerformBroadcast(int numNodes, int sourceNode, int perspectiveNode)
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

        for (int round = dimensions - 1; round >= 0; round--)
        {
            Console.WriteLine($"Round {dimensions - round}:");
            for (int i = 0; i < rounds[round].Count; i += 2)
            {
                int sender = rounds[round][i];
                int receiver = rounds[round][i + 1];

                Console.WriteLine($"  Node {sender} sends to Node {receiver}");

                for (int nextRound = round - 1; nextRound >= 0; nextRound--)
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
        for (int round = dimensions - 1; round >= 0; round--)
        {
            for (int i = 0; i < rounds[round].Count; i += 2)
            {
                int sender = rounds[round][i];
                int receiver = rounds[round][i + 1];

                if (perspectiveNode == sender)
                {
                    Console.WriteLine($"  Round {dimensions - round}: Sent to Node {receiver}");
                }
                else if (perspectiveNode == receiver)
                {
                    Console.WriteLine($"  Round {dimensions - round}: Received from Node {sender}");
                }
            }
        }
    }
}
