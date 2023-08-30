package mydata.ds.view.dataset;

import com.querydsl.core.types.SubQueryExpression;

import ds.condition.medicare.emr.EmrTerm;
import ds.ehr.research.condition.UIEhrResearchConditions;
import ds.ehr.research.dataset.UIDataSetEHR;
import ds.ehr.research.dataset.emr.EmrEhrDataSet;

public class DataSetService {
	public static int instanceCounter = 0;
	
	// -- EMR ----------------------
	private EmrEhrDataSet mEmrQuery = EmrEhrDataSet.getInstance() ;
	
	
	public SubQueryExpression<?> getEmrTerm() {
		//EMR 기록 조회////////////////////////////////////////////////////////////////////////////
		UIEhrResearchConditions c = UIDataSetEHR.add();
		c.patno.setValue("00582680");		
		c.emrTerm.setValue(EmrTerm.rckiCd("113")); // 다학제
		SubQueryExpression<?> query1 = mEmrQuery.getQuerySearch(c);
		return query1;
	}
	
}
