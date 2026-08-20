//3aeee2ffb6bdcec698011572b6bbcaf180807419

// public enum Tok {
//     PLUS("plus"),
//     MINUS("minus"),
//     TIMES("times"),
//     DIVIDE("divide"),
//     LPAREN("lparen"),
//     RPAREN("lparen"),
//     IDENT("ident"),
//     INT("ident"),
//     FLOAT("float");

//     private final String name;
//     private Tok (String s){
//         name = s;
//     }
//     @Override
//     public String toString(){
//         return name;
//     }

// }

public enum Tok {
    // Operators
    PLUS("plus"),
    MINUS("minus"),
    TIMES("times"),
    DIVIDE("divide"),
    EQUALS("equals"),
    LT("lt"),
    LTE("lte"),
    ASSIGN("assign"),
    AT("at"),

    // Logical Operators
    NOT("not"),

    // Separators
    LPAREN("lparen"),
    RPAREN("rparen"),
    LBRACE("lbrace"),
    RBRACE("rbrace"),
    LBRACKET("lbracket"),
    RBRACKET("rbracket"),
    SEMI("semi"),
    COMMA("comma"),

    //punctuations
    DOT("dot"),
    COLON("colon"),
    UMINUS("uminus"),

    // Literals
    INT_LITERAL("int"),
    STRING_LITERAL("string"),

    // Identifiers
    IDENT("ident"),

    // Keywords
    CLASS("class"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    FOR("for"),
    INT("int"),
    FLOAT("float"),
    STRING("string"),
    TRUE("true"),
    FALSE("false"),
    ISVOID("isvoid"),
    NEW("new"),
    LET("let"),

    // End of File
    EOF("eof");

    private final String name;

    private Tok(String s) {
        name = s;
    }

    @Override
    public String toString() {
        return name;
    }
}
