package mydata.ds.view.toolbar;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mydata.ds.view.dataset.AddContactDialogView;
import mydata.ds.view.dataset.AddContactDialogViewModel;
import mydata.ds.view.util.DialogHelper;

public class ToolbarView implements FxmlView<ToolbarViewModel> {

	@FXML
	public Button addNewContactButton;

	@InjectViewModel
	private ToolbarViewModel viewModel;

	@Inject
	private Stage primaryStage;

	public void initialize() {
		AwesomeDude.setIcon(addNewContactButton, AwesomeIcon.PLUS);
	}

	@FXML
	public void addNewContact() {
		ViewTuple<AddContactDialogView, AddContactDialogViewModel> load = FluentViewLoader
				.fxmlView(AddContactDialogView.class)
				.providedScopes(viewModel.getScopesForAddDialog())
				.load();
		Parent view = load.getView();
		Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/contacts.css");
		load.getCodeBehind().setDisplayingStage(showDialog);
	}

}
