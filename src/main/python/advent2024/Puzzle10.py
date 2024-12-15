from Base import Base
from typing import Tuple

Point = Tuple[int, int]
Path = list[Point]

class Grid:
  def __init__(self):
    self.data = list[list[int]]()

  def num_rows(self):
    return len(self.data)

  def num_cols(self):
    return len(self.data[0])

  def add_row(self, row: str):
    self.data.append(list(map(int, row)))

  def value(self, pos: Point) -> int:
    return self.data[pos[0]][pos[1]]

  def neighbors(self, pos: Point) -> list[Point]:
    deltas = [(1, 0), (-1, 0), (0, 1), (0, -1)]
    positions = list[Point](map(lambda x: (x[0] + pos[0], x[1] + pos[1]), deltas))
    return list(filter(lambda x: x[0] in range(self.num_rows()) and x[1] in range(self.num_cols()), positions))

MyDataType = Grid

class Puzzle(Base[MyDataType, int, int]):
    def main(self):
      solution1: int = self.solve_puzzle("puzzle10/inputs.txt", MyDataType())  # type: ignore
      print("Solution1: {}".format(solution1))

      solution2: int = self.solve_puzzle2("puzzle10/inputs.txt", MyDataType()) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_one_line(self, line: str, data: MyDataType):
      data.add_row(line)

    def compute_solution(self, data: MyDataType) -> int:
      ret = 0
      for row in range(data.num_rows()):
        for col in range(data.num_cols()):
          pos = row, col
          value = data.value(pos)
          if value == 0:
            ret += self.calculate_paths(pos, data)
      return ret

    def compute_solution2(self, data: MyDataType):
      ret = 0
      for row in range(data.num_rows()):
        for col in range(data.num_cols()):
          pos = row, col
          value = data.value(pos)
          if value == 0:
            ret += self.calculate_paths(pos, data, False)
      return ret

    def calculate_paths(self, pos: Point, data: MyDataType, distinct_end_points: bool = True) -> int:
      paths = list[Path]()
      path = Path()
      path.append(pos)
      paths.append(path)

      possible_end_points = set[Point]()
      distinct_paths = 0
      while len(paths) > 0:
        current = paths.pop()
        last_pos = current[-1]
        if data.value(last_pos) == 9:
          possible_end_points.add(last_pos)
          distinct_paths += 1
        else:
          neighbors = data.neighbors(last_pos)
          for neighbor in neighbors:
            if data.value(neighbor) == data.value(last_pos) + 1:
              new_path = current.copy()
              new_path.append(neighbor)
              paths.append(new_path)
      if distinct_end_points:
        return len(possible_end_points)
      return distinct_paths
    
puzzle: Puzzle = Puzzle()
puzzle.main()