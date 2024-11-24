from Base import Base
MyDataType = list[int]

class Puzzle(Base[MyDataType, int, int]):
    def main(self):
      solution1: int = self.solve_puzzle("puzzle1/inputs.txt", [])  # type: ignore
      print("Solution1: {}".format(solution1))

      solution2: int = self.solve_puzzle2("puzzle1/inputs.txt", []) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_one_line(self, line: str, data: list[int]):
      data.append(int(line))

    def compute_solution(self, data: list[int]) -> int:
      return sum(data)

    def compute_solution2(self, data: list[int]):
      return sum(data)
    
puzzle: Puzzle = Puzzle()
puzzle.main()