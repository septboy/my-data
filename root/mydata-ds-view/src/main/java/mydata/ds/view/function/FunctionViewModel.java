package mydata.ds.view.function;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.ViewModel;
import jakarta.inject.Inject;
import javafx.scene.layout.Pane;
import mydata.ds.view.scopes.AppContext;

public class FunctionViewModel implements ViewModel {

	@Inject
	private AppContext appContext;
	
	@Inject
	private ResourceBundle defaultResourceBundle;

	
	public void initialize() {
	}

	public void closeFunctionView(Pane relationViewPane) {
	}


}
