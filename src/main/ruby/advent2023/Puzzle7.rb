load 'base.rb'

module Puzzle7
  CARD_RANKING = %w{2 3 4 5 6 7 8 9 T J Q K A}.each_with_index.map {|value, index| [value[0],index+2]}.to_h

  class Puzzle
    include Base

    def main
      solution1 = self.solve_puzzle("puzzle7/inputs.txt", Array.new) do |line, data|
        data << Hand.from_str(line)
      end
      puts "Solution1: #{solution1}"

      solution2 = self.solve_puzzle2("puzzle7/inputs.txt", Array.new) do |line, data|
        data << Hand.from_str(line)
      end
      puts "Solution2: #{solution2}"
    end

    def compute_solution(data)
      data = sort_hands(data)
      data.reverse.each_with_index.sum do |hand,index|
        (index + 1) * hand.bet
      end
    end

    def compute_solution2(data)
      data = data.map do |hand| 
        hand.replace_wild_cards
      end
      compute_solution(data)
    end

    def sort_hands(data)
      data.sort do |hand1, hand2|
        group_nums1 = hand1.grouped.map {|g| g[1]}
        group_nums2 = hand2.grouped.map {|g| g[1]}

        if group_nums1.any? {|a| a.nil?}
          puts "groups_num1 was nil for #{hand1}"
          puts "num1: '#{group_nums1}'"
          throw Exception
        end
        if group_nums2.any? {|a| a.nil?}
          puts "groups_num2 was nil for #{hand2}"
          puts group_nums2
          throw Exception
        end

        cmp_groups = compare_arrays(group_nums1, group_nums2)
        cmp_values = compare_arrays(hand1.card_values, hand2.card_values)
        cmp_groups == 0 ? cmp_values : cmp_groups
      end.reverse!
    end

    def compare_arrays(a1, a2)
      x = 0
      y = 0
      while x < a1.length and y < a2.length
        return -1 if a1[x] < a2[y]
        return 1 if a1[x] > a2[y]
        x = x + 1
        y = y + 1
      end
      x < a1.length ? -1 : (y < a2.length ? 1 : 0)
    end
  end

  class Hand
    attr_reader :original
    attr_reader :card_values
    attr_reader :grouped
    attr_reader :bet

    def self.from_str(str)
      split = str.split(" ")

      original = split[0].chars
      card_values = original.map {|card| CARD_RANKING[card]}
      grouped = original.reduce(Hash.new) do |grouped, card|
        grouped[card] = grouped.fetch(card, 0) + 1
        grouped
      end.sort_by {|key, value| value }.reverse!
      bet = split[1].to_i
      Hand.new(original, card_values, grouped, bet)
    end

    def initialize(original, card_values, grouped, bet)
      @original = original
      @card_values = card_values
      @grouped = grouped
      @bet = bet
    end

    def replace_wild_cards
      values = card_values.map {|value| value == CARD_RANKING['J'] ? 0 : value }
      wild_groups = replace_wilds_in_groups(grouped)
      Hand.new(original, values, wild_groups, bet)
    end

    def replace_wilds_in_groups(groups)
      wilds = groups.find {|group| group[0] == 'J' }
      return groups if wilds.nil?

      groups = groups.filter {|group| group[0] != 'J'}
      return [['A', 5]] if groups.empty? 

      first_group = groups[0]
      first_group[1] = first_group[1] + wilds[1]
      [first_group] + groups[1..] 
    end

    def to_s
      "#{original} #{card_values} #{bet} #{grouped}"
    end
  end
end

p = Puzzle7::Puzzle.new
p.main