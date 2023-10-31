package mydata.ds.view.function;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import de.saxsys.mvvmfx.ViewModel;
import ds.data.core.column.Col;
import ds.data.core.func.Count;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import mydata.ds.view.scopes.AppContext;

public class CountViewModel implements ViewModel, BaseFunction {

	@Inject
	private AppContext appContext ;
	
	private HBox countHBox;
	
	private HBox countPartitionByHbox;
	
	private StringProperty functionNameProperty = new SimpleStringProperty();
	
	public void initialize() {}

	@Override
	public StringProperty functionNameProperty() {
		return functionNameProperty;
	}

	@Override
	public Col<?> getFunctionCol() {
		Col<?>[] countParamterCols = getCountParamterCols();
		Col<?>[] countPartitionByCols = getCountPartitionByCols();
		
		Count count = Count.c(countParamterCols[0]);
		if (ArrayUtils.isNotEmpty(countPartitionByCols))
			count.partitionBy(countPartitionByCols);
		
		if (this.functionNameProperty.get() != null)
			count.cc(this.functionNameProperty.get());
		
		return count;
	}


	public void putBaseFunction(int funcRootHashcode, BaseFunction baseFunction) {
		this.appContext.getBaseFunctionMap().put(funcRootHashcode, baseFunction);
	}

	public void putRootFuncPaneMap(Integer eachFuncPaneHashcode, StackPane countRootStackPane) {
		this.appContext.getRootFuncPaneMap().put(eachFuncPaneHashcode, countRootStackPane);
	}

	private Col<?>[] getCountParamterCols() {
		ObservableList<Node> nodeList = this.countHBox.getChildren();
		Col<?>[] cols = FunctionViewModel.getParameterCols(nodeList) ;
		return cols;
	}
	
	private Col[] getCountPartitionByCols() {
		ObservableList<Node> nodeList = this.countPartitionByHbox.getChildren();
		Col<?>[] cols = FunctionViewModel.getParameterCols(nodeList) ;
		return cols;
	}


	public void setCountHBox(HBox countHBox) {
		this.countHBox = countHBox;
	}

	public void setCountPartitionByHbox(HBox countPartitionByHbox) {
		this.countPartitionByHbox = countPartitionByHbox;
	}

}
