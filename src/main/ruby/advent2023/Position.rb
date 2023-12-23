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

  def +(obj)
    if obj.kind_of?(Position)
      Position.new(row + obj.row, col + obj.col)
    else
      nil
    end
  end

  def -(obj)
    if obj.kind_of?(Position)
      Position.new(row - obj.row, col - obj.col)
    else
      nil
    end
  end

  def *(factor)
    Position.new(row * factor, col * factor)
  end
end

NORTH = Position.new(-1,0)
SOUTH = Position.new(1,0)
EAST  = Position.new(0, 1)
WEST  = Position.new(0,-1)

