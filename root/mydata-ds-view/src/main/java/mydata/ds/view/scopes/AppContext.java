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
import javafx.scene.layout.Pane;
import mydata.ds.view.dataset.DataSetRelation;
import mydata.ds.view.dataset.DataSetView;
import mydata.ds.view.dataset.DataSetViewModel;
import mydata.ds.view.dataset.record.RelationHashCodePair;
import mydata.ds.view.events.BackgroundEventHandler;
import mydata.ds.view.events.DataSetEventHander;
import mydata.ds.view.function.BaseFunction;
import mydata.ds.view.grid.IntegratedIcon;
import mydata.ds.view.relation.RelationView;
import mydata.ds.view.relation.RelationViewModel;

@ApplicationScoped
public class AppContext {
	
	private static final Logger logger = LoggerFactory.getLogger(AppContext.class);

	private DataSetEventHander mouseEventStatus ;
	
	private BackgroundEventHandler backgroundEventHandler ;
	
	private Map<Integer, DataSetRelation> dataSetRelationMap ;

	private Scene scene;

	private Map<Integer, IntegratedIcon> relationIconMap;
	
	private Map<Integer, DataSetViewModel> dataSetViewModelMap;
	
	private Map<Integer, DataSetView> dataSetViewMap;

	private List<Integer> appContextIntegratedDataSetHashcodeList ;

	private Map<Integer, RelationHashCodePair>  relationHashCodePairMap;

	private Map<Integer, RelationViewModel> relationViewModelMap;

	private Map<Integer, RelationView> relationViewMap;

	private Map<Integer, BaseFunction> functionColMap = new HashMap<>();
	
	private Map<Integer, Pane> rootFuncPaneMap = new HashMap<>();
	
	public AppContext() {
		dataSetRelationMap = new HashMap<>();
		//////////////////////////////////////////////
		relationIconMap = new HashMap<>();
		//////////////////////////////////////////////
		dataSetViewModelMap = new HashMap<>();
		dataSetViewMap = new HashMap<>();

		//////////////////////////////////////////////
		relationViewModelMap = new HashMap<>();
		relationViewMap = new HashMap<>();
		
		//////////////////////////////////////////////
		relationHashCodePairMap = new HashMap<>();
		
		//////////////////////////////////////////////
		// 다양한 함수의 공통 interface를 저장한다.
		// 데이터셋 화면을 닫으면 같이 삭제되어야 함.
		functionColMap = new HashMap<>();
		
		//////////////////////////////////////////////
		// 다양한 함수의 입력 폼을 저장한다.
		// 데이터셋 화면을 닫으면 같이 삭제되어야 함.
		rootFuncPaneMap = new HashMap<>();
		
		appContextIntegratedDataSetHashcodeList = new ArrayList<>();
		mouseEventStatus = new DataSetEventHander(this);
		backgroundEventHandler = new BackgroundEventHandler(this);
	}

	public DataSetEventHander getMouseEventStatus() {
		return mouseEventStatus;
	}

	public DataSetRelation getDataSetRelation(int dataSetHashcode) {
		logger.debug("getDataSetRelation( {} )", dataSetHashcode);
		return this.dataSetRelationMap.get(dataSetHashcode);
	}

	public void putDataSetRelation(int dataSetHashcode, DataSetRelation dataSetRelation) {
		logger.debug("putDataSetRelation( {} )", dataSetHashcode); 
		
		if(!dataSetRelationMap.containsKey(dataSetHashcode) )
			this.dataSetRelationMap.put(dataSetHashcode, dataSetRelation);
	}

	public void putRelatedIcon(int dataSethashCode, IntegratedIcon relatedIcon) {
		this.relationIconMap.put(dataSethashCode, relatedIcon);
		
	}

	public ViewModel getViewModel(int dataSetKey) {
		return this.dataSetViewModelMap.get(dataSetKey);
	}
	
	public Scene getScene() {
		return this.scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene ;
	}

	/**
	 * @param dataSetHashcode  dataset hashcode
	 * @return
	 */
	public IntegratedIcon getIntegratedIcon(int dataSetHashcode) {
		return this.relationIconMap.remove(dataSetHashcode);
	}
	
	public void putDataSetViewModel(int hashcode, DataSetViewModel viewModel) {
		this.dataSetViewModelMap.put(hashcode, viewModel) ;
		
	}
	
	public DataSetViewModel getDataSetViewModel(int hashcode) {
		return this.dataSetViewModelMap.get(hashcode);
	}
	
	public DataSetView getDataSetView(int hashcode) {
		return this.dataSetViewMap.get(hashcode);
	}
	
	public void putDataSetView(int hashcode, DataSetView view) {
		this.dataSetViewMap.put(hashcode, view) ;
		
	}

	public void addDataSetHashcode(int dataSetHashcode) {
		this.appContextIntegratedDataSetHashcodeList.add(dataSetHashcode);
		
	}
	
	public List<Integer> getIntegratedDataSetHashcodeList() {
		return this.appContextIntegratedDataSetHashcodeList;
	}

	public void removeDataSetHashcode(Integer dataSetHashcode) {
		this.appContextIntegratedDataSetHashcodeList.remove(dataSetHashcode);
	}

	public void putRelationHashCode(int relationPainHashcode, RelationHashCodePair relationCodePairHashCode) {
		this.relationHashCodePairMap.put(relationPainHashcode, relationCodePairHashCode);
	}
	
	public RelationHashCodePair getRelationHashCodePair(int relationPaneHashcode) {
		return this.relationHashCodePairMap.get(relationPaneHashcode);
	}

	public void putRelationViewModel(int relationHashcode, RelationViewModel relationViewModel) {
		this.relationViewModelMap.put( relationHashcode, relationViewModel);
	}

	public RelationViewModel getRelationViewModel(int relationHashcode) {
		return this.relationViewModelMap.get(relationHashcode);
	}
	
	public void putRelationView(int relationHashcode, RelationView relationView) {
		this.relationViewMap.put( relationHashcode, relationView);
	}
	
	public RelationView getRelationView(int relationHashcode) {
		return this.relationViewMap.get(relationHashcode);
	}

	public void initalizeBackgroundEvent(Node background) {
		this.backgroundEventHandler.initilize(background);
		
	}

	public Map<Integer, Pane> getRootFuncPaneMap() {
		return this.rootFuncPaneMap ;
	}

	public Map<Integer, BaseFunction> getBaseFunctionMap() {
		return this.functionColMap;
	}
	
	public List<DataSetViewModel> getDataSetViewModelList(List<Integer> targetDataSetHashcodeList) {
		List<DataSetViewModel> list = new ArrayList<>();
		for (int targetDataSetHashcode: targetDataSetHashcodeList) {
			DataSetViewModel datasetViewModel = this.dataSetViewModelMap.get(targetDataSetHashcode);
			list.add(datasetViewModel);
		}
		return list;
	}
}
