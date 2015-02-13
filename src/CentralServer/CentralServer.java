package CentralServer;
import java.awt.Color;
import java.awt.GridLayout;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class CentralServer
{
	public static void main(String[] args)
	{	
		//create initial lists for companies and brokers
		List<String> cmps = new ArrayList<String>();
		List<String> brks = new ArrayList<String>();
		Registry reg = null;
		
		CentralServer.ServerGUI servGUI = new CentralServer.ServerGUI();
		servGUI.init();
		//write correct ip
		//System.setProperty("java.rmi.server.hostname", "192.168.0.7"); 

		try
		{
			reg = LocateRegistry.createRegistry(1099);
		}
		catch(RemoteException e)
		{
			e.printStackTrace();
			servGUI.showPortError();
		}
		
		try
		{
			//create the remote objects that Meters and Brokers will use to see what companies are available
			RemoteCollection companies = new RemoteCollection(cmps);
			RemoteCollection brokers = new RemoteCollection(brks);
			
			reg.rebind("Companies", companies);
			reg.rebind("Brokers", brokers);
			servGUI.setSuccessful();
		}
		catch (RemoteException e)
		{
			/*e.printStackTrace();
			System.err.println("Server could not create list to store the entities");*/
			servGUI.setError();
		}
	}
	
	static class ServerGUI extends JFrame 
	{
		JLabel companiesLoaded;
		JLabel brokersLoaded;
		
		public ServerGUI()
		{
			this.setSize(300, 75);
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
		
		public void init()
		{
			JPanel panel = new JPanel();
			this.setContentPane(panel);
			panel.setLayout(new GridLayout(2, 2));
			
			JLabel pwrCompanies = new JLabel("Power companies");
			JLabel brkrs = new JLabel("Brokers");
			companiesLoaded = new JLabel("Loading...");
			brokersLoaded = new JLabel("Loading...");
			
			panel.add(pwrCompanies);
			panel.add(brkrs);
			panel.add(companiesLoaded);
			panel.add(brokersLoaded);
			
			//this.pack();
			this.setVisible(true);
		}
		
		public void setSuccessful()
		{
			this.companiesLoaded.setForeground(Color.green);
			this.brokersLoaded.setForeground(Color.GREEN);
			this.companiesLoaded.setText("Companies Loaded");
			this.brokersLoaded.setText("Brokers loaded");
		}
		
		public void setError()
		{
			this.companiesLoaded.setForeground(Color.RED);
			this.brokersLoaded.setForeground(Color.RED);
			this.companiesLoaded.setText("Error loading companies");
			this.brokersLoaded.setText("Error loading brokers");
		}
		
		public void showPortError()
		{
			JOptionPane.showMessageDialog(this, "Could not create registry on that port", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
