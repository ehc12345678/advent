load 'base.rb'
require 'Set'

module Puzzle25
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle25/inputs.txt", Graph.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle25/inputsTest.txt", Graph.new) do |line, data|
        parse_line(data, line)
      end
      puts "Solution2: #{solution2}"
    end

    def parse_line(data, line)
      (src, dests) = line.split(":").map {|s| s.chomp}
      dests.split(" ").each {|dest| data.add_connection(src, dest)}
    end

    def compute_solution(data)
      all_connections = data.all_connections

      # Test the solution
      # clone = data.clone
      # clone.remove_connection("hfx","pzl")
      # clone.remove_connection("bvb","cmg")
      # clone.remove_connection("nvd","jqt")
      # puts "Solution num paths #{clone.distinct_paths}"

      # solution = nil
      # (0...all_connections.size-2).each do |i|
      #   clone = data.clone
      #   connection = all_connections[i]
      #   clone.remove_connection(connection[:src], connection[:dest])
      #   if clone.distinct_paths.size <= 2

      # solution = nil
      # one_iter = all_connections.map do |connection|
      #   clone = data.clone
      #   clone.remove_connection(connection[:src], connection[:dest])
      #   distinct_paths = clone.distinct_paths
      #   if distinct_paths.size <= 2
      #     if ["pzl","bvb","cmg"].to_set.include?(connection[:src])  and ["hfx","bvb","nvd"].to_set.include?(connection[:dest])
      #       puts "found solution #{connection}"
      #     end
      #     [connection, clone] 
      #   end
      # end.filter {|c| c}

      # puts "One iter: #{one_iter.size}"

      # first_iter_connections = one_iter.map {|a| a.first}
      # two_iter = []
      # one_iter.map do |connection, clone| 
      #   first_iter_connections.each do |first_conn|
      #     unless first_conn == connection
      #       # puts "Trying remove of #{connection} with #{first_conn}"
      #       # puts "Found #{clone.distinct_paths.size} paths"
      #       second_clone = clone.clone
      #       #puts "Found #{second_clone.distinct_paths.size} paths"
      #       second_clone.remove_connection(connection[:src], connection[:dest])
      #       distinct_paths = second_clone.distinct_paths
      #       #puts "Found #{distinct_paths.size} paths"

      #       if distinct_paths.size <= 2
      #         if ["pzl","bvb","cmg"].to_set.include?(connection[:src])  and ["hfx","bvb","nvd"].to_set.include?(connection[:dest])
      #           puts "2nd solution #{connection}"
      #         end
      #         two_iter << [[connection, first_conn].to_set, second_clone]
      #       end
      #     end
      #   end
      # end

      # second_iter_connections = two_iter.map {|a| a.first.to_a }.flatten.to_set
      # solution = nil

      # iterations = two_iter.size * second_iter_connections.size
      # puts "Two iter: #{two_iter.size} with #{second_iter_connections.size}, which means #{iterations}"

      # count = 0
      # canned = [{src:"hfx",dest:"pzl"},{src:"bvb",dest:"cmg"},{src:"nvd",dest:"jqt"}].to_set
      # two_iter.map do |existing_conns, clone| 
      #   second_iter_connections.each do |second_conn|
      #     unless (canned - existing_conns).empty?
      #       puts "We are close with #{existing_conns}, adding #{second_conn}"
      #     end
      #     if (existing_conns + second_conn).eql?(canned) 
      #       puts "The solution is being evaluated"
      #     end
      #     unless existing_conns.include?(second_conn)
      #       third_clone = clone.clone
      #       #puts "Found #{second_clone.distinct_paths.size} paths"
      #       third_clone.remove_connection(second_conn[:src], second_conn[:dest])
      #       distinct_paths = third_clone.distinct_paths
      #       #puts "Found #{distinct_paths.size} paths"

      #       if distinct_paths.size == 2
      #         solution = distinct_paths
      #         break
      #       end
      #       count += 1
      #       puts "Count = #{count}" if (count % 100000) == 0
      #     end
      #   end
      # end

      solution = find_solution(data)
      solution.distinct_paths.map {|p| p.size}.inject(:*)
    end

    def find_solution(data)
      all_connections = data.all_connections.filter do |connection|
        clone = data.clone
        clone.remove_connection(connection[:src], connection[:dest])
        distinct_paths = clone.distinct_paths
        distinct_paths.size < 2
      end

      puts "Found #{all_connections.size}"
      puts "Trying #{(all_connections.size-2)*(all_connections.size-1)*all_connections.size}"
      return nil

      (0...all_connections.size-2).each do |i|
        clone = data.clone
        conn = all_connections[i]
        if clone.remove_connection(conn[:src], conn[:dest]) and clone.distinct_paths.size <= 2
          (i + 1...all_connections.size-1).each do |j|            
            clone2 = clone.clone
            conn2 = all_connections[j]
            if clone2.remove_connection(conn2[:src], conn2[:dest]) and clone2.distinct_paths.size <= 2
              (j + 1...all_connections.size).each do |k|            
                clone3 = clone2.clone
                conn3 = all_connections[k]
                if clone3.remove_connection(conn3[:src], conn3[:dest])
                  if clone3.distinct_paths.size == 2
                    return clone3
                  end
                end
              end
            end
          end
        end
      end
      nil
    end

    def compute_solution2(data)
      0
    end
  end

  class Graph
    attr_reader :connections
    attr_reader :srcs

    def initialize
      @connections = {}
      @srcs = Set.new
    end

    def initialize_copy(orig)
      @connections = {}
      orig.connections.each do |k,v|
        @connections[k] = v.clone
      end
      @srcs = Set.new + orig.srcs
    end

    def first
      srcs.first
    end

    def add_connection(src, dest)
      one_sided_add(src, dest)
      one_sided_add(dest, src)
    end

    def num_connections(src)
      @connections[src].size
    end

    def total_connections
      @connections.sum {|k,v| v.size}
    end

    def remove_connection(src, dest)
      can_cut = num_connections(src) > 1 and num_connections(dest) > 1
      if can_cut
        one_sided_remove(src, dest)
        one_sided_remove(dest, src)
      end
      can_cut
    end

    def dests_for(label)
      @connections[label]
    end

    def all_connections
      srcs.map do |s| 
        dests_for(s).map do |d| 
          {src: s, dest: d} 
        end
      end.flatten
    end

    def one_sided_add(src, dest)
      @connections[src] = [] unless @connections.key?(src)
      @connections[src] << dest
      @srcs << src
    end

    def one_sided_remove(src, dest)
      @connections[src].delete(dest)
    end

    def find_all_dests_for_src(src, seen)
      unless seen.include?(src)
        seen << src
        dests = dests_for(src)
        dests.each do |dest|
          find_all_dests_for_src(dest, seen)
        end
      end
    end

    def distinct_paths(debug = false)
      all_srcs = srcs.clone
      paths = []
      puts "   Distinct paths with #{all_srcs} and origin #{srcs}" if debug
      until all_srcs.empty?
        path = Set.new
        find_all_dests_for_src(all_srcs.first, path)
        puts "   Adding path starting from #{all_srcs.first}; #{path}" if debug
        all_srcs -= path
        paths << path
      end
      paths
    end

  end
end

p = Puzzle25::Puzzle.new
p.main