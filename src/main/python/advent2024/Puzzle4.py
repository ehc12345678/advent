from Base import Base

MyDataType = list[str]

class Puzzle(Base[MyDataType, int, int]):
    def main(self):
      solution1: int = self.solve_puzzle("puzzle4/inputs.txt", [])  # type: ignore
      print("Solution1: {}".format(solution1))

      solution2: int = self.solve_puzzle2("puzzle4/inputs.txt", []) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_one_line(self, line: str, data: MyDataType):
      data.append(line)

    def compute_solution(self, data: MyDataType) -> int:
      ret = 0
      for rowIndex in range(len(data)):
          row = data[rowIndex]
          for colIndex in range(len(row)):
              deltas = [(1, 0), (-1, 0), (0, 1), (0, -1), (1,1), (-1,-1), (1,-1), (-1, 1)]
              for delta in deltas:
                  if self.find_word(data, rowIndex, colIndex, "XMAS", delta[0], delta[1]):
                      ret += 1
      return ret

    def find_word(self, data: MyDataType, row_index: int, col_index: int, word: str, row_delta: int, col_delta: int) -> bool:
        for ch in word:
            if not self.cell(data, row_index, col_index) == ch:
                return False
            row_index += row_delta
            col_index += col_delta
        return True

    def find_words(self, data: MyDataType, row_index: int, col_index: int, words: list[str], row_delta: int, col_delta: int) -> bool:
        for word in words:
            if self.find_word(data, row_index, col_index, word, row_delta, col_delta):
                return True
        return False

    def cell(self, data: MyDataType, row_index: int, col_index: int) -> chr:
        if row_index in range(len(data)) and col_index in range(len(data[row_index])):
            return data[row_index][col_index]
        return None

    def compute_solution2(self, data: MyDataType):
      ret = 0
      for rowIndex in range(len(data)):
          row = data[rowIndex]
          for colIndex in range(len(row)):
              if (self.find_words(data, rowIndex, colIndex, ["MAS", "SAM"], 1, 1) and
                  self.find_words(data, rowIndex, colIndex + 2, ["MAS", "SAM"], 1, -1)):
                      ret += 1
      return ret
    
puzzle: Puzzle = Puzzle()
puzzle.main()