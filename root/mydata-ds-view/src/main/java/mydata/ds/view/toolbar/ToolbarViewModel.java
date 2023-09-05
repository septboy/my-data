package mydata.ds.view.toolbar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import jakarta.inject.Inject;
import mydata.ds.view.scopes.DataSetScope;
import mydata.ds.view.scopes.ToolbarScope;

@ScopeProvider(scopes = {DataSetScope.class})
public class ToolbarViewModel implements ViewModel {

	private static Logger logger = LoggerFactory.getLogger(ToolbarViewModel.class);
	
	@Inject
	private DataSetScope dataSetScope;

	public DataSetScope getDataSetScope() {
		return dataSetScope;
	}
}
