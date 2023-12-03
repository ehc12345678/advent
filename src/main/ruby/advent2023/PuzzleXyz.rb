load 'base.rb'

class PuzzleXyz
  include Base

  def main
    solution1 = self.solve_puzzle("puzzleXYZ/inputs.txt", Array.new) do |line, data|
      data << line.to_i
    end
    puts "Solution1: #{solution1}"

    solution2 = self.solve_puzzle2("puzzleXYZ/inputs.txt", Array.new) do |line, data|
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

p = PuzzleXyz.new
p.main