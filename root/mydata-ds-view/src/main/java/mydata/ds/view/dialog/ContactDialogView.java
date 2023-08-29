package mydata.ds.view.dialog;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Pagination;
import javafx.scene.text.Text;
import mydata.ds.view.addressform.AddressFormView;
import mydata.ds.view.addressform.AddressFormViewModel;

public class ContactDialogView implements FxmlView<ContactDialogViewModel> {

	@FXML
	private Button okButton;

	@FXML
	private Button previousButton;

	@FXML
	private Button nextButton;

	@FXML
	private Text titleText;

	@FXML
	private Pagination formPagination;

	@InjectViewModel
	private ContactDialogViewModel viewModel;

	@InjectContext
	private Context context;

	public void initialize() {

		ViewTuple<AddressFormView, AddressFormViewModel> addressFormTuple = FluentViewLoader
				.fxmlView(AddressFormView.class)
				.context(context)
				.load();

		formPagination.getStyleClass().add("invisible-pagination-control");

		formPagination.setPageFactory(index -> {
			// 하나 더 있는데 지웠음.
				return addressFormTuple.getView();
		});

		formPagination.currentPageIndexProperty().bindBidirectional(viewModel.dialogPageProperty());

		AwesomeDude.setIcon(okButton, AwesomeIcon.CHECK);
		AwesomeDude.setIcon(nextButton, AwesomeIcon.CHEVRON_RIGHT, ContentDisplay.RIGHT);
		AwesomeDude.setIcon(previousButton, AwesomeIcon.CHEVRON_LEFT);

		okButton.disableProperty().bind(viewModel.okButtonDisabledProperty());
		okButton.visibleProperty().bind(viewModel.okButtonVisibleProperty());
		okButton.managedProperty().bind(viewModel.okButtonVisibleProperty());

		nextButton.disableProperty().bind(viewModel.nextButtonDisabledProperty());
		nextButton.visibleProperty().bind(viewModel.nextButtonVisibleProperty());
		nextButton.managedProperty().bind(viewModel.nextButtonVisibleProperty());

		previousButton.disableProperty().bind(viewModel.previousButtonDisabledProperty());
		previousButton.visibleProperty().bind(viewModel.previousButtonVisibleProperty());
		previousButton.managedProperty().bind(viewModel.previousButtonVisibleProperty());

		titleText.textProperty().bind(viewModel.titleTextProperty());
	}

	@FXML
	private void previous() {
		viewModel.previousAction();
	}

	@FXML
	private void next() {
		viewModel.nextAction();
	}

	@FXML
	private void ok() {
		viewModel.okAction();
	}

	public ContactDialogViewModel getViewModel() {
		return viewModel;
	}

}
