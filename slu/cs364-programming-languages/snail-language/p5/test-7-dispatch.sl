class Shape {
    let name;

    init(n) {
        name = n;
        self;
    };

    describe() {
        print_string("This shape is a ");
        print_string(name);
        print_string(" (type: ");
        print_string(self.get_type());
        print_string(")\n");
    };
};

class Circle : Shape {
    let radius;

    init(r) {
        name = "circle";
        radius = r;
        self;
    };
};

class Square : Shape {
    let side;

    init(s) {
        name = "square";
        side = s;
        self;
    };
};

class Main : IO {
    main() {
        let shape = new Shape.init("generic shape");
        let circle = new Circle.init(5);
        let square = new Square.init(4);

        // Should print each shape's type
        shape.describe();   // Should show Shape
        circle.describe();  // Should show Circle
        square.describe();  // Should show Square
    };
};
