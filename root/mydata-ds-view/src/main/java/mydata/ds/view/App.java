package mydata.ds.view;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.cdi.MvvmfxCdiApplication;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mydata.ds.view.events.TriggerShutdownEvent;
import mydata.ds.view.main.MainView;
import mydata.ds.view.main.MainViewModel;
import mydata.ds.view.util.EventUtils;
import mydata.ds.view.util.ViewUtils;

public class App extends MvvmfxCdiApplication {

	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String... args) {

		Locale.setDefault(Locale.KOREAN);

		launch(args);
	}

	@Inject
	private ResourceBundle resourceBundle;

	/**
	 *  초기화
	 */
	@Override
	public void initMvvmfx() throws Exception {
		logger.info("initializeing the DataSet View Application");
	}

	/**
	 * 초기화 이후 시작
	 */
	@Override
	public void startMvvmfx(Stage stage) throws Exception {
		logger.info("Starting the Application");
		MvvmFX.setGlobalResourceBundle(resourceBundle);
		
		stage.setTitle(resourceBundle.getString("window.title"));
		// 전체화면
		ViewTuple<MainView, MainViewModel> mainRoot = FluentViewLoader
				.fxmlView(MainView.class)
				.load();
		
		Scene rootScene = new Scene(mainRoot.getView());
		rootScene.getStylesheets().add("/ds-view2.css");
		ViewUtils.setSceneToAppContext(rootScene);
		
		initializeFileDragAndDrop(rootScene);
		initializeEventForSceneSizeChage(mainRoot, rootScene);
				
		
		rootScene.setOnMousePressed(this::handleMousePressedOnRootScene );
		
		stage.setScene(rootScene);
	    stage.setMaximized(true);
		stage.show();
	}

	private void initializeEventForSceneSizeChage(ViewTuple<MainView, MainViewModel> mainRoot, Scene rootScene) {
		AnchorPane toolbar = (AnchorPane) mainRoot.getView().lookup("#main_toolbar");
		
		// Add a listener to detect when the stage (window) size changes
		rootScene.heightProperty().addListener((observable, oldValue, newValue) -> {
            double height = newValue.doubleValue();
            System.out.println("Window Height: " + height);
            double toolbarHeight = toolbar.heightProperty().get();
            AnchorPane.setTopAnchor(toolbar, (height/2) - (toolbarHeight/2));
    	    AnchorPane.setLeftAnchor(toolbar, 10.0);
        });
	}

	private void initializeFileDragAndDrop(Scene scene) {
		
		scene.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();

            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }

            event.consume();
        });
		
		scene.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasFiles()) {
                success = true;

                List<File> files = db.getFiles();
                handleDroppedFiles(files);
            }

            event.setDropCompleted(success);
            event.consume();
        });
	}
	/**
	 * The shutdown of the application can be triggered by firing the
	 * {@link TriggerShutdownEvent} CDI event.
	 */
	public void triggerShutdown(@Observes TriggerShutdownEvent event) {
		logger.info("Application will now shut down");
		Platform.exit();
	}
	
	private void handleMousePressedOnRootScene(MouseEvent event) {
		logger.debug("handleMousePressedOnRootScene >> {}", EventUtils.getNodeNameWhenMousePressed(event));
	}
	
	private void handleDroppedFiles(List<File> files) {
        // Perform your desired action with the dropped files
        System.out.println("Dropped files:");
        for (File file : files) {
            System.out.println(file.getAbsolutePath());
            // Add your custom processing logic here
        }
    }

}
