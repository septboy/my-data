package mydata.ds.view.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.Initialize;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class MainView implements FxmlView<MainViewModel> {

	private static final Logger logger = LoggerFactory.getLogger(MainView.class);

	@InjectViewModel
	private MainViewModel viewModel;

	@InjectContext
	private Context context;

	public void initialize() {
		logger.info("initialize start");
		
	}
}
