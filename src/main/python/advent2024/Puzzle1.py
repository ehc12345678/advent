from Base import Base

class Puzzle(Base):
    def main(self):
      solution1: int = self.solve_puzzle("puzzle1/inputs.txt", [])  # type: ignore
      print("Solution1: {}".format(solution1))

      solution2: int = self.solve_puzzle_2("puzzle1/inputs.txt", []) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_one_line(self, line: str, data: list[int]):
      pass

    def read_one_line2(self, line: str, data: list[int]):
      pass

    def compute_solution(self, data: list[int]) -> int:
      return 0

    def compute_solution2(self, data: list[int]):
      return 0
    
puzzle: Puzzle = Puzzle()
puzzle.main()