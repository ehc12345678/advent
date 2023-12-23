load 'base.rb'
load 'grid.rb'
load 'position.rb'

module Puzzle13
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle13/inputs.txt", Array.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle13/inputsTest.txt", Array.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      if data.empty? 
        data << Cluster.new
      end

      if line.empty?
        data << Cluster.new
      else 
        data.last.add_row(line.chars)
      end
    end

    def compute_solution(data)
      data.sum do |cluster|
        cluster.cluster_value
      end
    end

    def compute_solution2(data)
      data.sum do |cluster|
        # cluster.find_first_smudge_value
        cluster.brute_force_solution_2
      end
    end
  end

  class Cluster < Grid
    def find_col_symmetry
      sym_around(num_cols) { |x| col(x) } 
    end

    def find_row_symmetry
      sym_around(num_rows) { |x| row(x) } 
    end

    def cluster_value
      col_sym = find_col_symmetry
      row_sym = find_row_symmetry
      col_sym + (100 * row_sym)
    end

    def sym_around(total, &block)
      (0...total-1).each do |d|
        return d + 1 if sym_around_pos?(d, total, &block)
      end
      0
    end

    def sym_around_pos?(pos, total)
      i = 0
      while (pos - i >= 0) and (pos + i + 1 < total)
        first = yield(pos - i)
        second = yield(pos + i + 1)
        return false unless first.eql?(second)
        i += 1
      end
      true
    end

    def brute_force_solution_2
      (0...num_cols-1).each do |col|
        (0...num_rows).each do |row|
          cell_val = cell(row, col)
          new_val = cell_val == '.' ? '#' : '.'
          orig_col_sym = find_col_symmetry
          set_cellp(Position.new(row, col), new_val)
          new_col_sym = find_col_symmetry
          unless new_col_sym == orig_col_sym
            puts "Found a column asymetry at #{row} #{col}, returning #{new_col_sym}"
            return new_col_sym 
          end
          set_cellp(Position.new(row, col), cell_val)
        end
      end

      (0...num_rows-1).each do |row|
        (0...num_cols).each do |col|
          cell_val = cell(row, col)
          new_val = cell_val == '.' ? '#' : '.'
          orig_row_sym = find_row_symmetry
          set_cellp(Position.new(row, col), new_val)
          new_row_sym = find_row_symmetry
          unless new_row_sym == orig_row_sym
            puts "Found a row asymetry at #{row} #{col}, returning #{new_row_sym}"
            return new_row_sym * 100 
          end
          set_cellp(Position.new(row, col), cell_val)
        end
      end

      puts "oops"
      0
    end

    # def only_one_diff?(pos, total, &block)
    #   i = 0
    #   count = 0
    #   while (pos - i >= 0) and (pos + i + 1 < total)
    #     first = yield(pos - i)
    #     second = yield(pos + i + 1)
    #     unless first.eql?(second)
    #       count += (0...first.length).sum do |j|
    #         first[j] != second[j] ? 1 : 0
    #       end
    #       puts "Diff for #{pos} with count #{count}"
    #       if count > 1
    #         puts "Count is too high #{pos} with #{count}"
    #         return false
    #       end
    #     end
    #     i += 1
    #   end
    #   puts "** Count is good #{pos} with #{count}"
    #   true
    # end

    # def find_first_smudge_value
    #   (0...num_cols-1).each do |col|
    #     if (only_one_diff?(col, num_cols) { |c| col(c) })
    #       puts "#{col} col can be changed"
    #       return (col * 100)
    #     end
    #   end
    #   (0...num_rows-1).each do |row|
    #     if (only_one_diff?(row, num_rows) { |r| row(r) })
    #       puts "#{row} row can be changed"
    #       return row  
    #     end
    #   end
    #   0
    # end

    def to_s
      (0..num_rows-1).map {|r| row(r).join + "\n"}
    end

  end
end

p = Puzzle13::Puzzle.new
p.main