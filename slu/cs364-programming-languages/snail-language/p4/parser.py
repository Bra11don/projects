#201967cc8d6a85b5befe76602ccbb86bd55df57f

## Core configuration
tokens = (
    'CLASS',
    'IDENT',
    'COLON',
    'LBRACE',
    'RBRACE',
    'SEMI',
    'LET',
    'PLUS',
    'MINUS',
    'TIMES',
    'DIVIDE',
    'INT',
    'FLOAT',
    'STRING',
    'EQUALS',
    'ASSIGN',
    'LTE',
    'LT',
    'UMINUS',
    'NOT',
    'IF',
    'ELSE',
    'NEW',
    'ISVOID',
    'WHILE',
    'TRUE',
    'FALSE',
    'AT',
    'COMMA',
    'DOT',
    'LBRACKET',
    'RBRACKET',
    'RPAREN',
    'LPAREN'
)


precedence = (
    ('nonassoc', 'ASSIGN'),
    ('nonassoc', 'NOT'),
    ('nonassoc', 'LT', 'LTE', 'EQUALS'),
    ('left', 'PLUS', 'MINUS'),
    ('left', 'TIMES', 'DIVIDE'),
    ('left', 'LBRACKET'),# Array access
    ('nonassoc', 'UMINUS'),# Unary operators
    ('nonassoc', 'AT'),
    ('nonassoc', 'DOT')
)

## Structure

# program ::= (class)+
# class ::= 'class' 'ident' (':' 'ident')? '{' (feature)* '}' ';'
def p_program(p):
    """
    program ::= class_list
    """
    p[0] = p [1]

# Recursively builds a list of class definitions from one or more class declarations
def p_class_list(p):
    """
    class_list ::= class class_list
                | class
    """

    # build up the list of classes
    if len(p) == 2:
        # we just have a class
        p[0] = [p[1]]
    else:
        p[0] = [p[1]] + p[2]

# program ::= class_list
# class_list ::= class class_list
#                class

# Creates class AST node with optional inheritance, collecting all methods and member variables
def p_class(p):
    """
    class ::= CLASS IDENT LBRACE feature_list RBRACE SEMI
            | CLASS IDENT COLON IDENT LBRACE feature_list RBRACE SEMI
    """
    check_restricted_class_name(p[2], p.lineno(2), p.lexpos(2))
    p[0] = {
        "class_name": p[2] # Simply use the identifier string
    }

    if len(p) >= 8:  # Class with o
        check_restricted_inherited_class_name(p[4], p.lineno(3), p.lexpos(3))
        p[0]["inherits"] = p[4]
        features = p[6]
    else:
        features = p[4]

    # Filter out just the members and just the methods
    p[0]["members"] = [f for f in features if f["type"] == "member"]
    p[0]["methods"] = [f for f in features if f["type"] == "method"]

# Creates class AST node for classes with no methods or member variables
def p_class_empty_body(p):
    """
    class ::= CLASS IDENT LBRACE RBRACE SEMI
    """
    check_restricted_class_name(p[2], p.lineno(2), p.lexpos(2))

    p[0] = {
        "class_name": p[2],
        "members": [],
        "methods": []
    }

# Validates that class name is not a reserved type name
def check_restricted_class_name(name, lineno, lexpos):
    restricted_names = ["Array", "Bool", "Int", "IO", "String", "Object"]
    if name in restricted_names:
        print(f"ERROR: {lineno}:{lexpos}: Parser: cannot name a class the same as a built-in type")
        exit(0)

# Prevents inheritance from restricted types like Int, Bool, String, Array
def check_restricted_inherited_class_name(name, lineno, lexpos):
    restricted_names = ["Array", "Bool", "Int", "String"]
    if name in restricted_names:
        print(f"ERROR: {lineno}:{lexpos}: Parser: Invalid class name '{name}'")
        exit(0)

##Feature Handling

# feature_list ::= (feature)*
# feature_list ::= empty_list
#                | nonempty_feature_list
# nonempty_feature_list ::= feature non_empty_feature_list
#                         | feature
# Handles list of class features (methods and members)
def p_feature_list(p):
    """
    feature_list ::= empty_list
                   | nonempty_feature_list
    """
    p[0] = p[1]

# Recursively builds list of class features, ensuring at least one feature exists
def p_nonempty_feature_list(p):
    """
    nonempty_feature_list ::= feature nonempty_feature_list
                            | feature
    """
    if len(p) == 2:
        p[0] = [p[1]]
    else:
        p[0] = [p[1]] + p[2]

# Represents an empty feature list for classes with no members or methods
def p_empty_list(p):
    """
    empty_list ::=
    """
    p[0] = []

# Processes member variable declarations, handling both initialized and uninitialized cases
def p_feature_member(p):
    """
    feature ::= LET IDENT SEMI
                | LET IDENT ASSIGN expression SEMI
    """
    if p[2] == 'self':
        print(f"ERROR: {p.lineno(2)}:{p.lexpos(2)}: Parser: 'self' cannot be used as a member variable name")
        exit(0)

    if len(p) == 4:  # uninitialized member variable
        p[0] = {
            "name": construct_identifier(p, 2),
            "type": "member"
        }
    elif len(p) == 6:  # initialized member variable
        p[0] = {
            "name": construct_identifier(p, 2),
            "type": "member",
            "init": p[4]
    }

# Handles method declarations with parameters and body
def p_feature_method(p):
    """
    feature ::= IDENT LPAREN parameter_list RPAREN block SEMI
              | IDENT LPAREN RPAREN block SEMI
    """
    # Validate method name
    if p[1] == 'self':
        print(f"ERROR: {p.lineno(1)}:{p.lexpos(1)}: Parser: 'self' cannot be used as method name")
        exit(0)

    if len(p) == 7:
        # IDENT LPAREN parameter_list RPAREN block SEMI
        parameters = p[3]
        body = p[5]
    else:
        # IDENT LPAREN RPAREN block SEMI
        parameters = []
        body = p[4]

    # Check if the method body is empty
    if not body['value']['body']:
        print(f"ERROR: {body['line']}:{body['col']}: Parser: Method body cannot be empty")
        exit(0)

    p[0] = {
        "name": construct_identifier(p, 1),
        "type": "method",
        "parameters": parameters,
        "body": body
    }

# Builds list of method parameters, handling single parameter and parameter sequences
def p_parameter_list(p):
    """
    parameter_list ::= IDENT COMMA parameter_list
                     | IDENT
                     | empty
    """
    if len(p) == 2:
        # Single parameter or empty
        if p[1] is None:
            p[0] = []
        else:
            p[0] = [{
                "line": p.lineno(1),
                "col": p.lexpos(1),
                "value": p[1]
            }]
    elif len(p) == 4:
        # Parameter followed by more parameters
        param = {
            "line": p.lineno(1),
            "col": p.lexpos(1),
            "value": p[1]
        }
        p[0] = [param] + p[3]

# Utility rule for representing empty productions in the grammar
def p_empty(p):
    """
    empty ::=
    """
    p[0] = None

## BLOCK AND EXPRESSION LIST HANDLING

# Handles code blocks enclosed in braces (containing sequences of expressions)
def p_block(p):
    """
    block ::= LBRACE expression_list RBRACE
    """
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "block",
            "body": p[2]
        }
    }

# Converts block into expression for uniform processing
def p_expression_block(p):
    "expression ::= block"
    p[0] = p[1]

# Builds list of expressions separated by semicolons within a block
def p_expression_list(p):
    """
    expression_list ::= expression SEMI
                      | expression SEMI expression_list
    """
    if len(p) == 3:
        # Base case: single expression followed by a semicolon
        p[0] = [p[1]]
    else:
        # Recursive case: expression followed by semicolon and more expressions
        p[0] = [p[1]] + p[3]


## BASIC EXPRESSION TYPES
# Handles integer literal expressions
def p_expression_int(p):
    "expression ::= INT"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "number",
            "line": p.lineno(1),
            "col": p.lexpos(1),
            "value": int(p[1])  # Ensure the value is an integer
        }
    }

# Handles string literal expressions
def p_expression_string(p):
    """
    expression ::= STRING
    """
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "string",
            "line": p.lineno(1),
            "col": p.lexpos(1),
            "value": p[1]
        }
    }

# Processes identifier references
def p_expression_ident(p):
    "expression ::= IDENT"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "identifier",
            "value": construct_identifier(p, 1)
        }
    }

# Handles boolean true litera
def p_expression_true(p):
    "expression ::= TRUE"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "bool",
            "value": True
        }
    }

# Handles boolean false literal
def p_expression_false(p):
    "expression ::= FALSE"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "bool",
            "value": False
        }
    }

#construct_identifier is our helper function that constructs an identifier object
def construct_identifier(p, index):
    return {
        "line": p.lineno(index),
        "col": p.lexpos(index),
        "value": p[index]
    }


## ARITHMETIC OPERATIONS

def p_expression_plus(p):
    "expression ::= expression PLUS expression"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "plus",
            "lhs": p[1],
            "rhs": p[3]
        }
    }

def p_expression_minus(p):
    "expression ::= expression MINUS expression"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "minus",
            "lhs": p[1],
            "rhs": p[3]
        }
    }

def p_expression_times(p):
    "expression ::= expression TIMES expression"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "times",
            "lhs": p[1],
            "rhs": p[3]
        }
    }

def p_expression_divide(p):
    "expression ::= expression DIVIDE expression"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "divide",
            "lhs": p[1],
            "rhs": p[3]
        }
    }

def p_expression_negate(p):
    "expression ::= UMINUS expression"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "negate",
            "body": p[2]
        }
    }


##COMPARISON AND LOGIC
def p_expression_lt(p):
    "expression ::= expression LT expression"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "lt",
            "lhs": p[1],
            "rhs": p[3]
        }
    }

def p_expression_lte(p):
    "expression ::= expression LTE expression"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "lte",
            "lhs": p[1],
            "rhs": p[3]
        }
    }

def p_expression_equals(p):
    "expression ::= expression EQUALS expression"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "equals",
            "lhs": p[1],
            "rhs": p[3]
        }
    }

def p_expression_not(p):
    "expression ::= NOT expression"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "not",
            "body": p[2]
        }
    }


## CONTROL FLOW

def p_expression_if(p):
    """
    expression ::= IF LPAREN expression RPAREN block ELSE block
    """
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "if",
            "guard": p[3],
            "then": p[5],
            "else": p[7]
        }
    }

def p_expression_while(p):
    """
    expression ::= WHILE LPAREN expression RPAREN block
    """
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "while",
            "guard": p[3],
            "body": p[5]
        }
    }


## VARIABLE OPERATIONS
# Creates AST node for uninitialized variable declarations in let expressions
def p_expression_let_no_init(p):
    "expression ::= LET IDENT"
    if p[2] == 'self':
        print(f"ERROR: {p.lineno(2)}:{p.lexpos(2)}: Parser: Cannot bind 'self' in let")
        exit(0)
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "let",
            "lhs": construct_identifier(p, 2),
        }
    }

# Creates AST node for initialized variable declarations in let expressions
def p_expression_let_init(p):
    "expression ::= LET IDENT ASSIGN expression"
    if p[2] == 'self':
        print(f"ERROR: {p.lineno(2)}:{p.lexpos(2)}: Parser: Cannot bind 'self' in let")
        exit(0)
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "let",
            "lhs": construct_identifier(p, 2),
            "rhs": p[4]
        }
    }

# Creates AST node for variable assignment operations
def p_expression_assign(p):
    "expression ::= IDENT ASSIGN expression"
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "assign",
            "lhs": construct_identifier(p, 1),
            "rhs": p[3]
        }
    }


## METHOD DISPATCH

# For method calls on the current object (self)
def p_expression_self_dispatch(p):
    """
    expression ::= IDENT LPAREN arg_list RPAREN
                | IDENT LPAREN RPAREN
    """

    if len(p) == 5:  # With arg_list
        args = p[3]
    else:  # Empty args
        args = []

    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "self-dispatch",
            "method": construct_identifier(p,1),
            "args": args
        }
    }

# Creates AST node for method calls with dynamic dispatch (obj.method())
def p_expression_dynamic_dispatch(p):
    """
    expression ::= expression DOT IDENT LPAREN arg_list RPAREN
                 | expression DOT IDENT LPAREN RPAREN
    """

    if len(p) == 7:  # With arg_list
        args = p[5]
    else:  # Empty args
        args = []

    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "dynamic-dispatch",
            "object": p[1],
            "method": construct_identifier(p,3),
            "args": args
        }
    }

# Creates AST node for method calls with static dispatch (obj@Type.method())
def p_expression_static_dispatch(p):
    """
    expression ::= expression AT IDENT DOT IDENT LPAREN arg_list RPAREN
                 | expression AT IDENT DOT IDENT LPAREN RPAREN
    """

    if len(p) == 9:  # With arg_list
        args = p[7]
    else:  # Empty args
        args = []

    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "static-dispatch",
            "object": p[1],
            "class": construct_identifier(p,3),  # Changed from static_type to class
            "method": construct_identifier(p,5),
            "args": args
        }
    }

# For method calls on parenthesized expressions
def p_expression_paren_dispatch(p):
    """
    expression ::= LPAREN expression RPAREN DOT IDENT LPAREN arg_list RPAREN
                | LPAREN expression RPAREN DOT IDENT LPAREN RPAREN
    """

    if len(p) == 9:  # With arg_list
        args = p[7]
    else:  # Empty args
        args = []

    p[0] = {
        "line": p.lineno(1),  # Use LPAREN's position
        "col": p.lexpos(1),   # Use LPAREN's position
        "value": {
            "type": "dynamic-dispatch",
            "object": p[2],
            "method": construct_identifier(p,5),
            "args": args
        }
    }

# Builds list of argument expressions for method calls
def p_arg_list(p):
    """
    arg_list ::= expression COMMA arg_list
               | expression
    """
    if len(p) == 2:
        # Single argument
        p[0] = [p[1]]
    else:
        # Multiple arguments
        p[0] = [p[1]] + p[3]


## ARRAY OPERATIONS

# Handles array creation
def p_expression_new_array(p):
    """
    expression ::= NEW LBRACKET expression RBRACKET IDENT
    """
    if p[5] != 'Array':
        print(f"ERROR: {p.lineno}:{p.lexpos}: Parser: Unexpected token: {p[5]}")
        exit(0)

    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "new-array",
            "size": p[3],
        }
    }

# Handles array access and assignment
def p_expression_array_access_assign(p):
    """
    expression ::= expression LBRACKET expression RBRACKET
                | expression LBRACKET expression RBRACKET ASSIGN expression
    """
    if len(p) == 5:
        p[0] = {
            "line": p.lineno(1),
            "col": p.lexpos(1),
            "value": {
                "type": "array-access",
                "object": p[1],
                "index": p[3]
            }
        }
    else:
        p[0] = {
            "line": p.lineno(1),
            "col": p.lexpos(1),
            "value": {
                "type": "array-assign",
                "lhs": p[1],
                "index": p[3],
                "rhs": p[6]
            }
        }


## OBJECT CREATION AND SPECIAL OPERATIONS

# Handles object instantiation (new Type())
def p_expression_new_instance(p):
    """
    expression ::= NEW IDENT
    """

    if p[2] == "Array":
        print(f"ERROR: {p.lineno}:{p.lexpos}: Parser: Unexpected token: {p.value}")
        exit(0)

    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "new",
            "class": construct_identifier(p,2)
        }
    }

# Handles isvoid type checking operation
def p_expression_isvoid(p):
    """
    expression ::= ISVOID LPAREN expression RPAREN
    """
    p[0] = {
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": {
            "type": "isvoid",
            "body": p[3]
        }
    }

## PARENTHESES
# Handles expressions within parentheses, maintaining operator precedence
def p_expression_paren(p):
    "expression ::= LPAREN expression RPAREN"
    p[0] = p[2]

# this function gets called if ply runs into an error condition
def p_error(p):
    if p:
        print(f"ERROR: {p.lineno}:{p.lexpos}: Parser: Unexpected token: {p.value}")
    else:
        print("ERROR: Unexpected end of file")
    exit(0)


# main program
if __name__ == "__main__":
    import sys
    import ply.yacc as yacc
    from token_reader import TokenReader
    import json

    # create the parser
    # this reads all of the p_ functions and
    # compiles them into a grammar
    # and parser object we can use to process a token stream
    parser = yacc.yacc()

    # create a lexer object
    lexer = TokenReader(sys.stdin)

    # parse the tokens and produce an AST
    # tracking=True turns on line number tracking
    program = parser.parse(lexer=lexer, tracking=True)

    print(json.dumps(program, indent=2))