package PowerCompany;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

import HouseholdMeter.RemoteMeterInterface;
import CentralServer.RemoteCollectionInterface;
import CentralServer.CompanyNameExistsException;

public class PowerCompany extends UnicastRemoteObject implements RemoteCompanyInterface
{
	private String name;
	private double tariff;
	private Registry reg;
	private Object lock = new Object();
	
	private Map<String, Double> meters;
	private DefaultListModel<String> meterModel;
	private JTextArea alertArea;
	
	//billing
	private Timer timer;
	
	public PowerCompany(String companyName, double companyTariff) throws RemoteException
	{
		this.name = companyName;
		this.tariff = companyTariff;
		this.meters = new HashMap<String, Double>();
		timer = new Timer(true);
		reg = LocateRegistry.getRegistry(1099);
		//write correct ip for running on different machines and comment previous line
		//reg = LocateRegistry.getRegistry("192.168.0.7", 1099);
	}
	
	//-------------remote methods
	public void registerMeter(String meterId) throws RemoteException
	{
		synchronized (lock) 
		{
			this.meters.put(meterId, 0.0);
			this.meterModel.addElement(meterId);
		}
	}
	
	public void unregisterMeter(String meterId) throws RemoteException, BillNotPaidException
	{
		synchronized (lock) 
		{
			if (this.meters.get(meterId) > 0)
			{
				throw new BillNotPaidException("Outstanding bills");
			}
			this.meters.remove(meterId);
		}
		this.meterModel.removeElement(meterId);
	}
	
	public void recieveReadings(RemoteMeterInterface meter, int total) throws RemoteException 
	{
		String meterName = meter.getID();
		double bill = tariff * total + this.meters.get(meterName);
		synchronized (lock)
		{
			this.meters.put(meterName, bill);
		}
		try
		{
			meter.recieveBill(bill);
		}
		catch (RemoteException e) 
		{
			e.printStackTrace();
		}
		timer.schedule(new BillCustomer(meter, meterName), 15000);
	}
	
	public boolean getMoney(String meter) throws RemoteException
	{
		synchronized (lock) 
		{
			if (this.meters.get(meter) == 0)
			{
				return false;
			}
			else
			{
				this.meters.put(meter, 0.0);
				return true;
			}
		}
	}
	
	public void getAlert(String meter) throws RemoteException
	{
		this.alertArea.append("Meter " + meter + " tried to adjust meter\n");
	}
	
	public String getName() throws RemoteException
	{
		return this.name;
	}
	
	public double getTariff() throws RemoteException
	{
		return this.tariff;
	}
	
	public String formatCompanyData() throws RemoteException
	{
		return ("Name: " + this.name + "\n" + "Tariff: " + this.tariff);
	}
	
	
	//----------non-remote methods
	public void registerCompany() throws NotBoundException, CompanyNameExistsException, RemoteException
	{
		RemoteCollectionInterface companies = (RemoteCollectionInterface) this.reg.lookup("Companies");
		companies.addEntity(this.name);
		reg.rebind(this.name, this);
	}
	
	class BillCustomer extends TimerTask
	{
		private RemoteMeterInterface meter;
		private String meterID;
		
		public BillCustomer(RemoteMeterInterface customer, String id)
		{
			this.meter = customer;
			this.meterID = id;
		}
		
		public void run()
		{
			synchronized (lock) 
			{
				Double tax = meters.get(this.meterID);
				if (tax != null && tax > 0)
				{
					try 
					{
						this.meter.switchOff();
						//this.meter.sendReadings();
					} 
					catch (RemoteException e) 
					{
						timer.schedule(this, 8000);
					}
				}
			}
		}
	}
	
	public void setModel(DefaultListModel<String> m)
	{
		this.meterModel = m;
	}
	
	public void setAlertArea(JTextArea alerts)
	{
		this.alertArea = alerts;
	}
}
