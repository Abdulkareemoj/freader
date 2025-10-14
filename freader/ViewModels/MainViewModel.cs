using CommunityToolkit.Mvvm.ComponentModel;

namespace freader.ViewModels;

public partial class MainViewModel : ViewModelBase
{
    // This is your HOME/DASHBOARD page  
    // Remove NavigationItems and nested navigation  

    [ObservableProperty]
    private string _welcomeMessage = "Welcome to freader!";

    // Add home page specific properties here  
    [ObservableProperty]
    private int _totalBooks;

    [ObservableProperty]
    private int _booksReadThisMonth;

    public MainViewModel()
    {
        // Initialize home page data  
        _totalBooks = 0;
        _booksReadThisMonth = 0;
    }
}