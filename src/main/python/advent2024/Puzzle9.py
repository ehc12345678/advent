from Base import Base
from advent2024.DoublyLinkedList import DoublyLinkedList, Node

class NodeData:
  def __init__(self, ordinal: int, free_space: bool, block_size: int = 1):
    self.ordinal = ordinal
    self.free_space = free_space
    self.block_size = block_size

MyDataType = DoublyLinkedList[NodeData]

class Puzzle(Base[MyDataType, int, int]):
    def __init__(self):
      self.split_blocks = True

    def main(self):
      solution1: int = self.solve_puzzle("puzzle9/inputs.txt", MyDataType())  # type: ignore
      print("Solution1: {}".format(solution1))

      self.split_blocks = False
      solution2: int = self.solve_puzzle2("puzzle9/inputs.txt", MyDataType()) # type: ignore
      print("Solution2: {}".format(solution2))

    def read_one_line(self, line: str, data: MyDataType):
      ordinal = 0
      for i in range(0, len(line)):
        block_size = int(line[i])
        free_space = (i % 2) != 0
        if self.split_blocks:
          for n in range(block_size):
            data.append(NodeData(ordinal, free_space))
        else:
          if free_space:
            data.append(NodeData(0, free_space, block_size))
          else:
            data.append(NodeData(ordinal, free_space, block_size))

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
          for i in range(node.block_size):
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

    # Too high: 6388294593135
    def compute_solution2(self, data: MyDataType):
      data = self.rearrange_list_whole_blocks(data)
      ret = 0

      i = 0
      for node in data:
        for n in range(node.block_size):
          ret += i * node.ordinal
          i += 1
      return ret

    def rearrange_list_whole_blocks(self, data: MyDataType) -> MyDataType:
      back = data.tail
      while back is not None:
        back_prev = back.prev
        if not back.value.free_space:
          first_free = self.find_free_space(data, back, back.value.free_space)
          if first_free:
            remaining_space = first_free.value.block_size - back.value.block_size
            data.delete_node(back)
            data.insert_after_node(back_prev, Node(NodeData(0, True, back.value.block_size)))
            data.insert_after_node(first_free.prev, back)
            if remaining_space > 0:
              first_free.value.block_size = remaining_space
            else:
              data.delete_node(first_free)

        back = back_prev
      return data

    def find_free_space(self, data: MyDataType, back: Node[NodeData], size: int) -> Node[NodeData] | None:
      first_free_space = data.head
      while first_free_space is not None and first_free_space != back:
        remaining_space = first_free_space.value.block_size - back.value.block_size
        if first_free_space.value.free_space and remaining_space >= 0:
          return first_free_space
        first_free_space = first_free_space.next
      return None

puzzle: Puzzle = Puzzle()
puzzle.main()