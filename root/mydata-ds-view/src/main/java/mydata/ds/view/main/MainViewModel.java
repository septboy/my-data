package mydata.ds.view.main;

import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import jakarta.inject.Inject;
import javafx.scene.Node;
import mydata.ds.view.scopes.AppContext;
import mydata.ds.view.scopes.ApplicationScope;


@ScopeProvider(ApplicationScope.class)
public class MainViewModel implements ViewModel {

	@Inject
	private AppContext appContext ;


	public void initalizeBackgrounEvent(Node background) {
		appContext.initalizeBackgroundEvent(background);
	}
	
	
}
