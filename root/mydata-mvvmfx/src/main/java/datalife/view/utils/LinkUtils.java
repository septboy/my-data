package datalife.view.utils;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;

import datalife.ds.mydata.TupleValueFactory;
import ds.common.util.ArrayUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class LinkUtils {

	public static void link(TableView<Tuple> tableView, TableViewData tableViewData) {
		
		Expression<?>[] colExpres = tableViewData.getColumnExpressions();
		
		TableColumn<Tuple, ?>[] columns = null ;
		for (Expression<?> colExpre: colExpres) {
			TableColumn<Tuple, ?> column = getTableColumn(tableViewData, colExpre);
			columns = ArrayUtil.addArrayOne(columns, column, TableColumn.class);
		}
		
		ObservableList<Tuple> observavleTupleList = FXCollections.observableArrayList();
		for(Tuple item : tableViewData.getTupleList()) {
			observavleTupleList.add(item);
		}
		tableView.setItems(observavleTupleList);
		tableView.getColumns().setAll(columns);
	}

	@SuppressWarnings("unchecked")
	private static TableColumn<Tuple, ?> getTableColumn(TableViewData tableViewData, Expression<?> expre) {
		Class<?> clazz = expre.getType() ;
		if (Timestamp.class.isAssignableFrom(clazz)) {
			TableColumn<Tuple, Timestamp> column = new TableColumn<>(tableViewData.getHeaderName(expre));
			column.setCellValueFactory (new TupleValueFactory<>((Expression<Timestamp>) expre));
			return column;
		} else if (Date.class.isAssignableFrom(clazz)) {
			TableColumn<Tuple, Date> column = new TableColumn<>(tableViewData.getHeaderName(expre));
			column.setCellValueFactory (new TupleValueFactory<>((Expression<Date>) expre));
			return column;
		} else if (String.class.isAssignableFrom(clazz)) {
			TableColumn<Tuple, String> column = new TableColumn<>(tableViewData.getHeaderName(expre));
			column.setCellValueFactory (new TupleValueFactory<>((Expression<String>) expre));
			return column;
		} else if (BigDecimal.class.isAssignableFrom(clazz)) {
			TableColumn<Tuple, BigDecimal> column = new TableColumn<>(tableViewData.getHeaderName(expre));
			column.setCellValueFactory (new TupleValueFactory<>((Expression<BigDecimal>) expre));
			return column;
		} else if (Long.class.isAssignableFrom(clazz)) {
			TableColumn<Tuple, Long> column = new TableColumn<>(tableViewData.getHeaderName(expre));
			column.setCellValueFactory (new TupleValueFactory<>((Expression<Long>) expre));
			return column;
		} else if (Integer.class.isAssignableFrom(clazz)) {
			TableColumn<Tuple, Integer> column = new TableColumn<>(tableViewData.getHeaderName(expre));
			column.setCellValueFactory (new TupleValueFactory<>((Expression<Integer>) expre));
			return column;
		}
		
		return null ;
	}
}
