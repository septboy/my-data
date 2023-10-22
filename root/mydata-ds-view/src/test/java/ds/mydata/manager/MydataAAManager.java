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
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Singleton
public class MydataAAManager implements DatabaseManager {

	private static final Logger logger = LoggerFactory.getLogger(MydataAAManager.class);

	
	@Inject
	@Ehr
	@Named(EHR.DataSource.AARODB)
	private EntityManagerFactory mEntityManagerFactoryEHR_AA;
	private EntityManager mEntityManagerEHR_AA;

	private String mLocate = null;

	@PostConstruct
	@Override
	public void initialize() {
		logger.info("initialize");
		mEntityManagerEHR_AA = mEntityManagerFactoryEHR_AA.createEntityManager();
	}

	public Connection getConnection() {

		Session session = null;

		session = mEntityManagerEHR_AA.unwrap(Session.class);

		JdbcWork work = new JdbcWork();
		session.doWork(work);

		return work.getConnection();

	}

	@Override
	public EntityManager getEntityManager() {

		return mEntityManagerEHR_AA;

	}

	@Override
	public EntityManager getEntityManager(String DBLocate) {
		return mEntityManagerEHR_AA;

	}

	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
		ContextUtils.getInteratedContext().setDatabaseConntected(true);
		ContextUtils.getInteratedContext().setDatabaseManager(this);
	}

	@Override
	public void setLocate(String locate) {
		mLocate = locate;
	}

	@Override
	public String getLocate() {
		return mLocate;
	}

	@Override
	public boolean isOpen() {
		return mEntityManagerEHR_AA.isOpen();

	}
	
	@Override
	public String[] getDatabaseNames() {
		return new String[] {
				"안암 RODB"
		};
	}
	
	@Override
	public String getDatabaseName(String databaseCode) {
		switch (databaseCode) {
		case EHR.DataSource.AARODB: {
			return "안암 RODB";
		}
		default:
			return "안암 RODB";
		}
	}
	
	@Override
	public String getDatabaseCode(String databaseName) {
		String dbLinkName = null;
		
		if ( databaseName.equals(getDatabaseNames()[0]) ) {
			dbLinkName = EHR.DataSource.AARODB;
		}
		
		return dbLinkName;
	}
}
