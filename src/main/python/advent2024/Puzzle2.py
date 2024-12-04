from Base import Base

Report = list[int]
MyDataType = list[Report]

class Puzzle(Base[MyDataType, int, int]):
    def main(self):
      solution1: int = self.solve_puzzle("puzzle2/inputs.txt", [])  # type: ignore
      print("Solution1: {}".format(solution1))

      # 417 too high
      solution2: int = self.solve_puzzle2("puzzle2/inputs.txt", []) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_one_line(self, line: str, data: MyDataType):
      report = list(map(int, line.split()))
      data.append(report)

    def compute_solution(self, data: MyDataType) -> int:
      safe = 0
      for report in data:
          if self.report_is_safe(report):
              safe += 1
      return safe

    def compute_solution2(self, data: MyDataType):
      safe = 0
      for report in data:
          bad_index = self.report_bad_index(report)
          if bad_index < 0 or self.safe_if_remove_around(bad_index, report):
              safe += 1
      return safe

    def report_bad_index(self, report: Report) -> int:
        increasing = report[0] < report[1]
        i = 0
        while i < len(report) - 1:
            if not self.neighbors_are_safe(report[i], report[i + 1], increasing):
                return i
            i += 1
        return -1

    def report_is_safe(self, report: Report) -> int:
        return self.report_bad_index(report) < 0

    def safe_if_remove_around(self, bad_index: int, report: Report) -> bool:
        ret = False
        if bad_index > 0:
            ret = self.report_is_safe(self.remove_element(bad_index - 1, report))
        ret = ret or self.report_is_safe(self.remove_element(bad_index, report))
        ret = ret or self.report_is_safe(self.remove_element(bad_index + 1, report))
        return ret

    def remove_element(self, index: int, report: Report) -> Report:
        return report[:index] + report[index+1:]

    @staticmethod
    def neighbors_are_safe(first: int, after: int, increasing: bool) -> bool:
        this_increasing = first < after
        diff = abs(after - first)
        return not (this_increasing != increasing or diff == 0 or diff > 3)

puzzle: Puzzle = Puzzle()
puzzle.main()