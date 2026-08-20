class CellularAutomaton : IO {
    let population_map;

    init(map) {
        population_map = new[map.length()] Array;

        let i = 0;
        while (i < map.length()) {
            population_map[i] = map.substr(i, 1);
            i = i + 1;
        };
        self;
    };

    print() {
        let i = 0;
        while (i < population_map.length()) {
            print_string(population_map[i]);
            i = i + 1;
        };
        print_string("\n");
    };

    num_cells() { population_map.length(); };

    cell(i) { population_map[i]; };

    cell_left_neighbor(pos) {
        if (pos == 0) { cell(num_cells() - 1 ); }
        else {
            cell(pos - 1);
        };
    };

    cell_right_neighbor(pos) {
        if (pos == num_cells() - 1) { cell(0); }
        else { cell(pos + 1); };
    };

    /*
     * A cell will live if exactly 1 of itself and its immediate
     * neighbors are alive
     */
    cell_at_next_evolution(pos) {
        let curr = if (cell(pos) == "X") { 1; } else { 0; };
        let left = if (cell_left_neighbor(pos) == "X") { 1; } else { 0; };
        let right = if (cell_right_neighbor(pos) == "X") { 1; } else { 0; };

        if ( (curr + left + right) == 1) {
            "X";
        } else {
            ".";
        };
    };

    evolve() {
        let pos = 0;
        let num = num_cells();

        while (pos < num) {
            population_map[pos] = cell_at_next_evolution(pos);
            pos = pos + 1;
        };

        self;
    };
};

class Main {
    let cells;

    main() {
        cells = (new CellularAutomaton).init("         X         ");
        cells.print();

        let countdown = 20;
        while (0 < countdown) {
            cells.evolve();
            cells.print();
            countdown = countdown - 1;
        };
    };
};