package mydata.ds.view.util;

import java.util.List;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.SubQueryExpression;

import de.saxsys.mvvmfx.data.TableViewData;
import ds.data.core.util.ColUtils;
import ds.data.core.util.SqlUtil;

public class ViewUtils {

	public static TableViewData getTableViewData(SubQueryExpression<?> query) {
		Expression<?>[] colExpres = ColUtils.getColumnExpressionsFromQuery(query);
		List<Tuple> tupleList = SqlUtil.fetch(query);
		
		for (Expression<?> expre: colExpres) {
			if (expre instanceof Operation ) {
				String columnName = ColUtils.getColumnName(expre);
				TableViewData.MAP_FOR_TAG.put(expre.toString(), columnName );	
			} else {
				TableViewData.MAP_FOR_TAG.put(expre.toString(), expre.toString() );	
			}
		}
		
		TableViewData tableViewData = new TableViewData();
		tableViewData.setColumnExpressions(colExpres);
		tableViewData.setTupleList(tupleList);
		
		return tableViewData;
	}

}
