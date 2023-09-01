package mydata.ds.view.dataset;

import java.util.Map;

import com.querydsl.core.types.SubQueryExpression;

import ds.condition.medicare.emr.EmrTerm;
import ds.ehr.research.condition.UIEhrResearchConditions;
import ds.ehr.research.dataset.UIDataSetEHR;
import ds.ehr.research.dataset.emr.EmrEhrDataSet;
import ds.ui.condition.UIQuerySearch;

public class DataSetService {
	
	private UIQuerySearch mUIQueryDataSet ;
	
	public SubQueryExpression<?> getQuerySearch(Map<String, ?> uiParam) {
		//EMR 기록 조회////////////////////////////////////////////////////////////////////////////
		UIEhrResearchConditions c = UIDataSetEHR.add();
		c.linkUIValue(uiParam);
		c.patno.setValue("00582680");		
		c.emrTerm.setValue(EmrTerm.rckiCd("113")); // 다학제
		SubQueryExpression<?> query1 = mUIQueryDataSet.getQuerySearch(c);
		
		return query1;
	}

	public UIQuerySearch selectDataSet(String dataSetName) {
		
		if (DataSetViewModel.MEDICAL_VISIT_HISTORY.equals(dataSetName))
			mUIQueryDataSet =  EmrEhrDataSet.getInstance();
		else if ( DataSetViewModel.TEXT_EMR_RECORD.equals(dataSetName) )
			mUIQueryDataSet =  EmrEhrDataSet.getInstance();
		
		return mUIQueryDataSet;
	}
	
}
