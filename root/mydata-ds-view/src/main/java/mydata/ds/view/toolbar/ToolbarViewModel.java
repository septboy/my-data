package mydata.ds.view.toolbar;

import java.util.Collections;
import java.util.List;

import de.saxsys.mvvmfx.Scope;
import de.saxsys.mvvmfx.ViewModel;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import mydata.ds.view.scopes.ContactDialogScope;

public class ToolbarViewModel implements ViewModel {

	@Inject
	private Instance<ContactDialogScope> scopeInstance;

	public List<Scope> getScopesForAddDialog() {
		return Collections.singletonList(scopeInstance.get());
	}
}
