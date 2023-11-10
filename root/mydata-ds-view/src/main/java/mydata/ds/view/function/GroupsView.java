package mydata.ds.view.function;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import mydata.ds.view.events.FunctionDropEventManager;
import mydata.ds.view.util.ViewUtils;

public class GroupsView implements FxmlView<GroupsViewModel>  {

	private static final Logger logger = LoggerFactory.getLogger(GroupsView.class);
	
	@FXML
	private StackPane GroupsRootStackPane;
	
	@FXML
	private HBox groupsHBox;
	
	@FXML
	private VBox groupListVBox;
	
	@FXML
	private Button addGroupButton;
	
	@FXML
	private TextField functionAliasTextField;
	
	@InjectViewModel
	private GroupsViewModel viewModel;

	@Inject
	FunctionDropEventManager functionDropEventManager;
	
	public void initialize() {
		int groupsFuncPaneHashcode = GroupsRootStackPane.hashCode();

		viewModel.putBaseFunction(groupsFuncPaneHashcode, viewModel);
		viewModel.putRootFuncPaneMap(groupsFuncPaneHashcode, GroupsRootStackPane);
		
		AwesomeDude.setIcon(addGroupButton, AwesomeIcon.PLUS, "11.0");
		
		viewModel.setGroupsHBox(this.groupsHBox);
		
		// 테스트 코드
		functionAliasTextField.textProperty().bindBidirectional(viewModel.functionNameProperty());
		
		functionDropEventManager.initializeHBoxDragAndDropEvent(groupsHBox);
		
		addButtonClickEvent(addGroupButton);
	}
	
	
	private void addButtonClickEvent(Button addGroupButton) {
		addGroupButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	HBox groupHBox = getGroupHBox();
            	ViewUtils.addNodeIntoPane(groupListVBox, groupHBox);
            }
		});
	}


	private HBox getGroupHBox() {
		 // Create the HBox
        HBox hBox = new HBox();
        hBox.setMaxHeight(30.0);
        hBox.setMinHeight(30.0);
        hBox.setPrefHeight(30.0);
        hBox.setPrefWidth(200.0);

        // Create the TextField
        TextField textField = new TextField();
        textField.setPrefWidth(290.0);
        HBox.setMargin(textField, new javafx.geometry.Insets(0, 2, 0, 2));

        // Create the "removeGroupButton"
        Button removeGroupButton = new Button();
        removeGroupButton.setMaxHeight(18.0);
        removeGroupButton.setMaxWidth(18.0);
        removeGroupButton.setMinHeight(18.0);
        removeGroupButton.setMinWidth(18.0);
        removeGroupButton.setPrefHeight(18.0);
        removeGroupButton.setPrefWidth(18.0);
        removeGroupButton.setMnemonicParsing(false);
        HBox.setMargin(removeGroupButton, new javafx.geometry.Insets(2, 3, 0, 0));
        AwesomeDude.setIcon(removeGroupButton, AwesomeIcon.MINUS, "11.0");
        
        // Add the elements to the HBox
        hBox.getChildren().addAll(textField, removeGroupButton);

        return hBox ;
	}
}
