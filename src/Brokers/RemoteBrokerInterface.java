package Brokers;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.swing.JTextArea;

import HouseholdMeter.RemoteMeterBrokerInterface;
import HouseholdMeter.RemoteMeterInterface;
import PowerCompany.BillNotPaidException;
import PowerCompany.RemoteCompanyInterface;

public interface RemoteBrokerInterface extends Remote
{
	public RemoteCompanyInterface findDeal(RemoteMeterBrokerInterface rmi) throws RemoteException, NoCompaniesException, NotBoundException;
	public void registerMeter(RemoteMeterBrokerInterface meter, RemoteCompanyInterface company) 
			throws RemoteException, BillNotPaidException;
}

