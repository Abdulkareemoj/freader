using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using Avalonia.Interactivity;
using freader.ViewModels;

using System.Threading.Tasks;

namespace freader.Views;

public partial class ExportView : UserControl
{
    public ExportView()
    {
        InitializeComponent();
    }

    private void InitializeComponent()
    {
        AvaloniaXamlLoader.Load(this);
    }

    private async void Export_Click(object? sender, RoutedEventArgs e)
    {
        if (this.DataContext is ExportViewModel vm)
        {
            await vm.ExportDataAsync();
        }
    }
}
