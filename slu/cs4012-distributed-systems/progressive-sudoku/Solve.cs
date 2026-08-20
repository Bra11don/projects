using System;
namespace sudoku;

// partial classes are likely a terrible thing to show you; I am almost certain
// someone will abuse it terribly. The idea is to allow you to nice partition the
// methods and fields of a class so that the entire class doesn't become too
// overwhelming. It has its use--and abuse.

public partial class Sudoku {
    // a delegate is a pointer to a function or method. This is something like
    // that awful typedef you learned about in C. I'd not show you these at all
    // except that they are heavily used in things we will be exploring in few
    // more weeks... So, here goes (at least the basic version).

    // here we define a type, Test, of method that takes three integer args
    // and returns a bool. [boolean ran away to Java] A variable of type Test
    // then, is a reference to such a method.
    //
    private delegate bool Constraint (int row, int col, int value);

    // Thus, each of the following three methods conform to the type Test

    private bool RowCheck (int row, int col, int value) {
	for (col = 0; col < 9; col++)
	    if (ThePuzzle[row, col] == value)
		return false;
	return true;
    }

    private bool ColCheck (int row, int col, int value) {
	for (row = 0; row < 9; row++)
	    if (ThePuzzle[row, col] == value)
		return false;
	return true;
    }

    private bool BoxCheck (int row, int col, int value) {
	// we need to compute the initial coordinates of the box containing
	// the [row, col] element...

	var rowOrg = row / 3 * 3;  // LTS: why?
	var colOrg = col / 3 * 3;

	for (row = 0; row < 3; row++)
	    for (col = 0; col < 3; col++)
		if (ThePuzzle[rowOrg + row, colOrg + col] == value)
		    return false;
	return true;
    }

    // Now, for pedagogical reasons, let's have a list of the three "Constraint"
    // compliant methods... This is initialized in the Sudoku ctor in other file.

    private List<Constraint> validators;

    private bool Validate(int step, int candidate) {
	// step is the "number" of the sudoku puzzle piece we are working on.
	// We will add a method to the Puzzle class to convert the step number
	// to a pair of indexes--go look there (Puzzle is in the file Sudoku.cs)
	//
	(var row, var col) = ThePuzzle.StepToIndices(step);

	// a validation accumulator
	var validated = true;

	// invoke the validaing methods (RowCheck, ColCheck and BoxCheck) until
	// all check out clean or one fails.
	//
	foreach (var validator in validators)
	    if (!(validated = validated && validator(row, col, candidate)))
		break;
	return validated;
    }

    // a private setter means nobody outside of the class can change values
    public int StepsTaken { get; private set; }

    public bool Solve(int step) {
	// remember recurion? How about backtracking?

	if (step == 81)
	    // terminal case--and successful! All squares filled/validated
	    return true;

	(var row, var col) = ThePuzzle.StepToIndices(step);
	if (ThePuzzle[row, col] != 0)
	    // if the location is "prefilled," leave it alone and move along
	    return Solve(step + 1);

	// an unfilled location can have (this is not optimized) 10 possible
	// solutions (1..9). Try each candidate in order, until one succeeds
	// or everyone fails (meaning the caller set us up for failure--it's
	// not our problem to fix--backtrack!)
	//
	for (var candidate = 1; candidate < 10; candidate++) {
	    if (Validate(step, candidate)) {
		// okay, the proposed candidate IS allowed (not in row, col or box)
		ThePuzzle[row, col] = candidate;
		StepsTaken++; // 'cause it's fun to know--sorta

		// So, see if the puzzle as now configured with the candidate
		// in the step location is part of a complete solution...
		// (common mistake--NEVER step++. We're still using it locally!)
		//
		if (Solve(step + 1))
		    // Solve worked! "Turtles all the way down!"
		    return true;
	    }
	}

	// nothing worked--Sigh. We were destined to fail from the start. Fall
	// back to the caller and let them try something else in an earlier step
	// position--we're out of options.
	//
	// Before leaving, clean up our mess!
	//
	ThePuzzle[row, col] = 0;
	return false;
    }
}
