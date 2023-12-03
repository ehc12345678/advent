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
