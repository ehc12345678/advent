using System.Numerics;

namespace advent.SharedBase;

public readonly struct Range<T> 
    (T low, T high, bool lowInclusive = true, bool highInclusive = false) 
    where T: INumber<T>
{
    // This allows the Range to be mutable
    // public T Low { get; set; } = low;
    // public T High { get; set; } = high;
    public T Low => low;
    public T High => high;
    
    private bool LowInclusive => lowInclusive;
    private bool HighInclusive => highInclusive;
    
    public bool InRange(T value)
    {
        var matchesLow = LowInclusive ?
           value.CompareTo(low) >= 0 : value.CompareTo(low) > 0;
        var matchesHigh = HighInclusive ?
            value.CompareTo(high) <= 0 : value.CompareTo(high) < 0;
        return matchesLow && matchesHigh;
    }
}