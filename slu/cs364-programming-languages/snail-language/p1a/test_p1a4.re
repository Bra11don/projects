/* test_p1a4.re */

open P1a4;

// Square root function
let sqrt = x => sqrt(x);

print_endline("Enter a number to start with:");
let start = read_line() |> float_of_string;

print_endline("Enter number of times to apply square root:");
let times = read_line() |> int_of_string;

let result = repeat(sqrt, start, times);

Printf.printf("Result after applying square root %d times to %.4f: %.4f\n", times, start, result);
