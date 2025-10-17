using Avalonia.Markup.Xaml;

namespace freader.Views;

public partial class MainWindowMobile : ShadUI.Window
{
    public MainWindowMobile()
    {
        InitializeComponent();
    }

    private void InitializeComponent()
    {
        AvaloniaXamlLoader.Load(this);
    }
}