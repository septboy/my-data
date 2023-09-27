package mydata.ds.view.scopes;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import javafx.scene.Scene;
import mydata.ds.view.dataset.DataSetRelation;
import mydata.ds.view.dataset.MouseEventStatus;

@ApplicationScoped
public class AppContext {
	private static final Logger logger = LoggerFactory.getLogger(AppContext.class);
	
	private MouseEventStatus mouseEventStatus ;
	private Map<Integer, DataSetRelation> dataSetRelationMap ;

	private Scene scene;
	
	public AppContext() {
		dataSetRelationMap = new HashMap<>();
		mouseEventStatus = new MouseEventStatus(this);
	}

	public MouseEventStatus getMouseEventStatus() {
		return mouseEventStatus;
	}

	public DataSetRelation getDataSetRelation(int dataSetIdNmeber) {
		logger.debug("getDataSetRelation( {} )", dataSetIdNmeber);
		return this.dataSetRelationMap.get(dataSetIdNmeber);
	}

	public void putDataSetRelation(int dataSetIdNumber, DataSetRelation dataSetRelation) {
		logger.debug("putDataSetRelation( {} )", dataSetIdNumber); 
		
		if(!dataSetRelationMap.containsKey(dataSetIdNumber) )
			this.dataSetRelationMap.put(dataSetIdNumber, dataSetRelation);
	}

	public Scene getScene() {
		return this.scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene ;
	}
	
	
}
