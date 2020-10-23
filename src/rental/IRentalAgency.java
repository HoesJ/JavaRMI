package rental;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Set;

import company.ICarRentalCompany;

public interface IRentalAgency extends java.rmi.Remote {

	String getName() throws RemoteException;

	void addCompany(String crcName) throws RemoteException, NotBoundException;

	void removeCompany(ICarRentalCompany company) throws RemoteException;

	ICarRentalCompany getCompany(String name) throws RemoteException;

	Set<ICarRentalCompany> getCompanies() throws RemoteException;

	ReservationSession getNewReservationSession(String owner) throws RemoteException;

	ManagerSession getNewManagerSession(String owner) throws RemoteException;

}