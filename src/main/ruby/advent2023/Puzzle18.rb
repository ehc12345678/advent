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

      solution2 = self.solve_puzzle2("puzzle18/inputsTest.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      data.add(Instruction.from_str(line))
    end

    def compute_solution(data)
      data.follow_instructions
      puts data.trench.to_s
      data.trench.holes_dug.size
    end

    def compute_solution2(data)
      0
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

    def initialize(pos, red, green, blue)
      @pos = pos
      @red = red
      @green = green
      @blue = blue
    end
  end
end

p = Puzzle18::Puzzle.new
p.main