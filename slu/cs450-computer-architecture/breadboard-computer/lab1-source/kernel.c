extern void putInMemory(int segment, int offset, char b);
extern void initVideo(void);

/*
 * Prototypes for our additional functions.
 * Note: The main function must be the first one in the file,
 * so we declare prototypes here and then define the functions after main.
 */
void putChar(char c, char color, int row, int col);
void putStr(char *str, char color, int row, int col);

int main(void) {
    int i;
    /* Using static arrays to ensure the strings are in our data segment */
    static char msgUpperLeft[] = "Hello World";      /* For upper left (white on black) */
    static char msgCenter[] = "Hello World";           /* For center (white on red) */
    static char msgLowerRight[] = "Hello World";       /* For lower right (black on light cyan) */

    /* Color attributes:
       For video memory, the color attribute is one byte:
         - High nibble: background color.
         - Low nibble: foreground color.
       According to the lab table:
         White = 0xF, Black = 0x0, Red = 0x4, Light Cyan = 0xB.

       Therefore:
         White on Black  : (BLACK << 4) | WHITE = (0x0 << 4) | 0xF = 0x0F.
         White on Red    : (RED << 4)   | WHITE = (0x4 << 4) | 0xF = 0x4F.
         Black on Light Cyan: (LIGHT_CYAN << 4) | BLACK = (0xB << 4) | 0x0 = 0xB0.
    */
    unsigned char colorUpperLeft = 0x0F;   /* white on black */
    unsigned char colorCenter    = 0x4F;   /* white on red */
    unsigned char colorLowerRight = 0xB0;   /* black on light cyan */

    /* Initialize video mode (80x25 text mode) */
    initVideo();

    /* 1. Display "Hello World" in the upper left corner using putStr.
       Start at row 1, column 1. */
    putStr(msgUpperLeft, colorUpperLeft, 1, 1);

    /* 2. Display "Hello World" at the center using putChar.
       For an 80-column screen and an 11-character message, calculate:
          centerRow = 13 (approximately mid-screen for 25 rows)
          centerCol = ((80 - 11) / 2) + 1 */
    {
        int centerRow = 13;
        int centerCol = ((80 - 11) / 2) + 1;
        for (i = 0; msgCenter[i] != '\0'; i++) {
            putChar(msgCenter[i], colorCenter, centerRow, centerCol + i);
        }
    }

    /* 3. Display "Hello World" in the lower right corner using putStr.
       Start at row 25 (last row) and near the right edge (for example, col 75).
       When the end of the line is reached (col > 80), our putStr function wraps the text to row 1. */
    putStr(msgLowerRight, colorLowerRight, 25, 75);

    /* Loop forever to keep the kernel running */
    while (1)
        ;

    return 0;
}

/*
 * Function definitions follow main.
 */

/**
 * Insert a character on the screen with the given color at the position (row, col).
 * Rows and columns are 1-indexed.
 *
 * @param c     The character to insert.
 * @param color The color attribute (background in high nibble, foreground in low nibble).
 * @param row   The 1-indexed row position.
 * @param col   The 1-indexed column position.
 */
void putChar(char c, char color, int row, int col) {
    /* Calculate the offset:
       - Each row has 80 characters.
       - Each character occupies 2 bytes (character and color attribute).
       - Offset = ((row - 1) * 80 + (col - 1)) * 2.
    */
    int offset = ((row - 1) * 80 + (col - 1)) * 2;
    putInMemory(0xB800, offset, c);
    putInMemory(0xB800, offset + 1, color);
}

/**
 * Insert the null-terminated string on the screen with the given color at the position (row, col).
 * Each successive character is printed one column to the right.
 * If the end of a line (column > 80) is reached, the text wraps to the next line.
 * If the text reaches beyond row 25, it wraps to the first row.
 *
 * @param str   The null-terminated string to insert.
 * @param color The color attribute (background in high nibble, foreground in low nibble).
 * @param row   The 1-indexed starting row.
 * @param col   The 1-indexed starting column.
 */
void putStr(char *str, char color, int row, int col) {
    while (*str != '\0') {
        putChar(*str, color, row, col);
        col++;
        if (col > 80) {       /* End of the current line, wrap to next line */
            col = 1;
            row++;
            if (row > 25) {   /* If we go past the bottom, wrap to the top */
                row = 1;
            }
        }
        str++;
    }
}
