package rental;

import java.rmi.RemoteException;
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

public class ReservationSession extends Session {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 857999803245762097L;
	
	private Set<Quote> quotes = new HashSet<>();
	
	public ReservationSession(IRentalAgency agency, String owner) {
		super(agency, owner);
	}
	
	/**
	 * Get the set of all available car types in a given period.
	 */
	private Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException {
		Set<CarType> types = new HashSet<>();
		for (ICarRentalCompany company : agency.getCompanies()) {
			types.addAll(company.getAvailableCarTypes(start, end));
		}
		
		return types;
	}
	
	/**
	 * Print the available car types in a given period.
	 */
	public void checkForAvailableCarTypes(Date start, Date end) throws Exception {
		for (CarType car : getAvailableCarTypes(start, end))
			System.out.println(car);
	}
	
	/**
	 * Create a quote for the given client with the given constraints.
	 */
	public void createQuote(ReservationConstraints constraints, String client) throws ReservationException, java.rmi.RemoteException {
		for (ICarRentalCompany company : agency.getCompanies()) {
			try {
				quotes.add(company.createQuote(constraints, client));
				break;
			} catch (ReservationException exception) {
				continue;
			}
		}
		
		throw new ReservationException("<" + agency.getName() + "> No cars available to satisfy the given constraints.");
	}
	
	/**
	 * Get the set of pending quotes of this reservation session.
	 */
	public Set<Quote> getCurrentQuotes() {
		return new HashSet<Quote>(quotes);
	}
	
	/**
	 * Confirm all quotes of this reservation session.
	 */
	public List<Reservation> confirmQuotes() throws RemoteException, ReservationException {
		List<Reservation> reservations = new ArrayList<>();
		for (Quote quote : quotes) {
			try {
				Reservation reservation = agency.getCompany(quote.getRentalCompany()).confirmQuote(quote);
				reservations.add(reservation);
			} catch (ReservationException exception) {
				for (Reservation reservation : reservations) {
					agency.getCompany(reservation.getRentalCompany()).cancelReservation(reservation);
				}
				throw exception;
			}
		}
		
		quotes.clear();
		return reservations;
	}
	
	/**
	 * Get the set of available car types in a given region for a given period.
	 */
	public Set<CarType> getAvailableCarTypesForRegion(Date start, Date end, String region) throws RemoteException {
		Set<CarType> types = new HashSet<>();
		for (ICarRentalCompany company : agency.getCompanies()) {
			if (company.getRegions().contains(region))
				types.addAll(company.getAvailableCarTypes(start, end));
		}
		
		return types;
	}
	
	/**
	 * Get the cheapest available car type in a given region for a given period.
	 */
	public CarType getCheapestCarType(Date start, Date end, String region) throws RemoteException {
		return getAvailableCarTypesForRegion(start, end, region).stream().reduce((CarType type1, CarType type2) ->
														 				 		 (type1.getRentalPricePerDay() < type2.getRentalPricePerDay()) ?
														 				 		  type1 : type2).get();
	}
	
}