load 'base.rb'
require 'Set'

module Puzzle4
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle4/inputs.txt", Array.new) do |line, data|
        data << ScratchCard.new(line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle4/inputs.txt", Array.new) do |line, data|
        data << ScratchCard.new(line)
      end
      puts "Solution2: #{solution2}"
    end

    def compute_solution(data)
      data.sum do |card|
        matching = card.matching_numbers.size
        (matching == 0 ? 0 : 2.pow(matching - 1))
      end
    end

    def compute_solution2(data)
      map = Array.new(data.size, 1)
      data.each do |card|
        num = card.card_num
        matching = card.matching_numbers.size
        (num .. num + matching - 1).each do |n|
          map[n] = map[n] + map[num - 1]
        end
      end
      map.sum
    end
  end

  class ScratchCard
    attr_reader :card_num

    @winning_numbers = []
    @picked_numbers = []
    @card_num

    def initialize(str)
      str = str["Card ".length..]
      @card_num = str.to_i
      str = str[str.index(':')+1..]
      parts = str.split("|")
      @winning_numbers = parts[0].split(" ").map { |s| s.to_i }
      @picked_numbers = parts[1].split(" ").map { |s| s.to_i }
    end

    def matching_numbers
      winning = Set.new @winning_numbers
      @picked_numbers.filter {|n| winning.member? n}
    end
  end
end

p = Puzzle4::Puzzle.new
p.main