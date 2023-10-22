package ds.mydata.manager;

import java.sql.Connection;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ds.data.core.context.ContextUtils;
import ds.data.core.dao.DatabaseManager;
import ds.data.core.dao.JdbcWork;
import ds.ehr.dao.constant.EHR;
import ds.ehr.dao.producer.Ehr;
import ds.ehr.dao.producer.EhrDev;
import ds.ehr.dao.producer.EhrTest;
import ds.ehr.meta.constant.META;
import ds.ehr.meta.producer.Meta;
import ds.sqlite.constant.LOCAL;
import ds.sqlite.producer.Local;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Vetoed;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Singleton
@Vetoed
public class MydataManager implements DatabaseManager {

	private static final Logger logger = LoggerFactory.getLogger(MydataManager.class);
	
	@Inject
	@Ehr
	@Named(EHR.DataSource.AAEHRPROD)
	private EntityManagerFactory mEntityManagerFactoryEHR_AA;
	private EntityManager mEntityManagerEHR_AA ;
	
	@Inject
	@EhrTest
	@Named(EHR.DataSource.AAEHRTEST)
	private EntityManagerFactory mEntityManagerFactoryTest_AA;
	private EntityManager mEntityManagerTest_AA ;
	
	
	@Inject
	@Ehr
	@Named(EHR.DataSource.GREHRPROD)
	private EntityManagerFactory mEntityManagerFactoryEHR_GR;
	private EntityManager mEntityManagerEHR_GR ;
	
	@Inject
	@EhrTest
	@Named(EHR.DataSource.GREHRTEST)
	private EntityManagerFactory mEntityManagerFactoryTest_GR;
	private EntityManager mEntityManagerTest_GR ;
	
	@Inject
	@Ehr
	@Named(EHR.DataSource.ASEHRPROD)
	private EntityManagerFactory mEntityManagerFactoryEHR_AS;
	private EntityManager mEntityManagerEHR_AS ;
	
	@Inject
	@EhrTest
	@Named(EHR.DataSource.ASEHRTEST)
	private EntityManagerFactory mEntityManagerFactoryTest_AS;
	private EntityManager mEntityManagerTest_AS ;
	
	
	@Inject
	@EhrDev
	@Named(EHR.DataSource.EHRDEV)
	private EntityManagerFactory mEntityManagerFactoryDev;
	private EntityManager mEntityManagerDev ;
	
	@Inject
	@Meta
	@Named(META.DataSource.META)
	private EntityManagerFactory mEntityManagerFactoryMeta;
	private EntityManager mEntityManagerMeta ;
	
	@Inject
	@Local
	@Named(LOCAL.DataSource.LOCAL)
	private EntityManagerFactory mEntityManagerFactoryLocal;
	private EntityManager mEntityManagerLocal ;
	
	private String mLocate = null;
	
	@PostConstruct
	@Override
	public void initialize() {
		logger.info("initialize");
		mEntityManagerEHR_AA = mEntityManagerFactoryEHR_AA.createEntityManager();
		mEntityManagerEHR_GR = mEntityManagerFactoryEHR_GR.createEntityManager();
		mEntityManagerEHR_AS = mEntityManagerFactoryEHR_AS.createEntityManager();
		
		mEntityManagerTest_AA = mEntityManagerFactoryTest_AA.createEntityManager();
		mEntityManagerTest_GR = mEntityManagerFactoryTest_GR.createEntityManager();
		mEntityManagerTest_AS = mEntityManagerFactoryTest_AS.createEntityManager();
		
		mEntityManagerDev = mEntityManagerFactoryDev.createEntityManager();
		mEntityManagerMeta = mEntityManagerFactoryMeta.createEntityManager();
		mEntityManagerLocal = mEntityManagerFactoryLocal.createEntityManager();
		
	}
	
	public Connection getConnection() {
		
		Session session = null ;
		
		if (EHR.Locate.EHRDEV == mLocate)
			session = mEntityManagerDev.unwrap(Session.class);
		
		else if (META.DataSource.META == mLocate)
			session = mEntityManagerMeta.unwrap(Session.class);
		
		else if (LOCAL.DataSource.LOCAL == mLocate)
			session = mEntityManagerLocal.unwrap(Session.class);
		
		else if (EHR.DataSource.AAEHRPROD == mLocate)
			session = mEntityManagerEHR_AA.unwrap(Session.class);

		else if (EHR.DataSource.AAEHRTEST == mLocate)
			session = mEntityManagerTest_AA.unwrap(Session.class);

		else if (EHR.DataSource.GREHRPROD == mLocate)
			session = mEntityManagerEHR_GR.unwrap(Session.class);

		else if (EHR.DataSource.GREHRTEST == mLocate)
			session = mEntityManagerTest_GR.unwrap(Session.class);

		else if (EHR.DataSource.ASEHRPROD == mLocate)
			session = mEntityManagerEHR_AS.unwrap(Session.class);

		else if (EHR.DataSource.ASEHRTEST == mLocate)
			session = mEntityManagerTest_AS.unwrap(Session.class);

		else // 기본 연결 DB는 안암 테스트
			session = mEntityManagerTest_AA.unwrap(Session.class);
		
		JdbcWork work = new JdbcWork();
		session.doWork(work);
		
		return work.getConnection();
		
	}

	@Override
	public EntityManager getEntityManager() {
		
		if (EHR.Locate.EHRDEV == mLocate)
			return mEntityManagerDev;
		
		else if (META.DataSource.META == mLocate)
			return mEntityManagerMeta;
		
		else if (LOCAL.DataSource.LOCAL == mLocate)
			return mEntityManagerLocal;
		
		else if (EHR.DataSource.AAEHRPROD == mLocate)
			return mEntityManagerEHR_AA;

		else if (EHR.DataSource.AAEHRTEST == mLocate)
			return mEntityManagerTest_AA;

		else if (EHR.DataSource.GREHRPROD == mLocate)
			return mEntityManagerEHR_GR;

		else if (EHR.DataSource.GREHRTEST == mLocate)
			return mEntityManagerTest_GR;

		else if (EHR.DataSource.ASEHRPROD == mLocate)
			return mEntityManagerEHR_AS;

		else if (EHR.DataSource.ASEHRTEST == mLocate)
			return mEntityManagerTest_AS;

		else // 기본 연결 DB는 안암 테스트
			return mEntityManagerTest_AA;
		
	}

	@Override
	public EntityManager getEntityManager(String DBLocate) {
		if (EHR.Locate.EHRDEV == DBLocate)
			return mEntityManagerDev ;
		
		else if (META.DataSource.META == DBLocate)
			return mEntityManagerMeta;
		
		else if (LOCAL.DataSource.LOCAL == DBLocate)
			return mEntityManagerLocal;
		
		else if (EHR.DataSource.AAEHRPROD == DBLocate)
			return mEntityManagerEHR_AA;

		else if (EHR.DataSource.AAEHRTEST == DBLocate)
			return mEntityManagerTest_AA;

		else if (EHR.DataSource.GREHRPROD == DBLocate)
			return mEntityManagerEHR_GR;

		else if (EHR.DataSource.GREHRTEST == DBLocate)
			return mEntityManagerTest_GR;

		else if (EHR.DataSource.ASEHRPROD == DBLocate)
			return mEntityManagerEHR_AS;

		else if (EHR.DataSource.ASEHRTEST == DBLocate)
			return mEntityManagerTest_AS;

		else // 기본 연결 DB는 안암 테스트
			return mEntityManagerTest_AA;
	}
	
	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
		ContextUtils.getInteratedContext().setDatabaseConntected(true);
		ContextUtils.getInteratedContext().setDatabaseManager(this);
	}

	@Override
	public void setLocate(String locate) {
		mLocate  = locate;
	}

	@Override
	public String getLocate() {
		return mLocate;
	}

	@Override
	public boolean isOpen() {
		if (EHR.Locate.EHRDEV == mLocate)
			return mEntityManagerDev.isOpen();
		
		else if (META.DataSource.META == mLocate)
			return mEntityManagerMeta.isOpen();
		
		else if (LOCAL.DataSource.LOCAL == mLocate)
			return mEntityManagerLocal.isOpen();
		
		else if (EHR.DataSource.AAEHRPROD == mLocate)
			return mEntityManagerEHR_AA.isOpen();

		else if (EHR.DataSource.AAEHRTEST == mLocate)
			return mEntityManagerTest_AA.isOpen();

		else if (EHR.DataSource.GREHRPROD == mLocate)
			return mEntityManagerEHR_GR.isOpen();

		else if (EHR.DataSource.GREHRTEST == mLocate)
			return mEntityManagerTest_GR.isOpen();

		else if (EHR.DataSource.ASEHRPROD == mLocate)
			return mEntityManagerEHR_AS.isOpen();

		else if (EHR.DataSource.ASEHRTEST == mLocate)
			return mEntityManagerTest_AS.isOpen();

		else // 기본 연결 DB는 안암 테스트
			return mEntityManagerTest_AA.isOpen();
		
	}

	@Override
	public String[] getDatabaseNames() {
		return new String[] {
				"안암 운영", "안암 테스트"
			    , "구로 운영", "구로 테스트"
			    , "안산 운영", "안산 테스트"
		};
	}
	
	@Override
	public String getDatabaseName(String databaseCode) {
		switch (databaseCode) {
		case EHR.DataSource.AAEHRPROD: {
			return "안암 운영";
		}
		case EHR.DataSource.AAEHRTEST: {
			return "안암 테스트";
		}
		case EHR.DataSource.GREHRPROD: {
			return "구로 운영";
		}
		case EHR.DataSource.GREHRTEST: {
			return "구로 테스트";
		}
		case EHR.DataSource.ASEHRPROD: {
			return "안산 운영";
		}
		case EHR.DataSource.ASEHRTEST: {
			return "안산 테스트";
		}
		default:
			return "안암 테스트";
		}
	}
	
	@Override
	public String getDatabaseCode(String databaseName) {
		String dbLinkName = null;
		
		if ( databaseName.equals(getDatabaseNames()[0]) ) {
			dbLinkName = EHR.DataSource.AAEHRPROD;
		}
		else if ( databaseName.equals(getDatabaseNames()[1]) ){
			dbLinkName = EHR.DataSource.AAEHRTEST;
		}
		else if ( databaseName.equals(getDatabaseNames()[2]) ){
			dbLinkName = EHR.DataSource.GREHRPROD;
		}
		else if ( databaseName.equals(getDatabaseNames()[3]) ){
			dbLinkName = EHR.DataSource.GREHRTEST;
		}
		else if ( databaseName.equals(getDatabaseNames()[4]) ){
			dbLinkName = EHR.DataSource.ASEHRPROD;
		}
		else if ( databaseName.equals(getDatabaseNames()[5]) ){
			dbLinkName = EHR.DataSource.ASEHRTEST;
		}
		
		return dbLinkName;
	}
}
