package PowerCompany;
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

public class RegisterCompanyPage extends JFrame
{
	private PowerCompany company;
	private JTextField companyName;
	private JTextField tariffField;
	private JButton register;
	
	public RegisterCompanyPage()
	{
		this.company = null;
		
		this.setTitle("Start your company");
		companyName = new JTextField(15);
		tariffField = new JTextField(15);
		
		register = new JButton("Start company");
		
		this.setSize(400, 100);
	}
	
	public void init()
	{
		JPanel panel = new JPanel();
		this.setContentPane(panel);
		panel.setLayout(new GridLayout(3, 2));
		
		JLabel nameLabel = new JLabel("Enter a name for the company:");
		JLabel tariffLabel = new JLabel("Enter a tariff for the company:");
		panel.add(nameLabel);
		panel.add(companyName);
		panel.add(tariffLabel);
		panel.add(tariffField);
		panel.add(register);
		
		register.addActionListener(new RegisterListener(this));
		
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
			String name = companyName.getText();
			double tariff = Integer.parseInt(tariffField.getText());
			
			try
			{
				company = new PowerCompany(name, tariff);
				company.registerCompany();
				new PowerCompanyPage(company).init();
				parent.dispose();
			}
			catch(CompanyNameExistsException cnee)
			{
				company = null;
				JOptionPane.showMessageDialog(parent, "Company with that name exists", "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch(NotBoundException nbe)
			{
				company = null;
				JOptionPane.showMessageDialog(parent, "Could not find list with company names", "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch(RemoteException re)
			{
				re.printStackTrace();
				company = null;
				JOptionPane.showMessageDialog(parent, "Could not connect to central server", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
