# example of using PLY to parse a sequence of SL-LEX tokens
# define grammar and provide code to build AST nodes

#define the valid token types
tokens = (
    'IDENT', 'INT', 'STRING', 'PLUS', 'MINUS', 'TIMES', 'DIVIDE', 'LPAREN', 'RPAREN', 'SEMI', 'ASSIGN', 'PRINT')

# parser rule for the addition expression
# words in quotes are documentation comments
# inside them are grammar rules that this function will match
#the p is the sequence of terminals and tokens from our grammar rules
# were skipping p[2] because its the plus sign
def p_expression_plus(p):
    """
    expression ::= expression PLUS expression
    """
    # assign something to p[0] to build the AST
    # p[0] = p[1] + p[3]
    # p[0] = ("addition" ,[p[1], p[3]]) # give us whatever the value of whatever p[0] is assigned to in INT
    # p[0] = 42 # give us 42 when we run

    p[0] = {
        "type": "addition",
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "left": p[1],
        "right": p[3]
    }


def p_expression_int(p):
    """
    expression ::= INT
    """
    # p[0] = ("int", p[1]) #treats the individual tokens as ints correctly
    # p[0] = 14 #treats the individual tokens or ints as 14

    p[0] ={
        "type": "integer",
        "line": p.lineno(1),
        "col": p.lexpos(1),
        "value": p[1]
    }



# main program
if __name__ == "__main__":
    import sys
    from token_reader import TokenReader
    import json

    import ply.yacc as yacc

    #create the parser
    #this reads all the p_ functions and compile them into a grammar
    # and parser object we can use to process a token stream
    parser = yacc.yacc()

    #create a lexer object
    #this reads in a file and tokenizes it
    lexer = TokenReader(sys.stdin) #read whatever is typed in the command line

    #parse the tokens and produce an AST
    #tracking=True means that the parser will keep track of the line number and column number
    program = parser.parse(lexer=lexer, tracking=True)

    #print the AST
    # print(program)
    print(json.dumps(program, indent=2))