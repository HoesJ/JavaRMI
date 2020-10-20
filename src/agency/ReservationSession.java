package agency;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import rental.CarType;
import rental.ICarRentalCompany;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

public class ReservationSession {
	
	private RentalAgency agency;
	private Set<Quote> quotes = new HashSet<>();
	
	public ReservationSession(RentalAgency agency) {
		this.agency = agency;
	}
	
	public void createQuote(ReservationConstraints constraints, String client) throws ReservationException, java.rmi.RemoteException {
		for (ICarRentalCompany company : agency.getCompanies().values()) {
			try {
				quotes.add(company.createQuote(constraints, client));
				break;
			} catch (ReservationException exception) {
				continue;
			}
		}
		
		throw new ReservationException("<" + agency.getName() + "> No cars available to satisfy the given constraints.");
	}
	
	public Set<Quote> getCurrentQuotes() {
		return new HashSet<Quote>(quotes);
	}
	
	public Set<Reservation> confirmQuotes() throws RemoteException, ReservationException {
		Set<Reservation> reservations = new HashSet<>();
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
	
	public Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException {
		Set<CarType> types = new HashSet<>();
		for (ICarRentalCompany company : agency.getCompanies().values()) {
			types.addAll(company.getAvailableCarTypes(start, end));
		}
		
		return types;
	}
	
	public Set<CarType> getAvailableCarTypesForRegion(Date start, Date end, String region) throws RemoteException {
		Set<CarType> types = new HashSet<>();
		for (ICarRentalCompany company : agency.getCompanies().values()) {
			if (company.getRegions().contains(region))
				types.addAll(company.getAvailableCarTypes(start, end));
		}
		
		return types;
	}
	
	public CarType getCheapestCarType(Date start, Date end, String region) throws RemoteException {
		return getAvailableCarTypesForRegion(start, end, region).stream().reduce((CarType type1, CarType type2) ->
														 				 		 (type1.getRentalPricePerDay() < type2.getRentalPricePerDay()) ?
														 				 		  type1 : type2).get();
	}
	
}
