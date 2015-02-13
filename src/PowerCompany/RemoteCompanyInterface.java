package PowerCompany;
import java.rmi.Remote;
import java.rmi.RemoteException;

import HouseholdMeter.RemoteMeterInterface;

public interface RemoteCompanyInterface extends Remote 
{
	public void registerMeter(String meterID) throws RemoteException;
	public void unregisterMeter(String meterID) throws RemoteException, BillNotPaidException;
	public void recieveReadings(RemoteMeterInterface meterID, int total) throws RemoteException;
	public boolean getMoney (String meterID) throws RemoteException;
	public void getAlert (String meterID) throws RemoteException;
	public String getName() throws RemoteException;
	public double getTariff() throws RemoteException;
	public String formatCompanyData() throws RemoteException;
}
