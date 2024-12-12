from __future__ import annotations
from typing import Generic, TypeVar, Optional, Iterator

T = TypeVar('T')  # Generic type for elements in the doubly linked list

class Node(Generic[T]):
    def __init__(self, value: T, prev: Optional[Node[T]] = None, next: Optional[Node[T]] = None):
        self.value: T = value
        self.prev: Optional[Node[T]] = prev
        self.next: Optional[Node[T]] = next

class DoublyLinkedList(Generic[T]):
    def __init__(self):
        self.head: Optional[Node[T]] = None
        self.tail: Optional[Node[T]] = None

    def append(self, value: T) -> None:
        """Adds a new node to the end of the list."""
        new_node = Node(value, prev=self.tail)
        if self.tail:
            self.tail.next = new_node
        self.tail = new_node
        if not self.head:
            self.head = new_node

    def prepend(self, value: T) -> None:
        """Adds a new node to the start of the list."""
        new_node = Node(value, next=self.head)
        if self.head:
            self.head.prev = new_node
        self.head = new_node
        if not self.tail:
            self.tail = new_node

    def delete_node(self, target_node: Node[T]) -> None:
        if target_node.prev:
            target_node.prev.next = target_node.next
        else:
            self.head = target_node.next

        if target_node.next:
            target_node.next.prev = target_node.prev
        else:
            self.tail = target_node.prev

        # Clear the target_node's references
        target_node.prev = None
        target_node.next = None

    def insert_after_node(self, target_node: Node[T], new_node: Node[T]):
        new_node.prev = target_node
        new_node.next = target_node.next

        if target_node.next:
            target_node.next.prev = new_node
        target_node.next = new_node

        if target_node == self.tail:
            self.tail = new_node

    def __iter__(self) -> Iterator[T]:
        """Allows iteration over the list."""
        current = self.head
        while current:
            yield current.value
            current = current.next

    def __repr__(self) -> str:
        """Returns a string representation of the list."""
        values = [str(value) for value in self]
        return " <-> ".join(values)
