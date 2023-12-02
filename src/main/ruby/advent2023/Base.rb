module Base
    def read_input(filename:, data:)
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

    def compute_solution
        nil
    end

    def compute_solution2
        nil
    end
end