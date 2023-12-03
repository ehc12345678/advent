load 'base.rb'

module Puzzle1
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle1/inputs.txt", Array.new) do |line, data|
        data << num_from_str(line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle1/inputs.txt", Array.new) do |line, data|
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
      str_nums = /(one|two|three|four|five|six|seven|eight|nine)/
      map = {"one"=>1,"two"=>2,"three"=>3,"four"=>4,
        "five"=>5,"six"=>6,"seven"=>7,"eight"=>8,"nine"=>9}
      (0..str.length).each do |i|
        substr = str[i..]
        if substr.start_with? str_nums
          ret << map[substr.match(str_nums)[0]]
        elsif substr.start_with? /\d/
          ret << str[i].to_s.to_i
        end
      end
      ret
    end

    def num_from_str2(str)
      scans = get_all_nums(str)
      first_int = scans[0]
      second_int = scans[-1]
      (first_int * 10) + second_int
    end
  end
end

p = Puzzle1::Puzzle.new
p.main
