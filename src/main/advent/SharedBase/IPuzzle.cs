namespace advent.SharedBase;

public interface IPuzzle
{
    void Run(bool testSolution1, bool testSolution2);
    bool Solution1TestSolution { get; }
    bool Solution2TestSolution { get; }
}