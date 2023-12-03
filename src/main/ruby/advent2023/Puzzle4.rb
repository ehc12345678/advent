load 'base.rb'

module Puzzle4
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle4/inputs.txt", Array.new) do |line, data|
        data << line.to_i
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle4/inputs.txt", Array.new) do |line, data|
        data << line.to_i
      end
      puts "Solution2: #{solution2}"
    end

    def compute_solution(data)
      data.sum
    end

    def compute_solution2(data)
      data.sum + 5
    end
  end
end

p = Puzzle4::Puzzle.new
p.main