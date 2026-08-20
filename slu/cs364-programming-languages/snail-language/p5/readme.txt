# Snail Language Interpreter
## Implementation Details and Design Decisions

This project implements an interpreter for the Snail programming language using ReasonML. The implementation consists of three main components: the AST representation (ast.re), the operational semantics (opsems.re), and the main interpreter (interpreter.re).

### Core Components

1. **AST Representation (ast.re)**
   - Implements a comprehensive Abstract Syntax Tree structure using ReasonML's variant types
   - Handles JSON parsing of the AST input using Yojson
   - Special attention to string literal handling, particularly escape sequences (\n, \t)
   - Modular design separating AST node types (expressions, methods, members) for clear code organization

2. **Operational Semantics (opsems.re)**
   - Implements environment-based evaluation with explicit store management
   - Uses two key data structures:
     * Environment (StringMap): Maps identifiers to locations
     * Store (LocationMap): Maps locations to values
   - Carefully handles object orientation features:
     * Method dispatch (dynamic, static, and self)
     * Inheritance through recursive method lookup
     * Member variable access and initialization

3. **Main Interpreter (interpreter.re)**
   - Handles program initialization and command-line interface
   - Sets up initial environment and store
   - Implements main class instantiation and method invocation

### Key Design Decisions

1. **Memory Management**
   - Implemented location-based store management instead of direct value storage
   - Used unique location generation for proper variable scoping
   - Reason: This design enables proper handling of aliasing and shared state

2. **Method Dispatch**
   - Recursive method lookup for inheritance chain
   - Separate handling for built-in vs. user-defined methods
   - Reason: Ensures proper method resolution while maintaining clean separation between built-in and user functionality

3. **Error Handling**
   - Comprehensive runtime error reporting with line and column information
   - Strict type checking and bounds checking
   - Reason: Provides clear feedback for debugging and maintains language semantics

4. **Scoping Implementation**
   - Block-level scope with environment chaining
   - Proper handling of self in method calls
   - Reason: Ensures correct variable visibility and method dispatch semantics

---

## Usage

### Prerequisites
- Ensure you have `dune`, `dune-project`, and `dune-workspace` configured in your project.

### Running a Snail Program
1. Build the project using `dune`:
   ```bash
   dune build
   ```
2. Create a `.sl` test file with your Snail program.
3. Parse the `.sl` file to generate an AST file using the following command:
   ```bash
   snail --parse <your-file>.sl
   ```
   This creates a corresponding `<your-file>.sl-ast` file.
4. Run the interpreter on the generated AST file using:
   ```bash
   dune exec ./interpreter.exe <your-file>.sl-ast > my-output.txt
   ```
5. Check the output in `my-output.txt` to verify the program's behavior.

---

### Testing Strategy

The implementation was tested using a comprehensive suite of test cases covering:
1. Basic Operations
   - Arithmetic and boolean operations
   - String manipulation
   - Array operations
2. Object-Oriented Features
   - Inheritance and method override
   - Dynamic dispatch
   - Self dispatch
3. Scoping and Variables
   - Block scoping
   - Variable shadowing
   - Member access
4. Edge Cases
   - Error handling
   - Type checking
   - Boundary conditions


### Implementation Status

The interpreter successfully handles:
- All basic Snail operations
- Object-oriented features including inheritance
- Built-in types and methods
- Proper scoping and variable access
- Array operations
- String manipulation
- Runtime error reporting

Areas of focus for future improvements:
- Enhanced error reporting
- Optimization of method dispatch
- More robust input/output handling
