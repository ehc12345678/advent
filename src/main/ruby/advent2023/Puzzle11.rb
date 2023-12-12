load 'base.rb'
load 'position.rb'
require 'Set'

module Puzzle11
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle11/inputs.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle11/inputs.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      data.add_row(line)
    end

    def compute_solution(data)
      compute_solution_with_growth(data, 2)
    end

    def compute_solution2(data)
      compute_solution_with_growth(data, 1000000)
    end

    def compute_solution_with_growth(data, growth)
      data.expand_universe!(growth)

      sum = 0
      galaxies = data.galaxies.to_a
      (0...galaxies.size).each do |i|
        (i + 1...galaxies.size).each do |j|
          if i != j
            pos1 = galaxies[i]
            pos2 = galaxies[j]
            sum += (pos1.row - pos2.row).abs + (pos1.col - pos2.col).abs
          end
        end
      end
      sum
    end
  end

  class Data
    attr_reader :galaxies
    attr_reader :rows
    attr_reader :cols

    def initialize
      @galaxies = Set.new
      @rows = 0
      @cols = 0
    end

    def add_row(line)
      line.chars.each_with_index do |ch, col|
        if ch == '#'
          add_galaxy(Position.new(rows, col))
        end
      end
      @rows += 1
    end

    def add_galaxy(pos)
      @galaxies << pos
      @cols = pos.col if pos.col > @cols
      @rows = pos.row if pos.row > @rows
    end

    def galaxies_on_row(row)
      @galaxies.filter {|g| g.row == row}
    end

    def galaxies_on_col(col)
      @galaxies.filter {|g| g.col == col}
    end

    def empty_rows
      (0...rows).filter {|row| galaxies_on_row(row).empty?}
    end

    def empty_cols
      (0...cols).filter {|col| galaxies_on_col(col).empty?}
    end

    def galaxy_exist?(row, col)
      @galaxies.member?(Position.new(row, col))
    end

    def expand_universe!(growth)
      # Make each empty row two rows by moving everything beyond down
      empty_rows.reverse.each do |empty_row|
        @galaxies.filter {|g| g.row > empty_row}.each do |g|
          @galaxies.delete g
          add_galaxy(g + Position.new(growth - 1, 0))
        end
      end

      # Make each empty col two col by moving everything beyond right 
      empty_cols.reverse.each do |empty_col|
        @galaxies.filter {|g| g.col > empty_col}.each do |g|
          @galaxies.delete g
          add_galaxy(g + Position.new(0, growth - 1))
        end
      end
    end

    def to_s
      str = ""
      (0..rows).each do |row|
        (0..cols).each do |col|
          str += galaxy_exist?(row, col) ? '#' : '.'
        end
        str += "\n"
      end
      str
    end
  end

end

p = Puzzle11::Puzzle.new
p.main