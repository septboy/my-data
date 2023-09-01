package mydata.ds.view.toolbar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.ViewModel;
import jakarta.inject.Inject;
import mydata.ds.view.scopes.ToolbarScope;

public class ToolbarViewModel implements ViewModel {

	private static Logger logger = LoggerFactory.getLogger(ToolbarViewModel.class);
	
	@Inject
	private ToolbarScope toolbarScope;

	public ToolbarScope getToolbarScope() {
		return toolbarScope;
	}
}
