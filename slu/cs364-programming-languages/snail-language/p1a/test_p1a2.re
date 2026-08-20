// test file fot p1a2 where we test the functionality

open P1a2;

print_endline("Enter the price: ");
let price = float_of_string(read_line());
print_endline("Enter number of people: ");
let n = int_of_string(read_line());
let wc = split_tip(price, n);

//print the result as option type
switch (wc){
    | None => Printf.printf("Cannot split the bill among 0 people. \n")
    | Some(x) => Printf.printf("Each person should pay %f. \n", x)
};
