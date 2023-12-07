load 'base.rb'
require 'Set'

module Puzzle5
  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle5/inputs.txt", Data.new) {|line, data| parse_data(line, data)}
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle5/inputs.txt", Data.new) {|line, data| parse_data(line, data)}
      puts "Solution2: #{solution2}"
    end

    def parse_data(line, data)
      if data.seeds.nil?
        data.add_seeds(line)
      elsif line.match? "map:"
        data.add_material_map(line)
      elsif not line.empty?
        data.add_range_map(line)
      end
    end

    def compute_solution(data)
      data.seeds.map {|seed| translate_src_into_location(seed, data) }.min
    end

    def translate_src_into_location(seed, data)
      src = "seed"
      while src != "location"
        seed_map = data.get_materials_map(src)
        num = seed_map.translate_src_into_dest(seed)
        seed = num
        src = seed_map.dest
      end
      seed
    end

    def compute_solution2(data)
      range_1 = (data.seeds[0]..data.seeds[0]+data.seeds[1])
      range_2 = (data.seeds[2]..data.seeds[2]+data.seeds[3])

      combined = data.get_materials_map("seed")
      while combined.dest != "location"
        combined = data.translate_to_try_2(combined, data.get_materials_map(combined.dest))
      end

      # Check
      # puts "Range: #{range_1} #{range_1.size}"
      # count = 0
      # range_1.step(1000).each do |seed|
      #   part1 = translate_src_into_location(seed, data)
      #   part2 = combined.translate_src_into_dest(seed)
      #   puts "#{seed} #{part1}" if (count % 1000) == 0
      #   if part1 != part2
      #     puts "#{seed} #{part1}!=#{part2}"
      #   end
      #   count = count + 1
      # end
      # puts "Range: #{range_2} #{range_2.size}"

      count = 0
      range_2.step(1000).each do |seed|
        part1 = translate_src_into_location(seed, data)
        part2 = combined.translate_src_into_dest(seed)
        puts "#{seed} #{part1}" if (count % 1000) == 0
        if part1 != part2
          puts "#{seed} #{part1}!=#{part2}"
        end
        count = count + 1
      end

      min1 = find_min_in_range(data, combined, range_1)
      min2 = find_min_in_range(data, combined, range_2)
      [min1, min2].min
    end

    def find_min_in_range(data, map, range)
      min = map.translate_src_into_dest(range.first)

      srcs = Set.new([range.first, range.last])
      map.range_map.each do |seed_range|
        overlap = data.get_overlapping_range(range, seed_range.src_range)
        unless overlap.size == 0
          srcs << overlap.first
          srcs << overlap.last + 1 if overlap.last < range.last
        end
      end
      mapped = srcs.map {|seed| map.translate_src_into_dest(seed) }

      # puts map
      # puts range
      # puts "srcs: #{srcs}"
      # puts "mapped: #{mapped}"
      # puts "min: #{mapped.to_a.min}"
      mapped.to_a.min
    end
  end

  class Data
    attr_reader :seeds
    attr_reader :material_maps

    def initialize
      @seeds = nil
      @material_maps = []
    end

    def add_seeds(str)
      @seeds = str[str.index(":")+1..].strip.split(" ").map {|s| s.to_i}
    end

    def add_material_map(str)
      @material_maps << MaterialsMap.from_str(str)
    end

    def add_range_map(str)
      @material_maps.last.add_range(SeedRange.from_str(str))
    end

    def get_materials_map(source_material)
      @material_maps.find {|m| m.source == source_material}
    end

    def overlap?(r1,r2)
      !(r1.first > r2.last || r1.last < r2.first)
    end

    def get_overlapping_range(src, dest)
      new_range_start = src.first > dest.first ? src.first : dest.first
      new_range_end = src.last < dest.last ? src.last : dest.last
      Range.new(new_range_start, new_range_end)
    end
    
    def translate_to_try_2(map_a, map_b)
      combined = MaterialsMap.new(map_a.source, map_b.dest)
      map_a_dests = Set.new

      # These are all the possible places that the equation changes from map_a
      map_a.range_map.each do |seed_range_a|
        range_a_dest = seed_range_a.dest_range
        map_a_dests << range_a_dest.first
        map_a_dests << range_a_dest.last + 1
      end

      # Now we add all the possible places that the equation changes from map_b
      map_b.range_map.each do |seed_range_b|
        range_b_src = seed_range_b.src_range
        map_a_dests << range_b_src.first
        map_a_dests << range_b_src.last + 1
      end

      # Will all the possible transition states, we can use the source to calculate the dest
      ary = map_a_dests.to_a.sort
      (0..ary.size-2).each do |i|
        first_dest = ary[i]
        last_dest = ary[i+1]
        num = last_dest - first_dest

        range = SeedRange.new(map_b.translate_src_into_dest(first_dest), map_a.translate_dest_into_src(first_dest), num)
        combined.add_range(range) unless range.delta == 0
      end

      combined
    end
  end

  class MaterialsMap
    attr_reader :source
    attr_reader :dest
    attr_reader :range_map

    def self.from_str(str)
      regex = /(.+)-to-(.+)\smap:/
      match = regex.match str
      MaterialsMap.new(match[1], match[2])
    end

    def initialize(source, dest)
      @source = source
      @dest = dest
      @range_map = []
    end

    def add_range(range)
      @range_map << range unless range.empty?
    end

    def translate_src_into_dest(src)
      find_range = range_map.find do |range| 
        range.src_range.member?(src)
      end
      find_range.nil? ? src : find_range.translate_src_into_dest(src)
    end

    def translate_dest_into_src(dest)
      find_range = range_map.find do |range| 
        range.dest_range.member?(dest)
      end
      find_range.nil? ? dest : find_range.translate_dest_into_src(dest)
    end

    def to_s
      s = "#{source}-to-#{dest} map:\n"
      range_map.sort_by {|r| r.src_start }.each do |r|
        s = s + r.to_s + "\n"
      end
      s
    end
  end

  class SeedRange
    attr_reader :dest_start
    attr_reader :src_start
    attr_reader :delta
    attr_reader :num_items

    def self.from_str(str)
      parts = str.split(" ")
      SeedRange.new(*parts)
    end

    def initialize(dest_start, src_start, num_items)
      @dest_start = dest_start.to_i
      @src_start = src_start.to_i
      @delta = @dest_start - @src_start
      @num_items = num_items.to_i
    end

    def empty?
      num_items == 0
    end

    def src_range
      (src_start .. src_start+num_items-1)
    end

    def dest_range
      (dest_start .. dest_start+num_items-1)
    end

    def translate_src_into_dest(src)
      src + delta
    end

    def translate_dest_into_src(dest)
      dest - delta
    end

    def to_s
      "#{dest_start} #{src_start} #{num_items}"
    end
  end
end

p = Puzzle5::Puzzle.new
p.main