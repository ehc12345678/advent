load 'base.rb'

load 'base.rb'

module Puzzle6
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle6/inputs.txt", Array.new) do |line, data|
        parse_data(line, data)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle6/inputs.txt", Array.new) do |line, data|
        parse_data(line, data)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_data(line, data)
      if line.start_with?("Time:")
        line["Time:".length+1..].split(" ").each do |s| 
          data << Race.new(s.to_i)
        end
      elsif line.start_with?("Distance:")
        line["Distance:".length+1..].split(" ").each_with_index do |s,i|
          data[i].distance = s.to_i
        end
      end
    end
  
    def compute_solution(data)
      data.map {|race| race.find_num_races_over_record }.reduce(1, :*)
    end

    def compute_solution2(data)
      new_data = []
      race = Race.new(data.reduce("") {|total, race| total + race.time.to_s }.to_i)
      race.distance = data.reduce("") {|total, race| total + race.distance.to_s }.to_i
      new_data << race
      compute_solution(new_data)
    end
  end

  class Race
    attr_reader :time
    attr_accessor :distance 

    def initialize(time)
      @time = time
    end

    def find_num_races_over_record
      max_index = find_max_index(0, time)
      return 0 unless time_breaks_record?(max_index) # if we didn't break the record, save time

      first_record_breaker_index = find_transition_point(0, max_index)
      last_record_breaker_index = find_transition_point(max_index, time)
      
      delta1 = max_index - first_record_breaker_index
      delta2 = last_record_breaker_index - max_index - 1
      delta1 + delta2 + 1
    end

    def find_transition_point(low, high)  
      low_broke_record = time_breaks_record?(low)
      l = low
      h = high
      index = -1   
        
      while l <= h 
        mid = (l + h) / 2
  
        mid_broke_record = time_breaks_record?(mid)
        if low_broke_record != mid_broke_record 
          index = mid 
          h = mid - 1
        else
          l = mid + 1
        end
      end
  
      index
    end 

    def time_breaks_record?(time)
      compute_distance_with_hold_time(time) > distance
    end

    def find_max_index(low, high)
      if high - low <= 3
        calc_low = compute_distance_with_hold_time(low)
        calc_high = compute_distance_with_hold_time(high)
        calc_low > calc_high ? low : high
      else
        low = (low * 2 + high) / 3
        high = (low + high * 2) / 3
        if compute_distance_with_hold_time(low) > compute_distance_with_hold_time(high)
          find_max_index(low, high - 1)
        else
          find_max_index(low + 1, high)
        end
      end
    end      

    def compute_distance_with_hold_time(t)
      travel_time = time - t
      t * travel_time
    end

    def to_s
      "Time: #{time}, Distance: #{distance}"
    end
  end
end

p = Puzzle6::Puzzle.new
p.main
