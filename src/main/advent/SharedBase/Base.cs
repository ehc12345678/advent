using System.Reflection;

namespace advent.SharedBase;

public abstract class Base<TData, TSolution1, TSolution2>
{
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
    public void ParseLine2(string line, TData data) => ParseLine(line, data);

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