package mydata.ds.view.condition;

import ds.common.util.CommonUtil;
import ds.data.core.column.ColumnType;
import ds.data.core.condition.ConditionInfo;
import javafx.scene.control.Control;

public class ConditionViewInfo extends ConditionInfo{

	private Control controlButton ;
	
	private Object value ;

	private Control prevControlButton;
	
	public ConditionViewInfo(String columnName, ColumnType columnType, String comment) {
		super(columnName, columnType, comment);
	}

	public ConditionViewInfo(ConditionInfo conditionInfo) {
		super(conditionInfo.getColumnName(), conditionInfo.getColumnType(), conditionInfo.getColumnComment());
	}

	public void setControlButton(Control controlButton) {
		this.controlButton = controlButton;
		
		Object controlButtonUserData = controlButton.getUserData();
		if (controlButtonUserData != null) {
			ConditionViewInfo info = (ConditionViewInfo)controlButtonUserData;
			this.value = info.getValue();
		}
		
	}

	public void setValue(Object value) {
		this.value = value ;
	}
	
	public Control getControlButton() {
		return this.controlButton;
	}
	
	public boolean hasValue() {
		return CommonUtil.isNotEmpty(value);
	}

	public Object getValue() {
		return this.value;
	}

	public void setPrevControlButton(Control prevControlButton) {
		this.prevControlButton = prevControlButton;
		
	}

	public Control getPrevControlButton() {
		return this.prevControlButton;
	}
}
