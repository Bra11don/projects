#example of generating JSON data from python
import json

#start with python objects
foo = {
    "data" : "bob",
    "nums" : [1,2,3,4,5],
    "valid" : True
}

# print(foo)
print( json.dumps(foo, indent=3) )