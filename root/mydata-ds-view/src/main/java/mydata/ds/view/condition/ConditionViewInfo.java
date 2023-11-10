package mydata.ds.view.condition;

import ds.common.util.CommonUtil;
import ds.data.core.column.ColumnType;
import ds.data.core.condition.ConditionInfo;
import javafx.scene.control.Control;

/**
 * 목적: Condition Label과 CondditionInfo 타입의 연동 로직 생성
 */
public class ConditionViewInfo extends ConditionInfo{

	private Control conditionLabel ;
	
	private Control prevConditionLabel;
	
	public ConditionViewInfo(String columnName, ColumnType columnType, String comment) {
		super(columnName, columnType, comment);
	}

 	public ConditionViewInfo(ConditionInfo conditionInfo) {
		super(conditionInfo.getColumnName(), conditionInfo.getColumnType(), conditionInfo.getColumnComment()
			, conditionInfo.getConditionTargetCol()
			, conditionInfo.getConditionGroupField(), conditionInfo.getConditionField()
				);
	}

	public void setConditionLabel(Control conditionLabel) {
		this.conditionLabel = conditionLabel;
		
		Object conditionLabelUserData = conditionLabel.getUserData();
		if (conditionLabelUserData != null) {
			ConditionViewInfo info = (ConditionViewInfo)conditionLabelUserData;
			setValue( info.getValue() );
		}
		
	}

	public Control getConditionLabel() {
		return this.conditionLabel;
	}
	
	public boolean hasValue() {
		return CommonUtil.isNotEmpty(getValue());
	}

	public void setPrevConditionLabel(Control prevControlButton) {
		this.prevConditionLabel = prevControlButton;
		
	}

	public Control getPrevConditionLabel() {
		return this.prevConditionLabel;
	}

}
