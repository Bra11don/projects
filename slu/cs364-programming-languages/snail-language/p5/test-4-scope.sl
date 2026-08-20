class Main : IO {
    main() {
        let x = 10;
        {
            let x = 20;
            print_int(x);    // Should print 20
            print_string("\n");
        };
        print_int(x);        // Should print 10
        print_string("\n");
    };
};
