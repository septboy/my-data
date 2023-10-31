package mydata.ds.view.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.ViewModel;
import ds.common.util.ArrayUtil;
import ds.data.core.column.Col;
import ds.data.core.column.ColumnInfo;
import jakarta.inject.Inject;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import mydata.ds.view.scopes.AppContext;

public class FunctionViewModel implements ViewModel {

	@Inject
	private AppContext appContext;
	
	@Inject
	private ResourceBundle defaultResourceBundle;

	private List<Integer> funcItemPaneHashcodeList ;
	
	public FunctionViewModel() {
		funcItemPaneHashcodeList = new ArrayList<>();
	}

	public void initialize() {
	}

	public void closeFunctionView(Pane relationViewPane) {}

	public Col<?> getFunctionCol(Integer funcItemPaneHashcode) {
		BaseFunction baseFunction= appContext.getBaseFunctionMap().get(funcItemPaneHashcode);
		return baseFunction.getFunctionCol();
	}

	public void makeVisibleLinkedFuncPane(Integer paneHashcode) {
		Map<Integer, Pane> rootFuncPaneMap= appContext.getRootFuncPaneMap();
		for ( Integer key: this.funcItemPaneHashcodeList  ) {
			Pane pane = rootFuncPaneMap.get(key);
			
			if (key.equals(paneHashcode)) {
				pane.setVisible(true);
			} else {
				pane.setVisible(false);
			}
		}
	}
	
	public static Col<?>[] getParameterCols(ObservableList<Node> nodeList) {
		Col<?>[] cols = null ;
		for (Node node: nodeList) {
			ColumnInfo columnInfo = (ColumnInfo)node.getUserData();
			Col<?> col = columnInfo.getCol();
			if( columnInfo != null)
				cols = ArrayUtil.addArrayOne(cols, col, Col.class);
		}
		return cols;
	}

	public String getFunctionLabelNameSelected(Integer funcItemHashcode) {
		BaseFunction basefunc = this.appContext.getBaseFunctionMap().get(funcItemHashcode);
		return basefunc.functionNameProperty().get();
	}

	public void addFuncItemPaneHashcode(int hashCode) {
		this.funcItemPaneHashcodeList.add(hashCode);
		
	}
	
}
