using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using ShadUI;

namespace freader.ViewModels;

public sealed partial class SettingsViewModel : ViewModelBase
{
    private readonly ThemeWatcher _themeWatcher;

    [ObservableProperty]
    private ThemeMode _currentTheme = ThemeMode.System;

    public SettingsViewModel(ThemeWatcher themeWatcher)
    {
        _themeWatcher = themeWatcher;
        CurrentTheme = ThemeMode.System; // Initialize with system theme  
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
}