package mydata.ds.view.function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import ds.common.util.ArrayUtil;
import ds.data.core.column.Col;
import ds.data.core.column.ColumnInfo;
import jakarta.inject.Inject;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mydata.ds.view.events.ColumnEventHandler;
import mydata.ds.view.events.FunctionDragEventHandler;
import mydata.ds.view.events.FunctionDropEventManager;
import mydata.ds.view.util.ViewUtils;

public class FunctionView implements FxmlView<FunctionViewModel> {

	private static final Logger logger = LoggerFactory.getLogger(FunctionView.class);
	
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
		initializeDragEventBlock();
		
	}

	private void initializeDragEventBlock() {
		//DataSetView로의 이벤트버블링 방지.
		functionViewPain.addEventHandler(MouseEvent. MOUSE_DRAGGED, event -> {
			event.consume();
		});
		
		functionViewPain.setOnDragOver(event -> {
			logger.debug("setOnDragOver");
			 Dragboard db = event.getDragboard();
	    	if (db.hasString() && db.getString().equals(FunctionDropEventManager.FUNCTION_PARAMETER_LABEL_DRAG) ) {
	            event.acceptTransferModes(TransferMode.MOVE);
	        }
	
	        event.consume();
	    });
		
		functionViewPain.setOnDragDropped(event -> {
	        Dragboard db = event.getDragboard();
	        logger.debug("Dropped: {}", db.getString());
	        boolean success = false;
	
	        if (db.hasString()) {
	            if (db.getString().equals(FunctionDropEventManager.FUNCTION_PARAMETER_LABEL_DRAG)) {
	            	Node source = (Node)event.getGestureSource();
	            	ColumnInfo columnInfo = (ColumnInfo)source.getUserData();
	            	Pane wapperPane = (Pane)columnInfo.getObject();
	            	ViewUtils.removeFromPane(wapperPane, source);
	            	columnInfo.setObject(null);
	            	success = true;
	            }
	        }
	        
	        event.setDropCompleted(success);
	        event.consume();
	    });
	}

	private void initializeFunctionLabels() {
		ObservableList<Node> nodeList = functionLabelVBox.getChildren();
		for (Node node: nodeList) {
			Label label = (Label)node ;
			String labelText = label.getText();
			FunctionInfo functionInfo= new FunctionInfo();
			functionInfo.setColumnComment(labelText);
			
			StackPane funcItemPain = getLinkedStackPane(labelText);
			if (funcItemPain != null)
				viewModel.addFuncItemPaneHashcode(funcItemPain.hashCode());
			
			if ( funcItemPain != null) {
				funcItemPain.setVisible(false);
				functionInfo.setFunctionRootStackPaneHashcode(funcItemPain.hashCode());
			}
			
			label.setUserData(functionInfo);
			FunctionDragEventHandler functionEventHandler = FunctionDragEventHandler.newInstance(viewModel, functionLabelVBox, label);
			functionEventHandler.initialize();
		}
		
	}

	private StackPane getLinkedStackPane(String labelText) {
		String functionStackPaneId = labelText+"RootStackPane" ; 
		StackPane eachStackPain = (StackPane)ViewUtils.findNodeById(functionViewPain, functionStackPaneId);
		return eachStackPain;
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
	
	public ColumnInfo[] getColumnInfos(ObservableList<Node> nodeList) {
		ColumnInfo[] columnInfos = null ;
		for (Node node: nodeList) {
			ColumnInfo columnInfo = (ColumnInfo)node.getUserData();
			if( columnInfo != null)
				columnInfos = ArrayUtil.addArrayOne(columnInfos, columnInfo, ColumnInfo.class);
		}
		return columnInfos;
	}
	
}
