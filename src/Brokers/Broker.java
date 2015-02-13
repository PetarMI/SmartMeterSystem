package Brokers;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextArea;

import CentralServer.CompanyNameExistsException;
import CentralServer.RemoteCollectionInterface;
import HouseholdMeter.AlreadyRegisteredException;
import HouseholdMeter.RemoteMeterBrokerInterface;
import HouseholdMeter.RemoteMeterInterface;
import PowerCompany.BillNotPaidException;
import PowerCompany.RemoteCompanyInterface;

public class Broker extends UnicastRemoteObject implements RemoteBrokerInterface
{
	private String name;
	private Registry reg;
	private Timer timer;
	
	public Broker (String brokerName) throws RemoteException
	{
		this.name = brokerName;
		reg = LocateRegistry.getRegistry(1099);
		//write correct ip for running on different machines and comment previous line
		//reg = LocateRegistry.getRegistry("192.168.0.7", 1099);
		this.timer = new Timer(true);
	}
	
	public void registerBroker() throws NotBoundException, CompanyNameExistsException, RemoteException
	{
		RemoteCollectionInterface brokers = (RemoteCollectionInterface) this.reg.lookup("Brokers");
		brokers.addEntity(this.name);
		reg.rebind(name, this);
	}

	@Override
	public RemoteCompanyInterface findDeal(RemoteMeterBrokerInterface meter) throws RemoteException, NotBoundException, NoCompaniesException
	{
		double minTariff = Integer.MAX_VALUE;
		RemoteCompanyInterface tempCompany = null;
		RemoteCompanyInterface bestCompany = null;
		
		//if there is no connection to the meter assume no matter what the readings are
		//the cheapest company should be returned
		try
		{
			List<Integer> meterHistory = meter.sendHistory();
		}
		catch(RemoteException re)
		{
			//continue with the execution and find some deal
		}
		RemoteCollectionInterface companiesCollection = (RemoteCollectionInterface) reg.lookup("Companies");
		List<String> companies = companiesCollection.getCompanies();
		
		for (String company : companies) 
		{
			try
			{
				tempCompany = (RemoteCompanyInterface) reg.lookup(company);
				double companyTariff = tempCompany.getTariff();
				if (companyTariff < minTariff)
				{
					minTariff = companyTariff;
					bestCompany = tempCompany;
				}
			}
			catch(RemoteException | NotBoundException e)
			{
				continue;
			}
		}
		
		if (bestCompany == null)
		{
			throw new NoCompaniesException("No available companies");
		}
		
		return bestCompany;
	}

	@Override
	public void registerMeter(RemoteMeterBrokerInterface meter, RemoteCompanyInterface company) 
			throws RemoteException, BillNotPaidException 
	{
		//if this fails user is notified of failure of operation
		if (meter.isRegistered())
		{
			meter.unregister();
		}
		try
		{
			meter.register(company);
		}
		catch (RemoteException re)
		{
			//because the operation should be atomic and the meter is already unregistered 
			//try to register it until the connection is restored
			timer.schedule(new HandleRegistrationFalure(meter, company), 5000);
		}
		catch (AlreadyRegisteredException e)
		{
			//impossible to get as meter is first unregistered
		}
	}
	
	class HandleRegistrationFalure extends TimerTask
	{
		private RemoteMeterBrokerInterface meter;
		private RemoteCompanyInterface company;
		
		public HandleRegistrationFalure(RemoteMeterBrokerInterface m, RemoteCompanyInterface c)
		{
			this.meter = m;
			this.company = c;
		}
		
		public void run()
		{
			try
			{
				this.meter.register(company);
			}
			catch (RemoteException re)
			{
				timer.schedule(this, 5000);
			}
			catch (AlreadyRegisteredException e)
			{
				//impossible to get as meter is first unregistered
			}
		}
	}
}
