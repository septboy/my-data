package ds.mydata.manager;

import ds.ehr.dao.constant.EHR;
import ds.ehr.dao.producer.Ehr;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


public class MydataAAProducer {
	
	@Produces
	@Singleton
	@Ehr
	@Named(EHR.DataSource.AARODB)
	private EntityManagerFactory getEntityManagerFactory_EHR() {
        return Persistence
                .createEntityManagerFactory(EHR.DataSource.AARODB);
    }

}