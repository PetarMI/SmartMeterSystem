package CentralServer;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import PowerCompany.RemoteCompanyInterface;

public class RemoteCollection extends UnicastRemoteObject implements RemoteCollectionInterface
{
	private List<String> collection;
	
	public RemoteCollection (List<String> list) throws RemoteException
	{
		collection = list;
		//collection = Collections.synchronizedList(list);
	}
	
	public synchronized void addEntity(String entity) throws RemoteException, CompanyNameExistsException
	{
		if (this.companyExists(entity))
		{
			throw new CompanyNameExistsException("This company already exists");
		}
		collection.add(entity);
	}
	
	//not actually used as Companies and Brokers cannot be unregistered from the system
	public synchronized void removeEntity(String entity) throws RemoteException
	{
		collection.remove(entity);
	}
	
	public synchronized List<String> getCompanies()
	{
		return this.collection;
	}
	
	private synchronized boolean companyExists(String name)
	{
		return this.collection.contains(name);
	}
}
