load 'base.rb'
load 'grid.rb'

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
        col_sym = cluster.find_col_symmetry
        row_sym = cluster.find_row_symmetry

        col_sym ||= 0
        row_sym ||= 0
        if (row_sym != 0) and (col_sym != 0)
          if row_sym > col_sym
            puts "setting column to 0 because row is greater"
            col_sym = 0
          else
            row_sym = 0
          end
        end

        answer = col_sym + (100 * row_sym)

        puts cluster.to_s
        puts "#{col_sym},#{row_sym}=#{answer}"
        puts ""
        answer
      end
    end

    def compute_solution2(data)
      # compute_solution(data)
    end
  end

  class Cluster < Grid
    def find_col_symmetry
      sym_around(num_cols) { |x| col(x) } 
    end

    def find_row_symmetry
      sym_around(num_rows) { |x| row(x) } 
    end

    def sym_around(total, &block)
      midpoint = total / 2

      (0..midpoint).each do |d|
        ret = sym_around_pos(midpoint + d, total, &block) if midpoint + d + 1 < total
        ret = sym_around_pos(midpoint - d, total, &block) if (midpoint - d >= 0 and ret.nil?)
        return ret unless ret.nil?
      end

      nil
    end

    def sym_around_pos(pos, total)
        # puts "Maybe it is #{pos}"
        first_pos = pos
        second_pos = pos + 1
        same = true
        while (first_pos > 0 and second_pos < total and same) do
          first = yield(first_pos)
          second = yield(second_pos)
          same = first.eql?(second)
          # puts "diff #{first.join} #{second.join}" unless same
          first_pos -= 1
          second_pos += 1
        end
        # puts same ? "found good pos #{pos + 1}" : "no match for #{pos + 1}"
        same ? pos + 1 : nil
      end

    def to_s
      (0..num_rows-1).map {|r| row(r).join + "\n"}
    end

  end
end

p = Puzzle13::Puzzle.new
p.main