package ds.mydata.manager;

import ds.ehr.dao.constant.EHR;
import ds.ehr.dao.producer.Ehr;
import ds.ehr.meta.constant.META;
import ds.sqlite.constant.LOCAL;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Vetoed;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

//@Vetoed
public class MydataProducer {
	
	@Produces
	@Singleton
	@Ehr
	@Named(EHR.DataSource.AAEHRPROD)
	private EntityManagerFactory getEntityManagerFactoryAA() {
        return Persistence
                .createEntityManagerFactory(EHR.DataSource.AAEHRPROD);
    }
	
	@Produces
	@Singleton
	@Ehr
	@Named(EHR.DataSource.AAEHRTEST)
	private EntityManagerFactory getEntityManagerFactoryTestAA() {
        return Persistence
                .createEntityManagerFactory(EHR.DataSource.AAEHRTEST);
    }
	
	@Produces
	@Singleton
	@Ehr
	@Named(EHR.DataSource.GREHRPROD)
	private EntityManagerFactory getEntityManagerFactoryGR() {
        return Persistence
                .createEntityManagerFactory(EHR.DataSource.GREHRPROD);
    }
	
	@Produces
	@Singleton
	@Ehr
	@Named(EHR.DataSource.GREHRTEST)
	private EntityManagerFactory getEntityManagerFactoryTestGR() {
        return Persistence
                .createEntityManagerFactory(EHR.DataSource.GREHRTEST);
    }
	
	@Produces
	@Singleton
	@Ehr
	@Named(EHR.DataSource.ASEHRPROD)
	private EntityManagerFactory getEntityManagerFactoryAS() {
        return Persistence
                .createEntityManagerFactory(EHR.DataSource.ASEHRPROD);
    }
	
	@Produces
	@Singleton
	@Ehr
	@Named(EHR.DataSource.ASEHRTEST)
	private EntityManagerFactory getEntityManagerFactoryTestAS() {
        return Persistence
                .createEntityManagerFactory(EHR.DataSource.ASEHRTEST);
        
    }
	
	@Produces
	@Singleton
	@Ehr
	@Named(EHR.DataSource.EHRDEV)
	private EntityManagerFactory getEntityManagerFactoryDev() {
        return Persistence
                .createEntityManagerFactory(EHR.DataSource.EHRDEV);
    }
	
	@Produces
	@Singleton
	@Ehr
	@Named(META.DataSource.META)
	private EntityManagerFactory getEntityManagerFactoryMeta() {
        return Persistence
                .createEntityManagerFactory(META.DataSource.META);
    }
	
	@Produces
	@Singleton
	@Ehr
	@Named(LOCAL.DataSource.LOCAL)
	private EntityManagerFactory getEntityManagerFactoryLocal() {
        return Persistence
                .createEntityManagerFactory(LOCAL.DataSource.LOCAL);
    }
}