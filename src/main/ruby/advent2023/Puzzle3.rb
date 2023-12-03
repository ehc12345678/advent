load 'base.rb'

class Puzzle3
  include Base

  class GameState
    attr_reader :numbers
    attr_reader :symbols

    class Position
      attr_reader :row
      attr_reader :col

      def initialize(row, col)
        @row = row
        @col = col
      end

      def to_s
        "(#{row},#{col})"
      end

      def eql?(other)
        @row == other.row and @col == other.col
      end

      def hash
        to_s.hash
      end
    end

    def initialize
      @numbers = []
      @symbols = {}
    end

    def add_number(num, row, col)
      numbers << [num, Position.new(row, col)]
    end

    def add_symbol(symbol, row, col)
      symbols[Position.new(row, col)] = symbol
    end

    def has_symbol?(row, col)
      symbols.key? Position.new(row, col)
    end

    def number_touches_symbol(number)
      num, pos = number
      (pos.row - 1 .. pos.row + 1).each do |row|
        (pos.col - 1 .. pos.col + num.to_s.length).each do |col|
          if has_symbol? row, col
            return true
          end
        end
      end
      false
    end
  end

  def main
    row = 0
    solution1 = self.solve_puzzle("puzzle3/inputs.txt", GameState.new) do |line, data|
      parse_line data, row, line
      row = row + 1
    end
    puts "Solution1: #{solution1}"

    solution2 = self.solve_puzzle2("puzzle3/inputs.txt", GameState.new) do |line, data|
      parse_line data, row, line
      row = row + 1
    end
    puts "Solution2: #{solution2}"
  end

  def parse_line(data, row, line)
    num_regex = /([\d]+)/
    sym_regex = /([^\.\d ])/
    n = 0
    loop do
      index = num_regex =~ line[n .. -1]
      break if index.nil?
      col = n + index
      num = line[col..-1].to_i

      data.add_number(num, row, col)
      n = col + num.to_s.length
    end

    (0..line.length).each do |col|
      if line[col] =~ sym_regex
        data.add_symbol line[col], row, col
      end
    end
  end

  def compute_solution(data)
    data.numbers.filter do |num| 
      data.number_touches_symbol(num)
    end.sum {|num| num[0]}
  end

  def number_touches_position?(number, pos)
    num, num_pos = number
    row_in_range = pos.row.between?(num_pos.row - 1, num_pos.row + 1) 
    col_in_range = pos.col.between?(num_pos.col - 1, num_pos.col + num.to_s.length)
    row_in_range and col_in_range
  end

  def find_gears(data)
    asterisks = data.symbols.filter {|key, value| value == '*' }.map {|key, value| key}.map do |a_pos|
      nums_touching = data.numbers.filter {|num| number_touches_position?(num, a_pos)}.map {|num| num[0]}
      if nums_touching.size == 2
        nums_touching[0] * nums_touching[1]
      else
        0
      end
    end
  end

  def compute_solution2(data)
    g = find_gears(data).sum
  end
end

p = Puzzle3.new
p.main