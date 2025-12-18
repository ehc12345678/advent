using System.Reflection;

namespace advent.SharedBase;

public abstract class Base<TData, TSolution1, TSolution2> : IPuzzle
{
    public virtual void Run(bool testSolution1 = false, bool testSolution2 = false)
    {
        try
        {
            var file1 = testSolution1 ? "inputsTest.txt" : "inputs.txt";
            var solution1 = SolvePuzzle(file1, CreateData());
            Console.WriteLine($"Solution1: {solution1}");

            var file2 = testSolution2 ? "inputsTest.txt" : "inputs.txt";
            var solution2 = SolvePuzzle2(file2, CreateData());
            Console.WriteLine($"Solution2: {solution2}");
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex);
        }
    }

    public virtual bool Solution1TestSolution => true;

    public virtual bool Solution2TestSolution => true;

    protected abstract TData CreateData();

    public delegate void ParseLineDelegate(string line, TData data);
    
    // Throws exception if the function cannot open the file
    public virtual TData ReadInput(string filename, TData data, ParseLineDelegate parseLineFunc)
    {
        try
        {
            // Build the expected resource name: "<Namespace>.<filename>"
            var resourceName = GetType().Namespace + "." + filename;
            using var stream = GetResourceStream(resourceName);
            using var reader = new StreamReader(stream);

            while (!reader.EndOfStream)
            {
                var line = reader.ReadLine();
                if (line != null)
                {
                    parseLineFunc(line, data);
                }
            }
            return data;
        }
        catch (IOException e)
        {
            Console.WriteLine(e.Message);
            throw;
        }
    }

    public abstract void ParseLine(string line, TData data);
    public virtual void ParseLine2(string line, TData data) => ParseLine(line, data);

    public virtual TSolution1 SolvePuzzle(string file, TData data)
    {
        var newData = ReadInput(file, data, ParseLine);
        return ComputeSolution(newData);
    }

    public virtual TSolution2 SolvePuzzle2(string file, TData data)
    {
        var newData = ReadInput(file, data, ParseLine2);
        return ComputeSolution2(newData);
    }

    public abstract TSolution1 ComputeSolution(TData data);
    public abstract TSolution2 ComputeSolution2(TData data);

    //----- Implementation details ----
    private static Stream GetResourceStream(string resourceName)
    {
        var assembly = Assembly.GetExecutingAssembly();
        return assembly.GetManifestResourceStream(resourceName)
               ?? throw new FileNotFoundException($"Resource not found: {resourceName}");
    }
}