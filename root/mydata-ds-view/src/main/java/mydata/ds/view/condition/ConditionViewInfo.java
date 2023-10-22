package mydata.ds.view.condition;

import ds.common.util.CommonUtil;
import ds.data.core.column.ColumnType;
import ds.data.core.condition.ConditionInfo;
import javafx.scene.control.Control;

public class ConditionViewInfo extends ConditionInfo{

	private Control controlButton ;
	
	private Control prevControlButton;
	
	public ConditionViewInfo(String columnName, ColumnType columnType, String comment) {
		super(columnName, columnType, comment);
	}

	public ConditionViewInfo(ConditionInfo conditionInfo) {
		super(conditionInfo.getColumnName(), conditionInfo.getColumnType(), conditionInfo.getColumnComment()
			, conditionInfo.getConditionTargetCol()
			, conditionInfo.getConditionGroupField(), conditionInfo.getConditionField()
				);
	}

	public void setControlButton(Control controlButton) {
		this.controlButton = controlButton;
		
		Object controlButtonUserData = controlButton.getUserData();
		if (controlButtonUserData != null) {
			ConditionViewInfo info = (ConditionViewInfo)controlButtonUserData;
			setValue( info.getValue() );
		}
		
	}

	public Control getControlButton() {
		return this.controlButton;
	}
	
	public boolean hasValue() {
		return CommonUtil.isNotEmpty(getValue());
	}

	public void setPrevControlButton(Control prevControlButton) {
		this.prevControlButton = prevControlButton;
		
	}

	public Control getPrevControlButton() {
		return this.prevControlButton;
	}

}
