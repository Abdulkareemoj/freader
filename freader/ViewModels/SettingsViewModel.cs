using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using ShadUI;
using System.Collections.ObjectModel;

namespace freader.ViewModels;

public partial class SettingsViewModel : ViewModelBase
{
    private readonly ThemeWatcher _themeWatcher;

    public SettingsViewModel(ThemeWatcher themeWatcher)
    {
        _themeWatcher = themeWatcher;
        _currentTheme = ThemeMode.System;
    }

    public ObservableCollection<ThemeMode> ThemeModes { get; } = new()
    {
        ThemeMode.System,
        ThemeMode.Light,
        ThemeMode.Dark
    };

    [ObservableProperty]
    private ThemeMode _currentTheme;

    partial void OnCurrentThemeChanged(ThemeMode value)
    {
        _themeWatcher.SwitchTheme(value);
    }
}