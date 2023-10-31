package mydata.ds.view.function;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import de.saxsys.mvvmfx.ViewModel;
import ds.data.core.column.Col;
import ds.data.core.func.Groups;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import mydata.ds.view.scopes.AppContext;

public class GroupsViewModel implements ViewModel, BaseFunction {

	@Inject
	private AppContext appContext ;
	
	private HBox groupsHBox;
	
	private HBox groupsPartitionByHbox;
	
	private StringProperty functionNameProperty = new SimpleStringProperty();
	
	public void initialize() {}

	@Override
	public StringProperty functionNameProperty() {
		return functionNameProperty;
	}

	@Override
	public Col<?> getFunctionCol() {
		Col<?>[] groupsParamterCols = getGroupsParamterCols();
		Col<?>[] groupsPartitionByCols = getGroupsPartitionByCols();
		
		Groups groups = Groups.c(groupsParamterCols[0]);
		if (ArrayUtils.isNotEmpty(groupsPartitionByCols))
			groups.partitionBy(groupsPartitionByCols);
		
		if (this.functionNameProperty.get() != null)
			groups.cc(this.functionNameProperty.get());
		
		return groups;
	}


	public void putBaseFunction(int funcRootHashcode, BaseFunction baseFunction) {
		this.appContext.getBaseFunctionMap().put(funcRootHashcode, baseFunction);
	}

	public void putRootFuncPaneMap(Integer eachFuncPaneHashcode, StackPane groupsRootStackPane) {
		this.appContext.getRootFuncPaneMap().put(eachFuncPaneHashcode, groupsRootStackPane);
	}

	private Col<?>[] getGroupsParamterCols() {
		ObservableList<Node> nodeList = this.groupsHBox.getChildren();
		Col<?>[] cols = FunctionViewModel.getParameterCols(nodeList) ;
		return cols;
	}
	
	private Col[] getGroupsPartitionByCols() {
		ObservableList<Node> nodeList = this.groupsPartitionByHbox.getChildren();
		Col<?>[] cols = FunctionViewModel.getParameterCols(nodeList) ;
		return cols;
	}


	public void setGroupsHBox(HBox groupsHBox) {
		this.groupsHBox = groupsHBox;
	}

	public void setGroupsPartitionByHbox(HBox groupsPartitionByHbox) {
		this.groupsPartitionByHbox = groupsPartitionByHbox;
	}

}
