package mydata.ds.view.function;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import mydata.ds.view.events.FunctionDropEventManager;

public class CountView implements FxmlView<CountViewModel>  {

	private static final Logger logger = LoggerFactory.getLogger(CountView.class);
	
	@FXML
	private StackPane CountRootStackPane;
	
	@FXML
	private HBox countHBox;
	
	@FXML
	private HBox countPartitionByHbox;
	
	@FXML
	private TextField functionAliasTextField;
	
	@InjectViewModel
	private CountViewModel viewModel;

	@Inject
	FunctionDropEventManager functionDropEventManager;
	
	public void initialize() {
		int countFuncPaneHashcode = CountRootStackPane.hashCode();

		viewModel.putBaseFunction(countFuncPaneHashcode, viewModel);
		viewModel.putRootFuncPaneMap(countFuncPaneHashcode, CountRootStackPane);
		
		viewModel.setCountHBox(this.countHBox);
		viewModel.setCountPartitionByHbox(this.countPartitionByHbox);
		
		// 테스트 코드
		functionAliasTextField.textProperty().bindBidirectional(viewModel.functionNameProperty());
		
		functionDropEventManager.initializeHBoxDragAndDropEvent(countHBox);
		functionDropEventManager.initializeHBoxDragAndDropEvent(countPartitionByHbox);
	}
	
}
