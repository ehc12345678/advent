from Base import Base
from collections.abc import Callable
import re

LABEL_MAP = {
  "Ti": "title",
  "Au": "author",
  "Se": "series",
  "Gn": "genre",
  "Co": "collection",
  "Nb": "notebook"
}

class BookNode:
  def __init__(self, line):
    self.children = []

    raw_indent = self.find_first_non_space(line)
    rest_line = line[raw_indent:]
    regex = r"^(- )?(.+?)\: (.*)$"
    m = re.match(regex, rest_line)

    attributes = {}
    if m is not None:
      minus = m.group(1)
      label = m.group(2)
      value = m.group(3)
      if minus is not None:
        attributes["have"] = False
        raw_indent = raw_indent + 2
      if re.match(r"^\d*$", label):
        attributes["ordinal"] = int(label)
        label = "Ti"
      if label.endswith("(hc)"):
        attributes["hard_cover"] = True
        label = label[:-4].strip()

      attributes[LABEL_MAP.get(label, label)] = value
    self.raw_indent_level = raw_indent
    self.attributes = attributes

  def merge_attributes(self, attributes):
    self.attributes = attributes | self.attributes

  def find_first_non_space(self, line):
    for i, char in enumerate(line):
      if not char.isspace():
        return i
    return 0

  def __str__(self):
    return self.attributes.__str__()

MyDataType = list[BookNode]

class Puzzle(Base[MyDataType, int, int]):
    ReadLineFunc = Callable[..., MyDataType]

    def main(self):
      solution1: int = self.solve_puzzle("books/inputs.txt", MyDataType())  # type: ignore
      print("Solution1: {}".format(solution1))

      solution2: int = self.solve_puzzle2("books/inputs.txt", MyDataType()) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_input(self, filename: str, data: MyDataType, read_func: ReadLineFunc) -> MyDataType:
      with open(filename) as file:
        for line in file:
          read_func(line, data)  # type: ignore
      return data

    def read_one_line(self, line: str, data: MyDataType):
      data.append(BookNode(line))

    def compute_solution(self, data: MyDataType) -> int:
      root = BookNode("root")
      root.raw_indent_level = -1

      all_books = []
      node_stack = [root]

      for i in range(len(data)):
         node = data[i]

         while node.raw_indent_level <= node_stack[-1].raw_indent_level:
             node_stack.pop()

         parent = node_stack[-1]
         node.merge_attributes(parent.attributes)

         if i + 1 == len(data) or node.raw_indent_level >= data[i + 1].raw_indent_level:
             all_books.append(node)
         else:
             node_stack.append(node)

      return len(all_books)

    def compute_solution2(self, data: MyDataType):
      pass
    
puzzle: Puzzle = Puzzle()
puzzle.main()