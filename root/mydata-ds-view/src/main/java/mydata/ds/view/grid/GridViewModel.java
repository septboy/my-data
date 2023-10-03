package mydata.ds.view.grid;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import jakarta.inject.Inject;
import javafx.scene.layout.Pane;
import mydata.ds.view.dataset.DataSetRelation;
import mydata.ds.view.dataset.DataSetView;
import mydata.ds.view.dataset.DataSetViewModel;
import mydata.ds.view.dataset.RelatedPane;
import mydata.ds.view.scopes.AppContext;
import mydata.ds.view.scopes.ApplicationScope;
import ds.data.core.column.ColumnInfo;

public class GridViewModel implements ViewModel {

	private static final Logger logger = LoggerFactory.getLogger(GridViewModel.class);
	
	@Inject
	private AppContext appContext;
	
	@InjectScope
	private ApplicationScope applicationScope ;

	private LinkedList<Integer> datasetHashcodeList = new LinkedList<>();
	
	public void initialize() {
		applicationScope.subscribe(applicationScope.ADD_OR_REMOVE_GRID_COLUMN, (key, payload) -> {
			logger.debug("ADD_OR_REMOVE_GRID_COLUMN- {}", ((ColumnInfo)payload[0]).getColumnComment());
		});
	}
	
	public boolean isNotConnectableDataSet() {
		if ( this.appContext.getMouseEventStatus().isMouseDraggedThenReleased() ) {
			return false ;
		} else {
			return true;
		}
		
	}

	public Pane getConnectableDataSetPane() {
		return this.appContext.getMouseEventStatus().getStartPane();
	}

	public void clearMouseStatus() {
		this.appContext.getMouseEventStatus().statusClear();
	}

	public void putRelatedIcon(int hashCode, RelatedIcon relatedIcon) {
		this.appContext.putRelatedIcon(hashCode, relatedIcon);
		
	}

	public DataSetViewModel getDataSetViewModel(int hashCode) {
		return this.appContext.getDataSetViewModel(hashCode);
	}

	public DataSetView getDataSetView(int hashCode) {
		return this.appContext.getDataSetView(hashCode);
	}

	public void addIntegratedDataSetHashCode(int hashcode) {
		this.datasetHashcodeList.add(hashcode);
	}
	
	public List<Integer> getIntegratedDataSetHashcodeList() {
		return this.datasetHashcodeList;
	}
	
	public List<Integer> getAppContextDataSetHashcodeList() {
		
		LinkedList<Integer> linkedDataSetHashcodeList = new LinkedList<>(appContext.getDataSetHashcodeList());
		
		List<RelatedPane> tmpRelatedPaneList = new ArrayList<>();
		for ( int dataSetHashcode: linkedDataSetHashcodeList ) {
			DataSetRelation dataSetRelation = appContext.getDataSetRelation(dataSetHashcode);
			List<RelatedPane> relatedPaneList = dataSetRelation.getRelatedPaneList();
			for(RelatedPane relatedPane: relatedPaneList) {
				if ( !tmpRelatedPaneList.contains(dataSetRelation) )
					tmpRelatedPaneList.add(relatedPane);
			}
		}
		
		for (RelatedPane relatedPane: tmpRelatedPaneList) {
			int startHashcode = relatedPane.startPane().hashCode();
			int endHashcode = relatedPane.endPane().hashCode();
			
			int startIndex = linkedDataSetHashcodeList.indexOf(startHashcode);
			int endIndex = linkedDataSetHashcodeList.indexOf(endHashcode);
			
			if( startIndex > endIndex ) {//start hashcode가 뒤에 위치하고 잇으면 endIndex 앞으로 보낸다.
				linkedDataSetHashcodeList.remove(startIndex);
				if(endIndex >= 1)
					linkedDataSetHashcodeList.add(endIndex-1, startHashcode);
				else
					linkedDataSetHashcodeList.addFirst(startHashcode);
			}
		}
		
		return linkedDataSetHashcodeList;
	}

	public DataSetView getBaseDataSetView(int hashcode) {
		return appContext.getDataSetView(hashcode);
	}

	public DataSetViewModel getBaseDataSetViewModel(int hashcode) {
		return appContext.getDataSetViewModel(hashcode);
	}
	
	
}
