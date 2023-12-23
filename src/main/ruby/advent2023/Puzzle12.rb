load 'base.rb'

module Puzzle12
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle12/inputs.txt", Array.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle12/inputs.txt", Array.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      data << ConditionRecord.from_str(line)
    end

    def compute_solution(data)
      index = 0 
      sum = 0
      data.each do |record|
        possible_reg = record.springs_to_reg
        simple_reg = record.springs_to_simple_reg
        sum += record.find_matches(record.pattern, possible_reg, simple_reg)
        index += 1
        puts "#{index} of #{data.size}: #{sum}" 
      end
      sum
    end

    def compute_solution2(data)
      data.sum do |record|
        unfold = record.unfold
        possible_reg = unfold.springs_to_reg
        simple_reg = unfold.springs_to_simple_reg
        # puts "#{unfold.pattern} #{possible_reg} #{simple_reg}"
        unfold.find_matches(unfold.pattern, possible_reg, simple_reg)                
      end
    end
  end

  class ConditionRecord
    attr_reader :pattern
    attr_reader :springs

    def self.from_str(str) 
      (pattern, springs) = str.split(" ")

      # regex will be so much easier without special characters
      pattern = pattern.gsub(".","d").gsub("?","q").gsub("#","n")
      ConditionRecord.new(pattern, springs.split(",").map {|s| s.to_i})
    end

    def initialize(pattern, springs)
      @pattern = pattern
      @springs = springs
    end

    def unfold
      unfold_pattern = (0...5).map {|m| pattern}.join('q')
      unfold_springs = []
      5.times do
        unfold_springs.concat(springs)
      end
      ConditionRecord.new(unfold_pattern, unfold_springs)
    end

    def springs_to_reg
      regex_str = springs.map do |num|
        num_signs = (0...num).map {|m| '[nq]'}.join('')
        "(#{num_signs})"
      end.join("[dq]+")
      Regexp.new(regex_str)
    end

    def springs_to_simple_reg
      regex_str = springs.map do |num|
        num_signs = (0...num).map {|m| 'n'}.join('')
        "(#{num_signs})"
      end.join("d+")
      regex_str = "^d*#{regex_str}d*$"
      Regexp.new(regex_str)
    end

    def find_matches(str, possible_reg, simple_reg)
      index = str.index('q')
      if index.nil?
        simple_reg.match(str).nil? ? 0 : 1
      elsif possible_reg.match(str).nil?
        0
      else
        dot_string = str.dup
        dot_string[index] = "d"
        num_string = str.dup
        num_string[index] = "n"

        find_matches(dot_string, possible_reg, simple_reg) + find_matches(num_string, possible_reg, simple_reg)
      end
    end

    def to_s
      "#{pattern} #{springs.join(',')}"
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