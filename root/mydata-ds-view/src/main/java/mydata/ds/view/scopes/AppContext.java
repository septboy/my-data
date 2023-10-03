package mydata.ds.view.scopes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.ViewModel;
import jakarta.enterprise.context.ApplicationScoped;
import javafx.scene.Node;
import javafx.scene.Scene;
import mydata.ds.view.dataset.DataSetEvent;
import mydata.ds.view.dataset.DataSetRelation;
import mydata.ds.view.dataset.DataSetView;
import mydata.ds.view.dataset.DataSetViewModel;
import mydata.ds.view.grid.RelatedIcon;

@ApplicationScoped
public class AppContext {
	private static final Logger logger = LoggerFactory.getLogger(AppContext.class);
	
	private DataSetEvent mouseEventStatus ;
	private Map<Integer, DataSetRelation> dataSetRelationMap ;

	private Scene scene;

	private Map<Integer, RelatedIcon> relationIconMap;
	
	private Map<Integer, DataSetViewModel> viewModelMap;
	
	private Map<Integer, DataSetView> viewMap;

	private List<Integer> dataSetHashcodeList ;

	private Map<Integer, Node> applicationHashcodeNodeMap;
	
	public AppContext() {
		dataSetRelationMap = new HashMap<>();
		relationIconMap = new HashMap<>();
		viewModelMap = new HashMap<>();
		viewMap = new HashMap<>();
		applicationHashcodeNodeMap = new HashMap<>();
		
		dataSetHashcodeList = new ArrayList<>();
		mouseEventStatus = new DataSetEvent(this);
	}

	public DataSetEvent getMouseEventStatus() {
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

	public void putRelatedIcon(int hashCode, RelatedIcon relatedIcon) {
		this.relationIconMap.put(hashCode, relatedIcon);
		
	}

	public ViewModel getViewModel(int dataSetKey) {
		return this.viewModelMap.get(dataSetKey);
	}
	
	public Scene getScene() {
		return this.scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene ;
	}

	public RelatedIcon getRelatedIcon(int hashcode) {
		return this.relationIconMap.get(hashcode);
	}

	public void putDataSetViewModel(int hashcode, DataSetViewModel viewModel) {
		this.viewModelMap.put(hashcode, viewModel) ;
		
	}
	
	public DataSetViewModel getDataSetViewModel(int hashcode) {
		return this.viewModelMap.get(hashcode);
	}
	
	public DataSetView getDataSetView(int hashcode) {
		return this.viewMap.get(hashcode);
	}
	
	public void putDataSetView(int hashcode, DataSetView view) {
		this.viewMap.put(hashcode, view) ;
		
	}

	public void addDataSetHashcode(int dataSetHashcode) {
		this.dataSetHashcodeList.add(dataSetHashcode);
		
	}
	
	public List<Integer> getDataSetHashcodeList() {
		return this.dataSetHashcodeList;
	}

	public void removeDataSetHashcode(Integer dataSetHashcode) {
		this.dataSetHashcodeList.remove(dataSetHashcode);
	}

	public void putNodeToAppContext(int hashcode, Node node) {
		this.applicationHashcodeNodeMap.put(hashcode, node);
		
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getNodeOnAppContext(int hashcode, T node) {
		return (T)this.applicationHashcodeNodeMap.get(hashcode);
	}
}
