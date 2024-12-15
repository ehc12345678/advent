from Base import Base

class InputLine:
  def __init__(self, goal: int, operands: list[int], running_total: int = 0):
    self.goal = goal
    self.running_total = running_total
    self.operands = operands[0:]

  @staticmethod
  def from_str(line: str) -> 'InputLine':
    split = line.split(':')
    return InputLine(int(split[0]), list(map(int, split[1].strip().split(' '))))

MyDataType = list[InputLine]

class Puzzle(Base[MyDataType, int, int]):
    def main(self):
      solution1: int = self.solve_puzzle("puzzle7/inputs.txt", [])  # type: ignore
      print("Solution1: {}".format(solution1))

      solution2: int = self.solve_puzzle2("puzzle7/inputs.txt", []) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_one_line(self, line: str, data: MyDataType):
      data.append(InputLine.from_str(line))

    def compute_solution(self, data: MyDataType) -> int:
      ret = 0
      for inputLine in data:
        if self.valid_calibration(inputLine):
          ret += inputLine.goal
      return ret

    def compute_solution2(self, data: MyDataType):
      ret = 0
      for inputLine in data:
        if self.valid_calibration(inputLine, True):
          ret += inputLine.goal
      return ret

    def valid_calibration(self, input_line: InputLine, allow_cat: bool = False) -> bool:
      top_stack = InputLine(input_line.goal, input_line.operands[1:], input_line.operands[0])
      stack = list[InputLine]()
      stack.append(top_stack)
      while len(stack) > 0:
        current_line = stack.pop()
        if current_line.running_total == input_line.goal and len(current_line.operands) == 0:
          return True
        if len(current_line.operands) == 0:
          continue

        next_operand = current_line.operands[0]
        rest_operands = current_line.operands[1:]
        add_operands = current_line.running_total + next_operand
        multi_operands = current_line.running_total * next_operand
        allow_concat_total = int(current_line.running_total.__str__() + next_operand.__str__())

        if current_line.goal >= add_operands:
          stack.append(InputLine(current_line.goal, rest_operands, add_operands))
        if current_line.goal >= multi_operands:
          stack.append(InputLine(current_line.goal, rest_operands, multi_operands))
        if allow_cat and current_line.goal >= allow_concat_total:
          stack.append(InputLine(current_line.goal, rest_operands, allow_concat_total))

      return False
    
puzzle: Puzzle = Puzzle()
puzzle.main()