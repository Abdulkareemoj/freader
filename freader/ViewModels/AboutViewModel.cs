using System.Reflection;
using CommunityToolkit.Mvvm.ComponentModel;

namespace freader.ViewModels;

public sealed partial class AboutViewModel : ViewModelBase
{
    public AboutViewModel()
    {
    }

    public string AppName => "freader";

    public string Version
    {
        get
        {
            var asm = Assembly.GetEntryAssembly() ?? Assembly.GetExecutingAssembly();
            var ver = asm.GetName().Version;
            return ver?.ToString() ?? "0.0.0";
        }
    }

    public string Copyright => "© 2025 freader";

    public string ProjectUrl => "https://github.com/Abdulkareemoj/freader";
}
