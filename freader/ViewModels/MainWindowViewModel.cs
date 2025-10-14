using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using ShadUI;

namespace freader.ViewModels;

public sealed partial class MainWindowViewModel : ViewModelBase
{
    // Properties for navigation    
    [ObservableProperty]
    private string _currentRoute = "home";

    [ObservableProperty]
    private object? _selectedPage;

    [ObservableProperty]
    private ThemeMode _currentTheme = ThemeMode.System;

    // Store references to page ViewModels and services  
    private readonly ThemeWatcher _themeWatcher;
    private readonly MainViewModel _mainViewModel;
    private readonly LibraryViewModel _libraryViewModel;
    private readonly DiscoverViewModel _discoverViewModel;
    private readonly CollectionsViewModel _collectionsViewModel;
    private readonly SettingsViewModel _settingsViewModel;


    // Single constructor with ALL dependencies  
    public MainWindowViewModel(
          ThemeWatcher themeWatcher,
           MainViewModel mainViewModel,
        LibraryViewModel libraryViewModel,
        DiscoverViewModel discoverViewModel,
        CollectionsViewModel collectionsViewModel,
        SettingsViewModel settingsViewModel
     )
    {
        _themeWatcher = themeWatcher;
        _mainViewModel = mainViewModel;
        _libraryViewModel = libraryViewModel;
        _discoverViewModel = discoverViewModel;
        _collectionsViewModel = collectionsViewModel;
        _settingsViewModel = settingsViewModel;

    }

    public void Initialize()
    {
        // Set home page as default    
        SelectedPage = _mainViewModel;
        CurrentRoute = "home";
    }

    [RelayCommand]
    private void SwitchTheme()
    {
        CurrentTheme = CurrentTheme switch
        {
            ThemeMode.System => ThemeMode.Light,
            ThemeMode.Light => ThemeMode.Dark,
            _ => ThemeMode.System
        };

        _themeWatcher.SwitchTheme(CurrentTheme);
    }

    // Navigation commands    
    [RelayCommand]
    private void OpenHome()
    {
        CurrentRoute = "home";
        SelectedPage = _mainViewModel;
    }

    [RelayCommand]
    private void OpenLibrary()
    {
        CurrentRoute = "library";
        SelectedPage = _libraryViewModel;
    }

    [RelayCommand]
    private void OpenDiscover()
    {
        CurrentRoute = "discover";
        SelectedPage = _discoverViewModel;
    }

    [RelayCommand]
    private void OpenCollections()
    {
        CurrentRoute = "collections";
        SelectedPage = _collectionsViewModel;
    }

    [RelayCommand]
    private void OpenSettings()
    {
        CurrentRoute = "settings";
        SelectedPage = _settingsViewModel;
    }

    // drawer state management 
    [ObservableProperty]
    private bool _isDrawerOpen = false;

    [RelayCommand]
    private void ToggleDrawer()
    {
        IsDrawerOpen = !IsDrawerOpen;
    }
}