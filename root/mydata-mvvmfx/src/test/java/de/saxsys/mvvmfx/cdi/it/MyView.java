package de.saxsys.mvvmfx.cdi.it;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;

import datalife.ds.mydata.TupleValueFactory;
import datalife.view.utils.LinkUtils;
import datalife.view.utils.TableViewData;
import datalife.view.utils.ViewUtils;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import ds.common.util.ArrayUtil;
import ds.data.core.column.C;
import ds.data.core.context.IntegratedContext;
import ds.data.core.excel.xssf.ItemDesc;
import ds.data.core.excel.xssf.TupleItemDesc;
import ds.data.core.util.ColUtils;
import ds.data.core.util.SqlUtil;
import jakarta.inject.Inject;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class MyView implements FxmlView<MyViewModel> {
	
	
	@FXML
	public TableView<Tuple> table_view;
	
	@FXML
	public Button btn;
	
	@Inject
	Stage primaryStage;
	
	@Inject
	Application.Parameters parameters;
	
	@Inject
	NotificationCenter notificationCenter;
	
	@Inject
	HostServices hostServices;
	
	@InjectViewModel
	private MyViewModel viewModel;
	
	public static int instanceCounter = 0;
	
	public MyView() {
		instanceCounter++;
	}
	
	@FXML
	private void search() {
				
		SubQueryExpression<?> query = viewModel.getMyService().getEmrTerm();
		
		TableViewData tableViewData = ViewUtils.getTableViewData(query);
		
		LinkUtils.link(table_view, tableViewData);
	}

	
}
