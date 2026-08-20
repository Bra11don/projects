// 38a1108d19f54a1fab115779487fbbcea3ae1843

// given a list return length of list
let rec list_length = (l: list('a)) : int => {
    switch l {
    | [] => 0
    | [_, ...tail] => 1 + list_length(tail)
    }
};
