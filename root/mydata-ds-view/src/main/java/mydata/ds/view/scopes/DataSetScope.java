package mydata.ds.view.scopes;

import de.saxsys.mvvmfx.Scope;

public class DataSetScope implements Scope {

	private String dataSetId;

	public void setDataSetId(String dataSetId) {
		this.dataSetId = dataSetId;
	}

	public String getDataSetId() {
		return dataSetId;
	}
	
}
