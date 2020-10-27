package rental;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import company.CarType;
import company.ICarRentalCompany;
import company.Reservation;
import company.ReservationConstraints;
import company.ReservationException;

public interface IRentalAgency extends java.rmi.Remote {

	String getName() throws RemoteException;

	void addCompany(String crcName) throws RemoteException, NotBoundException;

	void removeCompany(ICarRentalCompany company) throws RemoteException;

	ICarRentalCompany getCompany(String name) throws RemoteException;

	Set<ICarRentalCompany> getCompanies() throws RemoteException;

	ReservationSession getNewReservationSession(String owner) throws RemoteException;

	ManagerSession getNewManagerSession(String owner) throws RemoteException;
	
	public Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
	
	public Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException, java.rmi.RemoteException;

	public List<Reservation> confirmQuotes(Set<Quote> quotes) throws RemoteException, ReservationException;
	
	public Set<CarType> getAvailableCarTypesForRegion(Date start, Date end, String region) throws RemoteException;
	
}