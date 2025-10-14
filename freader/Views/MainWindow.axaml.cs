using Avalonia.Controls;
using Avalonia.Markup.Xaml;
namespace freader.Views;

using ShadUI;

public partial class MainWindow : Window  // ShadUI.Window, not Avalonia.Controls.Window  
{
    public MainWindow()
    {
        InitializeComponent();
    }

    private void InitializeComponent()
    {
#if MOBILE
        AvaloniaXamlLoader.Load(this, new Uri("avares://freader/Views/MainWindow.Mobile.axaml"));  //Utilizing Mobile layout instead
#else
        AvaloniaXamlLoader.Load(this);
#endif
    }

}