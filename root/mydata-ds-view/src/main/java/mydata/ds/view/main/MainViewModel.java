package mydata.ds.view.main;

import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import jakarta.inject.Inject;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import mydata.ds.view.scopes.AppContext;
import mydata.ds.view.scopes.ApplicationScope;


@ScopeProvider(ApplicationScope.class)
public class MainViewModel implements ViewModel {

	@Inject
	private AppContext appContext ;
	
	public void putBackGroundToAppContext(Pane appBackground) {
		appContext.putNodeToAppContext(appBackground.hashCode(), appBackground);
	}

	
}
