using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using System;
using System.Diagnostics;
using Avalonia.Interactivity;
using Avalonia.VisualTree;
using freader.ViewModels;

namespace freader.Views;

public partial class AboutView : UserControl
{
    public AboutView()
    {
        InitializeComponent();
    }

    private void InitializeComponent()
    {
        AvaloniaXamlLoader.Load(this);
    }

    private void VisitProject_Click(object? sender, RoutedEventArgs e)
    {
        var vm = this.DataContext as AboutViewModel;
        if (vm is null) return;
        OpenUrl(vm.ProjectUrl);
    }

    private void ProjectLink_Click(object? sender, RoutedEventArgs e)
    {
        var vm = this.DataContext as AboutViewModel;
        if (vm is null) return;
        OpenUrl(vm.ProjectUrl);
    }

    private void Close_Click(object? sender, RoutedEventArgs e)
    {
        // Walk the visual parent chain to find MainWindowViewModel
        Control? current = this;
        while (current is not null)
        {
            if (current.DataContext is MainWindowViewModel mainVm)
            {
                mainVm.BackCommand.Execute(null);
                return;
            }
            current = current.Parent as Control;
        }
    }

    private static void OpenUrl(string url)
    {
        try
        {
            // Only attempt Process.Start on desktop OSes. On platforms like iOS/maccatalyst
            // Process.Start is unsupported and will raise platform compatibility warnings.
            if (System.Runtime.InteropServices.RuntimeInformation.IsOSPlatform(System.Runtime.InteropServices.OSPlatform.Windows)
                || System.Runtime.InteropServices.RuntimeInformation.IsOSPlatform(System.Runtime.InteropServices.OSPlatform.Linux)
                || System.Runtime.InteropServices.RuntimeInformation.IsOSPlatform(System.Runtime.InteropServices.OSPlatform.OSX))
            {
                var psi = new ProcessStartInfo
                {
                    FileName = url,
                    UseShellExecute = true
                };
                Process.Start(psi);
            }
        }
        catch
        {
            // best-effort: ignore failures
        }
    }
}
