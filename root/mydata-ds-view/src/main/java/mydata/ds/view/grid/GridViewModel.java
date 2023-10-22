package mydata.ds.view.grid;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import ds.data.core.column.ColumnInfo;
import jakarta.inject.Inject;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import mydata.ds.view.dataset.DataSetRelation;
import mydata.ds.view.dataset.DataSetView;
import mydata.ds.view.dataset.DataSetViewModel;
import mydata.ds.view.dataset.RelatedPane;
import mydata.ds.view.events.BackgroundEventHandler;
import mydata.ds.view.scopes.AppContext;
import mydata.ds.view.scopes.ApplicationScope;

public class GridViewModel implements ViewModel {

	private static final Logger logger = LoggerFactory.getLogger(GridViewModel.class);
	
	@Inject
	private AppContext appContext;
	
	@InjectScope
	private ApplicationScope applicationScope ;

	private LinkedList<Integer> integratedDatasetHashcodeList = new LinkedList<>();

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

	public Pane getIntegratedSourceDataSetPane() {
		return this.appContext.getMouseEventStatus().getStartPane();
	}

	public void clearMouseStatus() {
		this.appContext.getMouseEventStatus().statusClear();
	}

	public void putIntegratedIcon(int dataSethashCode, IntegratedIcon relatedIcon) {
		this.appContext.putRelatedIcon(dataSethashCode, relatedIcon);
	}

	public DataSetViewModel getDataSetViewModel(int hashCode) {
		return this.appContext.getDataSetViewModel(hashCode);
	}

	public DataSetView getDataSetView(int hashCode) {
		return this.appContext.getDataSetView(hashCode);
	}

	public void addIntegratedDataSetHashCode(int dataSetHashcode) {
		this.integratedDatasetHashcodeList.add(dataSetHashcode);
	}
	
	public List<Integer> getIntegratedDataSetHashcodeList() {
		return this.integratedDatasetHashcodeList;
	}
	
	public List<Integer> getAppContextDataSetHashcodeList() {
		
		LinkedList<Integer> integratedDataSetHashcodeList = new LinkedList<>(appContext.getIntegratedDataSetHashcodeList());
		
		List<RelatedPane> tmpRelatedPaneList = new ArrayList<>();
		for ( int dataSetHashcode: integratedDataSetHashcodeList ) {
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
			
			int startIndex = integratedDataSetHashcodeList.indexOf(startHashcode);
			int endIndex = integratedDataSetHashcodeList.indexOf(endHashcode);
			
			if( startIndex > endIndex ) {//start hashcode가 뒤에 위치하고 잇으면 endIndex 앞으로 보낸다.
				integratedDataSetHashcodeList.remove(startIndex);
				if(endIndex >= 1)
					integratedDataSetHashcodeList.add(endIndex-1, startHashcode);
				else
					integratedDataSetHashcodeList.addFirst(startHashcode);
			}
		}
		
		return integratedDataSetHashcodeList;
	}

	public DataSetView getBaseDataSetView(int hashcode) {
		return appContext.getDataSetView(hashcode);
	}

	public DataSetViewModel getBaseDataSetViewModel(int hashcode) {
		return appContext.getDataSetViewModel(hashcode);
	}

	public void bindDraggableEvents(Node node) {
		
		logger.debug("node.getUserData()={}", node.getUserData() );
		
		node.setOnDragDetected(event -> {
	        Dragboard dragboard = node.startDragAndDrop(TransferMode.ANY);

	        ClipboardContent content = new ClipboardContent();
			content.putString(BackgroundEventHandler.DRAG_REMOVE_ON_BACKGROUND_INTEGRATED_RELATION_ICON);
	        dragboard.setContent(content);

	        event.consume();
	    });
		
		node.setOnMouseDragged((MouseEvent event) -> {
	        event.setDragDetect(true);
	        
	        event.consume();
	    });
		
		node.setOnDragDone(DragEvent::consume);
		
	}
	
}
