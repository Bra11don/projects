// 38a1108d19f54a1fab115779487fbbcea3ae1843

//Given a unary function f, an argument to the function arg, and an integer n, return f applied to arg n times.
let rec repeat = ((f: 'a => 'a), (arg: 'a), (n: int)) : 'a => {
    if (n == 0){
        arg;
    } else{
        repeat(f, f(arg), n-1);
    }
};