using CommunityToolkit.Mvvm.ComponentModel;

namespace freader.ViewModels
{
    public partial class ReaderViewModel : ViewModelBase
    {
        [ObservableProperty]
        private string _bookContent = "Lorem ipsum dolor sit amet...";

        [ObservableProperty]
        private int _fontSize = 16;

        [ObservableProperty]
        private ReaderToolbarViewModel _toolbar;

        public ReaderViewModel(ReaderToolbarViewModel toolbarViewModel)
        {
            _toolbar = toolbarViewModel;
        }
    }
}