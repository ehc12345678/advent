load 'base.rb'

module Puzzle12
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle12/inputsTest.txt", Array.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle12/inputsTest.txt", Array.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      data << ConditionRecord.from_str(line)
    end

    def compute_solution(data)
      data.each do |x|
        puts x
      end
      0
    end

    def compute_solution2(data)
      0
    end
  end

  class ConditionRecord
    attr_reader :pattern
    attr_reader :springs
    attr_reader :pattern_groups

    def self.from_str(str) 
      (pattern, springs) = str.split(" ")
      ConditionRecord.new(pattern, springs.split(",").map {|s| s.to_i})
    end

    def initialize(pattern, springs)
      @pattern = pattern
      @springs = springs
      @pattern_groups = group_pattern(pattern)
    end

    def group_pattern(pattern)
      groups = pattern.chars.reduce(Array.new) do |acc, ch|
        last = acc.empty? ? ' ' : acc.last[0]
        add_group = if last != ch
          empty = [ch, 0]
          acc << empty
          empty
        else
          acc.last
        end

        add_group[1] += 1
        acc
      end
      groups.map {|ch, size| PatternGroup.new(ch, size)}
    end

    def to_s
      grps = @pattern_groups.map {|g| "(#{g.size},#{g.ch})"}.join(' ')
      "#{pattern} #{springs.join(',')} #{grps}"
    end
  end

  class PatternGroup
    attr_reader :ch
    attr_reader :size

    def initialize(ch, size)
      @ch = ch
      @size = size
    end
  end
end

p = Puzzle12::Puzzle.new
p.main