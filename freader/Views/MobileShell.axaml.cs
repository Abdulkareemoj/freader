using Avalonia.Markup.Xaml;
using Avalonia.Controls;

namespace freader.Views;

public partial class MobileShell : UserControl
{
    public MobileShell()
    {
        InitializeComponent();
    }

    private void InitializeComponent()
    {
        AvaloniaXamlLoader.Load(this);
    }
}
