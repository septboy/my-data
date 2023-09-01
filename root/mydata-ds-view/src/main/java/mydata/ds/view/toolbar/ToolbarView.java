package mydata.ds.view.toolbar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	@InjectViewModel
	private ToolbarViewModel viewModel;

	@Inject
	private Stage primaryStage;

	private String buttonIdClicked;
	
	public void initialize() {
		AwesomeDude.setIcon(appatEHRButton, AwesomeIcon.PLUS);
		AwesomeDude.setIcon(emrdocFormButton, AwesomeIcon.PLUS);
		
		appatEHRButton.setOnMousePressed(this::handleMousePressedOnButton);
		emrdocFormButton.setOnMousePressed(this::handleMousePressedOnButton);
	}

	private void handleMousePressedOnButton(MouseEvent event) {
		logger.debug("handleMousePressedOnButton start!");
		
		Object source = event.getSource();
        if (source instanceof Button) {
        	Button button = (Button) source;
        	buttonIdClicked = button.getId();
        }
	}
	
	@FXML
	public void openNewDataSet() {
		logger.debug("openNewDataSet start!");
		
		ViewTuple<DataSetView, DataSetViewModel> load = FluentViewLoader
				.fxmlView(DataSetView.class)
				.load();
		Parent dataSetView = load.getView();
		load.getViewModel().setDataSetName(buttonIdClicked);
		AnchorPane rootAnchorPane = (AnchorPane) primaryStage.getScene().getRoot();

		DataSetHelper.openDataSet(rootAnchorPane, dataSetView );
	}

}
