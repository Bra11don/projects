
open P1b2;

let l1 = [1, 2, 3, 4, 5];
let l2 = [6, 7, 8, 9, 10];

let result = dot_product(l1, l2);

// Print the option result
switch (result) {
| Some(value) => Printf.printf("Dot product of l1 and l2: %d\n", value)
| None => print_endline("Lists are not of equal length")
};

// Test with unequal length lists
let l3 = [1, 2, 3];
let result2 = dot_product(l1, l3);

switch (result2) {
| Some(value) => Printf.printf("Dot product of l1 and l3: %d\n", value)
| None => print_endline("Lists l1 and l3 are not of equal length")
};

// Test with empty lists
let empty1 = [];
let empty2 = [];
let result3 = dot_product(empty1, empty2);

switch (result3) {
| Some(value) => Printf.printf("Dot product of empty lists: %d\n", value)
| None => print_endline("Empty lists result")
};
