package HouseholdMeter;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Brokers.NoCompaniesException;
import Brokers.RemoteBrokerInterface;
import PowerCompany.BillNotPaidException;
import PowerCompany.RemoteCompanyInterface;

public class MeterHomePage extends JFrame
{
	//------------------Important stuff
	private Meter meter;
	private RemoteCompanyInterface tempCompany;
	private RemoteBrokerInterface tempBroker;
	private String broker;
	private RemoteCompanyInterface tempSuggestedCompany;
	
	//GUI stuff
	private JTextArea readings;
	private JButton unregister;
	private JList<String> availableCompanies;
	private DefaultListModel<String> companiesModel;
	private JTextArea companyInfo;
	private JButton refreshCompanies;
	private JButton register;
	//broker GUI elements
	private JList<String> availableBrokers;
	private DefaultListModel<String> brokersModel;
	private JButton refreshBrokers;
	private JButton findDeal;
	private JButton acceptOffer;
	private JTextArea suggestedCompanyInfo;
	//billing
	private JTextField billField;
	private JButton payBillButton;
	private JButton tamperMeter;
	//labels
	private JTextField currentCompany;
	private JTextField currentTariff;
	private JTextField status;
	
	public MeterHomePage(String id)
	{	
		status = new JTextField(8);
		status.setText("Unregistered");
		status.setForeground(Color.RED);
		status.setEditable(false);
		
		billField = new JTextField(8);
		billField.setText("0");
		billField.setEditable(false);
		
		readings = new JTextArea(7, 10);
		readings.setEditable(false);
		//initializing meter for that household
		try 
		{
			meter = new Meter(id, this.status, this.billField, this.readings);
		} 
		catch (RemoteException e)
		{
			JOptionPane.showMessageDialog(this, "Could not register meter - restart", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		//GUI stuff
		unregister = new JButton("Unregister");
		companiesModel = new DefaultListModel<String>();
		availableCompanies = new JList<String>(companiesModel);
		companyInfo = new JTextArea(5, 8);
		refreshCompanies = new JButton("Refresh list");
		register = new JButton("Register");
		//broker GUI stuff
		brokersModel = new DefaultListModel<String>();
		availableBrokers = new JList<String>(brokersModel);
		refreshBrokers = new JButton("Refresh list");
		findDeal = new JButton("Find");
		acceptOffer = new JButton("Accept");
		suggestedCompanyInfo = new JTextArea(5, 8);
		//set labels
		currentCompany = new JTextField(8);
		currentCompany.setText("No Company");
		currentCompany.setEditable(false);
		currentCompany.setForeground(Color.RED);
		currentTariff = new JTextField(8);
		currentTariff.setText("0");
		currentTariff.setForeground(Color.RED);
		currentTariff.setEditable(false);
	
		payBillButton = new JButton("Pay bill");
		tamperMeter = new JButton("Edit meter");
		
		this.setTitle("Your Household Home Page");
		//this.setSize(200, 200);
	}
	
	//all GUI stuff + attaching listeners
	public void init()
	{
		JPanel mainPanel = new JPanel();
		this.setContentPane(mainPanel);
		mainPanel.setLayout(new GridLayout(3, 1));
		
		//Internal meter information panel
		JPanel meterPanel = new JPanel();
		meterPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel companyLabel = new JLabel("Registered with: ");
		JLabel tariffLabel = new JLabel("Current tariff: ");
		JLabel readingsLabel = new JLabel("Readings: ");
		JLabel statusLabel = new JLabel("Status: ");
		
		c.insets = new Insets(0, 0, 10, 10);
		meterPanel.add(companyLabel, c);
		c.gridx = 1;
		meterPanel.add(currentCompany, c);
		c.gridx = 0;
		c.gridy = 1;
		meterPanel.add(tariffLabel, c);
		c.gridx = 1;
		meterPanel.add(currentTariff, c);
		c.gridy = 2;
		c.gridx = 0;
		meterPanel.add(unregister, c);
		unregister.setEnabled(false);
		c.gridy = 3;
		c.anchor = GridBagConstraints.PAGE_START; 
		meterPanel.add(statusLabel, c);
		c.gridx = 1;
		meterPanel.add(status, c);
		c.gridy = 4;
		c.gridx = 0;
		meterPanel.add(new JLabel("Meter id:"), c);
		c.gridx = 1;
		try 
		{
			meterPanel.add(new JLabel(String.valueOf(this.meter.getID())), c);
		} 
		catch (RemoteException e) 
		{	}
		c.gridwidth = 3;
		c.gridy = 0;
		c.gridx = 2;
		meterPanel.add(readingsLabel, c);
		c.gridheight = 4;
		c.gridy = 1;
		meterPanel.add(new JScrollPane(readings), c);
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridy = 0;
		c.gridx = 5;
		meterPanel.add(new JLabel("Bills:"), c);
		c.gridy = 1;
		meterPanel.add(billField, c);
		c.gridy = 2;
		meterPanel.add(payBillButton, c);
		this.payBillButton.setEnabled(false);
		c.gridy = 3;
		meterPanel.add(tamperMeter, c);
		
		//Panel for company for meter
		JPanel companyPanel = new JPanel();
		companyPanel.setLayout(new GridBagLayout());
		GridBagConstraints c1 = new GridBagConstraints();
		
		c1.insets = new Insets(0, 0, 10, 10);
		c1.gridx = 1;
		companyPanel.add(new JLabel("Companies:"), c1);
		c1.gridx = 2;
		companyPanel.add(new JLabel("Info:"), c1);
		c1.gridx = 0;
		c1.gridy = 1;
		companyPanel.add(refreshCompanies, c1);
		c1.gridheight = 3;
		c1.gridy = 1;
		c1.gridx = 1;
		companyPanel.add(new JScrollPane(availableCompanies), c1);
		c1.gridheight = 2;
		c1.gridx = 2;
		companyPanel.add(new JScrollPane(companyInfo), c1);
		c1.gridheight = 1;
		c1.gridx = 3;
		c1.gridy = 1;
		companyPanel.add(register, c1);
		register.setEnabled(false);
		
		//Panel for brokers
		JPanel brokerPanel = new JPanel();
		brokerPanel.setLayout(new GridBagLayout());
		GridBagConstraints c2 = new GridBagConstraints();
		c2.insets = new Insets(0, 0, 10, 10);
		c2.gridy = 1;
		brokerPanel.add(refreshBrokers, c2);
		c2.gridy = 0;
		c2.gridx = 1;
		brokerPanel.add(new JLabel("Brokers"), c2);
		c2.gridy = 1;
		c2.gridheight = 4;
		brokerPanel.add(new JScrollPane(availableBrokers), c2);
		c2.gridheight = 1;
		c2.gridx = 2;
		brokerPanel.add(findDeal, c2);
		c2.gridy = 2;
		brokerPanel.add(acceptOffer, c2);
		c2.gridy = 0;
		c2.gridx = 3;
		brokerPanel.add(new JLabel("Suggestion"), c2);
		c2.gridheight = 2;
		c2.gridy = 1;
		brokerPanel.add(new JScrollPane(suggestedCompanyInfo), c2);
		suggestedCompanyInfo.setEditable(false);
		findDeal.setEnabled(false);
		acceptOffer.setEnabled(false);
		
		refreshCompanies.addActionListener(new RefreshListener(this));
		availableCompanies.addListSelectionListener(new SelectItemListener(this));
		register.addActionListener(new RegisterListener(this));
		unregister.addActionListener(new UnregisterListener(this));
		payBillButton.addActionListener(new PayBillListener(this));
		tamperMeter.addActionListener(new AlertListener(this));
		refreshBrokers.addActionListener(new RefreshBrokersListener(this));
		availableBrokers.addListSelectionListener(new SelectBrokerListener());
		findDeal.addActionListener(new FindDealListener(this, this.meter));
		acceptOffer.addActionListener(new AcceptOfferListener(this));
		
		mainPanel.add(meterPanel);
		mainPanel.add(companyPanel);
		mainPanel.add(brokerPanel);
		
		this.pack();
		this.setVisible(true);
 	}
	
	//retrieving the names of the currently registered company
	class RefreshListener implements ActionListener
	{
		private JFrame parent;
		
		public RefreshListener(JFrame p)
		{
			this.parent = p;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{	
			try 
			{
				//put the company names in the JList
				writeCompanies(meter.searchCompanies());
			} 
			catch (RemoteException | NotBoundException e1) 
			{
				JOptionPane.showMessageDialog(parent, "Could not retrieve list with companies", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}	
	}
	
	//write the company names represented as string in the JList
	private void writeCompanies(List<String> list)
	{
		this.companiesModel.clear();
		
		for (int i = 0; i < list.size(); i++)
		{
			//add all elements from list to the model
			this.companiesModel.addElement(list.get(i));
		}
	}
	
	//selecting a company and retrieving the remote object for it
	class SelectItemListener implements ListSelectionListener
	{
		private JFrame parent;
		
		public SelectItemListener(JFrame p)
		{
			this.parent = p;
		}
		
		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting() == false) 
			{
				if (availableCompanies.getSelectedIndex() == -1)
				{
		            //No selection, disable fire button.
		            register.setEnabled(false);    
		            companyInfo.setText("");
				} 
				else 
				{
					//Selection of a company
					//clear info about previous company 
					companyInfo.setText("");
					String selectedName = availableCompanies.getSelectedValue();
					//show info about new company
					try
					{
						tempCompany = meter.findCompany(selectedName);
						companyInfo.setText(tempCompany.formatCompanyData());
						if (!meter.isRegisteredWith(tempCompany.getName()))
						{
							register.setEnabled(true);
						}
					}
					catch (NotBoundException nbe)
					{
						JOptionPane.showMessageDialog(parent, "No company with that name", "Error", JOptionPane.ERROR_MESSAGE);
					}
					catch (RemoteException re)
					{
						JOptionPane.showMessageDialog(parent, "Cannot search for company", "Error", JOptionPane.ERROR_MESSAGE);
					}
		        }
		    }
		}
	}
	
	class RegisterListener implements ActionListener
	{
		private JFrame parent;
		
		public RegisterListener(JFrame p)
		{
			this.parent = p;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				meter.register(tempCompany);
				
				unregister.setEnabled(true);
				register.setEnabled(false);
				currentCompany.setText(tempCompany.getName());
				currentCompany.setForeground(Color.GREEN);
				currentTariff.setText(String.valueOf(meter.getTariff()));
				currentTariff.setForeground(Color.GREEN);
				status.setText("Registered");
				status.setForeground(Color.GREEN);
				payBillButton.setEnabled(true);
			}
			catch(RemoteException re)
			{
				JOptionPane.showMessageDialog(parent, "Could not register meter - try again", "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch(AlreadyRegisteredException are)
			{
				JOptionPane.showMessageDialog(parent, are.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	class UnregisterListener implements ActionListener
	{
		private JFrame parent;
		
		public UnregisterListener(JFrame p)
		{
			this.parent = p;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				meter.unregister();
				
				unregister.setEnabled(false);
				register.setEnabled(true);
				currentCompany.setText("Unregistered");
				currentCompany.setForeground(Color.RED);
				currentTariff.setText("0");
				currentTariff.setForeground(Color.RED);
				status.setText("Unregistered");
				status.setForeground(Color.RED);
				payBillButton.setEnabled(false);
			}
			catch(RemoteException re)
			{
				JOptionPane.showMessageDialog(parent, "Could not unregister meter - try again\n"
						+ "you are still getting electricity from the company", "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch(BillNotPaidException bne)
			{
				JOptionPane.showMessageDialog(parent, "You have not paid your bills to the company", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	class PayBillListener implements ActionListener
	{
		private JFrame parent;
		
		public PayBillListener(JFrame p)
		{
			this.parent = p;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				boolean paid = meter.payBill();
				
				if (paid)
				{
					status.setText("Switched ON");
					status.setForeground(Color.GREEN);
					billField.setText("0");
				}
			}
			catch(RemoteException re)
			{
				JOptionPane.showMessageDialog(parent, "Could not pay bill - try again", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	class AlertListener implements ActionListener
	{
		private JFrame parent;
		
		public AlertListener(JFrame p)
		{
			this.parent = p;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				if (meter.isRegistered())
				{
					meter.sendAlert();
					JOptionPane.showMessageDialog(parent, "Company has been notified of your behavior\nDon't do that", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			catch(RemoteException re)
			{
				//impossible to get as method call is local
			}
		}
	}
	
	//-----------broker listeners
	class RefreshBrokersListener implements ActionListener
	{
		private JFrame parent;
		
		public RefreshBrokersListener(JFrame p)
		{
			this.parent = p;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{	
			try 
			{
				//put the company names in the JList
				writeBrokers(meter.searchBrokers());
			} 
			catch (RemoteException | NotBoundException e1) 
			{
				JOptionPane.showMessageDialog(parent, "Could not retrieve list with companies", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}	
	}
	
	//write the company names represented as string in the JList
	private void writeBrokers(List<String> list)
	{
		this.brokersModel.clear();
		
		for (int i = 0; i < list.size(); i++)
		{
			//add all elements from list to the model
			this.brokersModel.addElement(list.get(i));
		}
	}
	
	class SelectBrokerListener implements ListSelectionListener
	{	
		public SelectBrokerListener()
		{
			
		}
		
		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting() == false) 
			{
				if (availableBrokers.getSelectedIndex() == -1)
				{
		            //No selection, disable fire button.
		            findDeal.setEnabled(false);    
		            suggestedCompanyInfo.setText("");
		            broker = null;
		            tempSuggestedCompany = null;
				} 
				else 
				{
					//Selection of a broker
					//clear info about previous company 
					suggestedCompanyInfo.setText("");
					broker = availableBrokers.getSelectedValue();
					//show info about new company
					findDeal.setEnabled(true);
		        }
		    }
		}
	}
	
	class FindDealListener implements ActionListener
	{
		private JFrame parent;
		private Meter meter;
		
		public FindDealListener(JFrame p, Meter m)
		{
			this.parent = p;
			this.meter = m;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				tempBroker = (RemoteBrokerInterface) meter.findBroker(broker);
				tempSuggestedCompany = tempBroker.findDeal(this.meter);
				acceptOffer.setEnabled(true);
				suggestedCompanyInfo.setText(tempSuggestedCompany.formatCompanyData());
			}
			catch(RemoteException re)
			{
				JOptionPane.showMessageDialog(parent, "Could not connect to broker", "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch (NoCompaniesException nce)
			{
				JOptionPane.showMessageDialog(parent, nce.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch (NotBoundException nbe) 
			{
				JOptionPane.showMessageDialog(parent, "That broker does not exist", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	class AcceptOfferListener implements ActionListener
	{
		private JFrame parent;
		
		public AcceptOfferListener(JFrame p)
		{
			this.parent = p;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			boolean sameCompany = true;
			try
			{
				sameCompany = meter.isRegisteredWith(tempSuggestedCompany.getName());
			}
			catch (RemoteException re)
			{
				
			}
			
			if (sameCompany)
			{
				JOptionPane.showMessageDialog(parent, "Already registered with that company", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				try 
				{
					tempBroker.registerMeter(meter, tempSuggestedCompany);
					unregister.setEnabled(true);
					register.setEnabled(false);
					currentCompany.setText(tempSuggestedCompany.getName());
					currentCompany.setForeground(Color.GREEN);
					currentTariff.setText(String.valueOf(meter.getTariff()));
					currentTariff.setForeground(Color.GREEN);
					status.setText("Registered");
					status.setForeground(Color.GREEN);
					payBillButton.setEnabled(true);
				} 
				catch (RemoteException e1) 
				{
					JOptionPane.showMessageDialog(parent, "Could not connect to broker", "Error", JOptionPane.ERROR_MESSAGE);
				}
				catch (BillNotPaidException e1)
				{
					JOptionPane.showMessageDialog(parent, "Broker cannot register you\nas you have not paid your "
							+ "bills", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
