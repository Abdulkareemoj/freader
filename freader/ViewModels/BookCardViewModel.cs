namespace freader.ViewModels
{
    public class BookCardViewModel : ViewModelBase
    {
        public string CoverImage { get; set; }
        public string Title { get; set; }
        public string Author { get; set; }
        public int Progress { get; set; }
    }
}
