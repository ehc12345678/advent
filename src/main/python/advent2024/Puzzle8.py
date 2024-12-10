from Base import Base
from typing import Tuple

Point = Tuple[int, int]

class Grid:
  def __init__(self):
    self.num_rows = 0
    self.num_cols = 0
    self.antennae = dict[chr, list(Point)]()

  def add_antenna(self, antenna: Point, ch: chr):
    if self.antennae.get(ch, None) is None:
      self.antennae[ch] = list()
    self.antennae[ch].append(antenna)

  def get_antenna(self, point: Point) -> chr:
    for antenna in self.antennae:
      if point in self.antennae[antenna]:
        return antenna
    return ''

  def print_graph(self, antinodes: set[Point]):
    for row in range(self.num_rows):
      for col in range(self.num_cols):
        antenna = self.get_antenna((row, col))
        if antenna != '':
          print(antenna, end='')
        elif (row, col) in antinodes:
          print('#', end='')
        else:
          print('.', end='')
      print()

MyDataType = Grid

class Puzzle(Base[MyDataType, int, int]):
    def main(self):
      # 467 too high
      solution1: int = self.solve_puzzle("puzzle8/inputs.txt", MyDataType())  # type: ignore
      print("Solution1: {}".format(solution1))

      solution2: int = self.solve_puzzle2("puzzle8/inputsTest.txt", MyDataType()) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_one_line(self, line: str, data: MyDataType):
      data.num_cols = len(line)
      for col in range(data.num_cols):
        if line[col] != '.':
          pos = data.num_rows, col
          data.add_antenna(pos, line[col])
      data.num_rows += 1

    def compute_solution(self, data: MyDataType) -> int:
      antinodes = set[Point]()
      for ch in data.antennae:
        points = data.antennae[ch]
        for i in range(len(points) - 1):
          for j in range(i + 1, len(points)):
            antinodes |= self.calculate_antinodes(points[i], points[j], data)

      data.print_graph(antinodes)

      return len(antinodes)

    def compute_solution2(self, data: MyDataType):
      pass

    def calculate_antinodes(self, point1: Point, point2: Point, data: MyDataType) -> set[Point]:
      delta_row = point2[0] - point1[0]
      delta_col = point2[1] - point1[1]
      ret = set[Point]()
      for pos in [
        (point1[0] - delta_row, point1[1] - delta_col),
        (point2[0] + delta_row, point2[1] + delta_col),
      ]:
        if pos[0] in range(data.num_rows) and pos[1] in range(data.num_cols):
          ret.add(pos)
      return ret


puzzle: Puzzle = Puzzle()
puzzle.main()