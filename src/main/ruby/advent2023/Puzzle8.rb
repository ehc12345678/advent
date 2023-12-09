load 'base.rb'
require 'Set'
require 'prime'

module Puzzle8
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle8/inputs.txt", Data.new) do |line, data|
        parse_data(data, line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle8/inputs.txt", Data.new) do |line, data|
        parse_data(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def compute_solution(data)
      num_steps_to_reach(data, "AAA", "ZZZ")
    end

    def compute_solution2(data)
      start_nodes = data.nodes.filter { |key, value| key.end_with? 'A' }.map {|key, value| key}

      nodes_map = data.nodes.map do |key, value|
        node = data.node(key)
        [key, run_instructions(data, node).key]
      end.to_h

      all_zs = start_nodes.map do |start|
        find_all_z_nodes(data, start, nodes_map)
      end

      # This tells us that everything is prime.  
      #   all_primes = all_zs.map {|z| Prime.prime? z}
      #   puts "All Z Nodes: #{all_zs} #{all_primes}"
      #
      # From looking at the paths, all the nodes are distinct and they all
      # repeat at position 1, so the LCD is just the numbers multiplied
      all_zs.reduce(1, :*) * data.instructions.size
    end

    def find_all_z_nodes(data, start, nodes_map)
      path = []
      set = Set.new

      while not set.member?(start)
        set << start
        path << start
        dest = nodes_map[start]
        start = dest
      end

      path.each_with_index.find do |value, index|
        value.end_with? 'Z'
      end[1]
    end

    def num_steps_to_reach(data, src, dest) 
      count = 0
      node = data.node(src)
      while node.key != dest
        new_node = run_instructions(data, node)
        # puts "Went from #{node.key} to #{new_node.key}"
        node = new_node
        count = count + data.instructions.size
      end
      count
    end

    def run_instructions(data, src_node)
      data.instructions.each do |left_or_right|
        next_key = left_or_right == 'L' ? src_node.left : src_node.right
        src_node = data.node(next_key)
      end
      src_node
    end

    def parse_data(data, line)
      if data.instructions.nil?
        data.instructions = line.chars
      elsif line.empty?

      else
        node = Node.from_str(line)
        data.add_node(node)
      end
    end
  end

  class Data
    attr_accessor :instructions
    attr_reader :nodes

    def initialize
      @instructions = nil
      @nodes = {}
    end

    def add_node(node)
      @nodes[node.key] = node
    end

    def node(key)
      @nodes[key]
    end

    def to_s
      node_str = nodes.map {|n| n.to_s + '\n'}
      "#{instructions}\n#{node_str}"
    end
  end

  class Node
    attr_reader :key
    attr_reader :left
    attr_reader :right

    def self.from_str(str) 
      regex = /(.*) = \((.*), (.*)\)/
      match = regex.match(str)
      Node.new(match[1], match[2], match[3])
    end

    def initialize(key, left, right)
      @key = key
      @left = left
      @right = right
    end

    def to_s
      "#{key} = (#{left}, #{right})"
    end
  end
end

p = Puzzle8::Puzzle.new
p.main