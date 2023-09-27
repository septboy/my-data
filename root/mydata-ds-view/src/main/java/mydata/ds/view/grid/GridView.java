package mydata.ds.view.grid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;

public class GridView implements FxmlView<GridViewModel> {

	private static final Logger logger = LoggerFactory.getLogger(GridView.class);
	
	@InjectViewModel
	private GridViewModel viewModel;

	@InjectContext
	private Context context ;
	
}
