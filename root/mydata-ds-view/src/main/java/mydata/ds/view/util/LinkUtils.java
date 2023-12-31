package mydata.ds.view.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;

import de.saxsys.mvvmfx.data.TableViewData;
import de.saxsys.mvvmfx.data.TupleCellFactory;
import de.saxsys.mvvmfx.data.TupleValueFactory;
import ds.common.util.ArrayUtil;
import ds.data.core.column.ColumnInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import mydata.ds.view.events.ColumnEventHandler;
import mydata.ds.view.executor.Executor;

public class LinkUtils {
	private static final Logger logger = LoggerFactory.getLogger(LinkUtils.class);
	
	public static void link(TableView<Tuple> tableView, TableViewData tableViewData) {
		if (tableViewData == null || tableViewData.getTupleList() == null)
			return;

		Expression<?>[] colExpres = tableViewData.getColumnExpressions();

		TableColumn<Tuple, ?>[] columns = null;
		for (Expression<?> colExpre : colExpres) {
			TableColumn<Tuple, ?> column = getTableColumn(tableViewData, colExpre);
			column.getStyleClass().add("cell-font");
			columns = ArrayUtil.addArrayOne(columns, column, TableColumn.class);
		}

		ObservableList<Tuple> observavleTupleList = FXCollections.observableArrayList();
		for (Tuple item : tableViewData.getTupleList()) {
			observavleTupleList.add(item);
		}
		tableView.setItems(observavleTupleList);
		tableView.getColumns().setAll(columns);
		tableView.getStyleClass().add("header-cell-font");

	}

	public static void link(Label dataSetTitleLabel, String datasetTitle) {
		dataSetTitleLabel.setText(datasetTitle);
	}

	public static void link(VBox columInfoLabelVBox, ColumnInfo[] columnInfos,
			EventHandler<? super MouseEvent> eventHandler) {
		link(columInfoLabelVBox, columnInfos, eventHandler, null);
	}

	public static void link(VBox columInfoLabelVBox, ColumnInfo[] columnInfos, Executor<Integer> executor) {
		link(columInfoLabelVBox, columnInfos, null, executor);
	}

	public static void link(VBox columInfoLabelVBox, ColumnInfo[] columnInfos,
			EventHandler<? super MouseEvent> eventHandler, Executor<Integer> executor) {
		Label[] labels = null;
		if (columnInfos == null)
			return;

		for (ColumnInfo columnInfo : columnInfos) {
			Label label = new Label();
			label.setText(columnInfo.getColumnComment());
			double width = ViewUtils.getRigionalWidth(columInfoLabelVBox);
			if (width > 0) {
				ViewUtils.setWidth(label, width);
			}
			label.setUserData(columnInfo);
			label.setPadding(new Insets(4, 0, 4, 0));
			label.setStyle("-fx-border-color: white; -fx-border-width: 1px 0px 1px 0px;");
			label.setAlignment(Pos.CENTER);

			if (eventHandler != null) {
				label.setOnMouseClicked(eventHandler);
			}
			
			label.setOnMouseDragOver(event -> {
	            logger.debug("setOnMouseDragOver -> ", columnInfo.getColumnName());
	        });
			
			if (executor != null) {
				ColumnEventHandler columnSelectEventHandler = ColumnEventHandler.newInstance(label);
				columnSelectEventHandler.setExecutor(executor);
			}

			labels = ArrayUtil.addArrayOne(labels, label, Label.class);
		}
		columInfoLabelVBox.getChildren().addAll(labels);
	}

	@SuppressWarnings("unchecked")
	private static TableColumn<Tuple, ?> getTableColumn(TableViewData tableViewData, Expression<?> expre) {
		
		TableColumn<Tuple, ?> tableColumn = null ;
		
		Class<?> clazz = expre.getType();
		if (Timestamp.class.isAssignableFrom(clazz)) {
			TableColumn<Tuple, Timestamp> column = new TableColumn<>(tableViewData.getHeaderName(expre));
			column.setCellValueFactory(new TupleValueFactory<>((Expression<Timestamp>) expre));
			tableColumn = column;

		} else if (Date.class.isAssignableFrom(clazz)) {
			TableColumn<Tuple, Date> column = new TableColumn<>(tableViewData.getHeaderName(expre));
			column.setCellValueFactory(new TupleValueFactory<>((Expression<Date>) expre));
			tableColumn = column;

		} else if (String.class.isAssignableFrom(clazz)) {
			TableColumn<Tuple, String> column = new TableColumn<>(tableViewData.getHeaderName(expre));
			column.setCellValueFactory(new TupleValueFactory<>((Expression<String>) expre));
			tableColumn = column;

		} else if (BigDecimal.class.isAssignableFrom(clazz)) {
			TableColumn<Tuple, BigDecimal> column = new TableColumn<>(tableViewData.getHeaderName(expre));
			column.setCellValueFactory(new TupleValueFactory<>((Expression<BigDecimal>) expre));
			tableColumn = column;

		} else if (Long.class.isAssignableFrom(clazz)) {
			TableColumn<Tuple, Long> column = new TableColumn<>(tableViewData.getHeaderName(expre));
			column.setCellValueFactory(new TupleValueFactory<>((Expression<Long>) expre));
			tableColumn = column;

		} else if (Integer.class.isAssignableFrom(clazz)) {
			TableColumn<Tuple, Integer> column = new TableColumn<>(tableViewData.getHeaderName(expre));
			column.setCellValueFactory(new TupleValueFactory<>((Expression<Integer>) expre));
			tableColumn = column;
		}
		
		tableColumn.setCellFactory(new TupleCellFactory<>(tableViewData.getColumnInfos()));
		
		return tableColumn;
	}
}
