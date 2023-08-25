package ds.mydata.manager;

import ds.ehr.dao.constant.EHR;
import ds.ehr.dao.producer.Ehr;
import ds.ehr.dao.producer.EhrTest;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;


public class AAEhrProducer {
	
	@Produces
	@Singleton
	@Ehr
	@Named(EHR.DataSource.AAEHRPROD)
	private EntityManager getEntityManager_EHR() {
        return Persistence
                .createEntityManagerFactory(EHR.DataSource.AAEHRPROD)
                .createEntityManager();
    }
	
//	@Produces
//	@Singleton
//	@EhrTest
//	@Named(EHR.DataSource.AAEHRTEST)
//	private EntityManager getEntityManager_EHRTEST() {
//        return Persistence
//                .createEntityManagerFactory(EHR.DataSource.AAEHRTEST)
//                .createEntityManager();
//    }
}