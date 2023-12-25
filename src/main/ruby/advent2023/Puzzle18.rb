load 'base.rb'
load 'position.rb'

module Puzzle18
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle18/inputs.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution1: #{solution1}"

      # solution2 = self.solve_puzzle2("puzzle18/inputsTest.txt", Data.new) do |line, data|
      #   parse_line(data, line)
      # end
      # puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      data.add(Instruction.from_str(line))
    end

    def compute_solution(data)
      data.follow_instructions
      # trench_holes = data.trench.holes_dug.size 
      # enclosed = data.calculate_enclosed
      # trench_holes + enclosed

      answer = data.minecraft_spread_water
      # puts data.trench.to_s
      data.trench.holes_dug.size
    end

    def compute_solution2(data)
      compute_solution(data)
    end
  end

  class Data
    attr_reader :trench

    def initialize
      @instructions = []
      @trench = Trench.new
    end

    def add(instruction)
      @instructions << instruction
    end

    def follow_instructions
      pos = Position.new(0, 0)
      @instructions.each do |instruction|
        pos = follow_instruction(pos, instruction)
      end
    end

    def follow_instruction(pos, instruction)
      instruction.num_times.times do
        @trench.dig_hole(Hole.new(pos, instruction.red, instruction.green, instruction.blue))
        pos = pos + instruction.direction
      end
      pos
    end

    def calculate_enclosed
      holes = trench.holes_dug.values.sort
      row = [holes.first]
      count = 0

      (1 ... holes.size).each do |i|
        next_hole = holes[i]
        if next_hole.row > row.last.row
          count += fill_in_contained(row) 
          row = []
        end  
        row << next_hole
      end
      count
    end

    def fill_in_contained(row_in_trench)
      count = 0
      i = 1

      # puts "#{row.first.row}: #{row.join(",")}"

      row = row_in_trench.clone
      while i < row.size
        diff = row[i].col - row[i - 1].col
        if diff > 1
          (row[i - 1].col + 1 ... row[i].col).each do |c|
            above = Position.new(row[i].row - 1, c)
            unless trench.hole(above).nil?
              count += 1
              trench.dig_hole(Hole.new(Position.new(row[i].row, c), "00", "00", "00"))
            end
          end
        end
        i += 1
      end
      count
    end

    def minecraft_spread_water
      trench.minecraft_spread_water
    end
  end

  class Instruction 
    attr_reader :num_times
    attr_reader :direction
    attr_reader :red
    attr_reader :green
    attr_reader :blue

    def self.from_str(str)
      (dir_str, num_str, color_str) = str.split(" ")
      direction = case(dir_str)
      when "R"
        EAST
      when "L"
        WEST
      when "U"
        NORTH
      when "D"
        SOUTH
      end
      num_times = num_str.to_i

      color_str = color_str[2..]
      red = color_str[0..1]
      green = color_str[2..3]
      blue = color_str[4..5]
      Instruction.new(num_times, direction, red, green, blue)
    end

    def initialize(num_times, direction, red, green, blue) 
      @num_times = num_times
      @direction = direction
      @red = red
      @green = green
      @blue = blue
    end
  end

  class Trench 
    attr_reader :holes_dug
    attr_reader :upper_left
    attr_reader :lower_right
    def initialize
      @holes_dug = Hash.new # position to hole
    end

    def dig_hole(hole)
      holes_dug[hole.pos] = hole
      if upper_left.nil?
        @upper_left = hole.pos
        @lower_right = hole.pos
      else 
        @upper_left = Position.new([upper_left.row, hole.pos.row].min, [upper_left.col, hole.pos.col].min)
        @lower_right = Position.new([lower_right.row, hole.pos.row].max, [lower_right.col, hole.pos.col].max)
      end
    end

    def hole(pos)
      holes_dug[pos]
    end

    def minecraft_spread_water
      stack = []
      stack << (holes_dug.values.sort.first.pos + Position.new(1,1))
      until stack.empty?
        top = stack.pop
        
        dig_hole(Hole.new(top, "00", "00", "00"))
        [NORTH, SOUTH, EAST, WEST].each do |dir|
          neighbor = top + dir
          stack << neighbor if hole(neighbor).nil?
        end
      end

      holes_dug.size
    end

    def to_s
      ret = ""
      (upper_left.row..lower_right.row).each do |r|
        (upper_left.col..lower_right.col).each do |c|
          ret += hole(Position.new(r, c)).nil? ? " ": "#"
        end
        ret += "\n"
      end
      ret
    end
  end

  class Hole
    attr_reader :pos
    attr_reader :red
    attr_reader :green
    attr_reader :blue
    include Comparable

    def initialize(pos, red, green, blue)
      @pos = pos
      @red = red
      @green = green
      @blue = blue
    end

    def row
      pos.row
    end

    def col
      pos.col
    end

    def <=>(other)
      pos <=> other.pos
    end

    def to_s
      "#{pos} (\##{red}#{green}#{blue})"
    end
  end
end

p = Puzzle18::Puzzle.new
p.main