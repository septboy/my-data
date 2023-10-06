package mydata.ds.view.model;

import java.util.ResourceBundle;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import ds.data.core.dao.DatabaseManager;
import jakarta.inject.Inject;
import mydata.ds.view.scopes.AppContext;
import mydata.ds.view.scopes.ApplicationScope;

public class DSViewModel implements ViewModel{
	@Inject
	private DatabaseManager databaseManager ;
	
	@Inject
	private AppContext appContext;
	
	@InjectScope
	private ApplicationScope applicationScope ;
	
	@Inject
	private ResourceBundle defaultResourceBundle;

	public AppContext getAppContext() {
		return appContext;
	}

	public ApplicationScope getApplicationScope() {
		return applicationScope;
	}

	public ResourceBundle getDefaultResourceBundle() {
		return defaultResourceBundle;
	}

	public void closeEntityManager() {
		databaseManager.closeConnectionAndManager();
	}
	
	public void rebuildEntityManager() {
		databaseManager.initialize();
	}
}
