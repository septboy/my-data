package mydata.ds.view.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlPath;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;

@FxmlPath("/some/other/path/HalloWelt.fxml")
public class ConditionView implements FxmlView<ConditionViewModel> {

	private static final Logger logger = LoggerFactory.getLogger(ConditionView.class);
	
	@InjectViewModel
	private ConditionViewModel viewModel;

	@InjectContext
	private Context context ;
	
	public void initialize() {
		logger.debug("ConditionView initialize!");
	}
	
}
