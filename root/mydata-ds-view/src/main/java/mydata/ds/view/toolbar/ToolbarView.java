package mydata.ds.view.toolbar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mydata.ds.view.dataset.DataSetView;
import mydata.ds.view.dataset.DataSetViewModel;
import mydata.ds.view.util.DataSetHelper;

public class ToolbarView implements FxmlView<ToolbarViewModel> {

	public static final Logger logger = LoggerFactory.getLogger(ToolbarView.class);
	
	@FXML
	public Button appatEHRButton;

	@FXML
	public Button emrdocFormButton;
	
	@FXML
	public Button prescriptionButton;

	@InjectViewModel
	private ToolbarViewModel viewModel;

	@InjectContext
	private Context context ;
	
	@Inject
	private Stage primaryStage;

	public void initialize() {
		AwesomeDude.setIcon(appatEHRButton, AwesomeIcon.PLUS);
		appatEHRButton.setOnMousePressed(this::handleMousePressedOnButton);
		
		AwesomeDude.setIcon(emrdocFormButton, AwesomeIcon.PLUS);
		emrdocFormButton.setOnMousePressed(this::handleMousePressedOnButton);
		
		AwesomeDude.setIcon(prescriptionButton, AwesomeIcon.PLUS);
		prescriptionButton.setOnMousePressed(this::handleMousePressedOnButton);
	
	}

	private void handleMousePressedOnButton(MouseEvent event) {
		logger.debug("handleMousePressedOnButton start!");
		
		Object source = event.getSource();
        if (source instanceof Button) {
        	Button button = (Button) source;
        	String buttonIdClicked = button.getId();
        	viewModel.getDataSetScope().setDataSetId(buttonIdClicked);
        	
        }
	}
	
	@FXML
	public void openNewDataSet() {
		logger.debug("openNewDataSet start!");
		
		ViewTuple<DataSetView, DataSetViewModel> load = FluentViewLoader
				.fxmlView(DataSetView.class)
				.context(context)
				.providedScopes(viewModel.getDataSetScope())
				.load();
		
		Parent dataSetView = load.getView();
		AnchorPane appRootAnchorPane = (AnchorPane) primaryStage.getScene().getRoot();

		DataSetHelper.openDataSet(appRootAnchorPane, dataSetView );
	}

}
