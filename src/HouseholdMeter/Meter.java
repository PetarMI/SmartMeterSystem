package HouseholdMeter;
import java.awt.Color;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import Brokers.NoCompaniesException;
import Brokers.RemoteBrokerInterface;
import CentralServer.RemoteCollectionInterface;
import PowerCompany.BillNotPaidException;
import PowerCompany.RemoteCompanyInterface;

public class Meter extends UnicastRemoteObject implements RemoteMeterInterface, RemoteMeterBrokerInterface
{
	private RemoteCompanyInterface company;
	private double tariff;
	Registry reg;
	private String id;
	private boolean switchedOff;
	
	//used for readings
	private Timer timer;
	private List<Integer> readings;
	private List<Integer> historyPerMonth;
	private MakeReadings currentTask;
	private int numReadings;
	
	//used for lost communications
	private Timer commsTimer;
	
	//GUI updates
	private JTextField status;
	private JTextField lastBill;
	private JTextArea readingsArea;
	
	public Meter(String idNum, JTextField st, JTextField lb, JTextArea rd) throws RemoteException
	{
		this.company = null;
		this.tariff = 0;
		this.id = idNum;
		this.switchedOff = false;
		//GUI elements 
		this.status = st;
		this.lastBill = lb;
		this.readingsArea = rd;
		
		//readings
		this.timer = new Timer(true);
		this.readings = new ArrayList<Integer>(10);
		this.historyPerMonth = new ArrayList<Integer>();
		this.numReadings = 0;
		
		//lost connections
		commsTimer = new Timer(true);
		
		reg = LocateRegistry.getRegistry(1099);
		//write correct ip for running on different machines and comment previous line
		//reg = LocateRegistry.getRegistry("192.168.0.7", 1099);
	}
	
	public void register(RemoteCompanyInterface comp) throws RemoteException, AlreadyRegisteredException
	{
		if (this.company != null)
		{
			throw new AlreadyRegisteredException("Already registered with company");
		}
		this.company = comp;
		this.company.registerMeter(this.id);
		this.tariff = this.company.getTariff();
		this.startReadings();
	}
	
	//assume customer pays everything upon unregister
	public void unregister() throws RemoteException, BillNotPaidException
	{
		this.company.unregisterMeter(this.id);
		this.stopReading();
		this.numReadings = 0;
		this.company = null;
		this.tariff = 0;
	}
	
	private void startReadings()
	{
		currentTask = new MakeReadings();
		this.timer.schedule(currentTask, new Date(), 2000);
	}
	
	private void stopReading()
	{
		this.currentTask.cancel();
	}
	
	public synchronized void sendReadings()
	{
		int total = 0;
		for (int reading : readings) 
		{
			total += reading;
		}
		
		//if there is no connection and meter is unable to send the readings 
		//it will do the operation again next time the readings should be send and the company will
		//receive the total amount for all of the times
		try 
		{
			this.company.recieveReadings(this, total);	
			historyPerMonth.add(total);
			readings.clear();
		} 
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}
	
	public void recieveBill(double bill)
	{
		this.lastBill.setText(String.valueOf(bill));
	}
	
	public boolean payBill() throws RemoteException
	{
		boolean success = this.company.getMoney(this.id);
		if (success == true && this.switchedOff == true)
		{
			this.startReadings();
			this.switchedOff = false;
		}
		
		return success;
	}
	
	public void switchOff()
	{
		this.stopReading();
		this.switchedOff = true;
		this.status.setText("Switched Off");
		this.status.setForeground(Color.RED);
	}
	
	public void sendAlert()
	{
		try
		{
			this.company.getAlert(this.id);
		}
		catch(RemoteException re)
		{
			commsTimer.schedule(new TimerTask() {
				public void run() {
					sendAlert();
				}
			}, 1500);
			re.printStackTrace();
		}
	}
	
	public List<String> searchCompanies() throws RemoteException, NotBoundException
	{
		RemoteCollectionInterface rc = (RemoteCollectionInterface) reg.lookup("Companies");
		
		return rc.getCompanies();
	}
	
	public List<String> searchBrokers() throws RemoteException, NotBoundException
	{
		RemoteCollectionInterface rc = (RemoteCollectionInterface) reg.lookup("Brokers");
		
		return rc.getCompanies();
	}
	
	public RemoteCompanyInterface findCompany(String name) throws RemoteException, NotBoundException
	{
		return (RemoteCompanyInterface) reg.lookup(name);
	}
	
	public RemoteBrokerInterface findBroker(String name) throws RemoteException, NotBoundException
	{
		return (RemoteBrokerInterface) reg.lookup(name);
	}
	
	public RemoteCompanyInterface findDeal (RemoteBrokerInterface broker) throws RemoteException, NoCompaniesException, NotBoundException
	{
		return broker.findDeal(this);
	}
	
	public List<Integer> sendHistory() throws RemoteException
	{
		return this.historyPerMonth;
	}
	
	public boolean isRegistered() throws RemoteException
	{
		return (this.company != null);
	}
	
	public boolean isRegisteredWith(String name) throws RemoteException
	{
		if (this.company != null && this.company.getName().equals(name))
		{
			return true;
		}
		return false;
	}
	
	public String getID() throws RemoteException
	{
		return this.id;
	}
	
	public double getTariff()
	{
		return this.tariff;
	}
	
	class MakeReadings extends TimerTask
	{
		private Random rand;
		
		public MakeReadings()
		{
			this.rand = new Random();
		}
		
		@Override
		public void run()
		{
			int read = rand.nextInt(20);
			readings.add(read);
			readingsArea.append(String.format("%d\n", read));
			numReadings++;
			if (numReadings % 10 == 0)
			{
				sendReadings();
			}
		}
	}
}
