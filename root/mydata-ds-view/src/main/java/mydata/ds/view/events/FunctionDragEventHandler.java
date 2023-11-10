package mydata.ds.view.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ds.data.core.column.Col;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import mydata.ds.view.executor.Executor;
import mydata.ds.view.function.FunctionInfo;
import mydata.ds.view.function.FunctionViewModel;
import mydata.ds.view.util.ViewUtils;

public class FunctionDragEventHandler {

	public static final String FUNCTION_SELECTED = "FUNCTION_SELECTED";

	private static final Logger logger = LoggerFactory.getLogger(FunctionDragEventHandler.class);

	public static final String css_function_label= 
			"-fx-border-width: 0 0 1 0; "
			+ "-fx-border-color: black; "
			+ "-fx-background-color: #d2b48c;"
			+ "-fx-text-fill: #40424a;";
	
	public static final String css_function_label_selected = 
			"-fx-border-width: 0 0 1 0; "
			+ "-fx-border-color: black; "
			+ "-fx-background-color: blue; "
			+ "-fx-text-fill: white;";

	private Node functionLabel;

	private Executor<Integer> executor;

	private VBox parent;

	private FunctionViewModel functionViewModel;

	private FunctionDragEventHandler() {}

	public static FunctionDragEventHandler newInstance(FunctionViewModel viewModel, VBox parent, Node node) {

		FunctionDragEventHandler functionEventHandler = new FunctionDragEventHandler();
		functionEventHandler.functionViewModel = viewModel;
		functionEventHandler.parent = parent;
		functionEventHandler.functionLabel = node;

		return functionEventHandler;
	}

	public void initialize() {

		this.functionLabel.addEventHandler(MouseDragEvent.DRAG_DETECTED, event -> {
			logger.debug("drag detected");

			FunctionInfo functionInfo = (FunctionInfo) functionLabel.getUserData();
			if (functionInfo == null) {
				event.consume();
				return ;
			} else {
				if ( !functionInfo.isSelected() ) {
					event.consume();
					return;
				}
			}
			
			Col<?> functionCol = getFunctionCol(functionLabel);
					
			functionInfo.setCol(functionCol);
			String funcName = functionViewModel.getFunctionLabelNameSelected(functionInfo.getFuncItemPaneHashcode());
			logger.debug("Function label Name selected ! -> {}", funcName);
			
			if (funcName != null)
				functionInfo.setColumnComment(funcName);
			
			this.functionLabel.setUserData(functionInfo);
			
			WritableImage snapshot = this.functionLabel.snapshot(null, null);

			ClipboardContent content = new ClipboardContent();
			content.putString(FUNCTION_SELECTED);

			Dragboard db = functionLabel.startDragAndDrop(TransferMode.MOVE);
			db.setContent(content);
			db.setDragView(snapshot);
			db.setDragViewOffsetX(snapshot.getWidth() / 2);
			db.setDragViewOffsetY(-snapshot.getHeight() / 2);

			event.consume();

		});

		this.functionLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			logger.debug("MouseEvent.MOUSE_CLICKED");
			if (event.getButton() == MouseButton.PRIMARY) {

				Node targetNode = (Node)event.getTarget();

				Label label = null;
				if (targetNode instanceof Label) {
					label = (Label) targetNode;
				} else {
					label = (Label) ViewUtils.searchParentNodeWithType(Label.class, targetNode);
				}

				if (label == null) {
					event.consume();
					return;
				}
				
				FunctionInfo functionInfo = (FunctionInfo) label.getUserData();
				if (functionInfo == null) {
					event.consume();
					return ;
				}
					
				if (functionInfo.isSelected()) {
					functionInfo.setSelected(false);
					label.setStyle(css_function_label);
					
				} else {
					setLabelsUnSelected();
					functionInfo.setSelected(true);
					label.setStyle(css_function_label_selected);
					int eachFuncRootPainHashcode = functionInfo.getFuncItemPaneHashcode();
					this.functionViewModel.makeVisibleLinkedFuncPane(eachFuncRootPainHashcode);
				}
			}

			event.consume();
		});

	}

	private Col<?> getFunctionCol(Node functionLabel) {
		FunctionInfo functionInfo = (FunctionInfo)functionLabel.getUserData();
		return this.functionViewModel.getFunctionCol(functionInfo.getFuncItemPaneHashcode()) ;
	}

	private void setLabelsUnSelected() {
		for( Node node: parent.getChildren() ) {
			FunctionInfo functionInfo = (FunctionInfo) node.getUserData();
			if (functionInfo == null) {
				continue ;
			}
				
			if (functionInfo.isSelected()) {
				functionInfo.setSelected(false);
				node.setStyle(css_function_label);
			}
		}
	}

	public void setExecutor(Executor<Integer> executor) {
		this.executor = executor;
	}

}
