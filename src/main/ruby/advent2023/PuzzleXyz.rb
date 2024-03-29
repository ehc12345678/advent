load 'base.rb'

module PuzzleXyz
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzleXYZ/inputs.txt", Array.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzleXYZ/inputs.txt", Array.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      data << line.to_i
    end

    def compute_solution(data)
      data.sum
    end

    def compute_solution2(data)
      data.sum + 5
    end
  end
end

p = PuzzleXyz::Puzzle.new
p.main