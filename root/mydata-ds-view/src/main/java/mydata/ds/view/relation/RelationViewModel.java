package mydata.ds.view.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.ViewModel;
import ds.common.util.ArrayUtil;
import ds.data.core.column.C;
import ds.data.core.join.JoinOn;
import jakarta.inject.Inject;
import javafx.scene.layout.Pane;
import mydata.ds.view.dataset.DataSetRelation;
import mydata.ds.view.dataset.DataSetViewModel;
import mydata.ds.view.dataset.RelatedLine;
import mydata.ds.view.dataset.record.RelationHashCodePair;
import mydata.ds.view.scopes.AppContext;
import mydata.ds.view.util.ViewUtils;

public class RelationViewModel implements ViewModel {

	@Inject
	private AppContext appContext;
	
	@Inject
	private ResourceBundle defaultResourceBundle;

	private List<RelationColumnInfo> relationColumnInfoList ;

	private Integer relationHashCode;
	
	public void initialize() {
		this.relationColumnInfoList = new ArrayList<>();
	}

	public void closeRelationView(Pane relationViewPane) {
		int relationHashcode = relationViewPane.hashCode();
		removeRelation(relationHashcode);
	}

	protected void setRelationHashCode(int relationHashcode) {
		this.relationHashCode = relationHashcode;
	}

	public int getRelationHashCode() {
		return this.relationHashCode;
	}
	
	public void removeRelation(int relationViewHashcode) {
		RelationHashCodePair relationCodePair = appContext.getRelationHashCodePair(relationViewHashcode);
		int sourcePaneHashcode = relationCodePair.sourcePaneHashcode();
		removeRelation(relationViewHashcode, sourcePaneHashcode);
		DataSetViewModel sourceDataSetViewModel = appContext.getDataSetViewModel(sourcePaneHashcode);
		sourceDataSetViewModel.getTargetDataSetViewModel();
		
		
		int targetPaneHashcode = relationCodePair.targetPaneHashcode();
		removeRelation(relationViewHashcode, targetPaneHashcode);
		
		//  관계뷰가 없어지면서 관련된 taget datasetPane의 hashcode를 삭제한다.
		sourceDataSetViewModel.removeTargetDataSetHashcode(targetPaneHashcode);
	}

	private void removeRelation(int relationPaneHashcode, int paneHashcode) {
		DataSetRelation datasetRelation = appContext.getDataSetRelation(paneHashcode);
		List<RelatedLine> relatedLineList = datasetRelation.getRelatedLineList(relationPaneHashcode);

		for (RelatedLine relatedLine : relatedLineList) {
			//0:source //1:target
			ViewUtils.removeFromScene(relatedLine.line());
			ViewUtils.removeFromScene(relatedLine.arrowhead());
			ViewUtils.removeFromScene(relatedLine.relationPane());
		}
		datasetRelation.reflashRelatedLine(relatedLineList);
		
		datasetRelation.getRelatedPaneList().removeIf(item -> item.relationPane().hashCode() == relationPaneHashcode);
	}

	public JoinOn getJoinOn() {
		C[] cols = new C[] {C.patno} ;
		for( RelationColumnInfo relationColumnInfo :this.relationColumnInfoList) {
			C col = (C)relationColumnInfo.getCol();
			cols = ArrayUtil.addArrayOne(cols, col, C.class); 
		}
		return JoinOn.c(cols);
	}

	public void addRelationColumnInfo(RelationColumnInfo relationColumInfo) {
		this.relationColumnInfoList.add(relationColumInfo);
		
	}
	
	public void removeRelationColumnInfo(RelationColumnInfo relationColumInfo) {
		this.relationColumnInfoList.remove(relationColumInfo);
	}

	public void putRelationViewModel(int relationHashcode, RelationViewModel relationViewModel) {
		this.appContext.putRelationViewModel(relationHashcode, relationViewModel);
	}

	public void putRelationView(int relationHashcode, RelationView relationView) {
		this.appContext.putRelationView(relationHashcode, relationView);
	}

}
