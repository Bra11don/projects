// 38a1108d19f54a1fab115779487fbbcea3ae1843

//option type is a type that can either be None or Some(value)
//split bill + tip (20%) among n people
let split_tip = (( price: float), (n : int)) : option(float) =>{
    if (n > 0 && price > 0.0){
        Some((price *. 1.2) /. float_of_int(n));
    } else{
        None;
    }
};