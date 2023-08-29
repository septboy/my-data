package de.saxsys.mvvmfx.cdi.it;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.SubQueryExpression;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.data.TableViewData;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import jakarta.inject.Inject;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import mydata.ds.view.util.LinkUtils;
import mydata.ds.view.util.ViewUtils;

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
