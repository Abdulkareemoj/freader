namespace freader.Models;

public class Book
{
    public required string Title { get; set; }
    public required string Author { get; set; }
    public string? CoverPath { get; set; }
    public required string FilePath { get; set; }
    public double Progress { get; set; }
}
