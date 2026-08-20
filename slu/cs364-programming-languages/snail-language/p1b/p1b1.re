//38a1108d19f54a1fab115779487fbbcea3ae1843

//Given a list of strings, return a list of strings with greetings

let rec salutations = (l:list(string)) : list(string) =>{
    switch(l){
    | [] => []
    | [hd, ...tl] => ["Salutations, " ++ hd, ...salutations(tl)]
    }
}

//ALTERNATIVELY USING TAIL RECURSION
// let salutations = (l: list(string)) : list(string) => {
//     let rec helper = (l: list(string), acc: list(string)) : list(string) => {
//         switch(l){
//             | [] => List.rev(acc)
//             | [hd, ...tl] => helper(tl, ["Salutations, " ++ hd, ...acc]) //prepend the greeting to the accumulator
//         }

//     }
//     helper(l, [])
// };
