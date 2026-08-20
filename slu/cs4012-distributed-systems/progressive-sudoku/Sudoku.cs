using System;
namespace sudoku;

public partial class Sudoku {
    // an inner class to represent the state of the solution
    //
    public class Puzzle {
	// A "property" is an abstraction of encapsulation of data with fields
	// and accessors. The type and name of the Property correspond to the
	// type and name of a field. The { braced } elements are the accessor
	// methods: a getter and an optional setter are common. They are a bit
	// more than just shortcuts, but for now, that is enough.

	// BoxLen of the length/width of the inner "cubes" of a Sudoku puzzle
	// (typically 3). Note: no Setter.
	private int BoxLen { get; }

	// Size of the length/width of a Sudoku puzzle (typically 9). Note
	// that there's not a real field here: the "gotten" value is calculated
	public int Size { get => BoxLen * BoxLen; }

	// and this is just a regular, old field... a 2D array. This will be
	// accessed using an "indexer property" (see comments below)
	//
	private int[,] grid;

	// I've included this next part because it is an example of a special
	// form of Property known as an indexer. These are useful because they
	// allow you to access an object as if it were an indexable entity. In
	// this case, "indexing" an instance of Puzzle will provide the indexed
	// elements of grid. Index getters (see below) could return any value
	// that matches the type of the indexer property.
	//
	public int this[int row, int col] {
	    get => grid[row, col];
	    set => grid[row, col] = value;    // value is implicitly defined
	}
	// so, if we had an instance of a Puzzle, say, P, then
	//    P[2,7] "gets" the value of the grid array element grid[2,7]
	// and
	//    P[4,1] = 7 invokes the "setter" with value set to 7.

	// Note the override! If you don't remember it, you will be so very
	// disappointed with your ToString method. Chaulk one up for Java!
	//
	public override String ToString() {
	    String result = String.Empty;
	    for (var r = 0; r < Size; r++) {
		for (var c = 0; c < Size; c++) {
		    // the next line assumes that you are familiar with the so-called
		    // "trinary" operator, ?:
		    //
		    // and that you understand coallating sequences (like ASCII)
		    // and the realization of char values as small integers.
		    //
		    result += $"{(this[r,c] == 0 ? '.' : (char)('A' + this[r,c]-1))} ";
		    if (c % BoxLen == BoxLen - 1)
			result += ' ';
		}
		result += '\n';
		if (r % BoxLen == BoxLen - 1)
		    result += '\n';
	    }
	    return result;
	}

	// a silly but pedagogically useful method, because it returns a pair
	// of values as a tuple. Take a look at the Validate method in Solve.cs
	// to see it used!
	//
	public (int, int) StepToIndices (int step) {
	    var row = step / Size;
	    var col = step % Size;
	    return (row, col);
	}

	// a pair of overloaded constructors (ctors). CSharp isn't so clever as
	// Java here, either. The ability to cascade ctors simply isn't here. But,
	// CSharp DOES provide default values for method fields--including for use
	// in ctors.

	public Puzzle (int[,] grid) {
	    // provided grid, calculate the fundamental characteristic BoxLen
	    this.grid = grid;
	    BoxLen = (int)Math.Sqrt(grid.GetLength(0));
	}

	public Puzzle (int boxLen) {
	    // provided boxLen, create an appropriate grid
	    BoxLen = boxLen;
	    grid = new int[Size, Size];
	}
    }

    // some part of me really doesn't like this...
    public Puzzle ThePuzzle { get; }

    // a very simple ToString... It will become fancier later on
    public override String ToString() =>
	ThePuzzle.ToString();

    // a trivial constructor... It will also become more complicated
    public Sudoku () {
	// build the puzzle (well, the grid, anyway)
	ThePuzzle = new(3);

	// set up the constraints we need checked
	validators = new() {RowCheck, ColCheck, BoxCheck};
    }
}
