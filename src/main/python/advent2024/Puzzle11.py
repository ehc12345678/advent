from Base import Base
from typing import Tuple

MyDataType = list[int]
NumAndSteps = Tuple[int, int]

class Puzzle(Base[MyDataType, int, int]):
    def main(self):
      solution1: int = self.solve_puzzle("puzzle11/inputsTest.txt", MyDataType())  # type: ignore
      print("Solution1: {}".format(solution1))

      solution2: int = self.solve_puzzle2("puzzle11/inputsTest.txt", MyDataType()) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_one_line(self, line: str, data: MyDataType):
      data += map(int, line.split(" "))

    def compute_solution(self, data: MyDataType) -> int:
      return self.solve_for_n_steps(data, 25)

    def compute_solution2(self, data: MyDataType):
      return self.solve_for_n_steps(data, 75)

    def solve_for_n_steps(self, data: MyDataType, steps: int) -> int:
      calculated = dict[NumAndSteps, int]()
      todo = dict[NumAndSteps, list[int]]()

      to_solve = map(lambda x: (x, steps), data)
      while self.unsolved(to_solve):
        print("{}: {}", len(data), data)
        data = self.do_one_step(data)
      return len(data)

    def do_one_step(self, data: MyDataType) -> MyDataType:
      new_data = MyDataType()
      for entry in data:
        self.next_value(entry, new_data)
      return new_data

    def next_value(self, entry, new_data):
      str_entry = str(entry)
      if entry == 0:
        new_data.append(1)
      elif (len(str_entry) % 2) == 0:
        first_half = int(str_entry[0:len(str_entry) // 2])
        second_half = int(str_entry[len(str_entry) // 2:])
        new_data.append(int(first_half))
        new_data.append(int(second_half))
      else:
        new_data.append(2024 * entry)

    def unsolved(self, to_solve) -> bool:
      return not all(isinstance(ele, int) for ele in to_solve)

puzzle: Puzzle = Puzzle()
puzzle.main()