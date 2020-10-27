package rental;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import company.CarType;
import company.ICarRentalCompany;
import company.Reservation;
import company.ReservationConstraints;
import company.ReservationException;

public class RentalAgency implements IRentalAgency {
	
	private String name;
	
	private Set<ICarRentalCompany> companies = new HashSet<>();
	
	public RentalAgency(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Add a car rental company to this rental agency by looking it up in the registry.
	 */
	@Override
	public void addCompany(String crcName) throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(14540);
		companies.add((ICarRentalCompany) registry.lookup(crcName));
	}
	
	/**
	 * Remove a car rental company from this rental agency.
	 */
	@Override
	public void removeCompany(ICarRentalCompany company) throws RemoteException {
		companies.remove(company);
	}
	
	/**
	 * Get the car rental company of this rental agency with the specified name.
	 */
	@Override
	public ICarRentalCompany getCompany(String name) throws RemoteException {
		for (ICarRentalCompany company : companies) {
			if (company.getName().equals(name))
				return company;
		}
		
		return null;
	}
	
	/**
	 * Get the set of all car rental companies of this rental agency.
	 */
	@Override
	public Set<ICarRentalCompany> getCompanies() {
		return new HashSet<>(companies);
	}
	
	/**
	 * Create a new reservation session.
	 */
	@Override
	public ReservationSession getNewReservationSession(String owner) {
		return new ReservationSession(this, owner);
	}
	
	/**
	 * Create a new manager session.
	 */
	@Override
	public ManagerSession getNewManagerSession(String owner) {
		return new ManagerSession(this, owner);
	}
	
	/**
	 * Get the set of all available car types in a given period.
	 */
	@Override
	public Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException {
		Set<CarType> types = new HashSet<>();
		for (ICarRentalCompany company : getCompanies()) {
			types.addAll(company.getAvailableCarTypes(start, end));
		}
		
		return types;
	}
	
	/**
	 * Create a quote for the given client with the given constraints.
	 */
	@Override
	public Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException, java.rmi.RemoteException {
		for (ICarRentalCompany company : getCompanies()) {
			try {
				return company.createQuote(constraints, client);
			} catch (ReservationException exception) {
				continue;
			}
			// For if there is no car with the given car type in the current car rental company.
			catch (IllegalArgumentException exception) {
				continue;
			}
		}
		
		throw new ReservationException("<" + getName() + "> No cars available to satisfy the given constraints.");
	}
	
	/**
	 * Confirm all quotes of this reservation session.
	 */
	@Override
	public List<Reservation> confirmQuotes(Set<Quote> quotes) throws RemoteException, ReservationException {
		List<Reservation> reservations = new ArrayList<>();
		for (Quote quote : quotes) {
			try {
				Reservation reservation = getCompany(quote.getRentalCompany()).confirmQuote(quote);
				reservations.add(reservation);
			} catch (ReservationException exception) {
				for (Reservation reservation : reservations) {
					getCompany(reservation.getRentalCompany()).cancelReservation(reservation);
				}
				throw exception;
			}
		}
		
		return reservations;
	}
	
	/**
	 * Get the set of available car types in a given region for a given period.
	 */
	@Override
	public Set<CarType> getAvailableCarTypesForRegion(Date start, Date end, String region) throws RemoteException {
		Set<CarType> types = new HashSet<>();
		for (ICarRentalCompany company : getCompanies()) {
			if (company.getRegions().contains(region))
				types.addAll(company.getAvailableCarTypes(start, end));
		}
		
		return types;
	}
	
}
