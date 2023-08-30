package mydata.ds.view.toolbar;

import java.util.Collections;
import java.util.List;

import de.saxsys.mvvmfx.Scope;
import de.saxsys.mvvmfx.ViewModel;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import mydata.ds.view.scopes.ContactDialogScope;
import mydata.ds.view.scopes.DataSetScope;

public class ToolbarViewModel implements ViewModel {

	@Inject
	private Instance<DataSetScope> scopeInstance;

	public List<Scope> getScopesForDataSet() {
		return Collections.singletonList(scopeInstance.get());
	}
}
