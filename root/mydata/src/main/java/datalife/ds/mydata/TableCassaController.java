package datalife.ds.mydata;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.MapValueFactory;
import javafx.util.Callback;

public class TableCassaController<K, V> extends TableView<Map.Entry<K, V>> implements Initializable {
	@FXML
	private TableColumn<K, V> column1;
	@FXML
	private TableColumn<K, V> column2;

	public TableCassaController(ObservableMap<K, V> map, String col1Name, String col2Name) {
		
		System.out.println("Costruttore table");
		
		// Map Key
		TableColumn<Map.Entry<K, V>, K> column1 = new TableColumn<>(col1Name);
		column1.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Map.Entry<K, V>, K>, ObservableValue<K>>() {

					@Override
					public ObservableValue<K> call(TableColumn.CellDataFeatures<Map.Entry<K, V>, K> p) {
						// this callback returns property for just one cell, you can't use a loop here
						// for first column we use key
						return new SimpleObjectProperty<K>(p.getValue().getKey());
					}
				});

		
		// 구현된 MapValueFactory를 참고로 TupleValueFactory를 만들수 있다.
		//class MapValueFactory<T> implements Callback<CellDataFeatures<Map,T>, ObservableValue<T>>
		//new MapValueFactory<>("firstName")
		
		// Map Value
		TableColumn<Map.Entry<K, V>, V> column2 = new TableColumn<>(col2Name);
		column2.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Map.Entry<K, V>, V>, ObservableValue<V>>() {

					@Override
					public ObservableValue<V> call(TableColumn.CellDataFeatures<Map.Entry<K, V>, V> p) {
						// for second column we use value
						return new SimpleObjectProperty<V>(p.getValue().getValue());
					}
				});


		ObservableList<Map.Entry<K, V>> items = FXCollections.observableArrayList(map.entrySet());

		this.setItems(items);
		this.getColumns().setAll(column1, column2);

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
}