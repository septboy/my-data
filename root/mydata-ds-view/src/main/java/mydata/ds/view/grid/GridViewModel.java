package mydata.ds.view.grid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import jakarta.inject.Inject;
import javafx.scene.layout.Pane;
import mydata.ds.view.dataset.DataSetView;
import mydata.ds.view.dataset.DataSetViewModel;
import mydata.ds.view.scopes.AppContext;
import mydata.ds.view.scopes.ApplicationScope;
import ds.data.core.column.ColumnInfo;

public class GridViewModel implements ViewModel {

	private static final Logger logger = LoggerFactory.getLogger(GridViewModel.class);
	
	@Inject
	private AppContext appContext;
	
	@InjectScope
	private ApplicationScope applicationScope ;
	
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
	
	
}
