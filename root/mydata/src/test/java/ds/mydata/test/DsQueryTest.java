package ds.mydata.test;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAlternativeStereotypes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.SubQueryExpression;

import ds.data.core.column.C;
import ds.data.core.func.Append;
import ds.data.core.func.IndentSpace;
import ds.data.core.param.Param;
import ds.data.core.query.Orderby;
import ds.data.core.query.SqlTextQuery;
import ds.data.core.util.SqlUtil;
import ds.ehr.dao.producer.AAEhrProducer;
import ds.ehr.dao.producer.DevProducer;
import ds.ehr.dao.producer.Ehr;
import ds.ehr.dao.producer.EhrDev;
import ds.ehr.dao.producer.EhrTest;
import ds.ehr.meta.dataset.example.MetaliteUI;
import ds.ehr.meta.producer.Meta;
import ds.ehr.meta.producer.MetaProducer;
import ds.ehr.research.condition.EhrResearchConditions;
import ds.ehr.research.dataset.DataSetEHR;
import ds.ehr.research.dataset.medvisit.ApPatientEhrDataSet;
import ds.mydata.manager.MydataManager;
import ds.sqlite.producer.Local;
import ds.sqlite.producer.LocalProducer;

@EnableAutoWeld
@AddBeanClasses({AAEhrProducer.class, DevProducer.class, MetaProducer.class, LocalProducer.class, MydataManager.class})
@EnableAlternativeStereotypes( Ehr.class )
@EnableAlternativeStereotypes( EhrTest.class )
@EnableAlternativeStereotypes( EhrDev.class )
@EnableAlternativeStereotypes( Meta.class )
@EnableAlternativeStereotypes( Local.class )
public class DsQueryTest {

	// WeldInitiator is used to bootstrap the CDI container.
    // Specify your custom CDIProvider implementation here.
//    @WeldSetup
//    public WeldInitiator weld = WeldInitiator.of( 
//    		GREhrProducer.class		
//			, MetaProducer.class
//			, DevProducer.class
//			, LocalProducer.class
//			, EhrResearchManager.class
//    		);
    
    
	String PATH_WRITE = "D:\\Notepad Content\\데이터 요청 처리\\발송파일\\";
	String PATH_READ = "d:\\TMP\\";
	
	// D:\Notepad Content\EMR\SQL\mds-query\
	String PATH_SQL = "D:\\Notepad Content\\EMR\\SQL\\mds-query\\";
	
	// --진료 -----------------------------------
	private ApPatientEhrDataSet mAppatientEHR = ApPatientEhrDataSet.getInstance();
	
	// -- Sql text -----------------------
	private SqlTextQuery mSqlTextQuery =  SqlTextQuery.getInstance();
			
	private MetaliteUI mMetaUI =  MetaliteUI.getInstance();
	
	
	/*
	 20230719_구로_노수현_2964_[금기약어]기록지 내 약어 사용 데이터 요청
	 */
	@Test
	public void GR_금기약어_모니터링() {
		
		EhrResearchConditions c = DataSetEHR.add();
		
		c.setParam(
			    Param.c(C.s("in_gvUserId")  	, "") // 로그인 사용자
			    ,Param.c(C.s("in_useUnitCd")  	, "01") // 사용단위코드    >  병동: 01 , 그외: 02
			    ,Param.c(C.s("in_formTypCd")  	, "1") // 서식타입코드    >  1:병동, 2:외래, 3:응급, 4:다학제
			    ,Param.c(C.s("in_wrtrAthrCd")  	, "") // 작성자권한코드 > 작성권한(간호사: 0003, 치료검사자: 0004, 이외엔 ''
			  );
		c.select(
				C.s("SPRN_DPRT_CD").cc("주관부서코드")
				,C.s("FOCL_ID").cc("서식분류ID")
				,C.s("HGRN_FOCL_ID").cc("상위서식분류ID")
				,C.ni("FOCL_LVEL_VL").cc("서식분류레벨값")
				,Append.c( IndentSpace.c(C.ni("FOCL_LVEL_VL")), C.s("FOCL_NM") ) .cc("서식분류명2")
				,C.s("FOCL_NM").cc("서식분류명")
				,C.s("FOCL_SQNC_VL").cc("서식분류순서값")
				,C.s("FOCL_NM_1").cc("서식분류명")

				);
		SubQueryExpression<?> query1 = mSqlTextQuery.getQuerySQLText(c, "외래 병동서식을 제외한 공통서식을 조회.sql", PATH_SQL);
		
		SqlUtil.excel(query1, Orderby.c(C.nl("FOCL_SQNC_VL")), "[구로]외래 병동서식을 제외한 공통서식을 조회", PATH_WRITE
				);
		
	}

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }
    
    
}
