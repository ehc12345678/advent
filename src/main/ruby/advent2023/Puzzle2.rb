load 'base.rb'

class Puzzle2
  include Base
  
  class Game
    attr_reader :game
    attr_reader :turns

    class Turn
      attr_reader :parts

      def initialize(str)
        @parts = str.split(",").map do |s| 
          s = s.strip
          num = s.to_i
          color = s[s.index(" ")..].strip.to_sym
          [color, num]
        end.to_h
      end

      def has_enough_blocks(blocks)
        blocks.all? do |color, num|
          @parts.fetch(color, 0) <= num
        end
      end
    end
  
    def initialize(str)
      str = str = str["Game ".length..]
      @game = str[0..str.index(":")-1].to_i    
  
      str = str[str.index(":")+2..]
      @turns = str.split(";").map { |s| s.strip }.map { |s| Turn.new s }
    end

    def has_enough_blocks(blocks)
      @turns.all? { |b| b.has_enough_blocks(blocks) }
    end

    def min_possible_blocks
      ret = {:red=>0, :blue=>0, :green=>0}
      @turns.each do |t|
        t.parts.each do |key, value|
          ret[key] = value if value > ret[key]
        end
      end
      ret
    end
  end

  def main
    solution1 = self.solve_puzzle("puzzle2/inputs.txt", Array.new) do |line, data|
        data << parse_game(line)
    end
    puts "Solution1: #{solution1}"

    solution2 = self.solve_puzzle2("puzzle2/inputs.txt", Array.new) do |line, data|
        data << parse_game(line)
    end
    puts "Solution2: #{solution2}"
  end

  def compute_solution(data)
    ok_games = data.filter {|g| g.has_enough_blocks(:red=>12, :green=>13, :blue=>14)}
    ok_games.sum { |g| g.game }
  end

  def compute_solution2(data)
    mins = data.map do |g|
      g.min_possible_blocks
    end
    mins.sum do |m|
      m.values.reduce(1, :*)
    end
  end

  def parse_game(str)
    Game.new str
  end
end

p = Puzzle2.new
p.main
