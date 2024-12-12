from Base import Base
from advent2024.DoublyLinkedList import DoublyLinkedList

class NodeData:
  def __init__(self, ordinal: int, free_space: bool):
    self.ordinal = ordinal
    self.free_space = free_space

MyDataType = DoublyLinkedList[NodeData]

class Puzzle(Base[MyDataType, int, int]):
    def main(self):
      solution1: int = self.solve_puzzle("puzzle9/inputs.txt", MyDataType())  # type: ignore
      print("Solution1: {}".format(solution1))

      solution2: int = self.solve_puzzle2("puzzle9/inputs.txt", MyDataType()) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_one_line(self, line: str, data: MyDataType):
      ordinal = 0
      for i in range(0, len(line)):
        block_size = int(line[i])
        free_space = (i % 2) != 0
        for n in range(block_size):
          data.append(NodeData(ordinal, free_space))

        if not free_space:
          ordinal += 1

    def compute_solution(self, data: MyDataType) -> int:
      data = self.rearrange_list(data)
      ret = 0

      i = 0
      for node in data:
        ret += i * node.ordinal
        i += 1
      return ret

    def print_list(self, data: MyDataType) -> None:
        for node in data:
          if node.free_space:
            print(".", end='')
          else:
            print(node.ordinal, end='')
        print()

    def rearrange_list(self, data: MyDataType) -> MyDataType:
      back = data.tail
      forward = data.head
      while forward != back:
        # self.print_list(data)

        # Skip all trailing back space
        while back is not None and back.value.free_space:
          prev = back.prev
          data.delete_node(back)
          back = prev

        # Skip over all beginning values
        while forward is not None and not forward.value.free_space:
          forward = forward.next

        if back is None or forward is None:
          break

        prev_node = forward.prev
        back_prev = back.prev
        data.delete_node(forward)

        if next is None:
          break

        data.delete_node(back)
        data.insert_after_node(prev_node, back)

        forward = back
        back = back_prev

      return data

    def compute_solution2(self, data: MyDataType):
      pass
    
puzzle: Puzzle = Puzzle()
puzzle.main()