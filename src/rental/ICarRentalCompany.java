package rental;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import agency.Quote;

public interface ICarRentalCompany extends java.rmi.Remote {

	/********
	 * NAME *
	 ********/

	String getName() throws java.rmi.RemoteException;

	List<String> getRegions() throws java.rmi.RemoteException;

	boolean operatesInRegion(String region) throws java.rmi.RemoteException;

	/*************
	 * CAR TYPES *
	 *************/

	Collection<CarType> getAllCarTypes() throws java.rmi.RemoteException;

	CarType getCarType(String carTypeName) throws java.rmi.RemoteException;

	// mark
	boolean isAvailable(String carTypeName, Date start, Date end) throws java.rmi.RemoteException;

	Set<CarType> getAvailableCarTypes(Date start, Date end) throws java.rmi.RemoteException;

	/****************
	 * RESERVATIONS *
	 ****************/

	Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException, java.rmi.RemoteException;

	Reservation confirmQuote(Quote quote) throws ReservationException, java.rmi.RemoteException;

	void cancelReservation(Reservation res) throws java.rmi.RemoteException;

	List<Reservation> getReservationByRenter(String clientName) throws java.rmi.RemoteException;

	int getNumberOfReservationsForCarType(String carType) throws java.rmi.RemoteException;
}