# Yanked nearly verbatim from https://www.geeksforgeeks.org/python-linked-list/
from typing import Generic, TypeVar

ListType = TypeVar("ListType")
class Node(Generic[ListType]):
  def __init__(self, data: ListType) -> None:
    self.data = data
    self.next = None

# Create a LinkedList class
class LinkedList(Generic[ListType]):
  def __init__(self):
    self.head = None

  # Method to add a node at the beginning of the LL
  def insert_at_begin(self, data: ListType) -> None:
    new_node = Node(data)
    new_node.next = self.head
    self.head = new_node

  # Method to add a node at any index
  # Indexing starts from 0.
  def insert_at_index(self, data: ListType, index: int):
    if index == 0:
      self.insert_at_begin(data)
      return

    position = 0
    current_node = self.head
    while current_node is not None and position + 1 != index:
      position += 1
      current_node = current_node.next

    if current_node is not None:
      new_node = Node(data)
      new_node.next = current_node.next
      current_node.next = new_node
    else:
      print("Index not present")

  # Method to add a node at the end of LL
  def insert_at_end(self, data: ListType):
    new_node = Node(data)
    if self.head is None:
      self.head = new_node
      return

    current_node = self.head
    while current_node.next:
      current_node = current_node.next

    current_node.next = new_node

  # Update node at a given position
  def update_node(self, val: ListType, index: int):
    current_node = self.head
    position = 0
    while current_node is not None and position != index:
      position += 1
      current_node = current_node.next

    if current_node is not None:
      current_node.data = val
    else:
      print("Index not present")

  # Method to remove first node of linked list
  def remove_first_node(self):
    if self.head is None:
      return

    self.head = self.head.next

  # Method to remove last node of linked list
  def remove_last_node(self):
    if self.head is None:
      return

    # If there's only one node
    if self.head.next is None:
      self.head = None
      return

    # Traverse to the second last node
    current_node = self.head
    while current_node.next and current_node.next.next:
      current_node = current_node.next

    current_node.next = None

  # Method to remove a node at a given index
  def remove_at_index(self, index: int):
    if self.head is None:
      return

    if index == 0:
      self.remove_first_node()
      return

    current_node = self.head
    position = 0
    while current_node is not None and current_node.next is not None and position + 1 != index:
      position += 1
      current_node = current_node.next

    if current_node is not None and current_node.next is not None:
      current_node.next = current_node.next.next
    else:
      print("Index not present")

  # Method to remove a node from the linked list by its data
  def remove_node(self, data: ListType):
    current_node = self.head

    # If the node to be removed is the head node
    if current_node is not None and current_node.data == data:
      self.remove_first_node()
      return

    # Traverse and find the node with the matching data
    while current_node is not None and current_node.next is not None:
      if current_node.next.data == data:
        current_node.next = current_node.next.next
        return
      current_node = current_node.next

    # If the data was not found
    print("Node with the given data not found")

  # Print the size of the linked list
  def size_of_ll(self):
    size = 0
    current_node = self.head
    while current_node:
      size += 1
      current_node = current_node.next
    return size

  # Print the linked list
  def print_ll(self):
    current_node = self.head
    while current_node:
      print(current_node.data)
      current_node = current_node.next
