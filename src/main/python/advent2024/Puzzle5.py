from Base import Base
from typing import Self

class Node:
  def __init__(self, key: int):
    self.key = key
    self.child_nodes = {}

  def children(self) -> Self:
    return self.child_nodes.values()

  def add_child_node(self, node: Self) -> Self:
    self.child_nodes[node.key] = node
    return node

  def remove_child(self, child: int):
    if child in self.child_nodes:
      del self.child_nodes[child]

  def child(self, key: int) -> Self | None:
    return self.child_nodes.get(key, None)

  def __str__(self):
    return str(self.key) + ": " + str(map(lambda x: x.key, self.child_nodes))

class Graph:
  def __init__(self):
    self.root = Node(0)
    self.nodes = {}

  def add_child_to(self, first_num: int, second_num: int):
    must_come_before = self.get_or_create_node(first_num)
    must_come_after = self.get_or_create_node(second_num)
    must_come_after.add_child_node(must_come_before)

  def get_or_create_node(self, key: int) -> Node:
    node = self.nodes.get(key, None)
    if node is None:
      node = Node(key)
      self.nodes[key] = node
    return node

  def find_root_children(self):
    node_keys = set(self.nodes.keys())
    all_children = set()
    for node in self.nodes.values():
      for child in node.children():
        if child.key in node_keys:
          all_children.add(child.key)
    for root in (node_keys - all_children):
      self.root.add_child_node(self.nodes[root])

  def index_nodes(self) -> dict[int, int]:
    self.find_root_children()

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
    # 10877 too high
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
    sorted_update = sorted(update, key=lambda x: -indexed.get(x, -1))
    return sorted_update == update

  def compute_solution2(self, data: MyDataType):
    pass


puzzle: Puzzle = Puzzle()
puzzle.main()
