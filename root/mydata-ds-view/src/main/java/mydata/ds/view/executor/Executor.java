package mydata.ds.view.executor;

import de.saxsys.mvvmfx.data.TableViewData;

public interface Executor<T> {

	void execute(T param);

	TableViewData getTableViewData() ;
	
}
