from Base import Base
from typing import Self

class Node:
  def __init__(self, key: int):
    self.key = key
    self.child_nodes = {}

  def children(self) -> Self:
    return self.child_nodes.values()

  def add_child(self, child: int) -> Self:
    node = Node(child)
    self.child_nodes[child] = node
    return node

  def remove_child(self, child: int):
    if child in self.child_nodes:
      del self.child_nodes[child]

  def child(self, key: int) -> Self | None:
    return self.child_nodes.get(key, None)

  def __str__(self):
    return str(self.key) + ": " + str(map(lambda x: x.key, self.children))

class Graph:
  def __init__(self):
    self.root = Node(0)
    self.nodes = {}

  def add_child_to(self, key: int, child: int):
    node = self.nodes.get(key, None)
    root_child = self.root.child(child)

    if root_child is not None:
      self.root.remove_child(child)
    if node is None:
      node = self.root.add_child(key)

    self.nodes[key] = node
    node.add_child(child)

  def index_nodes(self) -> dict[int, int]:
    indexed = dict[int, int]()
    bfs_queue = [self.root]
    depth_queue = [0]
    while len(bfs_queue) > 0:
      f = bfs_queue.pop()
      depth = depth_queue.pop()
      children = f.children()
      for node in children:
        bfs_queue.append(node)
        depth_queue.append(depth + 1)
        indexed[node.key] = depth
    return indexed

  def __str__(self):
    return str(self.root)

class MyDataType:
  def __init__(self):
    self.path_dict = Graph()
    self.page_updates = []

class Puzzle(Base[MyDataType, int, int]):
  def __init__(self):
    self.first_part_input = True

  def main(self):
    solution1: int = self.solve_puzzle("puzzle5/inputsTest.txt", MyDataType())  # type: ignore
    print("Solution1: {}".format(solution1))

    self.first_part_input = True
    solution2: int = self.solve_puzzle2("puzzle5/inputsTest.txt", MyDataType())  # type: ignore
    print("Solution2: {}".format(solution2))

  def read_one_line(self, line: str, data: MyDataType):
    if self.first_part_input:
      if line == "":
        self.first_part_input = False
      else:
        parts = line.split("|")
        data.path_dict.add_child_to(int(parts[0]), int(parts[1]))
    else:
      data.page_updates.append(list(map(int, line.split(","))))

  def compute_solution(self, data: MyDataType) -> int:
    ret = 0
    indexed = data.path_dict.index_nodes()
    for update in data.page_updates:
      if self.updates_in_right_order(update, indexed):
        ret += update[len(update) // 2]
    return ret

  def updates_in_right_order(self, update: list[int], indexed: dict[int, int]) -> bool:
    sorted_update = sorted(update, key=lambda x: indexed.get(x, -1))
    return sorted_update == update

  def compute_solution2(self, data: MyDataType):
    pass


puzzle: Puzzle = Puzzle()
puzzle.main()
