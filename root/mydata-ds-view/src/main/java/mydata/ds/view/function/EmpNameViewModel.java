package mydata.ds.view.function;

import de.saxsys.mvvmfx.ViewModel;
import ds.data.core.column.Col;
import ds.data.core.func.EmpName;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import mydata.ds.view.scopes.AppContext;

public class EmpNameViewModel implements ViewModel, BaseFunction {

	@Inject
	private AppContext appContext ;
	
	private HBox empNameHBox;
	
	private StringProperty functionNameProperty = new SimpleStringProperty();
	
	public void initialize() {}

	@Override
	public StringProperty functionNameProperty() {
		return functionNameProperty;
	}

	@Override
	public Col<?> getFunctionCol() {
		Col<?>[] empNameParamterCols = getEmpNameParamterCols();
		
		EmpName empName = EmpName.c(empNameParamterCols[0]);
		
		if (this.functionNameProperty.get() != null)
			empName.cc(this.functionNameProperty.get());
		
		return empName;
	}


	public void putBaseFunction(int funcRootHashcode, BaseFunction baseFunction) {
		this.appContext.getBaseFunctionMap().put(funcRootHashcode, baseFunction);
	}

	public void putRootFuncPaneMap(Integer eachFuncPaneHashcode, StackPane empNameRootStackPane) {
		this.appContext.getRootFuncPaneMap().put(eachFuncPaneHashcode, empNameRootStackPane);
	}

	private Col<?>[] getEmpNameParamterCols() {
		ObservableList<Node> nodeList = this.empNameHBox.getChildren();
		Col<?>[] cols = FunctionViewModel.getParameterCols(nodeList) ;
		return cols;
	}
	

	public void setEmpNameHBox(HBox empNameHBox) {
		this.empNameHBox = empNameHBox;
	}

}
