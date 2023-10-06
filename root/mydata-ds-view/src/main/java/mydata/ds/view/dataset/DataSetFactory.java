package mydata.ds.view.dataset;

import java.util.Map;

import com.querydsl.core.types.SubQueryExpression;

import ds.condition.medicare.emr.EmrTerm;
import ds.data.core.base.DataSet;
import ds.data.core.condition.ui.UIConditions;
import ds.ehr.research.condition.EhrResearchConditions;
import ds.ehr.research.condition.UIEhrResearchConditions;
import ds.ehr.research.dataset.UIDataSetEHR;
import ds.ehr.research.dataset.emr.EmrEhrDataSet;
import ds.ehr.research.dataset.medvisit.ApPatientEhrDataSet;
import ds.ehr.research.dataset.prescription.PrescriptionEhrDataSet;
import ds.ui.condition.DataSetUI;

public class DataSetFactory {
	
	private DataSetUI mUIQueryDataSet ;
	
	public SubQueryExpression<?> getQuerySearch(Map<String, ?> uiParam) {
		//EMR 기록 조회////////////////////////////////////////////////////////////////////////////
		UIEhrResearchConditions c = UIDataSetEHR.add();
		c.linkUIValue(uiParam);
		c.patno.setValue("00582680");		
		c.emrTerm.setValue(EmrTerm.rckiCd("113")); // 다학제
		SubQueryExpression<?> query1 = mUIQueryDataSet.getQuerySearch(c);
		
		return query1;
	}

	public DataSetUI getDataSetUI(String dataSetName) {
		
		if (DataSetViewModel.MEDICAL_VISIT_HISTORY.equals(dataSetName))
			mUIQueryDataSet =  ApPatientEhrDataSet.getInstance();
		
		else if ( DataSetViewModel.TEXT_EMR_RECORD.equals(dataSetName) )
			mUIQueryDataSet =  EmrEhrDataSet.getInstance();
		
		else if ( DataSetViewModel.PRESCRIPTION.equals(dataSetName) )
			mUIQueryDataSet =  PrescriptionEhrDataSet.getInstance();
		
		return mUIQueryDataSet;
	}

	public UIConditions getUIConditionsAndAddDataSetContext(String dataSetName) {
		UIConditions c = getUIConditions(dataSetName);
		DataSet.add(c);
		return c ;
	}
	
	public UIConditions getUIConditions(String dataSetName) {
		UIConditions c = null ;
		if (DataSetViewModel.MEDICAL_VISIT_HISTORY.equals(dataSetName))
			c = new EhrResearchConditions();
		
		else if ( DataSetViewModel.TEXT_EMR_RECORD.equals(dataSetName) )
			c = new EhrResearchConditions();
		
		else
			c = new EhrResearchConditions();
		return c;
	}
	
}
