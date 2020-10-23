package agency;

import java.rmi.RemoteException;
import java.util.Set;

import rental.ICarRentalCompany;

public interface IRentalAgency extends java.rmi.Remote {

	String getName() throws RemoteException;

	void addCompany(ICarRentalCompany company) throws RemoteException;

	void removeCompany(ICarRentalCompany company) throws RemoteException;

	ICarRentalCompany getCompany(String name) throws RemoteException;

	Set<ICarRentalCompany> getCompanies() throws RemoteException;

	ReservationSession getNewReservationSession(String owner) throws RemoteException;

	ManagerSession getNewManagerSession(String owner) throws RemoteException;

}