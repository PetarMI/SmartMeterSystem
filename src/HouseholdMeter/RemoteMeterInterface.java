package HouseholdMeter;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Brokers.NoCompaniesException;
import Brokers.RemoteBrokerInterface;
import PowerCompany.RemoteCompanyInterface;

public interface RemoteMeterInterface extends Remote
{
	public String getID() throws RemoteException;
	public void switchOff() throws RemoteException;
	public void recieveBill(double bill) throws RemoteException;
	//public void sendReadings() throws RemoteException;
}
