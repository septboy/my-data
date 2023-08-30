package mydata.ds.view.dataset;

import java.util.ResourceBundle;

import com.querydsl.core.types.SubQueryExpression;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import jakarta.inject.Inject;
import mydata.ds.view.model.Contact;
import mydata.ds.view.scopes.ContactDialogScope;
import mydata.ds.view.scopes.DataSetScope;

public class DataSetViewModel implements ViewModel {

	public static final String CLOSE_DATASET_NOTIFICATION = "closeDialog";

	static final String TITLE_LABEL_KEY = "dialog.addcontact.title";

	@Inject
	private DataSetService dataSetService;

	@InjectScope
	private DataSetScope dataSetScope;

	@Inject
	private ResourceBundle defaultResourceBundle;

	public void initialize() {
		dataSetScope.subscribe(ContactDialogScope.OK_BEFORE_COMMIT, (key, payload) -> {
			addContactAction();
		});

		dataSetScope.dialogTitleProperty().set(defaultResourceBundle.getString(TITLE_LABEL_KEY));
		dataSetScope.publish(ContactDialogScope.RESET_FORMS);
		Contact contact = new Contact();
		dataSetScope.setContactToEdit(contact);
	}

	public void addContactAction() {
		if (dataSetScope.isContactFormValid()) {

			dataSetScope.publish(ContactDialogScope.COMMIT);

			Contact contact = dataSetScope.getContactToEdit();

//			repository.save(contact);

			dataSetScope.publish(ContactDialogScope.RESET_DIALOG_PAGE);
			dataSetScope.setContactToEdit(null);

			publish(CLOSE_DATASET_NOTIFICATION);
		}
	}
	
	public SubQueryExpression<?> getEmrTerm() {
		return dataSetService.getEmrTerm();
	}

	public DataSetService getDataSetService() {
		return dataSetService;
	}

}
