using System;
using System.Globalization;
using Avalonia.Data.Converters;
using ShadUI;

namespace freader.Converters;

public static class ThemeModeConverters
{
    public static readonly IValueConverter ToLucideIcon =
        new FuncValueConverter<ThemeMode, string>(mode => mode switch
        {
            ThemeMode.Light => "\uE2B1",      // Sun icon  
            ThemeMode.Dark => "\uE122",       // Moon icon  
            _ => "\uE2B2"                    // Monitor/System icon  
        });
}