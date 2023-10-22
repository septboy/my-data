package mydata.ds.view.relation;

import ds.data.core.column.C;
import ds.data.core.column.Col;
import ds.data.core.column.ColumnInfo;
import ds.data.core.column.ColumnType;

public class RelationColumnInfo extends ColumnInfo{

	private Col<?> relationTargetCol;
	private Object value ;
	private Integer relationHashcode;

	public RelationColumnInfo(String columnName, ColumnType columnType, String comment) {
		super(columnName, columnType, comment);
	}

	public RelationColumnInfo(String columnName, ColumnType columnType, String comment,
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
	
	public int getRelationHashcode() {
		return this.relationHashcode ;
	}
}
