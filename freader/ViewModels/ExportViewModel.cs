using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using System.Threading.Tasks;
using System.IO;
using System.Text.Json;

namespace freader.ViewModels;

public partial class ExportViewModel : ViewModelBase
{
    [RelayCommand]
    public async Task ExportDataAsync()
    {
        // Placeholder: export a small JSON summary to user's Documents folder
        var data = new { exportedAt = System.DateTime.UtcNow, notes = "User progress export (placeholder)" };
        var json = JsonSerializer.Serialize(data, new JsonSerializerOptions { WriteIndented = true });
        var path = Path.Combine(System.Environment.GetFolderPath(System.Environment.SpecialFolder.MyDocuments), "freader_export.json");
        await File.WriteAllTextAsync(path, json);
    }
}
