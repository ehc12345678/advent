load 'base.rb'
load 'grid.rb'
load 'position.rb'

module Puzzle14
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle14/inputs.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle14/inputs.txt", Data.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      data.add_row(line.chars)
    end

    def compute_solution(data)
      data.all_rocks_fall_up
      data.total_load
    end

    def compute_solution2(data)
      seen = Hash.new
      repeat_start = nil
      repeat_end = nil
      scores = Array.new

      cycles = 1000000000
      cycles.times do |i|
        data.one_full_rotation
        s = data.to_s
        if seen.key?(s)
          repeat_start = seen[s]
          repeat_end = i
          break
        else
          scores << data.total_load
          seen[s] = i
        end
      end

      # repeats starts at seen(repeat), so we subtract that off
      cycles -= (repeat_start + 1)
      range = repeat_end - repeat_start
      mod_result = cycles % range
      scores[repeat_start + mod_result]    
    end
  end

  class Data < Grid  
    def all_rocks_fall_up
      all_rocks_fall_dir(NORTH)
    end

    def all_rocks_fall_dir(dir)
      if (dir == NORTH || dir == WEST)
        (0...num_rows).each do |r|
          (0...num_cols).each do |c|
            if cell(r, c) == 'O'
              fall_to_dir(Position.new(r, c), dir)
            end
          end
        end
      else
        (num_rows - 1).downto(0).each do |r|
          (num_cols - 1).downto(0).each do |c|
            if cell(r, c) == 'O'
              fall_to_dir(Position.new(r, c), dir)
            end
          end
        end
      end
    end

    def one_full_rotation
      all_rocks_fall_dir(NORTH)
      all_rocks_fall_dir(WEST)
      all_rocks_fall_dir(SOUTH)
      all_rocks_fall_dir(EAST)
    end

    def total_load
      sum = 0
      (0...num_rows).each do |r|
        (0...num_cols).each do |c|
          if cell(r, c) == 'O'
            sum += (num_rows - r)
          end
        end
      end
      sum      
    end

    def fall_up(rock_pos) 
      fall_to_dir(rock_pos, NORTH)
    end

    def fall_to_dir(rock_pos, dir)
      while is_open?(rock_pos + dir)
        above = cellp(rock_pos + dir)
        set_cellp(rock_pos + dir, cellp(rock_pos))
        set_cellp(rock_pos, above)
        rock_pos = rock_pos + dir
      end
    end

    def puts_this
      rows.each do |r|
        puts(r.join(''))
      end
    end

    def to_s
      rows.join do |r|
        r.join + " "
      end
    end

    def is_open?(pos)
      cellp(pos) == '.'
    end
  end
end

p = Puzzle14::Puzzle.new
p.main