load 'base.rb'

module Puzzle9
  class Puzzle
    include Base

    DEBUG = false

    def main
      solution1 = self.solve_puzzle("puzzle9/inputs.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle9/inputs.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      data.add_sensor_history(line)
    end

    def compute_solution(data)
      diffs = data.sensor_histories.map {|history| pyramid_diff(history, true)}
      diffs.sum {|diff| diff.first.last }
    end

    def compute_solution2(data)
      diffs = data.sensor_histories.map {|history| pyramid_diff(history, false)}
      diffs.sum {|diff| diff.first.first }
    end

    def pyramid_diff(sensor_history, add_to_end, debug = DEBUG)
      working = sensor_history
      ret = [working]

      puts "\n\nFinding pyr: #{sensor_history}" if debug

      # This gives all the diffs
      while working.any? {|h| h != 0}
        diff = (0..working.size - 2).map do |n|
          working[n+1] - working[n]
        end
        ret << diff
        working = diff
      end

      puts "Pyr before filling last: #{ret}" if debug

      working << 0            
      for n in (ret.size - 2).downto(0)
        if add_to_end
          ret[n] << (ret[n + 1].last + ret[n].last)
        else
          ret[n] = [ret[n].first - ret[n + 1].first] + ret[n]
        end
      end

      puts "Final #{ret}" if debug
      ret
    end
  end

  class Data
    attr_reader :sensor_histories

    def initialize
      @sensor_histories = []
    end

    def add_sensor_history(str)
      @sensor_histories << str.split.map { |s| s.to_i }
    end
  end
end

p = Puzzle9::Puzzle.new
p.main