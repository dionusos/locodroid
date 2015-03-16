package hu.denes.command_center.storage;

import hu.denes.command_center.client_connection.Loco;
import hu.denes.command_center.roco_connection.RailwayConnection;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Storage {

	private static final String PERSISTENCE_UNIT_NAME = "mav-iit";
	private static final String DBASE_DIRECTORY = "/home/denes/Work/teszt";

	private EntityManagerFactory factory;
	private EntityManager em;

	RailwayConnection connection;

	public Storage(final RailwayConnection connection) {
		this.connection = connection;
	}

	private static String getDir() {
		return DBASE_DIRECTORY;
	}

	private static String getUnit() {
		return PERSISTENCE_UNIT_NAME;
	}

	public void initDB() {
		factory = Persistence.createEntityManagerFactory(getUnit());
		em = factory.createEntityManager();
	}

	public void closeDB() {
		em.close();
	}

	public void addLoco(final Loco l) {
		newEntity(l);
	}

	public void newEntity(final Object o) {
		em.getTransaction().begin();
		try {
			em.persist(o);
			em.getTransaction().commit();
		} catch (final Exception e) {
			em.getTransaction().rollback();
			System.out.println("Adding object failed!");
		}
	}

	public Loco getLocoByAddress(final Integer address) {
		final Query q = em
				.createQuery("SELECT l FROM Loco l WHERE l.address = :address");
		q.setParameter("address", address);
		Loco l;
		try {
			l = (Loco) q.getSingleResult();
			l.setRailwayConnection(connection);
		} catch (final NoResultException nre) {
			l = null;
		}
		return l;
	}

	public boolean isLocoExistByAddress(final Integer address) {
		try {
			final Query q = em
					.createQuery("SELECT l FROM Loco l WHERE l.address = :address");
			q.setParameter("address", address);

			final Loco l = (Loco) q.getSingleResult();
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	public List<Integer> getLocoAddresses() {
		final List<Integer> l = new ArrayList<Integer>();
		try {
			final Query q = em.createQuery("SELECT l.address FROM Loco l");

			final List<Object> l2 = q.getResultList();
			for (final Object o : l2) {
				l.add((Integer) o);
			}
		} catch (final Exception e) {

		}
		return l;
	}
}
