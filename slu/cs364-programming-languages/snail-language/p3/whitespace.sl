// This is a single-line comment with leading whitespace

    /*
     * This is a multi-line comment.
     * It spans multiple lines and includes various whitespace characters.
     */

class ExampleClass {
    let validInt = 123;                // Valid integer
    let invalidOctal = 0123;           // Invalid octal (should trigger an error)
    let invalidHex = 0x1A3F;           // Invalid hexadecimal (should trigger an error)

    // Operators and punctuation
    if (validInt <= invalidOctal) {
        validInt = validInt + 1;
    } else {
        validInt = validInt - 1;
    }

    while (validInt < 100) {
        validInt = validInt * 2;
    }

    // String literals with various escape sequences
    let validString1 = "Hello, World!";
    let validString2 = "Line1\nLine2\tTabbed";
    let validString3 = "Quote: \"Double Quotes\" and \'Single Quotes\'";
    let invalidEscape1 = "Invalid escape sequence: \q";
    let invalidEscape2 = "Another invalid escape: \y";
    let nullCharacter = "Null character here:\0end";

    // String with actual newline character (should trigger unterminated string error)
    let unterminatedString = "This string is not closed properly"

    // String with backslashes
    let backslashes = "Path to folder: C:\\Users\\Example\\Documents";

    // String with unicode characters
    let unicodeString = "Unicode test: \u263A \u03A9";
}
