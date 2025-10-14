using System.Collections.ObjectModel;
using CommunityToolkit.Mvvm.ComponentModel;

namespace freader.ViewModels
{
    public partial class LibraryViewModel : ViewModelBase
    {
        public ObservableCollection<BookItem> Books { get; } = new();

        public LibraryViewModel()
        {
            // Initialize with sample data or load from service  
            Books.Add(new BookItem { Title = "Book One", Author = "Author A", Cover = "cover1.jpg" });
            Books.Add(new BookItem { Title = "Book Two", Author = "Author B", Cover = "cover2.jpg" });
            Books.Add(new BookItem { Title = "Book Three", Author = "Author C", Cover = "cover3.jpg" });
        }
    }
}

public class BookItem
{
    public string? Cover { get; set; }
    public string? Title { get; set; }
    public string? Author { get; set; }
}
