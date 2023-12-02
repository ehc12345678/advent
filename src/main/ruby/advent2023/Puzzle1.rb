class Puzzle1
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
        solution1 = self.solve_puzzle(filename: "puzzle1/inputs.txt", data: Array.new) do |line, data|
            data << num_from_str(line)
        end
        puts "Solution1: #{solution1}"

        solution2 = self.solve_puzzle2(filename: "puzzle1/inputs.txt", data: Array.new) do |line, data|
            data << num_from_str2(line)
        end
        puts "Solution2: #{solution2}"
    end

    def compute_solution(data)
        data.sum
    end

    def compute_solution2(data)
        data.sum
    end

    def num_from_str(str)
       regex = /(\d)/
       first_int = str[regex].to_i
       second_int = str[str.rindex(regex)].to_i
       (first_int * 10) + second_int
    end

    def get_all_nums(str)
      ret = []
      (0..str.length).each do |i|
        if str[i..].start_with? "one"
          ret << 1
        elsif str[i..].start_with? "two"
          ret << 2
        elsif str[i..].start_with? "three"
          ret << 3
        elsif str[i..].start_with? "four"
          ret << 4
        elsif str[i..].start_with? "five"
          ret << 5
        elsif str[i..].start_with? "six"
          ret << 6
        elsif str[i..].start_with? "seven"
          ret << 7
        elsif str[i..].start_with? "eight"
          ret << 8
        elsif str[i..].start_with? "nine"
          ret << 9
        elsif str[i].to_s.match(/\d/)
          ret << str[i].to_i
        end
      end
      ret
    end

    def num_from_str2(str)
       scans = get_all_nums(str)
       first_int = scans[0]
       second_int = scans[-1]
       ret = (first_int * 10) + second_int
       ret
    end
end

p = Puzzle1.new
p.main