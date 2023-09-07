package mydata.ds.view.condition;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import ds.common.util.CommonUtil;
import jakarta.inject.Inject;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mydata.ds.view.dataset.DataSetView;

public class ConditionView implements FxmlView<ConditionViewModel> {

	private static final Logger logger = LoggerFactory.getLogger(ConditionView.class);
	
	@FXML
	private AnchorPane conditionRoot;

	@FXML
	private TextField inputText ;
	
	@FXML
	private Label conditionItemName;
	
	@Inject
	private Stage mainRootStage;
	
	@InjectViewModel
	private ConditionViewModel viewModel;

	@InjectContext
	private Context context ;
	
	public void initialize() {
		logger.debug("ConditionView initialize!");
		
		conditionItemName.setText(viewModel.getConditionViewInfo().getColumnComment());
		Object value = viewModel.getConditionViewInfo().getValue();
		
		if(CommonUtil.isNotEmpty(value))
			viewModel.textPropery().set((String)value);
		
		// 이미 Open되어 있는 Condition 입력 상자를 닫는다.
		viewModel.subscribe(ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION, this::closeConditionView );
		
		inputText.textProperty().bindBidirectional(viewModel.textPropery());

		// 입력창에서 enter키 누른 경우 이벤트 발생
		inputText.setOnAction(this::inputTextEnterAction);
	}
	
	private void inputTextEnterAction(Event event) {
		closeConditionView(null);
	}
	
	private void closeConditionView(String key, Object... payload) {
		
		ConditionViewInfo conditionViewInfo = viewModel.getConditionViewInfo(); 
		
		conditionViewInfo.setValue(viewModel.textPropery().get());
		
		Control dataSetConditioncontrolButton = conditionViewInfo.getControlButton();
		
		if (conditionViewInfo.hasValue()) {
			conditionViewInfo.setSelected(true);
			dataSetConditioncontrolButton.setStyle(
		            "-fx-background-color: orange;" +
		            "-fx-text-fill: white;" +
		            DataSetView.css_column_label_border
		        );
			
		} else {
			conditionViewInfo.setSelected(false);
			dataSetConditioncontrolButton.setStyle(DataSetView.css_column_label_border);
		}
		
		dataSetConditioncontrolButton.setUserData(conditionViewInfo);
		((AnchorPane)mainRootStage.getScene().getRoot()).getChildren().remove(conditionRoot);
		
	}
	
}
