/*
 * Ported from a cool program by Ann Sheets (sheets@cory)
 *
 * Tests the arithmetic operations and various other things
 */

class A : IO {
    let var = 0;

    value() { var; };

    set_var(num) { var = num; self; };

    // identity
    method1(num) { self; };

    // plus
    method2(num1, num2) {
        let x = num1 + num2;
        (new B).set_var(x);
    };

    // negate
    method3(num) {
        let x = ~num;
        (new C).set_var(x);
    };

    // diff
    method4(num1, num2) {
        if (num2 < num1) {
            let x = num1 - num2;
            (new D).set_var(x);
        } else {
            let x = num2 - num1;
            (new D).set_var(x);
        };
    };

    // factorial
    method5(num) {
        let x = 1;
        let y = 1;
        while (y <= num) {
            x = x * y;
            y = y + 1;
        };
        (new E).set_var(x);
    };
};

// B is a number squared
class B : A {
    // square
    method5(num) {
        let x = num * num;
        (new E).set_var(x);
    };
};

class C : B {

    // negate
    method6(num) {
        let x = ~num;
        (new A).set_var(x);
    };

    // cube
    method5(num) {
        let x = num * num * num;
        (new E).set_var(x);
    };
};

class D : B {

    // divisible by 3
    method7(num) {
        let x = num;
        if (x < 0) { method7(~x); } else {
            let y = x / 3;
            if ( 3*y == x) {
                true;
            } else {
                false;
            };
        };
    };
};

class E : D {

    // division
    method6(num) {
        let x = num / 8;
        (new A).set_var(x);
    };
};

// The following code is from atoi.sl

/*
 * The class A2I provides integer-to-string and string-to-integer conversion
 * routines.  To use these routines, either inherit from A2I in the class where
 * this is needed, have a dummy variable bound to something of type A2I, or
 * write (new A2I).method(argument).
 */

class A2I {

    /*
     * c2i converts a 1-character string to an integer.  Aborts if the string
     * is not "0" through "9"
     */
    c2i(char) {
        if (char == "0") {
            0;
        } else {
            if (char == "1") {
                1;
            } else {
                if (char == "2") {
                    2;
                } else {
                    if (char == "3") {
                        3;
                    } else {
                        if (char == "4") {
                            4;
                        } else {
                            if (char == "5") {
                                5;
                            } else {
                                if (char == "6") {
                                    6;
                                } else {
                                    if (char == "7") {
                                        7;
                                    } else {
                                        if (char == "8") {
                                            8;
                                        } else {
                                            if (char == "9") {
                                                9;
                                            } else {
                                                abort();
                                            };
                                        };
                                    };
                                };
                            };
                        };
                    };
                };
            };
        };
    };

    /*
     * convert an Int to a String
     */
    i2c(i) {
        if (i == 0) {
            "0";
        } else {
            if (i == 1) {
                "1";
            } else {
                if (i == 2) {
                    "2";
                } else {
                    if (i == 3) {
                        "3";
                    } else {
                        if (i == 4) {
                            "4";
                        } else {
                            if (i == 5) {
                                "5";
                            } else {
                                if (i == 6) {
                                    "6";
                                } else {
                                    if (i == 7) {
                                        "7";
                                    } else {
                                        if (i == 8) {
                                            "8";
                                        } else {
                                            if (i == 9) {
                                                "9";
                                            } else {
                                                abort();
                                            };
                                        };
                                    };
                                };
                            };
                        };
                    };
                };
            };
        };
    };

    /*
     * a2i converts an ASCII string into an integer.  The empty string is
     * converted to 0.  Signed and unsigned strings are handled. The method
     * aborts if the string does not represent an integer.  Arithmetic overflow
     * can result in strange answers for very long strings.
     */
    a2i(s) {
        if (s.length() == 0) { 0; }
        else {
            let fst = s.substr(0,1);
            let rst = s.substr(1, s.length()-1);
            if (fst == "-") {
                ~a2i_helper(rst);
            } else {
                if (s.substr(0,1) == "+") {
                    a2i_helper(rst);
                } else {
                    a2i_helper(s);
                };
            };
        };
    };

    /*
     * helper function for converting unsigned portion of a string
     */
    a2i_helper(s) {
        let int = 0;
        let j = s.length();
        let i = 0;
        while (i < j) {
            // shift by ten and get the next digit
            int = int * 10 + c2i(s.substr(i, 1));
            i = i + 1;
        };
        int;
    };

    /*
     * i2a converts an integer to a string. Positive and negative numbers
     * are handled correctly.
     */
    i2a(i) {
        if (i == 0) { "0"; }
        else {
            if (0 < i) {
                i2a_helper(i);
            } else {
                "-".concat(i2a_helper(i * ~1));
            };
        };
    };

    /*
     * helper function for converting int to string (recursively)
     */
    i2a_helper(i) {
        if (i == 0) { ""; }
        else {
            let next = i / 10;
            // convert the last digit and prepend the rest of the number
            i2a_helper(next).concat(i2c(i - next * 10));
        };
    };

};

class Main : IO {
    let char;
    let avar;
    let a_var;
    let flag = true;

    // get option as a string
    menu() {
        print_string("\n\tTo add a number to ");
        print(avar);
        print_string("...enter a:\n");
        print_string("\tTo negate ");
        print(avar);
        print_string("...enter b:\n");
        print_string("\tTo find the difference between ");
        print(avar);
        print_string("and another number...enter c:\n");
        print_string("\tTo find the factorial of ");
        print(avar);
        print_string("...enter d:\n");
        print_string("\tTo square ");
        print(avar);
        print_string("...enter e:\n");
        print_string("\tTo cube ");
        print(avar);
        print_string("...enter f:\n");
        print_string("\tTo find out if ");
        print(avar);
        print_string("is a multiple of 3...enter g:\n");
        print_string("\tTo divide ");
        print(avar);
        print_string("by 8...enter h:\n");
        print_string("\tTo get a new number...enter j:\n");
        print_string("\tTo quit...enter q:\n\n");
        read_string();
    };

    // return string of input
    prompt() {
        print_string("\n");
        print_string("Please enter a number... ");
        read_string();
    };

    get_int() {
        let z = new A2I;
        let s = prompt();
        z.a2i(s);
    };

    is_even(num) {
        let x = num;
        if (x < 0) { is_even(~x); }
        else {
            let y = x / 2;
            if (2*y == x) {
                true;
            } else {
                false;
            };
        };
    };

    class_type(var) {
        let t = var.get_type();
        print_string("Class type is now ".concat(t).concat("\n"));
    };

    print(var) {
        let z = new A2I;
        print_string(z.i2a(var.value()));
        print_string(" ");
    };

    main() {
        avar = (new A);
        while (flag) {
            // avar = (new A).set_var(get_int());
            print_string("number ");
            print(avar);
            if (is_even(avar.value())) {
                print_string("is even!\n");
            } else {
                print_string("is odd!\n");
            };

            // print(avar); // prints out answer
            class_type(avar);
            char = menu();
            if (char == "a") {
                // add
                a_var = (new A).set_var(get_int());
                avar = (new B).method2(avar.value(), a_var.value());
            } else {
            if (char == "b") {
                // negate
                if (avar.is_a("C")) { avar = avar.method6(avar.value()); } else {
                if (avar.is_a("A")) { avar = avar.method3(avar.value()); } else {
                    print_string("Ooooops\n");
                    abort();
                };
                };
            } else {
            if (char == "c") {
                // difference
                a_var = (new A).set_var(get_int());
                avar = (new D).method4(avar.value(), a_var.value());
            } else {
                // factorial
            if (char == "d") { avar = (new C)@A.method5(avar.value()); } else {
                // square
            if (char == "e") { avar = (new C)@B.method5(avar.value()); } else {
                // cube
            if (char == "f") { avar = (new C)@C.method5(avar.value()); } else {
            if (char == "g") {
                // multiple of 3?
                if ((new D).method7(avar.value())) {
                    // avar = (new A) method1(avar.value())
                    print_string("number ");
                    print(avar);
                    print_string("is divisible by 3.\n");
                } else {
                    // avar = (new A).set_var(0);
                    print_string("number ");
                    print(avar);
                    print_string("is not divisible by 3.\n");
                };
            } else {
            if (char == "h") {
                let x = (new E).method6(avar.value());
                let r = (avar.value() - (x.value() * 8));
                print_string("number ");
                print(avar);
                print_string("is equal to ");
                print(x);
                print_string("times 8 with a remainder of ");
                let a = new A2I;
                print_string(a.i2a(r));
                print_string("\n");
                avar = x;
            } else {
            if (char == "j") { avar = (new A); } else {
            if (char == "q") { flag = false; } else {
                avar = (new A).method1(avar.value()); // divide by 8
            }; }; }; }; }; }; }; }; }; };
        }; // while
    };
};