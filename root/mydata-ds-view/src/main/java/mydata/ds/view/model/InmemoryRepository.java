package mydata.ds.view.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import mydata.ds.view.events.ContactsUpdatedEvent;

@Singleton
public class InmemoryRepository implements Repository {

	private final Set<Contact> contacts = new HashSet<>();

	@Inject
	private Event<ContactsUpdatedEvent> contactsUpdatedEvent;

	@Override
	public Set<Contact> findAll() {
		return Collections.unmodifiableSet(contacts);
	}

	@Override
	public Optional<Contact> findById(String id) {
		return contacts.stream().filter(contact -> contact.getId().equals(id)).findFirst();
	}

	@Override
	public void save(Contact contact) {
		contacts.add(contact);
		fireUpdateEvent();
	}

	@Override
	public void delete(Contact contact) {
		contacts.remove(contact);
		fireUpdateEvent();
	}

	private void fireUpdateEvent() {
		if (contactsUpdatedEvent != null) {
			contactsUpdatedEvent.fire(new ContactsUpdatedEvent());
		}
	}

}
