package mydata.ds.view.relation;

import java.util.ResourceBundle;

import de.saxsys.mvvmfx.ViewModel;
import jakarta.inject.Inject;
import mydata.ds.view.scopes.AppContext;

public class RelationViewModel implements ViewModel {

	@Inject
	private AppContext appContext;
	
	@Inject
	private ResourceBundle defaultResourceBundle;
	
	public void initialize() {
		
	}


}
