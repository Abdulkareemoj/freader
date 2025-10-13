using Jab;
using freader.ViewModels;
using ShadUI;
using Avalonia;

namespace freader.Services;

[ServiceProvider]
[Singleton(typeof(ThemeWatcher), Factory = nameof(ThemeWatcherFactory))]
[Transient<MainWindowViewModel>]
[Transient<MainViewModel>]
[Transient<LibraryViewModel>]
[Transient<DiscoverViewModel>]
[Transient<CollectionsViewModel>]
[Transient<SettingsViewModel>]
[Transient<ReaderViewModel>]
[Transient<ReaderToolbarViewModel>]

public partial class ServiceProvider
{
    public ThemeWatcher ThemeWatcherFactory()
    {
        return new ThemeWatcher(Application.Current!);
    }
}