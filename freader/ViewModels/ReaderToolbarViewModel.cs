using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using System.Windows.Input;

namespace freader.ViewModels
{
    public partial class ReaderToolbarViewModel : ViewModelBase
    {
        [ObservableProperty]
        private string _currentBookTitle = "The Lord of the Rings";

        public ReaderToolbarViewModel()
        {

        }

        [RelayCommand]
        private void GoBack()
        {
            // TODO: Implement go back logic
        }

        [RelayCommand]
        private void DecreaseFont()
        {
            // TODO: Implement decrease font logic
        }

        [RelayCommand]
        private void IncreaseFont()
        {
            // TODO: Implement increase font logic
        }

        [RelayCommand]
        private void ToggleTheme()
        {
            // TODO: Implement toggle theme logic
        }
    }
}