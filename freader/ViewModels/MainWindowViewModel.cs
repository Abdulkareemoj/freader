using System;
using System.Linq;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using ShadUI;
using System.Threading.Tasks;
using Avalonia.Platform.Storage;
using freader.Services;

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

    [ObservableProperty]
    private bool _canGoBack = false;

    // Store references to page ViewModels and services  
    private readonly ThemeWatcher _themeWatcher;
    private readonly MainViewModel _mainViewModel;
    private readonly LibraryViewModel _libraryViewModel;
    private readonly DiscoverViewModel _discoverViewModel;
    private readonly CollectionsViewModel _collectionsViewModel;
    private readonly SettingsViewModel _settingsViewModel;
    private readonly AboutViewModel _aboutViewModel;
    private readonly ExportViewModel _exportViewModel;
    private readonly freader.Services.FileService _fileService;


    // Single constructor with ALL dependencies  
    public MainWindowViewModel(
        ThemeWatcher themeWatcher,
        MainViewModel mainViewModel,
        LibraryViewModel libraryViewModel,
        DiscoverViewModel discoverViewModel,
        CollectionsViewModel collectionsViewModel,
        SettingsViewModel settingsViewModel,
        AboutViewModel aboutViewModel,
        ExportViewModel exportViewModel,
        FileService fileService,
        NavigationService navigationService)
    {
        _themeWatcher = themeWatcher;
        _mainViewModel = mainViewModel;
        _libraryViewModel = libraryViewModel;
        _discoverViewModel = discoverViewModel;
        _collectionsViewModel = collectionsViewModel;
        _settingsViewModel = settingsViewModel;
        _aboutViewModel = aboutViewModel;
        _exportViewModel = exportViewModel;
        _fileService = fileService;
        _navigationService = navigationService;
    }

    public void Initialize()
    {
        // Set home page as default    
        SelectedPage = _mainViewModel;
        CurrentRoute = "home";
    }

    private readonly NavigationService _navigationService;

    [RelayCommand]
    private void Back()
    {
        var previous = _navigationService.GoBack();
        if (previous != null)
        {
            CurrentRoute = previous.Value.Route;
            SelectedPage = previous.Value.Page;
            CanGoBack = _navigationService.CanGoBack;
        }
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
        _navigationService.NavigateTo(CurrentRoute, SelectedPage);
        CanGoBack = _navigationService.CanGoBack;
    }

    // Mobile-specific wrappers: navigate and close drawer
    [RelayCommand]
    private void OpenHomeMobile()
    {
        OpenHome();
        IsDrawerOpen = false;
    }

    [RelayCommand]
    private void OpenLibrary()
    {
        CurrentRoute = "library";
        SelectedPage = _libraryViewModel;
        _navigationService.NavigateTo(CurrentRoute, SelectedPage);
        CanGoBack = _navigationService.CanGoBack;
    }

    [RelayCommand]
    private void OpenLibraryMobile()
    {
        OpenLibrary();
        IsDrawerOpen = false;
    }

    [RelayCommand]
    private void OpenDiscover()
    {
        CurrentRoute = "discover";
        SelectedPage = _discoverViewModel;
        _navigationService.NavigateTo(CurrentRoute, SelectedPage);
        CanGoBack = _navigationService.CanGoBack;
    }

    [RelayCommand]
    private void OpenDiscoverMobile()
    {
        OpenDiscover();
        IsDrawerOpen = false;
    }

    [RelayCommand]
    private void OpenCollections()
    {
        CurrentRoute = "collections";
        SelectedPage = _collectionsViewModel;
        _navigationService.NavigateTo(CurrentRoute, SelectedPage);
        CanGoBack = _navigationService.CanGoBack;
    }

    [RelayCommand]
    private void OpenCollectionsMobile()
    {
        OpenCollections();
        IsDrawerOpen = false;
    }

    [RelayCommand]
    private void OpenSettings()
    {
        CurrentRoute = "settings";
        SelectedPage = _settingsViewModel;
        _navigationService.NavigateTo(CurrentRoute, SelectedPage);
        CanGoBack = _navigationService.CanGoBack;
    }

    [RelayCommand]
    private void OpenSettingsMobile()
    {
        OpenSettings();
        IsDrawerOpen = false;
    }

    [RelayCommand]
    private void OpenAboutMobile()
    {
        OpenAbout();
        IsDrawerOpen = false;
    }

    [RelayCommand]
    private void OpenAbout()
    {
        CurrentRoute = "about";
        SelectedPage = _aboutViewModel;
        _navigationService.NavigateTo(CurrentRoute, SelectedPage);
        CanGoBack = _navigationService.CanGoBack;
    }

    [RelayCommand]
    private void OpenExport()
    {
        CurrentRoute = "export";
        SelectedPage = _exportViewModel;
        _navigationService.NavigateTo(CurrentRoute, SelectedPage);
        CanGoBack = _navigationService.CanGoBack;
    }

    [RelayCommand]
    private void OpenExportMobile()
    {
        OpenExport();
        IsDrawerOpen = false;
    }

    // drawer state management 
    [ObservableProperty]
    private bool _isDrawerOpen = false;

    [ObservableProperty]
    private bool _isLoading = false;

    [RelayCommand]
    private void ToggleDrawer()
    {
        IsDrawerOpen = !IsDrawerOpen;
    }

    private async Task ImportEpub(IStorageFile file)
    {
        // TODO: Implement EPUB import
        await Task.Delay(100); // Placeholder
    }

    private async Task ImportPdf(IStorageFile file)
    {
        // TODO: Implement PDF import
        await Task.Delay(100); // Placeholder
    }

    private async Task ImportText(IStorageFile file)
    {
        // TODO: Implement text import
        await Task.Delay(100); // Placeholder
    }

    private async Task ImportMobi(IStorageFile file)
    {
        // TODO: Implement MOBI import
        await Task.Delay(100); // Placeholder
    }

    [RelayCommand]
    private async Task Import()
    {
        try
        {
            var files = await _fileService.OpenFilesAsync();
            if (files is null || !files.Any())
                return;

            // Show loading state - we'll need to add this property
            IsLoading = true;

            // Process each file
            foreach (var file in files)
            {
                try
                {
                    // Get the file extension
                    var extension = System.IO.Path.GetExtension(file.Name).ToLowerInvariant();

                    // Process based on file type
                    switch (extension)
                    {
                        case ".epub":
                            await ImportEpub(file);
                            break;
                        case ".pdf":
                            await ImportPdf(file);
                            break;
                        case ".txt":
                            await ImportText(file);
                            break;
                        case ".mobi":
                            await ImportMobi(file);
                            break;
                        default:
                            System.Diagnostics.Debug.WriteLine($"Unsupported file type: {extension}");
                            break;
                    }
                }
                catch (Exception ex)
                {
                    System.Diagnostics.Debug.WriteLine($"Error importing {file.Name}: {ex}");
                    // TODO: Add error to list for user feedback
                }
            }
        }
        catch (Exception ex)
        {
            System.Diagnostics.Debug.WriteLine($"Error in import process: {ex}");
            // TODO: Show error dialog
        }
        finally
        {
            IsLoading = false;
        }
    }
}