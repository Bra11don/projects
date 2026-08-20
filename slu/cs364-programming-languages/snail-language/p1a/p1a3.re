// 38a1108d19f54a1fab115779487fbbcea3ae1843

//calculate the area of a triangle given the lengths of its sides
let triangle_area = ((s1: float), (s2: float), (s3: float)) : option(float) => {

    if (s1 <= 0.0 || s2 <= 0.0 || s3 <= 0.0){
        None;
    } else if (s1 +. s2 <= s3 || s1 +. s3 <= s2 || s2 +. s3 <= s1){
        None;
    } else{
        let s = (s1 +. s2 +. s3) /. 2.0;
        let a = sqrt(s *. (s -. s1) *. (s -. s2) *. (s -. s3));
        Some(a);
    }

};