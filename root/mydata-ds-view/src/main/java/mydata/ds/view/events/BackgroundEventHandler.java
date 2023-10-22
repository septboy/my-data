package mydata.ds.view.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import mydata.ds.view.grid.IntegratedIcon;
import mydata.ds.view.relation.RelationColumnInfo;
import mydata.ds.view.relation.RelationView;
import mydata.ds.view.relation.RelationViewModel;
import mydata.ds.view.scopes.AppContext;
import mydata.ds.view.util.ViewUtils;

public class BackgroundEventHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(BackgroundEventHandler.class);

	public static final String DRAG_REMOVE_ON_BACKGROUND_INTEGRATED_RELATION_ICON = "DRAG_REMOVE_ON_BACKGROUND_INTEGRATED_RELATION_ICON";
	public static final String DRAG_REMOVE_ON_BACKGROUND_DATASET_ICON = "DRAG_REMOVE_ON_BACKGROUND_DATASET_ICON";
	public static final String DRAG_REMOVE_ON_BACKGROUND_RELATION_COLUMN = "DRAG_REMOVE_ON_BACKGROUND_RELATION_COLUMN";
	public static final String DRAG_REMOVE_ON_BACKGROUND_FUNCTION_COLUMN = "DRAG_REMOVE_ON_BACKGROUND_FUNCTION_COLUMN";
	
	private AppContext appContext;
	
	public BackgroundEventHandler(AppContext appContext) {
		this.appContext = appContext ;
	}

	public  void initilize(Node background) {
		
		background.setOnDragOver(event -> {
			logger.debug("setOnDragOver");
	    	if (event.getGestureSource() != background && event.getDragboard().hasString()) {
	            event.acceptTransferModes(TransferMode.MOVE);
	        }
	
	        event.consume();
	    });
		
		background.setOnDragDropped(event -> {
	        Dragboard db = event.getDragboard();
	        boolean success = false;
	
	        Node source = (Node)event.getGestureSource();
	        
	        if (db.hasString()) {
	            logger.debug("Dropped: [{}] {}", source.hashCode(), db.getString());
	            
	            if (db.getString().equals(DRAG_REMOVE_ON_BACKGROUND_INTEGRATED_RELATION_ICON))
	            	bindEvent_DRAG_REMOVE_ON_BACKGROUND_INTEGRATED_RELATION_ICON(source);
	            
	            else if (db.getString().equals(DRAG_REMOVE_ON_BACKGROUND_RELATION_COLUMN))
	            	bindEvent_DRAG_REMOVE_ON_BACKGROUND_RELATION_COLUMN(source);
	            
	            else if (db.getString().equals(DRAG_REMOVE_ON_BACKGROUND_FUNCTION_COLUMN))
	            	bindEvent_DRAG_REMOVE_ON_BACKGROUND_FUNCTION_COLUMN(source);
	            success = true;
	        }
	        
	        event.setDropCompleted(success);
	        event.consume();
	    });
		
	}

	public static void bindDragAndRemoveOnBackgroundEvent(Node node, String eventKey) {
		
		node.setOnDragDetected(event -> {
	        Dragboard dragboard = node.startDragAndDrop(TransferMode.ANY);

	        ClipboardContent content = new ClipboardContent();
			content.putString(eventKey);
	        dragboard.setContent(content);

	        event.consume();
	    });
		
		node.setOnMouseDragged((MouseEvent event) -> {
	        event.setDragDetect(true);
	        
	        event.consume();
	    });
		
		node.setOnDragDone(DragEvent::consume);
		
	}

	private void bindEvent_DRAG_REMOVE_ON_BACKGROUND_RELATION_COLUMN(Node source) {
		RelationColumnInfo relationColumnInfo = (RelationColumnInfo)source.getUserData();
		RelationViewModel relationViewModel = appContext.getRelationViewModel(relationColumnInfo.getRelationHashcode());
		RelationView relationView = appContext.getRelationView(relationColumnInfo.getRelationHashcode());
		
		ViewUtils.removeFromPane(relationView.getRelationViewBox(), source);
		relationViewModel.removeRelationColumnInfo(relationColumnInfo);
	}

	private void bindEvent_DRAG_REMOVE_ON_BACKGROUND_FUNCTION_COLUMN(Node source) {
		VBox parentVBox = ViewUtils.searchParentNodeWithType(VBox.class, source);
		ViewUtils.removeFromPane(parentVBox, source);
	}

	private void bindEvent_DRAG_REMOVE_ON_BACKGROUND_INTEGRATED_RELATION_ICON(Node source) {
		int dataSetHashcode = (Integer)source.getUserData();
		IntegratedIcon integratedIcon = appContext.getIntegratedIcon(dataSetHashcode);
		
		if ( integratedIcon != null ) {
		    ViewUtils.removeFromPane( integratedIcon.dataSetIconParent(), integratedIcon.dataSetIcon());
		    ViewUtils.removeFromPane( integratedIcon.gridBarIconParent(), integratedIcon.gridBarIcon());
		}
	}
	
	
}
