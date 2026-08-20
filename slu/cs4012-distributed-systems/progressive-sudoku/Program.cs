using System;
namespace sudoku;

public class Program {
    public static void Main(String[] args) {
	var sdk = new Sudoku();

	// diagonal "prefilled" with 1..9
	//
	for (int d = 0; d < 9; d++)
	    sdk.ThePuzzle[d, d] = d + 1;
	Console.WriteLine(sdk);

	// so, solve it!
	//
	if (sdk.Solve(0))
	    // well, what were you expecting? Of course it works!
	    Console.WriteLine("{0}\n{1}", sdk.StepsTaken, sdk);
	else
	    // better not get here with a valid puzzle!
	    Console.WriteLine("No solution.");
    }
}
