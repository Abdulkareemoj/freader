using Jab;
using freader.ViewModels;
using ShadUI;
using Avalonia;

namespace freader.Services;

[ServiceProvider]
[Singleton<ThemeWatcher>(Factory = nameof(ThemeWatcherFactory))]
[Transient<MainWindowViewModel>]
[Transient<MainViewModel>]
[Transient<LibraryViewModel>]
[Transient<DiscoverViewModel>]
[Transient<CollectionsViewModel>]
[Transient<SettingsViewModel>]
[Transient<AboutViewModel>]
[Transient<ExportViewModel>]
[Transient<ReaderViewModel>]
[Transient<FileService>]
[Transient<ReaderToolbarViewModel>]

public partial class ServiceProvider
{
    public ThemeWatcher ThemeWatcherFactory()
    {
        return new ThemeWatcher(Application.Current!);
    }
}