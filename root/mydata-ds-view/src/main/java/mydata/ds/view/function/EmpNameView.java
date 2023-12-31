package mydata.ds.view.function;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import mydata.ds.view.events.FunctionDropEventManager;

public class EmpNameView implements FxmlView<EmpNameViewModel>  {

	private static final Logger logger = LoggerFactory.getLogger(EmpNameView.class);
	
	@FXML
	private StackPane EmpNameRootStackPane;
	
	@FXML
	private HBox empNameHBox;
	
	@FXML
	private TextField functionAliasTextField;
	
	@InjectViewModel
	private EmpNameViewModel viewModel;

	@Inject
	FunctionDropEventManager functionDropEventManager;
	
	public void initialize() {
		int empNameFuncPaneHashcode = EmpNameRootStackPane.hashCode();

		viewModel.putBaseFunction(empNameFuncPaneHashcode, viewModel);
		viewModel.putRootFuncPaneMap(empNameFuncPaneHashcode, EmpNameRootStackPane);
		
		viewModel.setEmpNameHBox(this.empNameHBox);
		
		// 테스트 코드
		functionAliasTextField.textProperty().bindBidirectional(viewModel.functionNameProperty());
		
		functionDropEventManager.initializeHBoxDragAndDropEvent(empNameHBox);
		
	}

	
}
