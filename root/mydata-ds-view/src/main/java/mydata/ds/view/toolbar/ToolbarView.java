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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mydata.ds.view.dataset.DataSetView;
import mydata.ds.view.dataset.DataSetViewModel;
import mydata.ds.view.main.MainView;
import mydata.ds.view.util.DataSetHelper;
import mydata.ds.view.util.DialogHelper;

public class ToolbarView implements FxmlView<ToolbarViewModel> {

	@FXML
	public Button appatEHRButton;

	@FXML
	public Button emrdocFormButton;

	@InjectViewModel
	private ToolbarViewModel viewModel;

	@Inject
	private Stage primaryStage;

	public void initialize() {
		AwesomeDude.setIcon(appatEHRButton, AwesomeIcon.PLUS);
		AwesomeDude.setIcon(emrdocFormButton, AwesomeIcon.PLUS);
	}

	@FXML
	public void openNewDataSet() {

		ViewTuple<DataSetView, DataSetViewModel> load = FluentViewLoader
				.fxmlView(DataSetView.class)
				.providedScopes(viewModel.getScopesForDataSet())
				.load();
		Parent dataSetView = load.getView();
		AnchorPane retrievedAnchorPane = (AnchorPane) primaryStage.getScene().getRoot();
		DataSetHelper.openDataSet(retrievedAnchorPane, dataSetView );
	}

}
