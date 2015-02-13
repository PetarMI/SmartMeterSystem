package Brokers;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import CentralServer.CompanyNameExistsException;

public class RegisterBrokerPage extends JFrame 
{
	private Broker broker;
	private JTextField nameField;
	private JButton register;
	
	public RegisterBrokerPage()
	{
		this.setTitle("Start your broker");
		
		this.broker = null;
		nameField = new JTextField(15);
		register = new JButton("Set-up broker");
	}
	
	public void init()
	{
		JPanel panel = new JPanel();
		this.setContentPane(panel);
		panel.setLayout(new GridLayout(2, 2));
		
		panel.add(new JLabel("Broker name: "));
		panel.add(nameField);
		panel.add(register);
		
		register.addActionListener(new RegisterListener(this));
		
		this.pack();
		this.setVisible(true);
	}
	
	class RegisterListener implements ActionListener
	{
		JFrame parent;
		
		public RegisterListener (JFrame p)
		{
			parent = p;
		}
		@Override
		public void actionPerformed(ActionEvent e)
		{
			//TODO 
			//check fields are not empty
			String name = nameField.getText();
			
			try
			{
				broker = new Broker(name);
				broker.registerBroker();
				new BrokerPage(broker).init();
				parent.dispose();
			}
			catch(CompanyNameExistsException cnee)
			{
				broker = null;
				JOptionPane.showMessageDialog(parent, "Broker with that name exists", "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch(NotBoundException nbe)
			{
				broker = null;
				JOptionPane.showMessageDialog(parent, "Could not find list with broker names", "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch(RemoteException re)
			{
				broker = null;
				JOptionPane.showMessageDialog(parent, "Could not connect to central server", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
