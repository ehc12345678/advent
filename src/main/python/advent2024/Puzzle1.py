from Base import Base

class Puzzle(Base):
    def main(self):
      solution1: int = self.solvePuzzle("puzzle1/inputs.txt", [])  # type: ignore
      print("Solution1: {}".format(solution1))

      solution2: int = self.solvePuzzle2("puzzle1/inputs.txt", []) # type: ignore
      print("Solution2: {}".format(solution2))

    def readOneLine(self, line: str, data: list[int]):
      pass

    def readOneLine2(self, line: str, data: list[int]):
      pass

    def computeSolution(self, data: list[int]) -> int:
      return 0

    def computeSolution2(self, data: list[int]):
      return 0
    
puzzle: Puzzle = Puzzle()
puzzle.main()