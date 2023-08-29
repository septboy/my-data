package mydata.ds.view.model;

import java.util.Optional;
import java.util.Set;

/**
 * 데이터 CRUD
 */
public interface Repository {

	Set<Contact> findAll();

	Optional<Contact> findById(String id);

	void save(Contact contact);

	void delete(Contact contact);

}
