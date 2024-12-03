from Base import Base
from typing import Tuple

Entry = Tuple[int, int]
MyDataType = list[Entry]

class Puzzle(Base[MyDataType, int, int]):
    def main(self):
      solution1: int = self.solve_puzzle("puzzle1/inputs.txt", [])  # type: ignore
      print("Solution1: {}".format(solution1))

      solution2: int = self.solve_puzzle2("puzzle1/inputs.txt", []) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_one_line(self, line: str, data: MyDataType):
      split = line.split()
      entry = int(split[0]), int(split[1])
      data.append(entry)

    def compute_solution(self, data: MyDataType) -> int:
      sorted_first = sorted(map(lambda x: x[0], data))
      sorted_second = sorted(map(lambda x: x[1], data))
      diff = 0
      for i in range(len(sorted_first)):
          diff += abs(sorted_first[i] - sorted_second[i])
      return diff

    def compute_solution2(self, data: MyDataType):
      mapped_second = {}
      for element in data:
          key = element[1]
          if key not in mapped_second:
              mapped_second[key] = 0
          mapped_second[key] += 1
      answer = 0
      for element in data:
          key = element[0]
          if key in mapped_second:
              answer += mapped_second[key] * key
      return answer
    
puzzle: Puzzle = Puzzle()
puzzle.main()