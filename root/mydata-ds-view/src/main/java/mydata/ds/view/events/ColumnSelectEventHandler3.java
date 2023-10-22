package mydata.ds.view.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.types.Expression;

import de.saxsys.mvvmfx.data.TableViewData;
import ds.data.core.column.ColumnInfo;
import ds.data.core.util.ColUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import mydata.ds.view.executor.Executor;
import mydata.ds.view.util.ViewUtils;

public class ColumnSelectEventHandler3 {

	private static final Logger logger = LoggerFactory.getLogger(ColumnSelectEventHandler3.class);
	
	public static final String css_column_label_border = "-fx-border-color: white; -fx-border-width: 1px 0px 1px 0px;";

	private long duringTimeForEvent = 1000L; // 1초

	private Timeline timeline;

	private Node node;

	private boolean isLessThanDuringTime = false;

	private boolean isMousePressed = false;

	private long mousePressedTime;

	private Executor<Integer> executor;
	
	private ColumnSelectEventHandler3(Node node) {
		this.node = node;
		this.timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> checkMousePressDuration()));
		this.timeline.setCycleCount(Timeline.INDEFINITE);
	}

	public static ColumnSelectEventHandler3 newInstance(Node node, int sec) {

		ColumnSelectEventHandler3 duringPressEvent = new ColumnSelectEventHandler3(node);

		duringPressEvent.duringTimeForEvent = sec * 10;

		duringPressEvent.initializeEvent(node);

		return duringPressEvent;
	}

	private void initializeEvent(Node node) {

		// Bubbling phase  대상에서 백업 루트로
		node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			logger.debug("ColumnSelectEventHandler MOUSE_PRESSED DataSet start.");
			
			if (event.getButton() == MouseButton.PRIMARY) {
				this.isMousePressed = true;
				mousePressedTime = System.currentTimeMillis();
				isLessThanDuringTime = true;
				timeline.play();
			}
			
			event.consume();
		});

		node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
			this.isMousePressed = false;
			event.consume();
		});

		node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {

				if (isLessThanDuringTime) {
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
						label.setStyle(css_column_label_border);
					} else {
						columnInfo.setSelected(true);
						label.setStyle(
								"-fx-background-color: blue;" + "-fx-text-fill: white;" + css_column_label_border);
					}
				}
				// viewModel.ifLinkedIntegratedGridModifyTableColumn(columnInfo);

			}

			endEvent();
			event.consume();
		});

	}

	public void setExecutor(Executor<Integer> executor) {
		this.executor = executor;
	}

	private void checkMousePressDuration() {
		if (this.isLessThanDuringTime && isMousePressed) {
			long currentTime = System.currentTimeMillis();

			if (currentTime - mousePressedTime >= duringTimeForEvent) {
				this.isLessThanDuringTime = false;
			} else {
				this.isLessThanDuringTime = true; 
			}

		} else {
			TableViewData tableViewData = executor.getTableViewData();
			
			if (tableViewData == null)
				return ;
			
			int idx = getColumnIndex(tableViewData);
			executor.execute(idx);
			// 초기화
			endEvent();
		}

	}

	private void endEvent() {
		this.timeline.stop();
		this.isLessThanDuringTime = false;
		this.isMousePressed = false;
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
