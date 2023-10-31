package mydata.ds.view.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.Tuple;

import de.saxsys.mvvmfx.data.TableViewData;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

// Service for the background task
public class DataSetService extends Service<List<Tuple>> {
	private static final Logger logger = LoggerFactory.getLogger(DataSetService.class);
	
	private TableViewData tableViewData;

	@Override
	protected Task<List<Tuple>> createTask() {
		return new Task<>() {
			@Override
			protected List<Tuple> call() {
				tableViewData.fetch();
				return tableViewData.getTupleList();
			}
			
		};
	}

	public void setTableViewData(TableViewData tableViewData) {
		this.tableViewData = tableViewData ;
	}
	
	@Override
	protected void succeeded() {
		logger.debug("Service is succeeded.");
	}
	
	@Override
	protected void failed() {
		logger.debug("Service is failed.");
	}
	
	@Override
	protected void cancelled() {
		logger.debug("Service Task is cancelled.");
		Thread myThread = new Thread(()->{
			tableViewData.getDatabaseManager().close();
			tableViewData.getDatabaseManager().refresh();
		});
        myThread.start();
	}
	
}