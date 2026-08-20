#201967cc8d6a85b5befe76602ccbb86bd55df57f
#module to read in SL-LEX formatted tokens

class Token:
    """
    the Token class will represent a snail token
    """

    def __init__ (self, lineno, colno, token_type, lexeme):
        """
        constructor for the Token class
        :param lineno: the line number of the token
        :param colno: the column number of the token
        :param token_type: the type of the token
        :param lexeme: the lexeme of the token
        """
        self.type = token_type.upper()
        self.value = lexeme
        self.lineno = lineno
        self.lexpos = colno   #we are hacking this

    def __repr__(self):
        tok = f"{self.lineno}: {self.lexpos} {self.type}"
        if self.type in ["IDENT", "STRING", "INT"]:
            tok += f" {self.value}"
        return tok



class TokenReader:
    """
    Class to read in SL-LEX tokens
    """

    def __init__(self,f):
        """
        f - file object containing the SL-LEX token information
        """

        #create a list of all of the lines from f
        lines = f.readlines()

        #list of token objects
        # any member variable with a name beginning with _ is essentially private
        self._tokens = []

        self.lineno = 0
        self.lexpos = 0

        #keep track of the current line in the SL-LEX file
        i = 0
        while ( i < len(lines)):
            line_no = int(lines[i])
            col_no = int(lines[i+1])
            tok_type = lines[i+2].strip()

            #if the token is a string, we need to remove the quotes
            if tok_type in ["ident", "string", "int"]:
                lexeme = lines[i+3].rstrip('\n')
                i += 1
            else:
                lexeme = tok_type

            #increment i
            i += 3

            #add token object to the list of tokens
            self._tokens.append(Token(line_no, col_no, tok_type, lexeme))

        #wrap the list of tokens in an iterator
        self.token_stream = iter(self._tokens)

    def token(self):
        """
        return the next token object in the token stream
        or None if there are no more tokens
        """
        try:
            tmp = next(self.token_stream)

            self.lineno = tmp.lineno
            self.lexpos = tmp.lexpos

            return tmp

            # return next(self.token_stream)  # next() is a built-in function that returns the next item in an iterator
        except StopIteration:
            return None

        #ALternatively
        # return next(self.token_stream, None)


#main program
if __name__ == "__main__":
    #this will only run when executed from the command line
    import argparse

    #create a command line argument parser
    parser = argparse.ArgumentParser()
    parser.add_argument("sl_file", help="SL-LEX file to read in")

    #parse the command line arguments
    args = parser.parse_args()

    #args.sl_file is the file name of the SL-LEX file
    with open(args.sl_file, "r") as f:
        #create a TokenReader object
        reader = TokenReader(f)

        #read in all of the tokens
        # := is the walrus operator, it assigns the value of the right side to the variable on the left side
        while (t := reader.token()) is not None:
            # print(f"{t.lineno} {t.lexpos} {t.type} {t.value}")
            # t = reader.token()
            print(t)
