package mydata.ds.view.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.scene.Node;
import mydata.ds.view.condition.ConditionViewInfo;

public class ConditionScope implements Scope{

	private ConditionViewInfo conditionViewInfo;
	
	public void setConditionViewInfo(ConditionViewInfo conditionViewInfo) {
		this.conditionViewInfo = conditionViewInfo;
	}

	public ConditionViewInfo getConditionViewInfo() {
		return this.conditionViewInfo;
	}
	
	
}
