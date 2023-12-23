load 'base.rb'
load 'grid.rb'
load 'position.rb'
require 'Set'

module Puzzle16
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle16/inputs.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle16/inputs.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      data.add_row(line.chars.map { |ch| Cell.new(ch) })
    end

    def compute_solution(data)
      compute_from_pos_dir(data, Position.new(0, 0) + WEST, :east)
    end

    def compute_solution2(data)
      max = 0
      (0...data.num_rows).each do |row|
        # Do left hand border
        left = compute_from_pos_dir(data.clone, Position.new(row, 0) + WEST, :east)

        # Do right hand border
        right = compute_from_pos_dir(data.clone, Position.new(row, data.num_cols), :west)
        max = [left, right, max].max
      end

      (0...data.num_cols).each do |col|
        # Do top hand border
        top = compute_from_pos_dir(data.clone, Position.new(0, col) + NORTH, :south)

        # Do bottom hand border
        bottom = compute_from_pos_dir(data.clone, Position.new(data.num_rows, col), :north)
        max = [top, bottom, max].max
      end
      max
    end

    def compute_from_pos_dir(data, pos, dir)
      stack = []
      stack << MovingBeam.new(pos, dir)
      until stack.empty?
        moving = stack.pop
        neighbor_pos = data.neighbor_pos(moving.pos, moving.dir)
        neighbor = data.cellp(neighbor_pos)
        unless neighbor.nil?
          if neighbor.mirror?
            handle_mirror(neighbor, neighbor_pos, moving.dir).each {|i| stack << i}
          # add the beam and keep it moving  
          elsif neighbor.add_beam(moving.dir)
            stack << MovingBeam.new(neighbor_pos, moving.dir)
          end
        end
        #puts "stack after #{stack.join(' ')}"
      end
      
      data.all_cells.count do |cell|
        cell.has_beam?
      end
    end

    def handle_mirror(cell, pos, dir)
      cell.add_beam(dir)
      case cell.mirror
      when '/'
        case dir
        when :east
          [ MovingBeam.new(pos, :north) ]
        when :west
          [ MovingBeam.new(pos, :south) ]
        when :north
          [ MovingBeam.new(pos, :east) ]
        when :south
          [ MovingBeam.new(pos, :west) ]
        end
      when '\\'
        case dir
        when :east
          [ MovingBeam.new(pos, :south) ]
        when :west
          [ MovingBeam.new(pos, :north) ]
        when :north
          [ MovingBeam.new(pos, :west) ]
        when :south
          [ MovingBeam.new(pos, :east) ]
        end
      when '|'
        case dir
        when :east, :west
          [ MovingBeam.new(pos, :north), MovingBeam.new(pos, :south) ]
        when :north, :south
          [ MovingBeam.new(pos, dir) ]
        end
      when '-'
        case dir
        when :east, :west
          [ MovingBeam.new(pos, dir) ]
        when :north, :south
          [ MovingBeam.new(pos, :east), MovingBeam.new(pos, :west) ]
        end
      end
    end

  end

  class MovingBeam
    attr_reader :pos
    attr_reader :dir

    def initialize(pos, dir)
      @pos = pos
      @dir = dir
    end

    def to_s
      "#{pos} #{dir}"
    end
  end

  class Cell
    attr_reader :mirror
    attr_reader :beams

    def initialize(ch)
      @beams = Set.new
      @mirror = ch unless ch == '.'
    end

    def initialize_copy(orig)
      @beams = orig.beams.clone
      @mirror = orig.mirror
    end

    def mirror?
      not mirror.nil?
    end

    def beam_can_be_placed?(beam)
      not beams.include?(beam)
    end

    def has_beam?
      beams.empty? == false
    end

    def add_beam(beam)
      can_do_it = beam_can_be_placed?(beam)
      @beams << beam if can_do_it
      can_do_it
    end

  end

  class Data < Grid
    def neighbor_pos(pos, dir)
      case dir
      when :east
        pos + EAST
      when :west
        pos + WEST
      when :south
        pos + SOUTH
      when :north
        pos + NORTH
      end
    end

    def neighbor(pos, dir)
      npos = neighbor_pos(pos, dir)
      cellp(npos)
    end
  end
end

p = Puzzle16::Puzzle.new
p.main