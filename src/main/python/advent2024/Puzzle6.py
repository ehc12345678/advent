from enum import Enum

from Base import Base
from typing import Tuple, Callable, Self

Point = Tuple[int, int]

class Direction(Enum):
  UP = 1
  DOWN = 2
  LEFT = 3
  RIGHT = 4

class Guard:
  pos: Point
  direction: Direction

  def __init__(self, pos: Point, direction: Direction):
    self.pos = pos
    self.direction = direction

  def clone(self):
    return Guard(self.pos, self.direction)

  def turn_clockwise(self) -> None:
    match self.direction:
      case Direction.UP:
        self.direction = Direction.RIGHT
      case Direction.DOWN:
        self.direction = Direction.LEFT
      case Direction.LEFT:
        self.direction = Direction.UP
      case Direction.RIGHT:
        self.direction = Direction.DOWN

  def next_pos(self) -> Point | None:
    row = self.pos[0]
    col = self.pos[1]
    match self.direction:
      case Direction.UP:
        row -= 1
      case Direction.DOWN:
        row += 1
      case Direction.LEFT:
        col -= 1
      case Direction.RIGHT:
        col += 1
    return row, col

  def set_pos(self, new_pos: Point) -> None:
    self.pos = new_pos

class Grid:
  obstructions: list[Point]
  guard: Guard

  def __init__(self) -> None:
    self.obstructions = list[Point]()
    self.guard = Guard((0, 0), Direction.UP)
    self.num_rows = 0
    self.num_cols = 0

  def clone(self) -> Self:
    grid = Grid()
    grid.guard = self.guard.clone()
    grid.obstructions = self.obstructions[:]
    grid.num_rows = self.num_rows
    grid.num_cols = self.num_cols
    return grid

  def get_points(self, f: Callable[[Point], bool]) -> list[Point]:
    return list[Point](filter(f, self.obstructions))

  def add_row(self, row: str):
    self.num_cols = len(row)
    for col in range(len(row)):
      pos = (self.num_rows, col)
      if row[col] == '#':
        self.obstructions.append(pos)
      elif row[col] == '^':
        self.guard = Guard(pos, Direction.UP)
    self.num_rows += 1

  def next_pos(self) -> Point | None:
    new_pos = self.guard.next_pos()
    if new_pos[0] not in range(self.num_rows) or new_pos[1] not in range(self.num_cols):
      return None
    return new_pos

  def add_obstruction(self, pos: Point) -> Self:
    new_grid = self.clone()
    new_grid.obstructions += [pos]
    return new_grid

MyDataType = Grid

class Puzzle(Base[MyDataType, int, int]):
  def main(self):
    solution1: int = self.solve_puzzle("puzzle6/inputs.txt", MyDataType())  # type: ignore
    print("Solution1: {}".format(solution1))

    solution2: int = self.solve_puzzle2("puzzle6/inputs.txt", MyDataType())  # type: ignore
    print("Solution2: {}".format(solution2))

  def read_one_line(self, line: str, data: MyDataType):
    data.add_row(line)

  def compute_solution(self, data: MyDataType) -> int:
    seen = self.get_path(data)
    return len(seen)

  def get_path(selfself, data: MyDataType) -> set[Point]:
    clone = data.clone()
    seen = set[Point]()
    pos = clone.guard.pos
    while pos is not None:
      seen.add(pos)
      next_pos = clone.next_pos()
      if next_pos in clone.obstructions:
        clone.guard.turn_clockwise()
        pos = clone.next_pos()
      else:
        pos = next_pos
      clone.guard.set_pos(pos)
    return seen

  def compute_solution2(self, data: MyDataType):
    ret = 0
    seen = self.get_path(data)
    seen.remove(data.guard.pos)
    for pos in seen:
      new_grid = data.add_obstruction(pos)
      if self.find_end(new_grid) is not None:
        ret += 1
    return ret

  def find_end(self, data: MyDataType) -> Point | None:
    seen = set[Tuple[Point, Direction]]()
    pos_dir = data.guard.pos, data.guard.direction
    while pos_dir[0] is not None and pos_dir not in seen:
      seen.add(pos_dir)
      next_pos = data.next_pos()
      if next_pos in data.obstructions:
        data.guard.turn_clockwise()
        pos = data.next_pos()
      else:
        pos = next_pos
      pos_dir = pos, data.guard.direction
      data.guard.set_pos(pos)
    return pos_dir[0]

puzzle: Puzzle = Puzzle()
puzzle.main()
