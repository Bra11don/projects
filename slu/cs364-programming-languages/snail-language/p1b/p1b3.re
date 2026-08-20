//38a1108d19f54a1fab115779487fbbcea3ae1843

// Given a list of elements, l, and an element, e, return the number of occurrences of e in l.
let rec count =  ((l: list ('a)), (e: 'a)) : int => {
    switch(l){
        | [] => 0
        | [hd, ...tl] => if(hd == e) {
            1 + count(tl, e)
        } else {
            count(tl, e)
        }
    }
}

//alternatively using tail recursion
let cout = ((l: list (int)), (e: int)) : int => {
    let rec count = ((l: list (int)), (e: int), (acc:int)) : int => {
        switch(l){
            | [] => acc
            | [hd, ...tail] => {
                if(hd == e){
                    count(tail, e, acc + 1)
                } else {
                    count(tail, e, acc)
                }
            }
        }
    };
    count(l, e, 0)
}