package mydata.ds.view.util;

import java.util.ArrayList;
import java.util.List;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.SubQueryExpression;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.data.TableViewData;
import ds.data.core.column.ColumnInfo;
import ds.data.core.util.CdiUtils;
import ds.data.core.util.ColUtils;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import mydata.ds.view.scopes.AppContext;

public class ViewUtils {

	public static TableViewData getTableViewData(SubQueryExpression<?> query) {
		if (query == null)
			return null ;
		
		Expression<?>[] colExpres = ColUtils.getColumnExpressionsFromQuery(query);
		
		for (Expression<?> expre: colExpres) {
			if (expre instanceof Operation ) {
				String columnName = ColUtils.getColumnName(expre);
				TableViewData.MAP_FOR_TAG.put(expre.toString(), columnName );	
			} else {
				TableViewData.MAP_FOR_TAG.put(expre.toString(), expre.toString() );	
			}
		}
		
		TableViewData tableViewData = new TableViewData();
		tableViewData.setColumnExpressions(colExpres);
		tableViewData.setQuery(query);
		
		return tableViewData;
	}
	
	public static Node searchParentNodeWithId(String id, Node targetNode) {
		Node parentNode = targetNode.getParent();
		if (parentNode == null)
			return null;
		else {
			if (id.equals(parentNode.getId()))
				return parentNode;
			else
				return searchParentNodeWithId(id, parentNode);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T searchParentNodeWithType(Class<T> clazz, Node targetNode) {
		Node parentNode = targetNode.getParent();
		if (parentNode == null)
			return null;
		else {
			if (clazz.isAssignableFrom(parentNode.getClass()))
				return (T)parentNode;
			else
				return searchParentNodeWithType(clazz, parentNode);
		}
	}

	public static double getSceneUpperLeftX(Node node) {
		return node.localToScene(node.getBoundsInLocal()).getMinX();
	}
	
	public static double getSceneUpperLeftY(Node node) {
		return node.localToScene(node.getBoundsInLocal()).getMinY();
	}
	
	public static double getSceneLowerRightX(Node node) {
		return node.localToScene(node.getBoundsInLocal()).getMaxX();
	}
	
	public static double getSceneLowerRightY(Node node) {
		return node.localToScene(node.getBoundsInLocal()).getMaxY();
	}
	
	public static double getSceneCenterX(Node node) {
		return node.localToScene(node.getBoundsInLocal()).getCenterX();
	}
	
	public static double getSceneCenterY(Node node) {
		return node.localToScene(node.getBoundsInLocal()).getCenterY();
	}
	
	public static double getRigionalWidth(Region region) {
		
		if (getWidth(region) > 0) {
			return getWidth(region);
		}
		
		Node parentNode = region.getParent();
		if (parentNode == null)
			return 0.0;
		
		if (parentNode instanceof Region)
			return ((Region)parentNode).widthProperty().doubleValue();
		else
			return getRigionalWidth((Region)parentNode);
		
	}

	public static double getRigionalHeight(Region region) {
		if (getHeight(region) > 0) {
			return getHeight(region);
		}
		
		Node parentNode = region.getParent();
		if (parentNode == null)
			return 0.0;
		
		if (parentNode instanceof Region)
			return ((Region)parentNode).heightProperty().doubleValue();
		else
			return getRigionalHeight((Region)parentNode);
		
	}
	
	private static double getWidth(Region region) {
		if (region.widthProperty().doubleValue() > 0D)
			return region.widthProperty().doubleValue();
		
		if ( region.getPrefWidth() > 0 )
			return region.getPrefWidth();
		
		return 0.0;
	}
	
	private static double getHeight(Region region) {
		if (region.heightProperty().doubleValue() > 0D)
			return region.heightProperty().doubleValue();
		
		if ( region.getPrefHeight() > 0 )
			return region.getPrefHeight();
		
		return 0.0;
	}

	public static void setWidth(Region region, double width) {
		region.setMinWidth(width);
		region.setMaxWidth(width);
		region.setPrefWidth(width);
	}
	
	
	public static FXMLLoader getFXMLLoader(Class<?> baseClazz, String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(baseClazz.getResource(fxml));
        return fxmlLoader;
    }
	
    @SuppressWarnings("unchecked")
	public static <T> List<T> getNodeList(Pane pane, Class<T> nodeClazz) {
        List<T> nodeList = new ArrayList<>();
        for (T node : (ObservableList<T>)pane.getChildren()) {
            if (nodeClazz.isAssignableFrom(node.getClass())) {
                nodeList.add((T) node);
            }
        }
        return nodeList;
    }
    
    public static void removeFromScene(Node node) {
	    AppContext appContext = getAppContext();
		if ( ((Pane)appContext.getScene().getRoot()).getChildren().remove(node) )
			node = null ;
    }

	private static AppContext getAppContext() {
		BeanManager bm = CDI.current().getBeanManager();
		AppContext appContext = CdiUtils.getOneReference(bm, AppContext.class);
		return appContext;
	}
    
    public static void removeFromPane(Pane parent, Node node) {
    	ObservableList<Node> nodeList = parent.getChildren();
    	if( nodeList.remove(node) )
    		node = null ;
    }
    
    @SuppressWarnings("unchecked")
	public static <V extends FxmlView<? extends M>, M extends ViewModel> void openView(Class<? extends V> mClazz, double posX, double posY) {
    	AppContext appContext = getAppContext();
		
		ViewTuple<V, M> load = (ViewTuple<V, M>) FluentViewLoader//
				.fxmlView(mClazz)        //
				.load();                                                    //
	
		Parent view = load.getView();
	
		Pane appPane = (Pane)appContext.getScene().getRoot();
	
		view.setLayoutX(posX);
		view.setLayoutY(posY);
		appPane.getChildren().add(view);
		
	}
    
    @SuppressWarnings("unchecked")
   	public static <V extends FxmlView<? extends M>, M extends ViewModel> Parent openView(Class<? extends V> viewClass) {
   		ViewTuple<V, M> load = (ViewTuple<V, M>) FluentViewLoader//
   				.fxmlView(viewClass)        //
   				.load();                                                    //
   		Parent view = load.getView();
   		return view ;
   	}
    
    public static void setSceneToAppContext(Scene scene) {
    	AppContext appContext = getAppContext();
 		appContext.setScene(scene);
    }

	public static void addNodeOnAppScene(Node node, double x, double y) {
		AppContext appContext = getAppContext();
 		
		AnchorPane parentPane = (AnchorPane) appContext.getScene().getRoot();
		node.setLayoutX(x);
		node.setLayoutY(y);
		parentPane.getChildren().add(node);
		node.toFront();
		
	}
	
	public static void moveOnAppScene(Node node, double x, double y) {
		System.out.println(  x+","+y);
		node.setLayoutX(x);
		node.setLayoutY(y);
		node.toFront();
	}
	
	public static void addNodeIntoPane(Pane parent, Node node) {
		parent.getChildren().add(node);
		node.toFront();
		
	}
	
	public static Button getImageButton(String imagePath, double buttonHeight) {
		//Create a Button
	    Button button = new Button();
	    button.setPrefSize(Region.USE_PREF_SIZE, buttonHeight);
	 
	    //Create imageview with background image
	    ImageView view = new ImageView(new Image(imagePath));
	    view.setFitHeight(buttonHeight);
	    view.setPreserveRatio(true);
	 
	    button.setGraphic(view);
	    
	    return button ;
	}
	
}
