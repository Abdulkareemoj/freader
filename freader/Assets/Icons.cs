using Avalonia.Media;

namespace freader.Assets;

public static class Icons
{
    /// <summary>
    /// Export / Download icon for export action
    /// </summary>
    // public static readonly StreamGeometry Export =
    //     Parse("M5,20H19V18H5V20M12,2L7,7H10V15H14V7H17L12,2Z");

    private static StreamGeometry Parse(string path) => StreamGeometry.Parse(path);
}