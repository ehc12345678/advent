class Grid
  attr_reader :rows
  
  def initialize
    @rows = []
  end

  def initialize_copy(orig)
    @rows = orig.rows.map do |r|
      r.map do |c|
        c.clone
      end
    end
  end

  def add_row(row)
    @rows << row
  end

  def row(r)
    @rows[r]
  end

  def col(c)
    @rows.map { |r| r[c] }
  end

  def num_rows
    @rows.size
  end

  def num_cols
    @rows.empty? ? 0 : @rows[1].size
  end

  def all_cells
    @rows.flatten
  end

  def cell(row, col)
    ((0..num_rows-1).member?(row) and (0..num_cols-1).member?(col)) ? @rows[row][col] : nil
  end

  def cellp(position)
    cell(position.row, position.col)
  end

  def set_cellp(position, value)
    @rows[position.row][position.col] = value
  end

  def neighbors(position, diagonals = false)
    neighbors_with_pos(position, diagonals).map {|value, pos| value}
  end

  def neighbors_with_pos(position, diagonals = false)
    deltas = [[-1, 0], [0, -1], [0, 1], [1, 0]]
    if diagonals
      deltas += [[-1, -1], [-1, 1],[1, -1], [1, 1]]
    end
      
    deltas.map do |delta| 
      neighbor_pos = Position.new(*delta) + position
      neighbor = cellp(neighbor_pos)
      neighbor.nil? ? nil : [neighbor, neighbor_pos]
    end.filter {|x| not x.nil? }
  end

  def find_cells(include_nil = false)
    ret = []
    (0..num_rows).each do |row|
      (0..num_cols).each do |col|
        value = cell(row, col)
        if not value.nil? or include_nil
          found_it = yield value
          ret << Position.new(row, col) if found_it
        end
      end
    end
    ret
  end

  def to_s
    rows.to_s
  end
end