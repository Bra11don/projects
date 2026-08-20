// 3aeee2ffb6bdcec698011572b6bbcaf180807419

// public class Token {
//     private int line,column;
//     private Tok tokenType;
//     private Object value;

//     public Token(int line, int column, Tok tokenType){
//         this.line = line;
//         this.column = column;
//         this.tokenType = tokenType;
//     }

//     public Token(int line, int column, Tok tokenType, Object value){
//         //call our existing constructor
//         this(line, column, tokenType);

//         // set the value
//         this.value = value;
//     }

//     @Override
//     public String toString(){
//         String fmt = String.format("(%d, %d) %s", line, column, tokenType);

//         if (value != null){
//             fmt += ", " + value;

//         }
//         return fmt;
//     }
// }

public class Token {
    private int line;
    private int column;
    private Tok tokenType;
    private Object value; // Should I change to String??

    // Constructor for tokens without a value
    public Token(int line, int column, Tok tokenType) {
        this.line = line;
        this.column = column;
        this.tokenType = tokenType;
        this.value = null;
    }

    // Constructor for tokens with a value (e.g., identifiers, literals)
    public Token(int line, int column, Tok tokenType, Object value) {
        this.line = line;
        this.column = column;
        this.tokenType = tokenType;
        this.value = value;
    }

    // Getter methods
    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Tok getTokenType() {
        return tokenType;
    }

    public Object getValue() {
        return value;
    }


    // toString method to output tokens in the required format
    // @Override
    // public String toString() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append(line).append("\n");
    //     sb.append(column).append("\n");
    //     sb.append(tokenType.toString()).append("\n");
    //     if (value != null) {
    //         sb.append(value).append("\n");
    //     }
    //     return sb.toString();
    // }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(line).append("\n");
        sb.append(column).append("\n");
        sb.append(tokenType.toString()).append("\n");
        if (value != null) {
            sb.append(escapeString(value)).append("\n");
        }
        return sb.toString();
    }

    private String escapeString(Object value2) {
        StringBuilder sb = new StringBuilder();
        for (char c : ((String) value2).toCharArray()) {
            if (c >= 32 && c <= 126) {
                sb.append(c);
            } else {
                // Represent control characters using escape sequences
                switch (c) {
                    case '\b':
                        sb.append("\b");
                        break;
                    case '\t':
                        sb.append("\t");
                        break;
                    case '\n':
                        sb.append("\n");
                        break;
                    case '\f':
                        sb.append("\f");
                        break;
                    case '\r':
                        sb.append("\r");
                        break;
                    case '\0':
                        sb.append("\0");
                        break;
                    // case '\\':
                    //     sb.append("\\\\");
                    //     break;
                    // case '\"':
                    //     sb.append("\\\"");
                    //     break;
                    default:
                        sb.append(c);
                        break;
                }
            }
        }
        return sb.toString();
    }

}
