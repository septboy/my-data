package mydata.ds.view.main;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;

public class MainView implements FxmlView<MainViewModel> {

	@InjectViewModel
	private MainViewModel viewModel;
}
