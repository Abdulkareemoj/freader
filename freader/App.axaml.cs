using Avalonia;
using Avalonia.Controls.ApplicationLifetimes;
using Avalonia.Data.Core.Plugins;
using System.Linq;
using Avalonia.Markup.Xaml;
using freader.Views;

using System.Threading;
using freader.ViewModels;
using ShadUI;


namespace freader;

public partial class App : Application
{
    public override void Initialize()
    {
        AvaloniaXamlLoader.Load(this);
    }
    private static Mutex? _appMutex;

    public override void OnFrameworkInitializationCompleted()
    {
        if (ApplicationLifetime is IClassicDesktopStyleApplicationLifetime desktop)
        {
            _appMutex = new Mutex(true, "FreaderSingleInstanceMutex", out var createdNew);
            if (!createdNew)
            {
                var instanceDialog = new InstanceDialog();
                instanceDialog.Show();
                return;
            }

            DisableAvaloniaDataAnnotationValidation();
            var provider = new Services.ServiceProvider();

            var themeWatcher = provider.GetService<ThemeWatcher>();
            themeWatcher.Initialize();
            var viewModel = provider.GetService<MainWindowViewModel>();
            viewModel.Initialize();

            var mainWindow = new MainWindow { DataContext = viewModel };
            this.RegisterTrayIconsEvents(mainWindow, viewModel);

            desktop.MainWindow = mainWindow;  // Use the mainWindow you just created!  
        }
        else if (ApplicationLifetime is ISingleViewApplicationLifetime singleViewPlatform)
        {
            singleViewPlatform.MainView = new MainView
            {
                DataContext = new MainViewModel()
            };
        }

        base.OnFrameworkInitializationCompleted();
    }
    private void DisableAvaloniaDataAnnotationValidation()
    {
        // Get an array of plugins to remove
        var dataValidationPluginsToRemove =
            BindingPlugins.DataValidators.OfType<DataAnnotationsValidationPlugin>().ToArray();

        // remove each entry found
        foreach (var plugin in dataValidationPluginsToRemove)
        {
            BindingPlugins.DataValidators.Remove(plugin);
        }
    }

}