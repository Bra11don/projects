open P1b1;

let lst = ["Harry", "Ron", "Hermione", "Minerva", "Albus"];

// Test the salutations function
let result = salutations(lst);

// Print the original list
print_endline("Original list:");
List.iter(name => print_endline(name), lst);
// Print the list of greetings
print_endline("\nGreetings:");
List.iter(greeting => print_endline(greeting), result);
