/*
 * complex numbers
 */

class Main : IO {
    main() {
        let c = (new Complex).init(1, 1);

        // trivially equal (because they are the same object)
        if (c.reflect_X() == c.reflect_0()) {
            print_string("passed\n");
        } else {
            print_string("failed\n");
        };

        // equal
        if (c.reflect_X().reflect_Y().equal(c.reflect_0())) {
            print_string("passed\n");
        } else {
            print_string("failed\n");
        };
    };
};

class Complex : IO {
    let x;
    let y;

    init(a, b) {
        x = a;
        y = b;
        self;
    };

    print() {
        if (y == 0) {
            print_int(x);
        } else {
            print_int(x);
            print_string("+");
            print_int(y);
            print_string("I");
        };
    };

    reflect_0() {
        x = ~x;
        y = ~y;
        self;
    };

    reflect_X() {
        y = ~y;
        self;
    };

    reflect_Y() {
        x = ~x;
        self;
    };

    equal(d) {
        if (x == d.x_value()) {
            if (y == d.y_value()) {
                true;
            } else {
                false;
            };
        } else {
            false;
        };
    };

    x_value() { x; };

    y_value() { y; };

};