package mydata.ds.view.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import ds.common.util.CommonUtil;
import ds.data.core.column.ColumnType;
import ds.data.core.condition.ConditionValue;
import ds.data.core.regexp.AnalyzedType;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mydata.ds.view.dataset.DataSetView;
import mydata.ds.view.util.ViewUtils;

public class ConditionView implements FxmlView<ConditionViewModel> {

	private static final Logger logger = LoggerFactory.getLogger(ConditionView.class);
	
	@FXML
	private AnchorPane conditionRoot;

	@FXML
	private TextField firstInputTextField ;
	
	@FXML
	private TextField secondInputTextField ;
	
	@FXML
	private Label firstCompareLabel;
	
	@FXML
	private Label secondCompareLabel;
	
	@FXML
	private CheckBox regexCheckBox;
	
	@FXML
	private Label conditionItemName;
	
	@FXML
	private Button addConditionButton;
	
	@FXML
	private Button removeConditionButton;
	
	@FXML
	private HBox secondConditionHBox;
	
	@FXML
	private VBox conditionWrapperVBox;
	
	@Inject
	private Stage mainRootStage;
	
	@InjectViewModel
	private ConditionViewModel viewModel;

	@InjectContext
	private Context context ;
	
	String[] iconsAll = {"=", ">", "≥", "<", "≤","≠"};
	String[] iconsG = {">", "≥"};
	String[] iconsL = {"<", "≤"};
	
	public void initialize() {
		logger.debug("ConditionView initialize!");
		
		conditionItemName.setText(viewModel.getConditionViewInfo().getColumnComment());
		
		initializeConditionInputField();
		
		initializeRegexCheckBox();
		
		initializeConditionButton();
		
		initializeCompareLabel();
		// 이미 Open되어 있는 Condition 입력 상자를 닫는다.
		viewModel.subscribe(ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION, this::closeConditionView );
	}

	private void initializeCompareLabel() {
		firstCompareLabel.setOnMouseClicked(event -> {
			if ( conditionWrapperVBox.getChildren().size() == 1 ) {
				int maxIconCount = iconsAll.length;
				for (int i=0; i < maxIconCount; i++) {
					String iconStr = firstCompareLabel.getText();
					if(iconStr.equals(iconsAll[i])) {
						if(i < maxIconCount-1) {
							viewModel.firstLabelTextProperty().set(iconsAll[i+1]);
							
						} else { 
							i = 0;
							viewModel.firstLabelTextProperty().set(iconsAll[i]);
						}
						break; 
					}
				}
				
			} else {
				if(firstCompareLabel.getText().equals(iconsL[0])) {
					viewModel.firstLabelTextProperty().set(iconsL[1]);
				} else {
					viewModel.firstLabelTextProperty().set(iconsL[0]);
				}
			}
				
			event.consume();
		});
	
		secondCompareLabel.setOnMouseClicked(event -> {
			if(secondCompareLabel.getText().equals(iconsG[0])) {
				viewModel.secondLabelTextProperty().set(iconsG[1]);
			} else {
				viewModel.secondLabelTextProperty().set(iconsG[0]);
			}
			
			event.consume();
		});
		
	}

	private void initializeConditionButton() {
		AwesomeDude.setIcon(addConditionButton, AwesomeIcon.PLUS, "11.0");
		AwesomeDude.setIcon(removeConditionButton, AwesomeIcon.MINUS, "11.0");	
		
		addConditionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if (!ViewUtils.existNodeWithId(conditionWrapperVBox, "secondConditionHBox")) {
            	   ViewUtils.addNodeIntoPane(conditionWrapperVBox, secondConditionHBox);
            	   
            	   secondInputTextField.textProperty().bindBidirectional(viewModel.secondInputTextPropery());
       			   secondCompareLabel.textProperty().bindBidirectional(viewModel.secondLabelTextProperty());
       			   
            	   viewModel.firstLabelTextProperty().set("≤");
            	   Text compareText = (Text) firstCompareLabel.lookup(".text");
            	   compareText.setLayoutY(-10.0); // Adjust the Y-axis position
            	   compareText.setStyle("-fx-font-size: 16;"); // Set font size
            	   
            	   viewModel.secondLabelTextProperty().set("≥");
            	}
            	event.consume();
            }
		});
		
		removeConditionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if (ViewUtils.existNodeWithId(conditionWrapperVBox, "secondConditionHBox")) {
            		
            		secondInputTextField.textProperty().unbindBidirectional(viewModel.secondInputTextPropery());
        			secondCompareLabel.textProperty().unbindBidirectional(viewModel.secondLabelTextProperty());
        			
            		ViewUtils.removeFromPane(conditionWrapperVBox, secondConditionHBox);
            		viewModel.firstLabelTextProperty().set("=");
            	}
            	
            	event.consume();
            }
		});
	}

	private void initializeRegexCheckBox() {
		ColumnType columnType = viewModel.getConditionViewInfo().getColumnType();
		if ( columnType == ColumnType.String )
			regexCheckBox.setVisible(true);
		else
			regexCheckBox.setVisible(false);
		
		this.regexCheckBox.selectedProperty().bindBidirectional(viewModel.checkRegexpProverty());
		
		this.regexCheckBox.setOnAction(event -> {
            if (this.regexCheckBox.isSelected()) {
                System.out.println("Feature enabled");
                
            } else {
                System.out.println("Feature disabled");
                
            }
        });
	}

	/**
	 * TODO
	 * - 배열 값이 있는 경우 어떻게 처리할 것인지 개선.
	 */
	private void initializeConditionInputField() {
		
		Object value = viewModel.getConditionViewInfo().getValue();
		
		firstInputTextField.textProperty().bindBidirectional(viewModel.firstInputTextPropery());
		firstCompareLabel.textProperty().bindBidirectional(viewModel.firstLabelTextProperty());
		
		// 입력창에서 enter키 누른 경우 이벤트 발생
		firstInputTextField.setOnAction(this::inputTextEnterAction);
		
		if ( value != null && value.getClass().isArray() ) {
			secondInputTextField.textProperty().bindBidirectional(viewModel.secondInputTextPropery());
			secondCompareLabel.textProperty().bindBidirectional(viewModel.secondLabelTextProperty());
			
			if(CommonUtil.isEmpty(value))
				return;
			
			initializeTwoInputTextField(value) ;
			
		} else {
			ViewUtils.removeFromPane(conditionWrapperVBox, secondConditionHBox);
			
			if(CommonUtil.isEmpty(value))
				return;
			
			initializeOneInputTextField(value) ;
			
		}
		
	}
	
	private void initializeTwoInputTextField(Object value) {
		ConditionValue[] conditionValues = (ConditionValue[])value;
		
		viewModel.firstInputTextPropery().set(conditionValues[0].value());
		viewModel.firstLabelTextProperty().set(conditionValues[0].analyzedType().getSign());
		
		viewModel.secondInputTextPropery().set(conditionValues[1].value());
		viewModel.secondLabelTextProperty().set(conditionValues[1].analyzedType().getSign());
		
	}

	// TODO mall function
	private void initializeOneInputTextField(Object value) {
		ConditionValue conditionValue = (ConditionValue)value;
		
		viewModel.firstInputTextPropery().set(conditionValue.value());
		viewModel.firstLabelTextProperty().set(conditionValue.analyzedType().getSign());
	}

	private void inputTextEnterAction(Event event) {
		closeConditionView(null);
	}
	
	private void closeConditionView(String key, Object... payload) {
		
		ConditionViewInfo conditionViewInfo = viewModel.getConditionViewInfo(); 
		
		if (conditionWrapperVBox.getChildren().size() == 2) {
			ConditionValue[] objValues = {
				new ConditionValue(
						AnalyzedType.getType(viewModel.firstLabelTextProperty().get())
						, viewModel.firstInputTextPropery().get()
						)
				, new ConditionValue(
						AnalyzedType.getType(viewModel.secondLabelTextProperty().get())
						, viewModel.secondInputTextPropery().get()
						)
			};
			
			conditionViewInfo.setValue(objValues);
			conditionViewInfo.setCheckRegexp(viewModel.checkRegexpProverty().get());
		} else {
			ConditionValue objValue = getConditionValue();
			
			if ( CommonUtil.isNotEmpty(objValue)) {
				conditionViewInfo.setValue(objValue);
				conditionViewInfo.setCheckRegexp(viewModel.checkRegexpProverty().get());
			}
		}
		
		
		Control conditionLabel = conditionViewInfo.getConditionLabel();
		
		if (conditionViewInfo.hasValue()) {
			conditionViewInfo.setSelected(true);
			conditionLabel.setStyle(
		            "-fx-background-color: orange;" +
		            "-fx-text-fill: white;" +
		            DataSetView.css_column_label_border
		        );
			
		} else {
			conditionViewInfo.setSelected(false);
			conditionLabel.setStyle(DataSetView.css_column_label_border);
		}
		
		conditionLabel.setUserData(conditionViewInfo);
		((AnchorPane)mainRootStage.getScene().getRoot()).getChildren().remove(conditionRoot);
		
	}

	private ConditionValue getConditionValue() {
		String inputText = viewModel.firstInputTextPropery().get();
		
		if (CommonUtil.isEmpty(inputText))
			return null ;
		
		ConditionValue objValue = new ConditionValue(
				AnalyzedType.getType(firstCompareLabel.getText()) //TODO: propery 로 변경
				, inputText
				);
		
		return objValue;
	}
	
	public Node getFocusTargetNode() {
		return this.firstInputTextField;
	}
}
