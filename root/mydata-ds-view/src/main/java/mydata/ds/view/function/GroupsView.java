package mydata.ds.view.function;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class GroupsView implements FxmlView<GroupsViewModel>  {

	private static final Logger logger = LoggerFactory.getLogger(GroupsView.class);
	
	@FXML
	private StackPane GroupsRootStackPane;
	
	@FXML
	private HBox groupsHBox;
	
	@FXML
	private Button addGroupButton;
	
	@FXML
	private TextField functionAliasTextField;
	
	@InjectViewModel
	private GroupsViewModel viewModel;

	public void initialize() {
		int groupsFuncPaneHashcode = GroupsRootStackPane.hashCode();

		viewModel.putBaseFunction(groupsFuncPaneHashcode, viewModel);
		viewModel.putRootFuncPaneMap(groupsFuncPaneHashcode, GroupsRootStackPane);
		
		AwesomeDude.setIcon(addGroupButton, AwesomeIcon.PLUS, "13.0");
		
		viewModel.setGroupsHBox(this.groupsHBox);
		
		// 테스트 코드
		functionAliasTextField.textProperty().bindBidirectional(viewModel.functionNameProperty());
		
		FunctionView.initializeHBoxDragAndDropEvent(groupsHBox);
	//	FunctionView.initializeHBoxDragAndDropEvent(groupsPartitionByHbox);
	}
	
}
