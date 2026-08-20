// 3aeee2ffb6bdcec698011572b6bbcaf180807419
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        Lexer l = new Lexer(new InputStreamReader(System.in));

        try {
            // // Loop to read tokens
            // Token t;
            // while ((t = l.yylex()) != null) {
            //     // Print the token
            //     System.out.println(t);
            // }
            //loop forever to read tokens
            while (true) {
                Token t = l.yylex();
                if (t == null){
                    // the lexer is done reading input
                    break;
                }else{

                    //print the token.
                    System.out.println(t.getLine());

                    if (t.getTokenType() == Tok.STRING_LITERAL){
                        System.out.println(t.getColumn()+1); //+1 to account for the opening quote
                        }else{
                            System.out.println(t.getColumn());
                        }
                }
                System.out.println(t.getTokenType());
                if (t.getValue() != null){
                    System.out.println(t.getValue());
                }
            }
        } catch (Error e) {
            // Handle lexer errors
            // Print the error message to System.out in the required format
            System.out.println(e.getMessage());
            System.exit(0);
        } catch (IOException e) {
            // Handle IO exceptions
            String message = String.format("ERROR: %d:%d: Lexer: I/O error: %s", l.getLine(), l.getColumn(), e.getMessage());
            System.out.println(message);
            System.exit(0);
        }
    }
}



// public class Main{
//     public static void main(String[] args) {
//         Lexer l = new Lexer(new InputStreamReader(System.in));

//         try{
//             //loop forever to read tokens
//             while (true) {
//                 Token t = l.yylex();
//                 if (t == null){
//                     // the lexer is done reading input
//                     break;
//                 }else{
//                     //print the token
//                     System.out.println(t);
//                 }
//             }

//         }catch (IOException | Error e){
//             System.err.println(e.getLocalizedMessage());
//             System.exit(1);

//         }
//     }
// }


/**
public enum Tok {
    IDENTIFIER, INT_LITERAL, FLOAT_LITERAL, STRING_LITERAL,
    PLUS, MINUS, TIMES, DIVIDE, MODULO,
    EQUAL, NOT_EQUAL, LESS_EQUAL, GREATER_EQUAL,
    LESS, GREATER, ASSIGN,
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    LEFT_BRACKET, RIGHT_BRACKET, SEMICOLON, COMMA,
    IF, ELSE, WHILE, RETURN,
    // Add other token types as needed
}

public class Token {
    public int line;
    public int column;
    public Tok type;
    public String value;

    public Token(int line, int column, Tok type) {
        this(line, column, type, null);
    }

    public Token(int line, int column, Tok type, String value) {
        this.line = line;
        this.column = column;
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        if (value != null) {
            return String.format("%d:%d: %s %s", line, column, type.name(), value);
        } else {
            return String.format("%d:%d: %s", line, column, type.name());
        }
    }
}
**/