import abc
from typing import TypeVar, Generic
from collections.abc import Callable

DataType = TypeVar("DataType")
SolutionType = TypeVar("SolutionType")
SolutionType2 = TypeVar("SolutionType2")

class Base(Generic[DataType, SolutionType, SolutionType2]):
  ReadLineFunc = Callable[..., DataType]

  def read_input(self, filename: str, data: DataType, read_func: ReadLineFunc) -> DataType:
    with open(filename) as file:
      for line in file:
        read_func(line.strip(), data) # type: ignore
    return data

  def read_one_line(self, line: str, data: DataType):
     print("Read one line")

  def read_one_line2(self, line: str, data: DataType):
     self.read_one_line(line, data)

  def solve_puzzle(self, filename: str, data: DataType) -> SolutionType:
    new_data: object = self.read_input(filename, data, self.read_one_line)
    return self.compute_solution(new_data)

  def solve_puzzle2(self, filename: str, data: DataType) -> SolutionType2:
    new_data: object = self.read_input(filename, data, self.read_one_line2)
    return self.compute_solution2(new_data)

  @abc.abstractmethod
  def compute_solution(self, data: DataType) -> SolutionType:
    pass

  @abc.abstractmethod
  def compute_solution2(self, data: DataType) -> SolutionType2:
    pass

  