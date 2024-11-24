import abc
from typing import Any
from collections.abc import Callable

ReadLineFunc = Callable[..., Any]

class Base:
  def read_input(self, filename: str, data: Any, read_func: ReadLineFunc) -> Any:
    with open(filename) as file:
      for line in file:
        read_func(line.strip(), data)
    return data

  def read_one_line(self, line: str, data: Any):
     print("Read one line")

  def read_one_line2(self, line: str, data: Any):
     self.read_one_line(line, data)

  def solve_puzzle(self, filename: str, data: Any) -> Any:
    newData: object = self.read_input(filename, data, self.read_one_line)
    return self.compute_solution(newData)

  def solve_puzzle_2(self, filename: str, data: Any) -> Any:
    newData: object = self.read_input(filename, data, self.read_one_line2)
    return self.compute_solution2(newData)

  @abc.abstractmethod
  def compute_solution(self, data: Any) -> Any:
    pass

  @abc.abstractmethod
  def compute_solution2(self, data: Any) -> Any:
    pass

# from typing import TypeVar
# DataType = TypeVar("DataType")
# SolutionType = TypeVar("SolutionType")
# SolutionType2 = TypeVar("SolutionType2")

# class Base[DataType, SolutionType, SolutionType2]:
#   def __init__(self):
#     pass

#   def readInput(self, filename: str, data: DataType) -> DataType:
#     with open(filename) as file:
#       for line in file:
#         self.readOneLine(line.strip, data) # type: ignore
#     return data

#   def readOneLine(self, line: str, data: DataType): 
#      print("Read one line")

#   def readOneLine2(self, line: str, data: DataType): 
#      self.readOneLine(line, data)

#   def solvePuzzle(self, filename: str, data: DataType) -> SolutionType:
#     newData: object = self.readInput(filename, data)
#     return self.computeSolution(newData)

#   def solvePuzzle2(self, filename: str, data: DataType) -> SolutionType2: 
#     newData: object = self.readInput(filename, data)
#     return self.computeSolution2(newData)

#   @abc.abstractmethod
#   def computeSolution(self, data: DataType) -> SolutionType:
#     pass

#   @abc.abstractmethod
#   def computeSolution2(self, data: DataType) -> SolutionType2:
#     pass

  