# e5ea295659494239fa2a8e1d85d612351f712022

# def topological_sort(tasks):
#     # Build graph and in-degree count
#     # gotta first build a DAG with a dictionary of tasks and their dependencies
#     graph = {}
#     in_degree = {} # Count of incoming edges for each task

#     for task, dependency in tasks:
#         if task not in graph:
#             graph[task] = [] #initialize the graph
#         if dependency not in graph:
#             graph[dependency] = []

#         graph[dependency].append(task)
#         in_degree[task] = in_degree.get(task, 0) + 1 #increment the in-degree of the task
#         if dependency not in in_degree:
#             in_degree[dependency] = 0

#     # Initialize queue with nodes having zero in-degree
#     queue = [task for task in in_degree if in_degree[task] == 0]
#     queue.sort()  # Ensure ASCII alphabetical order

#     topological_order = []

#     while queue:
#         # Pop the first task in ASCII alphabetical order
#         current = queue.pop(0)
#         topological_order.append(current)

#         # Reduce in-degree of its neighbors
#         for neighbor in graph[current]:
#             in_degree[neighbor] -= 1
#             if in_degree[neighbor] == 0:
#                 queue.append(neighbor)
#                 queue.sort()  # Maintain alphabetical order

#     # If topological order contains all tasks, no cycle was detected
#     if len(topological_order) == len(in_degree):
#         return topological_order
#     else:
#         return ["cycle"]

# def read_input():
#     tasks = []
#     try:
#         while True:
#             task = input().strip()
#             dependency = input().strip()
#             tasks.append((task, dependency))
#     except EOFError:
#         pass
#     return tasks

# if __name__ == "__main__":
#     tasks = read_input()
#     result = topological_sort(tasks)
#     for task in result:
#         print(task)

def topological_sort(tasks):
    # Build graph and in-degree count
    graph = {}
    in_degree = {}  # Count of incoming edges for each task

    for task, dependency in tasks:
        if task not in graph:
            graph[task] = []  # Initialize the graph
        if dependency not in graph:
            graph[dependency] = []

        graph[dependency].append(task)
        in_degree[task] = in_degree.get(task, 0) + 1  # Increment the in-degree of the task
        if dependency not in in_degree:
            in_degree[dependency] = 0

    # Initialize stack with nodes having zero in-degree
    stack = [task for task in in_degree if in_degree[task] == 0]
    stack.sort(reverse=True)  # Reverse sort to maintain alphabetical order when popping

    topological_order = []

    while stack:
        # Pop the last task (in alphabetical order) from the stack
        current = stack.pop()
        topological_order.append(current)

        # Reduce in-degree of its neighbors
        for neighbor in graph[current]:
            in_degree[neighbor] -= 1
            if in_degree[neighbor] == 0:
                stack.append(neighbor)
                # Reverse sort the stack to ensure alphabetical order
                stack.sort(reverse=True)

    # If topological order contains all tasks, no cycle was detected
    if len(topological_order) == len(in_degree):
        return topological_order
    else:
        return ["cycle"]

def read_input():
    tasks = []
    try:
        while True:
            task = input().strip()
            dependency = input().strip()
            tasks.append((task, dependency))
    except EOFError:
        pass
    return tasks

if __name__ == "__main__":
    tasks = read_input()
    result = topological_sort(tasks)
    for task in result:
        print(task)
