package de.saxsys.mvvmfx.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;

import ds.data.core.context.IntegratedContext;
import ds.data.core.excel.xssf.TupleItemDesc;

public class TableViewData {

	Map<String, String> headerColumnNameMap = IntegratedContext.getInstance().getHeaderColumnNameMap();
	
	public static Map<String, String > MAP_FOR_TAG = new HashMap<>(); 
	
	private Expression<?>[] mColumnExpressions;
	private List<Tuple> mTupleList;

	public Expression<?>[] getColumnExpressions() {
		return mColumnExpressions;
	}

	public String getHeaderName(Expression<?> colExpre) {
		
		String engHeaderName = TupleItemDesc.getEngHeaderName(colExpre);
		String tagName = TupleItemDesc.getTagName(MAP_FOR_TAG, colExpre);
		
		if ( engHeaderName.equalsIgnoreCase(tagName) )
			tagName = colExpre.toString();
		
		String headerDetail = TupleItemDesc.getHeaderCellDefault(headerColumnNameMap, engHeaderName, tagName);
		
		return headerDetail;
	}

	public List<Tuple> getTupleList() {
		return mTupleList;
	}

	public void setColumnExpressions(Expression<?>[] colExpres) {
		this.mColumnExpressions = colExpres;
		
	}

	public void setTupleList(List<Tuple> tupleList) {
		this.mTupleList = tupleList;
		
	}

}
