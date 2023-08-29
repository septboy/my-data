package mydata.ds.view.menu;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import mydata.ds.view.events.TriggerShutdownEvent;
import mydata.ds.view.model.Contact;
import mydata.ds.view.model.Repository;
import mydata.ds.view.scopes.MasterDetailScope;

public class MenuViewModel implements ViewModel {

	@Inject
	private Event<TriggerShutdownEvent> shouldCloseEvent;

	@InjectScope
	private MasterDetailScope mdScope;

	@Inject
	private Repository repository;

	private final ReadOnlyBooleanWrapper removeItemDisabled = new ReadOnlyBooleanWrapper();

	public void initialize() {
		removeItemDisabled.bind(mdScope.selectedContactProperty().isNull());
	}

	public void closeAction() {
		shouldCloseEvent.fire(new TriggerShutdownEvent());
	}

	public void removeAction() {
		Contact selectedContact = mdScope.selectedContactProperty().get();
		if (selectedContact != null) {
			repository.delete(mdScope.selectedContactProperty().get());
		}
	}

	public ReadOnlyBooleanProperty removeItemDisabledProperty() {
		return removeItemDisabled.getReadOnlyProperty();
	}

}
