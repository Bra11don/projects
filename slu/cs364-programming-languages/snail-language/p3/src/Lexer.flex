// 3aeee2ffb6bdcec698011572b6bbcaf180807419
/*
 * JFlex specification for the snail language lexer
 */

import java.math.BigInteger;

%%

%class Lexer
%unicode
%line
%column
%type Token
%state COMMENT STRING
%{

    private Token token(Tok type) {
    return new Token(yyline + 1, yycolumn  + 1, type);
    }

    private Token token(Tok type,Object value) {
        return new Token(yyline + 1, yycolumn + 1, type, value);
    }

    private void reportError(String message) {
        System.out.printf("ERROR: %d:%d: Lexer: %s\n", yyline + 1, yycolumn + 1, message);
        System.exit(0);
    }

    public int getLine() {
    return yyline + 1;
    }

    public int getColumn() {
        return yycolumn + 1;
    }

    private int commentCount = 0;
    private StringBuilder stringBuffer; // Buffer to store string literals
    // Variables to store the starting position of a string
    private int stringStartLine;
    private int stringStartColumn;

%}

// Macros
Identifier = [\p{L}_][\p{L}\p{N}_]*//\p{L} matches any Unicode letter, and \p{N} matches any Unicode digit.

Whitespace = [ \t\n\s]
BadWhitespace = [\x0b\u2028\u2029\f\r]
StringChar = [^\\\"\n]+


DIGIT      = [0-9]
NZDIGIT    = [1-9]

// Single-line comment
SingleLineComment = "//".*

%%

// Initial state rules
<YYINITIAL>{
    {BadWhitespace} { yyline = yyline - 1; }
    {Whitespace}        { /* Ignore whitespace */ }
    // [\s\u00A0\u1680\u2000-\u200A\u202F\u205F\u3000] { /* Ignore whitespace */ }
    // {Newline}           { /* Update line number if necessary */ }
    "+"                 { return token(Tok.PLUS); }
    "-"                 { return token(Tok.MINUS); }
    "*"                 { return token(Tok.TIMES); }
    "/"                 { return token(Tok.DIVIDE); }
    "=="                { return token(Tok.EQUALS); }
    "<="                { return token(Tok.LTE); }
    "<"                 { return token(Tok.LT); }
    "@"                 { return token(Tok.AT); }
    "="                 { return token(Tok.ASSIGN); }
    "("                 { return token(Tok.LPAREN); }
    ")"                 { return token(Tok.RPAREN); }
    "{"                 { return token(Tok.LBRACE); }
    "}"                 { return token(Tok.RBRACE); }
    "["                 { return token(Tok.LBRACKET); }
    "]"                 { return token(Tok.RBRACKET); }
    ";"                 { return token(Tok.SEMI); }
    ","                 { return token(Tok.COMMA); }
    "!"                 { return token(Tok.NOT); }
    "~"                 { return token(Tok.UMINUS); }
    "."                 { return token(Tok.DOT); }
    ":"                 { return token(Tok.COLON); }
    "\"" {
        yybegin(STRING);
        stringBuffer = new StringBuilder();

        // Store the starting position (after the opening quote)
        stringStartLine = yyline + 1;
        stringStartColumn = yycolumn + 1;
    }

    "0"     { return token(Tok.INT, Integer.parseInt(yytext())); }

    // Integer literals: one or more digits (including leading zeros)
    //passes all the int range tests
    {DIGIT}+ {
        String lexeme = yytext();
        // Remove leading zeros
        String trimmedLexeme = lexeme.replaceFirst("^0+(?!$)", "");
        if (trimmedLexeme.isEmpty()) {
            trimmedLexeme = "0";
        }
        try {
            BigInteger value = new BigInteger(trimmedLexeme);

            // Maximum value for 64-bit signed integer: 2^63 - 1
            BigInteger maxLong = BigInteger.valueOf(Long.MAX_VALUE);
            BigInteger minLong = BigInteger.valueOf(Long.MIN_VALUE);

            if (value.compareTo(maxLong) <= 0 && value.compareTo(minLong) >= 0) {
                // Value is within range
                return token(Tok.INT_LITERAL, trimmedLexeme);
            } else {
                // Value is out of range
                reportError("integer literal out of range");
            }
        } catch (NumberFormatException e) {
            reportError("invalid integer literal");
        }
    }

    "\""                { yybegin(STRING); stringBuffer = new StringBuilder(); }
    {Identifier}    {
        String lexeme = yytext();
        String lexemeLower = lexeme.toLowerCase();

        // Check for keywords
        if (lexemeLower.equals("class")) {
            return token(Tok.CLASS);
        } else if (lexemeLower.equals("else")) {
            return token(Tok.ELSE);
        } else if (lexemeLower.equals("if")) {
            return token(Tok.IF);
        } else if (lexemeLower.equals("isvoid")) {
            return token(Tok.ISVOID);
        } else if (lexemeLower.equals("let")) {
            return token(Tok.LET);
        } else if (lexemeLower.equals("new")) {
            return token(Tok.NEW);
        } else if (lexemeLower.equals("while")) {
            return token(Tok.WHILE);
        } else if (lexemeLower.equals("true")){
            return token(Tok.TRUE);
        } else if(lexemeLower.equals("false")){
            return token(Tok.FALSE);
        }else {
            // It's an identifier
            return token(Tok.IDENT, lexeme);
        }

    }
    {SingleLineComment} { /* Ignore single-line comments */ }
    "/*"                { commentCount = 1; yybegin(COMMENT); }
    .                   { reportError("invalid character: '" + yytext() + "'"); }
}

// Comment state rules
<COMMENT> {
    "/*"                { commentCount++; }
    "*/"                {
                            commentCount--;
                            if (commentCount == 0) {
                                yybegin(YYINITIAL);
                            }
                        }
                        "\n"            {
                        if(commentCount == 0){
                            yybegin(YYINITIAL);
                        }
                    }

    <<EOF>> { reportError("EOF in comment"); }
    [^]                 { /* Ignore other characters */ }

}

<STRING> {

    // Null character handling (must raise an error)
    "\\\u0000" { reportError("null character in string"); }

    // Valid escape sequences - append the escape sequences as they appear
    "\\n"  { stringBuffer.append(yytext()); }
    "\\t"  { stringBuffer.append(yytext()); }
    "\\b"  { stringBuffer.append(yytext()); }
    "\\r"  { stringBuffer.append(yytext()); }
    "\\f"  { stringBuffer.append(yytext()); }
    "\\\\" { stringBuffer.append(yytext()); }
    "\\\"" { stringBuffer.append(yytext()); }
    "\\'"  { stringBuffer.append(yytext()); }
    "\\0" { stringBuffer.append(yytext()); }

    "\x12" { stringBuffer.append(yytext());
    yyline = yyline - 1;
    }



    // Invalid escape sequences (exactly one backslash followed by invalid character)
    "\\([^ntrbf\"'\\/u])" { reportError("invalid escape sequence"); } // this one passes 29 and 52 but fails 39

    "\\" . { stringBuffer.append(yytext()); }

    // Regular characters (non-backslash, non-quote, non-newline)
    [^\\\"\n\u0000]+ { stringBuffer.append(yytext()); }


    // Closing quote
    "\"" {
        yybegin(YYINITIAL);
        return new Token(stringStartLine, stringStartColumn, Tok.STRING_LITERAL, stringBuffer.toString());
    }

    // Unterminated string
    "\n"    { reportError("unterminated string literal"); }

    // Anything else is invalid
    .       { reportError("invalid character in string"); }
}
