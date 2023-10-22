package mydata.ds.view.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.types.Expression;

import de.saxsys.mvvmfx.data.TableViewData;
import ds.data.core.column.ColumnInfo;
import ds.data.core.util.ColUtils;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import mydata.ds.view.executor.Executor;
import mydata.ds.view.util.ViewUtils;

public class ColumnEventHandler {

	public static final String COLUM_JOIN_SELECTED = "colum_join_selected";

	private static final Logger logger = LoggerFactory.getLogger(ColumnEventHandler.class);
	
	public static final String css_column_label = 
			"-fx-border-color: white; "
			+ "-fx-border-width: 1px 0px 1px 0px;";
	
	public static final String css_column_label_selected = 
			"-fx-background-color: blue; "
			+ "-fx-text-fill: white; "
			+ "-fx-border-color: white; "
			+ "-fx-border-width: 1px 0px 1px 0px;";
	

	private Node node;

	private boolean isMousePressed = false;

	private Executor<Integer> executor;
	
	
	private ColumnEventHandler(Node node) {
		this.node = node;
	}

	public static ColumnEventHandler newInstance(Node node) {

		ColumnEventHandler selectEventHandler = new ColumnEventHandler(node);

		selectEventHandler.initializeEvent(node);

		return selectEventHandler;
	}

	private void initializeEvent(Node node) {

		// Bubbling phase  대상에서 백업 루트로
		node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			logger.debug("ColumnSelectEventHandler MOUSE_PRESSED DataSet start.");
			
			if (event.getButton() == MouseButton.PRIMARY) {
				this.isMousePressed = true;
				
			}
			
			event.consume();
		});

		node.addEventHandler(MouseDragEvent. DRAG_DETECTED, event -> {
			System.out.println("Circle 1 drag detected");

            WritableImage snapshot = node.snapshot(null, null);            
            
            ClipboardContent content = new ClipboardContent();
            content.putString(COLUM_JOIN_SELECTED);


            Dragboard db = node.startDragAndDrop(TransferMode.MOVE);            
            db.setContent(content);
            db.setDragView(snapshot);
            db.setDragViewOffsetX(snapshot.getWidth() / 2);
            db.setDragViewOffsetY(-snapshot.getHeight() / 2);

            event.consume();
            
		});
		
		node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			logger.debug("MouseEvent.MOUSE_CLICKED");
			if (  event.getButton() == MouseButton.PRIMARY) {

				if(event.isControlDown()) {
					
					TableViewData tableViewData = executor.getTableViewData();
					
					if (tableViewData == null)
						return ;
					
					int idx = getColumnIndex(tableViewData);
					executor.execute(idx);

					
				} else {
					Node targetNode = (Node) event.getTarget();
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
					
					ColumnInfo columnInfo = (ColumnInfo) label.getUserData();
					if (columnInfo.isSelected()) {
						columnInfo.setSelected(false);
						label.setStyle(css_column_label);
					} else {
						columnInfo.setSelected(true);
						label.setStyle(css_column_label_selected);
					}
				}
			// viewModel.ifLinkedIntegratedGridModifyTableColumn(columnInfo);

			}

			event.consume();
		});

	}

	public void setExecutor(Executor<Integer> executor) {
		this.executor = executor;
	}

	private int getColumnIndex(TableViewData tableViewData) {
		ColumnInfo columnInfo = (ColumnInfo)node.getUserData();
		String columnName = columnInfo.getColumnName();
		Expression<?>[] expres = tableViewData.getColumnExpressions();
		for (int i=0 ; i < expres.length ; i++ ) {
			String expreName = ColUtils.getColumnName(expres[i]);
			if (columnName.equalsIgnoreCase(expreName))
				return i ;
		}
		return -1 ;
	}

}
