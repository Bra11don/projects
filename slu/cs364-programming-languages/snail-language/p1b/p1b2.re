//38a1108d19f54a1fab115779487fbbcea3ae1843

//Given two lists of integers of equal length, return Some <int>, where int is the dot product of the two lists. If the lists are not the same length, return None.

let dot_product =  ((l1: list(int)), (l2: list(int))) : option(int) => {
    if(List.length(l1) != List.length(l2)) {
        None
    } else {
        Some(List.fold_left2((acc, x, y) => acc + (x * y), 0, l1, l2))
    }
};

//alternatively -  tail recursive version
// let dot_product = ((l1: list(int)), (l2: list(int))) : option(int) => {
//     let rec helper = ((l1: list(int)), (l2: list(int)), (acc: int)) : option(int) => {
//         switch(l1, l2){
//         | ([], []) => Some(acc)
//         | ([hd1, ...tl1], [hd2, ...tl2]) => helper(tl1, tl2, acc + hd1 * hd2)
//         | (_, _) => None
//         }
//     }
//     helper(l1, l2, 0)
// };