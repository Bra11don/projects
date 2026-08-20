// test file for p1a3

open P1a3; //open the module

print_endline("Enter s1: ");
let s1 = float_of_string(read_line());

print_endline("Enter s2: ");
let s2 = float_of_string(read_line());

print_endline("Enter s3: ");
let s3 = float_of_string(read_line());

let area = triangle_area(s1, s2, s3);

switch (area){
    | None => Printf.printf("The sides do not form a triangle. \n")
    | Some(x) => Printf.printf("The area of the triangle is %f. \n", x)
};