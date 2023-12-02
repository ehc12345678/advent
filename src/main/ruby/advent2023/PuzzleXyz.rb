class PuzzleXyz
    def read_input(filename, data)
        File.readlines(filename, chomp: true).each do |line|
            yield line, data
        end
        data
    end

    def solve_puzzle(filename:, data:, &block)
        new_data = read_input(filename, data, &block)
        self.compute_solution(new_data)
    end

    def solve_puzzle2(filename:, data:, &block)
        new_data = read_input(filename, data, &block)
        self.compute_solution2(new_data)
    end

    def main
        solution1 = self.solve_puzzle(filename: "puzzleXYZ/inputs.txt", data: Array.new) do |line, data|
            data << line.to_i
        end
        puts "Solution1: #{solution1}"

        solution2 = self.solve_puzzle2(filename: "puzzleXYZ/inputs.txt", data: Array.new) do |line, data|
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