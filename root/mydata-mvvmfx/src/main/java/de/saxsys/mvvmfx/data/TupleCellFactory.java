package de.saxsys.mvvmfx.data;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.Tuple;

import ds.data.core.column.ColumnInfo;
import ds.data.core.column.ColumnType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class TupleCellFactory<T> implements Callback<TableColumn<Tuple, T>, TableCell<Tuple, T>> {

	private static final Logger logger = LoggerFactory.getLogger(TupleCellFactory.class);

	private ColumnInfo[] columnInfos = null;

	public TupleCellFactory(ColumnInfo[] columnInfos) {
		this.columnInfos = columnInfos;
	}
	
	@Override
	public TableCell<Tuple, T> call(TableColumn<Tuple, T> param) {

		return (TableCell<Tuple, T>) new TableCell<Tuple, T>() {

			@Override
			protected void updateItem(T value, boolean empty) {
				super.updateItem(value, empty);

				if (value == null || empty) {
					setText(null);
					setStyle(""); // Clear any previous styles

					return;
				}

				// logger.debug("columnName:{}, dataType:{}", param.getText(),
				// value.getClass().getName());

				if (value instanceof Timestamp) {
					ColumnType columnType = getColumnType(param.getText());

					if (columnType == ColumnType.Date) {
						TimestampConverter converter = new TimestampConverter();
						setText(converter.toDateString((Timestamp) value));

					} else {
						StringConverter<Timestamp> converter = new TimestampConverter();
						setText(converter.toString((Timestamp) value));

					}

				} else if (value instanceof Date) {
					StringConverter<Date> converter = new DateConverter();
					setText(converter.toString((Date) value));

				} else if (value instanceof Float) {
					StringConverter<Float> converter = new RoundedFloatConverter();
					setText(converter.toString((Float) value));

				} else if (value instanceof BigDecimal) {
					StringConverter<BigDecimal> converter = new RoundedBigDecimalConverter();
					setText(converter.toString((BigDecimal) value));

				} else if (value instanceof Double) {
					StringConverter<Double> converter = new RoundedNumberConverter();
					setText(converter.toString((Double) value));

				}

//     		else if (value instanceof String) {
//     			
//                 String[] parts = value.split(" ");
//                 setText(null);
//                 for (String part : parts) {
//                     if (part.startsWith("Moderate")) {
//                     	 // Create a Text node and set different styles for different parts of the text
//                         Text text = new Text(part);
//                         text.setFill(Color.RED);
//                         text.setStyle("-fx-font-weight: bold");
//
//                         // Set the Text node as the graphic for the TableCell
//                         setGraphic(text);
//                     } else
//                     	setText(part);
//                     
//                 }
//     		} 
//
				else {
					setText(value.toString());
				}
			}

		};

	}

	private ColumnType getColumnType(String text) {
		for (ColumnInfo columnInfo : columnInfos) {
			if (columnInfo.getColumnComment().equals(text))
				return columnInfo.getColumnType();
		}
		return null;
	}

	public void setColumnInfos(ColumnInfo[] columnInfos) {
		this.columnInfos = columnInfos;
	}
}
