//139ba267302516bcc9e9e23141d94ac04cd56a91

// All the code we need for evaluating snail programs
open Ast;

type location = int

type value =
    | Void
    | Bool(bool)
    | String(int, string)
    | Int(Int64.t) // this might work
    | Array(int, Array.t(value))
    // dynamic object, model objects with members and their locations
    // string - class name for the object
    // list((string, location)): string name of the object, location where we map in the store
    | Dyn(string, list((string, location)))

module OrderedLocation = {
    type t = location;
    let compare = compare
};

// Map module for Store
module LocationMap = Map.Make(OrderedLocation);
// Map module for Environment
module StringMap = Map.Make(String);

type environment = StringMap.t(location);
type store = LocationMap.t(value);

// helper variable bindings
let emptyEnv : environment = StringMap.empty;
let emptyStore : store = LocationMap.empty;

// tracking of locations
let loc_val : ref(int) = ref(0);
// function to get a new location
// assumption every location is unique
let newloc = () => {
    let l = loc_val^;
    // increment the location value
    incr(loc_val);
    l;
}

// runtime error function
let runtime_error = (line: int, col: int, msg: string) => {
    Printf.printf("ERROR: %d:%d: Exception: %s\n", line, col, msg);
    exit(0);
};

let debug_value = (v: value) => {
    switch(v) {
        | String(len, str) => Printf.printf("Debug: String(%d, %s)\n", len, str)
        | Array(size, _) => Printf.printf("Debug: Array(size=%d)\n", size)
        | Void => Printf.printf("Debug: Void\n")
        | _ => Printf.printf("Debug: Other value type\n")
    }
};


// function to collect all member variables of a given type
// so that we can construct a new object
let rec get_members = (prog : program, typ : string) : list(member) => {
    // base case (all built in types have no members)
    // look up the class typ in our AST (is this a base case?)
    switch(List.assoc_opt(typ, prog)) {
        | Some(cls) => {
            // recursive case
            // recurse on our parent
            // then append our members to the returned list
            let (_name, inherits, members, _methods) = cls;
            let parent_members = switch(inherits) {
                | Some(parent_typ) => get_members(prog, parent_typ)
                | None => get_members(prog, "Object")
            };
            parent_members @ members;
        }
        | None => {
            // Not found --- check if this is a base class
            switch(typ) {
                | "Array"
                | "Bool"
                | "Int"
                | "IO"
                | "Object"
                | "String" => {
                    // Base case -> return an empty list of members
                    []
                }
                | _ => {
                    // error handling
                    runtime_error(0, 0, Printf.sprintf("Unknown class %s", typ));
                }
            }
        }
    }
};

// find_method is smth we use in a dispatch look up
// looking up a method given an AST and a type name
// method is tuple: name, list of params, body
// change method to Identifier so you can have both name and location
let rec find_method = (prog : program, typ : string, method : identifier) : method => {

    // look up in our AST the class type that we are working with
    switch(List.assoc_opt(typ, prog)) {
        | Some(cls) => {
            let (_name, inherits, _members, methods) = cls;
            switch(List.find_opt((m : method) => {
                let ((name, (_, _)), _, _) = m;
                let (m_name, _) = method;
                name == m_name;
            }, methods)) {
                | Some(method) => method
                | None => {
                    switch(inherits) {
                        | Some(parent) => find_method(prog, parent, method)
                        | None => find_method(prog, "Object", method)
                    }
                }
            }
        }
        | None => {
            // class was not found in the AST
            // it could be a built in type
            let (m_name, (line, col)) = method;
            switch(typ){
                | "Array" => {
                    switch(m_name){
                        | "length" => {
                            // return a method tuple for this method
                            // what is the body? the body is a snail expression
                            // but! there is no snail code for our built-in methods
                            (("length", (0,0)), [], (Internal("Array.length"), (0,0)))
                        }
                        | _ => {
                            // unknown method
                            // recurse
                            find_method(prog, "Object", method)
                        }
                    }
                }
                | "Bool" => {
                    find_method(prog, "Object", method)
                }
                | "Int" => {
                    find_method(prog, "Object", method)
                }
                | "IO" => {
                    switch(m_name) {
                        | "print_string" => {
                            // Add one String parameter
                            (("print_string", (0,0)),
                             [("str", (0, 0))],  // Parameter name "str"
                            (Internal("IO.print_string"), (line, col)))
                        }
                        | "print_int" => {
                            // Add one Int parameter
                            (("print_int", (0,0)),
                             [("n", (0, 0))],    // Parameter name "n"
                            (Internal("IO.print_int"), (line, col)))
                        }
                        | "read_string" => {
                            (("read_string", (0,0)), [], (Internal("IO.read_string"), (line,col)))
                        }
                        | "read_int" => {
                            (("read_int", (0,0)), [], (Internal("IO.read_int"), (line,col)))
                        }
                        | _ => {
                            find_method(prog, "Object", method)
                        }
                    }

                }
                | "String" => {
                    switch(m_name){
                        | "concat" => {
                            (("concat", (0,0)), [("other", (0,0))], (Internal("String.concat"), (line, col)))

                        }
                        | "length" => {
                                (("length", (0,0)), [], (Internal("String.length"), (line , col)))

                        }
                        | "substr" => {
                            (("substr", (0,0)), [("start", (0,0)), ("length", (0,0))], (Internal("String.substr"), (line,col)))
                        }
                        | _ => {
                            find_method(prog, "Object", method)
                        }
                    }
                }
                | "Object" => {
                    switch(m_name){
                        | "abort" => {
                            // flushes all output and halts program execution with the error message “abort\n”
                            (("abort", (0,0)), [], (Internal("Object.abort"), (line,col)))
                        }
                        | "copy" => {
                            // produces a shallow copy of the object.1 This method will fail for an Array
                            (("copy", (0,0)), [], (Internal("Object.copy"), (line,col)))
                        }
                        | "get_type" => {
                            // returns a String with the name of the class of the object
                            (("get_type", (0,0)), [], (Internal("Object.get_type"), (line,col)))
                        }
                        | "is_a" => {
                        (("is_a",(0,0)), [("t",(0,0))], (Internal("Object.is_a"), (line, col)))
                        }
                        | _ => {
                            find_method(prog, "Object", method)
                        }
                    }
                }
                | _ => {
                    // unknown class type
                    // have a runtime exception
                    runtime_error(line, col, Printf.sprintf("Unknown class %s", typ));
                }
            }
        }
    }
}

// expression evaluation code
let rec evaluate_expression = ((prog: program),
                                (so : value),
                                (e : StringMap.t(location)),
                                (s : LocationMap.t(value)),
                                (exp : expression)) : (value, environment, store) => {

    // expression = (expr_value, loc)
    // decompose
    let (expval, expl) = exp;
    let (expval, (line, col)) = exp;


    // switch on the expval type
    switch(expval) {
        | DynamicDispatch(target, method, arguments) => {
            let (method_name, _) = method;

            // Evaluate target
            let (target_v, e_1, s_1) = evaluate_expression(prog, so, e, s, target);

            // If void -> error
            switch(target_v) {
                | Void => runtime_error(line, col, "dispatch on void")
                | _ => ()
            };

            // Determine class and members
            let (typ, members) = switch(target_v) {
                | Dyn(t, mems) => (t, mems)
                | Int(_) => ("Int", [])
                | Bool(_) => ("Bool", [])
                | String(_, _) => ("String", [])
                | Array(_, _) => ("Array", [])
                | _ => runtime_error(line, col, "Cannot dispatch on non-object")
            };

            // Find method
            let (_, params, body) = find_method(prog, typ, method);

            // Evaluate arguments in order
            let (args_v, e_2, s_2) = List.fold_left(
                ((acc_vals, acc_env, acc_store), arg) => {
                let (arg_v, new_env, new_store) = evaluate_expression(prog, so, acc_env, acc_store, arg);
                (acc_vals @ [arg_v], new_env, new_store)
                },
                ([], e_1, s_1),
                arguments
            );

            // Construct method environment

            // 1. Add self
            let self_loc = newloc();
            let env_with_self = StringMap.add("self", self_loc, emptyEnv);
            let store_with_self = LocationMap.add(self_loc, target_v, s_2);

            // 2. Add members if Dyn object
            let env_with_members = List.fold_left(
                (acc_env, (name, loc)) => StringMap.add(name, loc, acc_env),
                env_with_self,
                members
            );

            // 3. Add parameters
            if (List.length(params) != List.length(args_v)) {
                runtime_error(line, col,
                Printf.sprintf("Method %s expects %d arguments but got %d",
                method_name,
                List.length(params),
                List.length(args_v))
                );
            };

            let (method_env, method_store) = List.fold_left2(
                ((acc_env, acc_store), (pname, _), arg_val) => {
                let param_loc = newloc();
                (
                    StringMap.add(pname, param_loc, acc_env),
                    LocationMap.add(param_loc, arg_val, acc_store)
                )
                },
                (env_with_members, store_with_self),
                params,
                args_v
            );

            // Evaluate method body
            let (v, e_3, s_3) = evaluate_expression(prog, target_v, method_env, method_store, body);
            (v, e_2, s_3);
        }

        | StaticDispatch(target, cls, method, arguments) => {
            let (class_name, _) = cls;
            let (method_name, _) = method;
            let (target_v, e_1, s_1) = evaluate_expression(prog, so, e, s, target);

            switch(target_v) {
                | Dyn(_, members) => {  // Note: we use class_name, not runtime type
                    let (_, params, body) = find_method(prog, class_name, method);

                    // Evaluate arguments in order
                    let (args_v, e_2, s_2) = List.fold_left(
                        ((acc_vals, acc_env, acc_store), arg) => {
                            let (arg_v, new_env, new_store) =
                                evaluate_expression(prog, so, acc_env, acc_store, arg);
                            (acc_vals @ [arg_v], new_env, new_store)
                        },
                        ([], e_1, s_1),
                        arguments
                    );

                    // Create method environment starting with member bindings
                    let base_env = List.fold_left(
                        (env, (name, loc)) => StringMap.add(name, loc, env),
                        emptyEnv,
                        members
                    );

                    // Add parameter bindings
                    let (method_env, method_store) = List.fold_left2(
                        ((acc_env, acc_store), param, value) => {
                            let (param_name, _) = param;
                            let loc = newloc();
                            (
                                StringMap.add(param_name, loc, acc_env),
                                LocationMap.add(loc, value, acc_store)
                            )
                        },
                        (base_env, s_2),
                        params,
                        args_v
                    );

                    let (v, e_3, s_3) = evaluate_expression(prog, target_v, method_env, method_store, body);
                    (v, e_2, s_3)
                }
                | _ => runtime_error(line, col, Printf.sprintf("Cannot dispatch %s statically", method_name))
            }
        }

        | SelfDispatch(method, arguments) => {
            switch(so) {
                | Dyn(class_name, members) => {
                    let (_, params, body) = find_method(prog, class_name, method);

                    // First evaluate all arguments
                    let (args_v, e_1, s_1) = List.fold_left(
                        ((acc_vals, acc_env, acc_store), arg) => {
                            let (arg_v, new_env, new_store) =
                                evaluate_expression(prog, so, acc_env, acc_store, arg);
                            (acc_vals @ [arg_v], new_env, new_store)
                        },
                        ([], e, s),
                        arguments
                    );

                    // Then setup method environment
                    let self_loc = newloc();
                    let env_with_self = StringMap.add("self", self_loc, emptyEnv);
                    let store_with_self = LocationMap.add(self_loc, so, s_1);

                    // Add member bindings
                    let env_with_members = List.fold_left(
                        (acc_env, (name, loc)) => StringMap.add(name, loc, acc_env),
                        env_with_self,
                        members
                    );

                    // Add parameters
                    let (method_env, method_store) = List.fold_left2(
                        ((acc_env, acc_store), (param_name, _), value) => {
                            let loc = newloc();
                            (
                                StringMap.add(param_name, loc, acc_env),
                                LocationMap.add(loc, value, acc_store)
                            )
                        },
                        (env_with_members, store_with_self),
                        params,
                        args_v
                    );

                    let (v, _, s_2) = evaluate_expression(prog, so, method_env, method_store, body);
                    (v, e_1, s_2)
                }
                | _ => runtime_error(line, col, "Self dispatch only valid within object context")
            }
        }

        | String(value, _) => (String(String.length(value), value), e, s)  // String literal

        | Int(value, _) => (Int(value), e, s)  // Direct integer literal
        | MathOp(lhs, rhs, op) => {
            let (lhs_v, e_1, s_1) = evaluate_expression(prog, so, e, s, lhs);
            let (rhs_v, e_2, s_2) = evaluate_expression(prog, so, e_1, s_1, rhs);


            switch (lhs_v, rhs_v) {
                | (Int(l), Int(r)) =>
                    let result = switch(op) {
                        | "plus" => Int(Int64.add(l, r))
                        | "minus" => Int(Int64.sub(l, r))
                        | "times" => Int(Int64.mul(l, r))
                        | "divide" =>
                            if (r == Int64.zero) {
                                runtime_error(line, col, "Division by zero")
                            } else {
                                Int(Int64.div(l, r))
                            }
                        | "modulo" =>
                            if (r == Int64.zero) {
                                runtime_error(line, col, "Modulo by zero")
                            } else {
                                Int(Int64.rem(l, r))
                            }
                        | _ => runtime_error(line, col, Printf.sprintf("Unknown math operator: %s", op))
                    };
                    (result, e_2, s_2)
                | _ => runtime_error(line, col, "Math operations require integer operands")
            }
        }

        | Comp(lhs, rhs, op) => {
            let (lhs_v, e_1, s_1) = evaluate_expression(prog, so, e, s, lhs);
            let (rhs_v, e_2, s_2) = evaluate_expression(prog, so, e_1, s_1, rhs);

            let result = switch (lhs_v, rhs_v) {
                | (Dyn(class1, members1), Dyn(class2, members2)) =>
                    switch (op) {
                        | "equals" => Bool(class1 == class2 && members1 == members2) // pointer equality
                        | _ => runtime_error(line, col, "Runtime error: invalid comparison operator for dynamic objects")
                    }
                | (Array(_, arr1), Array(_, arr2)) =>
                    switch (op) {
                        | "equals" => Bool(arr1 == arr2) // pointer equality
                        | _ => runtime_error(line, col, "Runtime error: invalid comparison operator for arrays")
                    }
                | (Int(l), Int(r)) =>
                    switch(op) {
                        | "lt" => Bool(Int64.compare(l, r) < 0)
                        | "lte" => Bool(Int64.compare(l, r) <= 0)
                        | "equals" => Bool(Int64.compare(l, r) == 0)
                        | "neq" => Bool(Int64.compare(l, r) != 0)
                        | _ => runtime_error(line, col, "Runtime error: invalid comparison operator")
                    }
                | (String(_, s1), String(_, s2)) =>
                    switch(op) {
                        | "lt" => Bool(String.compare(s1, s2) < 0)
                        | "lte" => Bool(String.compare(s1, s2) <= 0)
                        | "equals" => Bool(String.compare(s1, s2) == 0)
                        | "neq" => Bool(String.compare(s1, s2) != 0)
                        | _ => runtime_error(line, col, "Runtime error: invalid comparison operator")
                    }
                | (Bool(b1), Bool(b2)) =>
                    switch(op) {
                        | "lt" => Bool(!b1 && b2) // false < true
                        | "lte" => Bool(!b1 || b2) // false <= true
                        | "equals" => Bool(b1 == b2)
                        | "neq" => Bool(b1 != b2)
                        | _ => runtime_error(line, col, "Runtime error: invalid boolean comparison")
                    }
                | (Void, Void) =>
                    switch(op) {
                        | "equals" => Bool(true)
                        | "neq" => Bool(false)
                        | _ => runtime_error(line, col, "Runtime error: invalid void comparison")
                    }
                | _ => runtime_error(line, col, "Runtime error: invalid comparison between different types")
            };
            (result, e_2, s_2)
        }
        | BoolOp(lhs, rhs, op) => {
            let (lhs_v, e_1, s_1) = evaluate_expression(prog, so, e, s, lhs);

            switch(lhs_v) {
                | Bool(l) =>
                    switch(op) {
                        | "and" =>
                            if (!l) {
                                (Bool(false), e_1, s_1)  // Short circuit
                            } else {
                                let (rhs_v, e_2, s_2) = evaluate_expression(prog, so, e_1, s_1, rhs);
                                switch(rhs_v) {
                                    | Bool(r) => (Bool(l && r), e_2, s_2)
                                    | _ => runtime_error(line, col, "Runtime error: non-boolean in 'and'")
                                }
                            }
                        | "or" =>
                            if (l) {
                                (Bool(true), e_1, s_1)  // Short circuit
                            } else {
                                let (rhs_v, e_2, s_2) = evaluate_expression(prog, so, e_1, s_1, rhs);
                                switch(rhs_v) {
                                    | Bool(r) => (Bool(l || r), e_2, s_2)
                                    | _ => runtime_error(line, col, "Runtime error: non-boolean in 'or'")
                                }
                            }
                        | _ => runtime_error(line, col, "Runtime error: unknown boolean operator")
                    }
                | _ => runtime_error(line, col, "Runtime error: non-boolean in boolean operation")
            }
        }
        | Bool(b) => (Bool(b), e, s)

        | IsVoid(expr) => {
            let (value, e1, s1) = evaluate_expression(prog, so, e, s, expr);
            let result = switch(value) {
                | Void => Bool(true)
                | _ => Bool(false)
            };
            (result, e1, s1)
        }

        | Negate(expr) => {
            let (value, e1, s1) = evaluate_expression(prog, so, e, s, expr);
            switch(value) {
                | Int(i) => (Int(Int64.neg(i)), e1, s1)
                | _ => runtime_error(line, col, "Negate requires an integer")
            }
        }


        | If(guard, thn, els) => {
            // result of guard must be of type bool
            let (guard_v, e_1, s_1) = evaluate_expression(prog, so, e, s, guard);

            switch(guard_v) {
                | Bool(true) => {
                    let (thn_v, e_2, s_2) = evaluate_expression(prog, so, e_1, s_1, thn);
                    (thn_v, e_2, s_2);
                }
                | Bool(false) => {
                    let (els_v, e_2, s_2) = evaluate_expression(prog, so, e_1, s_1, els);
                    (els_v, e_2, s_2);
                }
                | _ => {
                    runtime_error(line, col, "guard has to result in a bool value");
                }
            }
        }

        | While(guard, body) => {
            let rec loop = (env, store) => {
                let (guard_v, e_1, s_1) = evaluate_expression(prog, so, env, store, guard);
                switch(guard_v) {
                    | Bool(true) => {
                        let (body_v, e_2, s_2) = evaluate_expression(prog, so, e_1, s_1, body);
                        loop(e_2, s_2)
                    }
                    | Bool(false) => (Void, e_1, s_1)
                    | _ => runtime_error(line, col, "guard needs to evaluate to bool")
                }
            };
            loop(e, s)
        }

        | LetInit(target, expr) => {
            let (init_val, e_1, s_1) = evaluate_expression(prog, so, e, s, expr);
            let (name, _) = target;
            let loc = newloc();

            // Add variable to a new temporary environment
            let temp_env = StringMap.add(name, loc, e_1);
            let new_store = LocationMap.add(loc, init_val, s_1);

            // Return value but do not modify the parent environment
            (init_val, temp_env, new_store);
        }


        | LetNoInit(target) => {
            let (name, _) = target;
            let loc = newloc();
            let new_env = StringMap.add(name, loc, e);
            let new_store = LocationMap.add(loc, Void, s);
            (Void, new_env, new_store)
        }
        | Assign(target, expr) => {
            let (v1, e_1, s_1) = evaluate_expression(prog, so, e, s, expr);

            let (name, loc) = target;

            switch (StringMap.find_opt(name, e_1)) {
                | Some(location) => {
                    // update the store with the new value at the target location
                    let newStore = LocationMap.add(location, v1, s_1);
                    (v1, e_1, newStore);
                }
                | None => runtime_error(line, col, Printf.sprintf("Undefined identifier: %s", name));
            }
        }
        | Ident(identifier) => {
            let (name, _) = identifier;
            if (name == "self") {
                (so, e, s)
            } else {
                switch(StringMap.find_opt(name, e)) {
                    | Some(loc) => {
                        switch(LocationMap.find_opt(loc, s)) {
                            | Some(value) => (value, e, s)
                            | None => runtime_error(line, col, Printf.sprintf("No value found for identifier %s", name))
                        }
                    }
                    | None => {
                        // Check if it's a member of the current object
                        switch(so) {
                            | Dyn(_, members) =>
                                switch(List.find_opt(((n, _)) => n == name, members)) {
                                    | Some((_, loc)) => (LocationMap.find(loc, s), e, s)
                                    | None => runtime_error(line, col, Printf.sprintf("Undefined identifier: %s", name))
                                }
                            | _ => runtime_error(line, col, Printf.sprintf("Undefined identifier: %s", name))
                        }
                    }
                }
            }
        }

        | ArrayAssign(array_expr, index_expr, value_expr) => {
            let (array_v, e_1, s_1) = evaluate_expression(prog, so, e, s, array_expr);
            let (index_v, e_2, s_2) = evaluate_expression(prog, so, e_1, s_1, index_expr);
            let (value_v, e_3, s_3) = evaluate_expression(prog, so, e_2, s_2, value_expr);

            let actual_array = switch(array_v) {
                | Array(size, arr) => Some((size, arr, array_v))
                | _ =>
                    // Try to find in current scope first
                    switch(StringMap.find_opt("data", e_3)) {
                        | Some(loc) =>
                            switch(LocationMap.find(loc, s_3)) {
                                | Array(size, arr) => Some((size, arr, Array(size, arr)))
                                | _ => None
                            }
                        | None =>
                            // Then try population_map
                            switch(StringMap.find_opt("population_map", e_3)) {
                                | Some(loc) =>
                                    switch(LocationMap.find(loc, s_3)) {
                                        | Array(size, arr) => Some((size, arr, Array(size, arr)))
                                        | _ => None
                                    }
                                | None => None
                            }
                    }
            };

            switch(actual_array) {
                | Some((size, arr, orig_array)) =>
                    switch(index_v) {
                        | Int(idx) => {
                            let index = Int64.to_int(idx);
                            if (index < 0) {
                                runtime_error(line, col, "Runtime error: negative array index")
                            } else if (index >= size) {
                                runtime_error(line, col, "Runtime error: array index out of bounds")
                            } else {
                                let new_arr = Array.copy(arr);
                                new_arr[index] = value_v;  // Store the value directly
                                let new_array_value = Array(size, new_arr);

                                // Find the correct location to update
                                let array_loc = switch(StringMap.find_opt("data", e_3)) {
                                    | Some(loc) => loc
                                    | None =>
                                        switch(StringMap.find_opt("population_map", e_3)) {
                                            | Some(loc) => loc
                                            | None => runtime_error(line, col, "Array variable not found")
                                        }
                                };

                                let new_store = LocationMap.add(array_loc, new_array_value, s_3);
                                (new_array_value, e_3, new_store)
                            }
                        }
                        | _ => runtime_error(line, col, "Runtime error: array index must be an integer")
                    }
                | None => runtime_error(line, col, "Not an array")
            }
        };


        | ArrayAccess(array_expr, index_expr) => {
            let (array_v, e_1, s_1) = evaluate_expression(prog, so, e, s, array_expr);
            let (index_v, e_2, s_2) = evaluate_expression(prog, so, e_1, s_1, index_expr);

            let actual_array = switch(array_v) {
                | Array(size, arr) => (size, arr)
                | _ =>
                    // Try to find in current scope first
                    switch(StringMap.find_opt("data", e_2)) {
                        | Some(loc) =>
                            switch(LocationMap.find(loc, s_2)) {
                                | Array(size, arr) => (size, arr)
                                | _ => runtime_error(line, col, "Not an array")
                            }
                        | None =>
                            // Then try population_map
                            switch(StringMap.find_opt("population_map", e_2)) {
                                | Some(loc) =>
                                    switch(LocationMap.find(loc, s_2)) {
                                        | Array(size, arr) => (size, arr)
                                        | _ => runtime_error(line, col, "Not an array")
                                    }
                                | None => runtime_error(line, col, "Array not found")
                            }
                    }
            };

            let (size, arr) = actual_array;
            switch(index_v) {
                | Int(idx) => {
                    let index = Int64.to_int(idx);
                    if (index < 0) {
                        runtime_error(line, col, "Runtime error: negative array index")
                    } else if (index >= size) {
                        runtime_error(line, col, "Runtime error: array index out of bounds")
                    } else {
                        (arr[index], e_2, s_2)
                    }
                }
                | _ => runtime_error(line, col, "Runtime error: array index must be an integer")
            }
        };

        | NewArray(expr) => {
            let (expr_v, e_1, s_1) = evaluate_expression(prog, so, e, s, expr);
            switch(expr_v) {
                | Int(sz) =>
                    if (Int64.compare(sz, Int64.zero) < 0) {
                        runtime_error(line, col, "Runtime error: negative array size")
                    } else {
                        let size = Int64.to_int(sz);
                        let new_arr = Array.make(size, Void);
                        (Array(size, new_arr), e_1, s_1)
                    }
                | _ => runtime_error(line, col, "Runtime error: array size must be an integer")
            }
        }


        | New(cls_ident) => {
            // decompose type identifier
            let (t, _location) = cls_ident;
                switch(t) {
                    | "Bool" => (Bool(false), e, s);
                    | "Int" => (Int(Int64.of_int(0)), e, s);
                    | "String" => (String(0, ""), e, s);
                    | _ => {
                        // get all the class members
                        let class_members : list(member) = get_members(prog, t);
                        // map each member to create a new location
                        let locs  : list(location)= List.map((_m) => newloc(), class_members);
                        let v_1 : value = Dyn(t, List.map2((mem, l) => {
                            let name = switch(mem) {
                                | NoInitMember((n, _l))
                                | InitMember((n, _l), _) => n
                            };
                            (name, l);
                        }, class_members,  locs));
                        // update store(for each location, add to the store Void)
                        let s_1 = List.fold_left((acc, l) => {
                            // add l => Void
                            LocationMap.add(l, Void,  acc);
                        }, s, locs);

                        // create an environment for the init
                        let e_1 = List.fold_left2((acc, member, loc) => {
                            let name : string = switch(member){
                                |NoInitMember((n, _l))
                                | InitMember((n, _l), _) => n
                            }
                            StringMap.add(name, loc, acc);
                        }, emptyEnv, class_members, locs);

                        // backwards --> need to reverse
                        let block_init = List.fold_left((acc, member) => {
                            switch(member) {
                                | NoInitMember(_) => acc
                                | InitMember(i, init_expr) => {
                                    // build up a block for the assignment
                                    let blk : expression = (Block([init_expr]), expl);
                                    // make an assignment
                                    let assn : expression = (
                                        Assign(i, blk),
                                        expl
                                    );
                                    // prepend
                                    [assn, ...acc];
                                }
                            }
                        }, [], class_members);

                        let blk : expression = (Block(List.rev(block_init)), expl);

                        let (v_2, e_2, s_2) = evaluate_expression(prog, v_1, e_1, s_1, blk);

                        (v_1, e, s_2);
                    }
            }
        }
        | Not(target) => {
            let (target_v, e_1, s_1) = evaluate_expression(prog, so, e, s, target);

            // negate value if it's a bool
            switch target_v {
                | Bool(value) => (Bool(!value), e_1, s_1);
                | _ => runtime_error(line, col, "Not operation requires a boolean");
            }
        }

        | Block(expressions) => {
            // Save the current environment
            let initial_env = e;

            // Evaluate each expression in sequence
            let (result, _, final_store) = List.fold_left(
                ((_, acc_env, acc_store), expr) => {
                    evaluate_expression(prog, so, acc_env, acc_store, expr)
                },
                (Void, e, s),
                expressions
            );

            // Restore the outer environment
            (result, initial_env, final_store);
        }


        | Internal(internal_name) => {
            switch(internal_name){
                | "Array.length" => {
                    // implement Array.length functionality
                    // access our self-object
                    switch(so){
                        | Array(sz, arr) => {
                            // return a value with the size
                            // no updates to the evinronment
                            (Int(Int64.of_int(sz)), e, s);
                        }
                        | _ => {
                            // error
                            runtime_error(line, col, "Tried to get array length for a non-array");
                        }
                    }
                }
                | "Object.abort" => {
                    flush(stdout)
                    Printf.printf("abort\n");
                    exit(0);
                }
                | "Object.copy" => {
                    switch(so) {
                        | Array(_,_) => runtime_error(line,col,"copy not allowed for arrays")
                        | Dyn(t, members) => {
                            // Create new locations for each member and copy values
                            let (new_members, final_store) = List.fold_left(
                                ((acc_members, acc_store), (name, old_loc)) => {
                                    let new_loc = newloc();
                                    // Get the original value and copy it
                                    let value = LocationMap.find(old_loc, acc_store);
                                    let new_store = LocationMap.add(new_loc, value, acc_store);
                                    (acc_members @ [(name, new_loc)], new_store)
                                },
                                ([], s),
                                members
                            );

                            // Create new object with copied members
                            let new_obj = Dyn(t, new_members);
                            (new_obj, e, final_store)
                        }
                        | String(len, str) => (String(len, str), e, s)
                        | Int(n) => (Int(n), e, s)
                        | Bool(b) => (Bool(b), e, s)
                        | Void => (Void, e, s)
                    }
                }

                | "Object.get_type" => {
                    let type_name = switch(so) {
                        | Int(_) => "Int"
                        | Bool(_) => "Bool"
                        | String(_, _) => "String"
                        | Array(_, _) => "Array"
                        | Void => "Object"  // Should this be "Void"?
                        | Dyn(t, _) => t
                    };
                    (String(String.length(type_name), type_name), e, s)
                }

                | "Object.is_a" => {
                    switch(so) {
                        | Dyn(class_name, _) => {
                            let t_loc = StringMap.find("t", e);
                            let t_val = LocationMap.find(t_loc, s);
                            switch(t_val) {
                            | String(_, target_type) => {
                                // Check inheritance chain if class_name is a subclass of target_type
                                let rec is_subtype(cls) = {
                                if (cls == target_type) {
                                    true
                                } else {
                                    switch(List.assoc_opt(cls, prog)) {
                                    | Some((_, Some(parent), _, _)) => is_subtype(parent)
                                    | _ => false
                                    }
                                }
                                };
                                (Bool(is_subtype(class_name)), e, s)
                            }
                            | _ => runtime_error(line, col, "Argument to is_a must be a string")
                            }
                        }
                        | _ => runtime_error(line, col, "is_a must be called on an object")
                    }
                }
                | "Object.isVoid" => {
                    switch(so) {
                        | Void => (Bool(true), e, s)
                        | _ => (Bool(false), e, s)
                    }
                }

                | "IO.print_string" => {
                    let str_loc = StringMap.find("str", e);
                    let str_val = LocationMap.find(str_loc, s);
                    switch(str_val) {
                        | String(_, str) => {
                            let processed_str =
                                str
                                |> Str.global_replace(Str.regexp("\\\\n"), "\n")
                                |> Str.global_replace(Str.regexp("\\\\t"), "\t");
                            print_string(processed_str);
                            flush(stdout);
                            (Void, e, s)
                        }
                        | _ => runtime_error(line, col, "Need a string");
                    }
                }

                | "IO.print_int" => {
                    try {
                        let int_loc = StringMap.find("n", e);
                        let int_val = LocationMap.find(int_loc, s);
                        switch(int_val) {
                            | Int(value) => {
                                Printf.printf("%Ld", value);
                                flush(stdout);
                                (Void, e, s)
                            }
                            | _ => runtime_error(line, col, "print_int requires an Int")
                        }
                    } {
                        | Not_found => runtime_error(line, col, "print_int: parameter not found")
                    }
                }

                | "IO.read_int" => {
                    let input = switch(input_line(stdin)) {
                        | value => value
                        | exception End_of_file => "0"
                    };
                    let num = (
                        try (Int64.of_string(String.trim(input))) {
                            | _ => Int64.zero  // Default to `0` on invalid input
                        }
                    );
                    (Int(num), e, s)
                }

                | "IO.read_string" => {
                    let input = switch(input_line(stdin)) {
                        | value => String(String.length(value), value)
                        | exception End_of_file => String(0, "")
                    };
                    (input, e, s)
                }

                | "String.length" => {
                    switch(so) {
                        | String(sz, arr) =>{
                            (Int(Int64.of_int(sz)), e, s);
                        }
                        | _ => {
                            runtime_error(line, col, "need a string");
                        }
                    }
                }
                | "String.concat" => {
                    switch(so) {
                        | String(_, s1) => {
                            // let str_loc = StringMap.find("str", e);
                            let str_loc = StringMap.find("other", e);
                            let str_val = LocationMap.find(str_loc, s);
                            switch(str_val) {
                                | String(_, s2) => {
                                    let concat_str = String.concat("", [s1, s2]);
                                    (String(String.length(concat_str), concat_str), e, s)
                                }
                                | _ => runtime_error(line, col, "concat requires string argument")
                            }
                        }
                        | _ => runtime_error(line, col, "concat requires string receiver")
                    }
                }

                | "String.substr" => {
                    switch(so) {
                        | String(len, str) => {
                            let start_loc = StringMap.find("start", e);
                            let length_loc = StringMap.find("length", e);
                            let start_val = LocationMap.find(start_loc, s);
                            let length_val = LocationMap.find(length_loc, s);

                            switch(start_val, length_val) {
                                | (Int(start), Int(length)) => {
                                    let start_i = Int64.to_int(start);
                                    let length_i = Int64.to_int(length);
                                    if (start_i < 0 || length_i < 0 || start_i + length_i > len) {
                                        runtime_error(line, col, "Runtime error: invalid substring parameters")
                                    } else {
                                        let result = String.sub(str, start_i, length_i);
                                        (String(length_i, result), e, s)
                                    }
                                }
                                | _ => runtime_error(line, col, "Runtime error: substring requires integer arguments")
                            }
                        }
                        | _ => runtime_error(line, col, "Runtime error: substring requires string receiver")
                    }
                }

                | _ => {
                    runtime_error(line, col, Printf.sprintf("Unimplemented internal method %s", internal_name));
                }
            }
        }
    }
};
