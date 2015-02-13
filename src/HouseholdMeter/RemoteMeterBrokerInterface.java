package HouseholdMeter;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JTextArea;

import PowerCompany.BillNotPaidException;
import PowerCompany.RemoteCompanyInterface;

public interface RemoteMeterBrokerInterface extends Remote
{
	public void register(RemoteCompanyInterface comp) throws RemoteException, AlreadyRegisteredException;
	public void unregister() throws RemoteException, BillNotPaidException;
	public List<Integer> sendHistory() throws RemoteException;
	public boolean isRegistered() throws RemoteException;
}
