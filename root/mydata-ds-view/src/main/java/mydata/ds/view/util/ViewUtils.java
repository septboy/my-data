package mydata.ds.view.util;

import java.io.IOException;
import java.util.List;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.SubQueryExpression;

import de.saxsys.mvvmfx.data.TableViewData;
import ds.data.core.util.ColUtils;
import ds.data.core.util.SqlUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

public class ViewUtils {

	public static TableViewData getTableViewData(SubQueryExpression<?> query) {
		if (query == null)
			return null ;
		
		Expression<?>[] colExpres = ColUtils.getColumnExpressionsFromQuery(query);
		List<Tuple> tupleList = SqlUtil.fetch(query);
		
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
		tableViewData.setTupleList(tupleList);
		
		return tableViewData;
	}
	
	public static Node searchPartentNodeWithId(String id, Node targetNode) {
		Node parentNode = targetNode.getParent();
		if (parentNode == null)
			return null;
		else {
			if (id.equals(parentNode.getId()))
				return parentNode;
			else
				return searchPartentNodeWithId(id, parentNode);
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

	private static double getWidth(Region region) {
		if (region.widthProperty().doubleValue() > 0D)
			return region.widthProperty().doubleValue();
		
		if ( region.getPrefWidth() > 0 )
			return region.getPrefWidth();
		
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
}
