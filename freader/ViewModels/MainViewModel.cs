using CommunityToolkit.Mvvm.ComponentModel;

namespace freader.ViewModels;

using System.Collections.ObjectModel;
using CommunityToolkit.Mvvm.ComponentModel;


public partial class MainViewModel : ViewModelBase
{
    [ObservableProperty]
    private ViewModelBase _currentPage;

    [ObservableProperty]
    private NavigationItemViewModel? _selectedNavigationItem;

    public ObservableCollection<NavigationItemViewModel> NavigationItems { get; }

    public MainViewModel()
    {
        NavigationItems = new ObservableCollection<NavigationItemViewModel>
        {
            new("Library", new LibraryViewModel()),
            new("Discover", new DiscoverViewModel()),
            new("Collections", new CollectionsViewModel()),
            new("Settings", new SettingsViewModel()),
        };

        _selectedNavigationItem = NavigationItems[0];
        _currentPage = _selectedNavigationItem.Page;
    }

    partial void OnSelectedNavigationItemChanged(NavigationItemViewModel? value)
    {
        if (value is not null)
        {
            CurrentPage = value.Page;
        }
    }
}