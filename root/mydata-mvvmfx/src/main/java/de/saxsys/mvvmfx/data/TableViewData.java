package de.saxsys.mvvmfx.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;

import ds.data.core.column.ColumnInfo;
import ds.data.core.context.IntegratedContext;
import ds.data.core.dao.DatabaseManager;
import ds.data.core.excel.xssf.TupleItemDesc;
import ds.data.core.util.ColUtils;
import ds.data.core.util.SqlUtil;

public class TableViewData {

	private static final Logger logger = LoggerFactory.getLogger(TableViewData.class);
	
	Map<String, String> headerColumnNameMap = IntegratedContext.getInstance().getHeaderColumnNameMap();
	
	public static Map<String, String > MAP_FOR_TAG = new HashMap<>(); 
	
	private Expression<?>[] mColumnExpressions;

	private SubQueryExpression<?> query;
	
	private List<Tuple> dataList  ;

	private DatabaseManager databaseManager;

	private ColumnInfo[] columnInfos;

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

	public void setColumnExpressions(Expression<?>[] colExpres) {
		this.mColumnExpressions = colExpres;
		
	}

	public void setQuery(SubQueryExpression<?> query) {
		this.query= query ;
	}
	
	public SubQueryExpression<?> getQuery() {
		return this.query;
	}

	public void fetch()  {
		this.dataList = SqlUtil.fetch(query);
		
		Integer size = null ; 
		if (this.dataList == null )
			size = 0 ;
		else 
			size= this.dataList.size();
		
		logger.info("{}건이 검색되었습니다.", size);
	}
	
	public List<Tuple> getTupleList(){
		return this.dataList;
	}

	@SuppressWarnings("unchecked")
	public <T> Expression<T> getColumnExpression(String name, Class<T> clazz) {
		
		for(Expression<?> columnExpre: mColumnExpressions) {
			String columnName = ColUtils.getColumnName(columnExpre);
			if(name.equalsIgnoreCase(columnName))
				return (Expression<T>)columnExpre;
		}
		return null;
	}

	public void setDatabaseManager(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	public DatabaseManager getDatabaseManager() {
		return this.databaseManager;
	}

	public void setColumnInfos(ColumnInfo[] columnInfos) {
		this.columnInfos = columnInfos ;
		
	}

	public ColumnInfo[] getColumnInfos() {
		return this.columnInfos;
		
	}
}
