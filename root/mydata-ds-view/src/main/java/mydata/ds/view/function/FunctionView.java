package mydata.ds.view.function;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mydata.ds.view.events.ColumnEventHandler;
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
			FunctionEventHandler functionEventHandler = FunctionEventHandler.newInstance(viewModel, functionLabelVBox, label);
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
	
	protected static Label getColumnLabel(ColumnInfo columnInfo) {
		String columnName = columnInfo.getColumnComment();
		Label fuctionParamLabel = new Label(columnName);
		fuctionParamLabel.setUserData(columnInfo);
		fuctionParamLabel.setAlignment(javafx.geometry.Pos.CENTER);
		fuctionParamLabel.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
		ViewUtils.setWidth(fuctionParamLabel, 80.0);
		ViewUtils.setHeight(fuctionParamLabel, 25.0);
		fuctionParamLabel.setStyle("-fx-background-color: orange;-fx-background-radius: 5;");
		fuctionParamLabel.setTextFill(Color.web("white"));

		// Set the font for the label
		Font font = Font.font("Arial Rounded MT Bold",FontWeight.BOLD, 14.0);
		fuctionParamLabel.setFont(font);
		
		return fuctionParamLabel;
	}
	
	public static void initializeHBoxDragAndDropEvent(HBox hBox) {
		hBox.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
            	
            	String dragBoardString = event.getDragboard().getString();
                if (event.getGestureSource() != hBox && event.getDragboard().hasString()) {
                	if ( dragBoardString.equals(ColumnEventHandler.COLUM_JOIN_SELECTED)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                	}
                }

                event.consume();
            }
        });

		hBox.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
            	System.out.println("Dropped: " + db.getString());
            	
            	Object eventSourceObj = event.getGestureSource();
            	if (! (eventSourceObj instanceof Label) ) {
            		event.setDropCompleted(false);
            		event.consume();
            		return ;
            	}
            		
            	Label sourceLabel = (Label)eventSourceObj;
            	ColumnInfo columnInfo = (ColumnInfo)sourceLabel.getUserData();
            	
            	Label columnLabel = getColumnLabel(columnInfo);
            	hBox.setAlignment(Pos.CENTER);
            	hBox.getChildren().add(columnLabel);
            	hBox.setMargin(columnLabel, new Insets(10.0, 5.0, 10.0, 5.0));
            	
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
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
