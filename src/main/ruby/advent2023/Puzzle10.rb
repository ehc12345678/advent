load 'base.rb'
load 'grid.rb'
load 'position.rb'
require 'Set'

module Puzzle10
  NORTH = Position.new(-1,0)
  SOUTH = Position.new(1,0)
  EAST  = Position.new(0, 1)
  WEST  = Position.new(0,-1)
  PIPE_MAP = {
    '|' => [NORTH, SOUTH],
    '-' => [EAST, WEST],
    'L' => [NORTH, EAST],
    'J' => [NORTH, WEST],
    '7' => [SOUTH, WEST],
    'F' => [SOUTH, EAST],
    'O' => [],
    ' ' => [],
  }

  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle10/inputs.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle10/inputs.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      data.add_row(line.chars.map { |c| c == ' ' ? PLACEHOLDER : Cell.new(c) })
    end

    def compute_solution(data)
      the_path = find_the_path(data)
      the_path.size / 2
    end

    DEBUG = true

    def compute_solution2(data)
      the_path = Set.new(find_the_path(data))
      (0..data.num_rows-1).each do |row|
        (0..data.num_cols-1).each do |col|
          pos = Position.new(row, col)
          if (not the_path.member?(pos))
            data.set_cellp(pos, PLACEHOLDER)
          end
        end
      end

      expanded = expanded_grid(data)
      expanded.fill_outside

      data.find_cells {|c| c == PLACEHOLDER }.each do |pos| 
        # every grid square in expanded is 3x3 times the original, this gets the middle
        expanded_pos = (pos * 3) + Position.new(1,1) 
        occupied = expanded.cellp(expanded_pos) == PLACEHOLDER ? Cell.new("I") : Cell.new("O")
        data.set_cellp(pos, occupied)
      end

      data.find_cells {|c| c.pipe == "I" }.size
    end

    def find_the_path(data)
      start = data.find_cells { |cell| cell.pipe == 'S' }.first
      paths = PIPE_MAP.filter do |pipe, value|
        data.makes_connection?(start, value)
      end.map do |pipe,value|
        data.set_cellp(start, Cell.new(pipe))
        data.find_circular_path(start)
      end
      paths[0]
    end

    def expanded_grid(data)
      expanded = Data.new
      (0..data.num_rows - 1).each do |row|
        l1 = ""
        l2 = ""
        l3 = ""
        (0..data.num_cols - 1).each do |col|
          cell = data.cell(row, col)
          case cell.pipe
          when ' '
            l1 += "   "
            l2 += "   "
            l3 += "   "
          when '-'
            l1 += "   "
            l2 += "---"
            l3 += "   "
          when '|'
            l1 += " | "
            l2 += " | "
            l3 += " | "
          when 'L'  
            l1 += " | "
            l2 += " |-"
            l3 += "   "
          when 'J'  
            l1 += " | "
            l2 += "-| "
            l3 += "   "
          when '7'  
            l1 += "   "
            l2 += "-| "
            l3 += " | "
          when 'F'  
            l1 += "   "
            l2 += " |-"
            l3 += " | "
          end
        end
        parse_line(expanded, l1)
        parse_line(expanded, l2)
        parse_line(expanded, l3)
      end
      expanded
    end
  end

  class Data < Grid
    # We make a connection if the cell we reach has a way to connect back to the position
    #    so, if we have a position like (1,3) and the pipe is [(1,0),(-1,0)]
    #    there is a connection if at each end we can come back to (1,3)
    #    First we example (2,3) which is (1,3)+(1,0) and see if we can find a (-1,0) at that cell
    def makes_connection?(start, value)
      (not value.empty?) and value.all? do |delta|
        pos = delta + start
        cell = cellp(pos)
        neg_delta = delta * -1
        cell&.connection&.any? { |conn| neg_delta.eql?(conn) } == true
      end
    end

    def find_circular_path(start)
      path = []
      seen_pos = Set.new
      p = start

      while not p.nil?
        path << p
        
        connection = cellp(p).connection
        if not connection.nil?
          p = connection.map {|delta| delta + p}.find do |next_p|
            next_p_cell = cellp(next_p)
            novel_node = (not seen_pos.member?(next_p))
            seen_pos << next_p
            ((not next_p_cell.nil?) and novel_node)
          end
        end
      end
      path
    end

    def fill_outside
      (0..num_cols-1).each { |col| fill_if_placeholder(0, col, 'O') }
      (1..num_rows-2).each do |row|
        fill_if_placeholder(row, 0, 'O') 
        fill_if_placeholder(row, num_cols-1, 'O') 
      end
      (0..num_cols-1).each { |col| fill_if_placeholder(num_rows - 1, col, 'O') }
    end

    def fill_if_placeholder(row, col, water)
      stack = [Position.new(row, col)]
      while not stack.empty?
        pos = stack.pop
        if cellp(pos) == PLACEHOLDER
          set_cellp(pos, Cell.new(water))
          neighs = neighbors_with_pos(pos).map { |value, p| p }.filter { |p| cellp(p) == PLACEHOLDER }.each do |p|
            stack.push(p)
          end
        end  
      end
    end


    # def fill_water_at_pos(pos, letter, direction, seen)
    #   return if seen.member?(pos)
      
    #   cell = cellp(pos)
    #   if cell.nil?
    #     set_cellp(pos, Cell.new(letter))
    #     seen << pos
    #   end

    #   delta = case direction
    #     when :south
    #       SOUTH
    #     when :north
    #       NORTH
    #     when :EAST
    #       EAST
    #     when :WEST
    #       WEST
    #   end

    #   if not delta.nil?
    #     fill_water_at_pos(pos + delta, letter, direction, seen)
    #   end

    # end

    def to_s
      ret = ""
      (0..num_rows-1).each do |row|
        (0..num_cols-1).each do |col|
          cell = cellp(Position.new(row, col))
          ret = ret + (cell.nil? ? " " : cell.to_s)
        end
        ret = ret + "\n"
      end
      ret
    end
  end

  class Cell
    attr_reader :pipe
    attr_reader :connection

    def initialize(pipe) 
      @pipe = pipe
      @connection = PIPE_MAP[pipe]
    end

    def to_s
      pipe
    end
  end

  PLACEHOLDER = Cell.new(' ')

end

p = Puzzle10::Puzzle.new
p.main