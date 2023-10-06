package ds.mydata.manager;

import java.sql.Connection;

import org.hibernate.Session;

import ds.data.core.context.ContextUtils;
import ds.data.core.dao.DatabaseManager;
import ds.data.core.dao.JdbcWork;
import ds.ehr.dao.constant.EHR;
import ds.ehr.dao.producer.Ehr;
import ds.ehr.meta.constant.META;
import ds.sqlite.constant.LOCAL;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Singleton
public class MydataManager implements DatabaseManager {

	@Inject
	@Ehr
	@Named(EHR.DataSource.AAEHRTEST)
	private EntityManagerFactory mEntityManagerFactoryEHR;
	
	private EntityManager mEntityManagerEHR ;
//	@Inject
//	@EhrTest
//	@Named(EHR.DataSource.AAEHRTEST)
//	private EntityManager mEntityManagerTest;
	
//	@Inject
//	@EhrDev
//	@Named(EHR.DataSource.EHRDEV)
	private EntityManager mEntityManagerDev;
	
//	@Inject
//	@Meta
//	@Named(META.DataSource.META)
	private EntityManager mEntityManagerMeta;
	
//	@Inject
//	@Local
//	@Named(LOCAL.DataSource.LOCAL)
	private EntityManager mEntityManagerLocal;
	
	private String mLocate = null;
	
	@PostConstruct
	public void initialize() {
		mEntityManagerEHR = mEntityManagerFactoryEHR.createEntityManager();
	}
	
	public Connection getConnection() {
		
		if (mEntityManagerEHR == null)
			return null ;
		
		Session session = null ;
		if (EHR.Locate.EHRDEV == mLocate)
			session = mEntityManagerDev.unwrap(Session.class);
		
		else if (META.DataSource.META == mLocate)
			session = mEntityManagerMeta.unwrap(Session.class);
		
		else if (LOCAL.DataSource.LOCAL == mLocate)
			session = mEntityManagerMeta.unwrap(Session.class);
		
		else
			session = mEntityManagerEHR.unwrap(Session.class);
		
		JdbcWork work = new JdbcWork();
		session.doWork(work);
		
		return work.getConnection();
		
	}

	@Override
	public EntityManager getEntityManager() {
		return mEntityManagerEHR;
	}

	@Override
	public EntityManager getEntityManager(String DBLocate) {
		if (EHR.Locate.EHRDEV == DBLocate)
			return mEntityManagerDev ;
		
		else if (META.DataSource.META == DBLocate)
			return mEntityManagerMeta;
		
		else if (LOCAL.DataSource.LOCAL == DBLocate)
			return mEntityManagerLocal;
		
		return mEntityManagerEHR ;
	}
	
	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
		if (mEntityManagerFactoryEHR == null)
			ContextUtils.getInteratedContext().setDatabaseConntected(false);
		else {
			ContextUtils.getInteratedContext().setDatabaseConntected(true);
			ContextUtils.getInteratedContext().setDatabaseManager(this);
		}
	}

	@Override
	public void setLocate(String locate) {
		mLocate  = locate;
	}

	@Override
	public String getLocate() {
		return mLocate;
	}


}
