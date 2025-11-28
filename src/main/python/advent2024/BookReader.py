from Base import Base
from typing import Tuple

MyDataType = list[int]

class Puzzle(Base[MyDataType, int, int]):
    def main(self):
      solution1: int = self.solve_puzzle("puzzleXYZ/inputs.txt", MyDataType())  # type: ignore
      print("Solution1: {}".format(solution1))

      solution2: int = self.solve_puzzle2("puzzleXYZ/inputs.txt", MyDataType()) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_one_line(self, line: str, data: MyDataType):
      data.append(int(line))

    def compute_solution(self, data: MyDataType) -> int:
      pass

    def compute_solution2(self, data: MyDataType):
      pass
    
puzzle: Puzzle = Puzzle()
puzzle.main()