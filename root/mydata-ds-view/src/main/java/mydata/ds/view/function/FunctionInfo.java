package mydata.ds.view.function;

import ds.data.core.column.C;
import ds.data.core.column.Col;
import ds.data.core.column.ColumnInfo;
import ds.data.core.column.ColumnType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import mydata.ds.view.events.ColumnEventHandler;

public class FunctionInfo extends ColumnInfo{

	private Col<?> relationTargetCol;
	private Object value ;
	private Integer relationHashcode;

	public FunctionInfo() {
		super();
	}
	
	public FunctionInfo(String columnName, ColumnType columnType, String comment) {
		super(columnName, columnType, comment);
	}

	public FunctionInfo(String columnName, ColumnType columnType, String comment,
			Col<?> conditionTargetCol) {
		super(columnName, columnType, comment);
		this.relationTargetCol = conditionTargetCol;
				
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}


	public void setCol(Col<?> cType) {
		this.relationTargetCol = cType ;
	}
	
	public Col<?> getConditionTargetCol() {
		return this.relationTargetCol;
	}

	@Override
	public Col<?> getCol() {
		return new C(getColumnName(), getColumnType());
	}

	public void setRelationHashcode(int relationHashCode) {
		this.relationHashcode = relationHashCode;
	}
	
	public Label getFunctionLabel() {
		Label label = new Label();
		label.setText(getColumnComment());
		label.setUserData(this);
		label.setPadding(new Insets(4, 0, 4, 0));
		label.setStyle(ColumnEventHandler.css_column_label_selected);
		label.setAlignment(Pos.CENTER);
		return label ;
	}
	
	public int getRelationHashcode() {
		return this.relationHashcode ;
	}
}
