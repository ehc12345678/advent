load 'base.rb'
require 'Set'

module Puzzle19
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle19/inputs.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle19/inputsTest.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      if line.empty?
        data.reached_tuples = true
      elsif data.reached_tuples
        data.add_tuple(line)
      else
        data.add_function(line)
      end
    end

    def compute_solution(data)
      data.tuples.sum do |tuple|
        data.part_destination(tuple) == "A" ? tuple.values.sum : 0
      end
    end

    def compute_solution2(data)
      tup_ranges = data.dft_solution_2
      data.combine_range_tuples(tup_ranges).sum do |tup_range|
        puts "Tup Range #{tup_range}"
        tup_range.values.map { |r| r.size }.inject(:*)
      end

      # # this extracts out each part "x","m","a","s" as the corresponding arrays of ranges
      # tup_ranges.keys.map do |key|
      #   tup_ranges.map {|tuple| tuple[key]}

      # # this makes sure the ranges do not overlap, as that will inflate the number  
      # end.map do |range_array|
      #   non_overlapping_ranges(range_array)

      # # we can add these together because of boolean algebra (x1 & y1 & z1) || (x2 & y2 & z2)   
      # end.map do |range|
      #   range.sum {|range| range.size}

      # # finally, we can multiply them to get our combination size  
      # end.inject(:*)
    end
  end

  class Data
    attr_reader :functions
    attr_reader :tuples
    attr_accessor :reached_tuples

    def initialize
      @functions = Hash.new
      @tuples = []
      @reached_tuples = false
    end

    def part_destination(tuple)
      current = "in"
      while (current != "A" and current != "R")
        current = functions[current].find_next_branch(tuple)
      end
      current
    end

    def add_function(str) 
      function = Function.from_str(str)
      @functions[function.label] = function
    end

    def add_tuple(str) 
      tuple = str[1..-2].split(",").map do |part|
        part.split("=")
      end.map {|a,b| [a, b.to_i]}.to_h
      @tuples << tuple
    end

    def dft_solution_2
      first = ["in", {"x"=>(0..4000), "m"=>(0..4000), "a"=>(0..4000), "s"=>(0..4000)}]
      stack = [first]
      solutions = []
      until stack.empty?
        (current, tuple) = stack.pop
        f = functions[current]

        f.find_next_branches(tuple).each do |next_item|
          (next_label, next_tuple) = next_item
          if next_label == "A"
            solutions << next_tuple
          elsif next_label != "R"
            stack << next_item
          end
        end
      end
      solutions.each {|s| puts s}
      solutions
    end

    def combine_range_tuples(tup_ranges)
      ret = Set.new
      ret << tup_ranges.first

      tup_ranges.each do |tuple|
        working = Set.new
        ret.each do |sub|
          merged = merge_range_tuples(tuple, sub)
          # puts "Merges #{merged}"
          working += merged
        end
        ret = working
      end
      ret
    end

# (x=0..21 and y=5..24) or 
# (x=6..16 and y=9..27)
#
# (x=0..6 or x=6..16 or x=16..21) and (y=5..9 or y=9..24)
# (x=0..6 and y=5..9) or
# (x=0..6 and y=9..24) or
# (x=6..16 and y=5..9) or
# (x=6..16 and y=9..24) or

# (x=6..16) and (y=9..24 or y=24..27)
# (x=6..16 and y=9..24) or
# (x=6..16 and y=24..27) or
# 
    def merge_range_tuples(tup_range1, tup_range2)
      return [tup_range1] if tup_range1.eql?(tup_range2)

      ret = Set.new
      tup_range_map = {}

      # combine overlapping 
      if tup_range1.count {|k,v| v == tup_range2[k]} >= 3
        (over_key, over_range) = tup_range1.find {|k,v| v.first == tup_range2[k].last}
        unless over_key.nil?
           return tup_range1.merge({over_key => tup_range2[over_key].first...over_range.last})
        end
        (over_key, over_range) = tup_range1.find {|k,v| v.last == tup_range2[k].first}
        unless over_key.nil?
          return tup_range2.merge({over_key => over_range.first...tup_range2[over_key].last})
        end
      end

      # puts "Merging #{tup_range1} #{tup_range2}"
      tup_range1.each do |k1, v1|
        v2 = tup_range2[k1]
        if v1.first > v2.first
          (v1, v2) = [v2, v1]
        end
        tup_range_map[k1] = [
          (v1.first ... v2.first),
          (v2.first ... [v1.last, v2.last].min),
          [v1.last, v2.last].min ... [v1.last, v2.last].max
        ].filter {|r| r.size > 0}
      end
      # puts "Map #{tup_range_map}"
      permute_to_tuples(tup_range_map)
    end

    def permute_to_tuples(tup_range_map)
      ret = []
      tup_range_map["x"].each do |x|
        tup_range_map["m"].each do |m|
          tup_range_map["a"].each do |a|
            tup_range_map["s"].each do |s|
              ret << {"x"=>x, "m"=>m, "a"=>a, "s"=>s}
            end
          end
        end
      end
      ret
    end

  end

  class Function
    attr_reader :label
    attr_reader :branches

    def self.from_str(str)
      label = str[0...str.index('{')]
      branches = str[str.index('{')+1..-2].split(",").map do |part|
        Branch.from_str(part)
      end
      Function.new(label, branches)
    end

    def initialize(label, branches)
      @label = label
      @branches = branches
    end

    def find_next_branch(tuple)
      branch = branches.find do |b|
        b.eval(tuple)
      end
      branch.dest
    end

    def find_next_branches(tup_range)
      ret = []
      branches.each do |b|
        # puts "Looking at #{b.lhs}#{b.op}#{b.rhs}:#{b.dest}"
        b_eval = b.eval_against_range(tup_range)
        ret << [b_eval[:dest], b_eval[:positive]] unless b_eval[:positive].size == 0
        tup_range = b_eval[:negative]
      end
      ret
    end
  end

  class Branch
    attr_reader :lhs
    attr_reader :op
    attr_reader :rhs
    attr_reader :dest

    def self.from_str(str)
      if str.index(':').nil?
        Branch.new(nil, nil, nil, str)
      else
        args = /(.*)([><])(.*):(.*)/.match(str).captures
        Branch.new(*args)
      end
    end

    def initialize(lhs, op, rhs, dest)
      @lhs = lhs
      @op = op
      @rhs = rhs.to_i
      @dest = dest
    end

    def eval(tuple)
      if op.nil?
        true
      elsif op == '>'
        tuple[lhs] > rhs
      elsif op == '<'
        tuple[lhs] < rhs
      end
    end

    def eval_against_range(tup_range)
      # puts "  tuple: #{tup_range} with #{lhs}#{op}#{rhs}:#{dest}"
      range = tup_range[lhs]
      if op.nil?
        positive = range.clone
        negative = (0..0)
      elsif op == '>'
        positive = (rhs+1...range.last)
        negative = (range.first...rhs)
      elsif op == '<'
        positive = (range.first..rhs)
        negative = (rhs...range.last)
      end

      pos = lhs.nil? ? {} : { lhs => positive }
      neg = lhs.nil? ? {} : { lhs => negative }
      ret = {
        positive: tup_range.merge(pos),
        negative: tup_range.merge(neg),
        dest: dest
      }
      # puts " pos: #{ret[:positive]}"
      # puts " neg: #{ret[:negative]}"
      ret
    end
  end
end

p = Puzzle19::Puzzle.new
p.main