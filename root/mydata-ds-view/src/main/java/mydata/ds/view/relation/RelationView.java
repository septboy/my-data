package mydata.ds.view.relation;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RelationView implements FxmlView<RelationViewModel> {

	@FXML
	private AnchorPane relationViewPain;

	@InjectViewModel
	private RelationViewModel viewModel;

	@Inject
	private Stage primaryStage;

	public void initialize() {
	}

}
