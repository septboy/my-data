package mydata.ds.view.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import mydata.ds.view.model.Contact;

public class ApplicationScope implements Scope {

	public static final String ADD_OR_REMOVE_GRID_COLUMN = "ADD_OR_REMOVE_GRID_COLUMN";
	
	private final ObjectProperty<Contact> selectedContact = new SimpleObjectProperty<>(this, "selectedContact");

	public ObjectProperty<Contact> selectedContactProperty() {
		return this.selectedContact;
	}

	public final Contact getSelectedContact() {
		return this.selectedContactProperty().get();
	}

	public final void setSelectedContact(final Contact selectedContact) {
		this.selectedContactProperty().set(selectedContact);
	}

}
