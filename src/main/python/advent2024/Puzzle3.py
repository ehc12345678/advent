from Base import Base
import re

MyDataType = list[str]

class Puzzle(Base[MyDataType, int, int]):
    def main(self):
      solution1: int = self.solve_puzzle("puzzle3/inputs.txt", [])  # type: ignore
      print("Solution1: {}".format(solution1))

      solution2: int = self.solve_puzzle2("puzzle3/inputs.txt", []) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_one_line(self, line: str, data: MyDataType):
      regex = re.compile("(mul\(\d+,\d+\)|do\(\)|don't\(\))")
      all_matches = regex.findall(line)
      data.extend(all_matches)

    def compute_solution(self, data: MyDataType) -> int:
      regex = re.compile("mul\((\d+),(\d+)\)")
      result = 0
      for element in data:
          match = regex.search(element)
          if match:
            result = result + (int(match.group(1)) * int(match.group(2)))
      return result

    def compute_solution2(self, data: MyDataType):
      regex = re.compile("mul\((\d+),(\d+)\)")
      result = 0
      do_flag = True
      for element in data:
          if element == "don't()":
              do_flag = False
          elif element == "do()":
              do_flag = True
          else:
              match = regex.search(element)
              if match and do_flag:
                result = result + (int(match.group(1)) * int(match.group(2)))
      return result
    
puzzle: Puzzle = Puzzle()
puzzle.main()