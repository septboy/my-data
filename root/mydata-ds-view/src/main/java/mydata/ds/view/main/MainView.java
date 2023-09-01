package mydata.ds.view.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;

public class MainView implements FxmlView<MainViewModel> {

	private static final Logger logger = LoggerFactory.getLogger(MainView.class);
	
	@InjectViewModel
	private MainViewModel viewModel;

	@InjectContext
	private Context context ;
	
}
