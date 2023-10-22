package mydata.ds.view.function;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import jakarta.inject.Inject;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mydata.ds.view.events.FunctionEventHandler;
import mydata.ds.view.util.ViewUtils;

public class FunctionView implements FxmlView<FunctionViewModel> {

	@FXML
	private AnchorPane functionViewPain;
	
	@FXML
	private AnchorPane functionTitle;
	
	@FXML
	private VBox functionLabelVBox;

	
	@InjectViewModel
	private FunctionViewModel viewModel;

	@Inject
	private Stage primaryStage;

	private AnchorPane sourceDataSetPane;

	public void initialize() {
		initializeCloseButton();
		initializeFunctionLabels();
		initializeDragEventBlcok();
	}

	private void initializeDragEventBlcok() {
		//DataSetView로의 이벤트버블링 방지.
		functionViewPain.addEventHandler(MouseEvent. MOUSE_DRAGGED, event -> {
			event.consume();
		});
	}

	private void initializeFunctionLabels() {
		ObservableList<Node> nodeList = functionLabelVBox.getChildren();
		for (Node node: nodeList) {
			Label label = (Label)node ;
			
			FunctionInfo functionInfo= new FunctionInfo();
			functionInfo.setColumnComment(label.getText());
			label.setUserData(functionInfo);
			FunctionEventHandler functionEventHandler = FunctionEventHandler.newInstance(functionLabelVBox, label);
			//columnSelectEventHandler.setExecutor(executor);
		}
		
	}

	private void initializeCloseButton() {
		Button closeButton = ViewUtils.getImageButton("/image/close-x.png", 21);
		closeButton.setStyle("-fx-background-color: #8b0000;");
		functionTitle.getChildren().add(closeButton);
		AnchorPane.setRightAnchor(closeButton, 0.0);
		
		closeButton.setOnAction(this::handleEventCloseButton);
	}
	
	private void handleEventCloseButton(ActionEvent event) {
		ViewUtils.removeFromPane(sourceDataSetPane, functionViewPain);
	}

	public void setSourceDataSetPane(AnchorPane sourceDataSetPane) {
		this.sourceDataSetPane = sourceDataSetPane;
		
	}
	
}
