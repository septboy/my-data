package mydata.ds.view.function;

import ds.data.core.column.Col;
import ds.data.core.column.ColumnInfo;
import ds.data.core.column.ColumnType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import mydata.ds.view.events.ColumnEventHandler;

public class FunctionInfo extends ColumnInfo{

	private Col<?> functionCol;
	private int functionRootStackPaneHashcode;

	public FunctionInfo() {
		super();
	}
	
	public FunctionInfo(String columnName, ColumnType columnType, String comment) {
		super(columnName, columnType, comment);
	}

	public FunctionInfo(String columnName, ColumnType columnType, String comment,
			Col<?> conditionTargetCol) {
		super(columnName, columnType, comment);
		this.functionCol = conditionTargetCol;
				
	}
 
	public void setCol(Col<?> functionCol) {
		this.functionCol = functionCol ;
	}
	
	@Override
	public Col<?> getCol() {
		return this.functionCol;
	}

	public Label getFunctionLabel() {
		Label label = new Label();
		label.setText(getColumnComment());
		label.setUserData(this);
		label.setPadding(new Insets(4, 0, 4, 0));
		label.setStyle(ColumnEventHandler.css_column_label_selected);
		label.setAlignment(Pos.CENTER);
		label.setUserData(this.clone());
		return label ;
	}

	public void setFunctionRootStackPaneHashcode(int hashCode) {
		this.functionRootStackPaneHashcode = hashCode;
	}
	
	protected FunctionInfo clone()  {
		FunctionInfo functionInfo = new FunctionInfo(getColumnName(), getColumnType(), getColumnComment(),
				getCol());
		functionInfo.setFunctionRootStackPaneHashcode(getFuncItemPaneHashcode());
		functionInfo.setSelected(isSelected());
		
		return functionInfo;
		
	}
	
	public int getFuncItemPaneHashcode() {
		return this.functionRootStackPaneHashcode;
	}
}
