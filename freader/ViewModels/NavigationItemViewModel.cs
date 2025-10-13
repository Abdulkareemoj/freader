using CommunityToolkit.Mvvm.ComponentModel;

namespace freader.ViewModels;

public partial class NavigationItemViewModel(string header, ViewModelBase page) : ObservableObject
{
    [ObservableProperty]
    private string _header = header;

    [ObservableProperty]
    private ViewModelBase _page = page;
}
