load 'base.rb'

module Puzzle15
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle15/inputs.txt", Array.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle15/inputs.txt", Array.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      line.split(",").each do |str| 
        data << str
      end
    end

    def compute_solution(data)
      data.sum {|str| weird_hash(str)}
    end

    def compute_solution2(data)
      buckets = []
      256.times {
        buckets << Array.new
      }

      data.each do |str|
        run_step(str, buckets)
      end

      focusing_power(buckets)
    end

    def weird_hash(str)
      current_value = 0
      str.chars.each do |ch|
        current_value += ch.ord
        current_value *= 17
        current_value = current_value % 256
      end
      current_value
    end

    def run_step(str, buckets)
      if str.include?("=")
        (label, focal_length) = str.split("=")
      else
        label = str[0..-2]
      end
      hash = weird_hash(label)

      bucket = buckets[hash]
      index = bucket.find_index {|i| i[0] == label}

      unless focal_length.nil?
        if index.nil?
          bucket << [label, focal_length]
        else
          bucket[index][1] = focal_length
        end
      else
        bucket.delete_at(index) unless index.nil?
      end
    end

    def focusing_power(buckets)
      count = 0
      buckets.each_with_index do |bucket, box_num|
        bucket.each_with_index do |slot, slot_num|
          count += (box_num + 1) * (slot_num + 1) * slot[1].to_i
        end
      end
      count
    end
  end
end

p = Puzzle15::Puzzle.new
p.main