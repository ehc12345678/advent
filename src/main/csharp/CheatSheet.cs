namespace advent;

public class CheatSheet
{
    public static void RunMain(string[] args)
    {
        var s = args[0];
        Console.WriteLine($"Length of {s} is {s.Length}");

        int[] myList = [100, 200];
        const string longString = """
      I can type any characters in here !#@$%^&*()__+ ''
           except double quotes and I will be taken literally. I even work with multiple lines.
      """;

        var dayOfWeek = getDayOfWeek(args[0]);
        switch (dayOfWeek)
        {
            case DayOfWeek.Monday:
                Console.WriteLine("Start of work week");
                break;
            case DayOfWeek.Friday:
                Console.WriteLine("End of work week");
                break;
            case DayOfWeek.Saturday:
            case DayOfWeek.Sunday:
                Console.WriteLine("Weekend");
                break;
            default:
                Console.WriteLine("Midweek");
                break;
        }

        var dictionary = new Dictionary<string, int>
        {
            { "Monday", 0 },
            { "Tuesday", 1 }
        };
        foreach (var (key, value) in dictionary)
        {
            Console.WriteLine($"{key}: {value}");
        }
    }

    private static DayOfWeek getDayOfWeek(string s)
    {
        var ret = s switch
        {
            "Monday" => DayOfWeek.Monday,
            "Tuesday" => DayOfWeek.Tuesday,
            _ => DayOfWeek.Monday
        };
        return ret;
    }
}
