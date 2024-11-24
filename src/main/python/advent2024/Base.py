import abc
from typing import Any
from collections.abc import Callable

ReadLineFunc = Callable[..., Any]

class Base:
  def readInput(self, filename: str, data: Any, readFunc: ReadLineFunc) -> Any:
    with open(filename) as file:
      for line in file:
        readFunc(line.strip(), data)
    return data

  def readOneLine(self, line: str, data: Any): 
     print("Read one line")

  def readOneLine2(self, line: str, data: Any): 
     self.readOneLine(line, data)

  def solvePuzzle(self, filename: str, data: Any) -> Any:
    newData: object = self.readInput(filename, data, self.readOneLine)
    return self.computeSolution(newData)

  def solvePuzzle2(self, filename: str, data: Any) -> Any: 
    newData: object = self.readInput(filename, data, self.readOneLine2)
    return self.computeSolution2(newData)

  @abc.abstractmethod
  def computeSolution(self, data: Any) -> Any:
    pass

  @abc.abstractmethod
  def computeSolution2(self, data: Any) -> Any:
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

  