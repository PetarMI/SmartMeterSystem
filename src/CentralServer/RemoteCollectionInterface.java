package CentralServer;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import PowerCompany.RemoteCompanyInterface;

public interface RemoteCollectionInterface extends Remote
{
	public void addEntity(String ent) throws RemoteException, CompanyNameExistsException;
	public void removeEntity(String ent) throws RemoteException;
	public List<String> getCompanies() throws RemoteException;
}
