package PowerCompany;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import HouseholdMeter.RemoteMeterInterface;

public class PowerCompanyPage extends JFrame
{
	//---------Important------------
	private PowerCompany company;
	
	//GUI stuff
	private JList<String> registeredMeters;
	private DefaultListModel<String> metersModel;
	private JTextArea meterAlerts;
	
	public PowerCompanyPage(PowerCompany pc)
	{
		this.company = pc;
		
		//GUI initialization
		this.metersModel = new DefaultListModel<String>();
		this.registeredMeters = new JList<String>(metersModel);
		this.meterAlerts = new JTextArea(10, 10);
		this.meterAlerts.setEditable(false);
		this.company.setAlertArea(meterAlerts);
		
		this.company.setModel(metersModel);
		
		this.setTitle("Power Company Home Page");
	}
	
	public void init()
	{
		JPanel panel = new JPanel();
		this.setContentPane(panel);
		panel.setLayout(new GridBagLayout());
		
		JScrollPane scrMeters = new JScrollPane(registeredMeters);
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 10, 10);
		panel.add(new JLabel("Registered Meters:"), c);
		c.gridy = 1;
		c.gridheight = 4;
		panel.add(scrMeters, c);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridy = 0;
		panel.add(new JLabel("Alerts"), c);
		c.gridx = 1;
		c.gridy = 2;
		panel.add(new JScrollPane(meterAlerts), c);
		
		this.pack();
		this.setVisible(true);
	}
}
