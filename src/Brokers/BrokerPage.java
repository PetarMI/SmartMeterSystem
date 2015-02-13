package Brokers;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BrokerPage extends JFrame
{
	private Broker broker;
	
	public BrokerPage(Broker br)
	{
		this.setSize(200, 100);
		this.broker = br;
	}
	
	public void init()
	{
		JPanel panel = new JPanel();
		this.setContentPane(panel);
		
		panel.add(new JLabel("So you know a broker is working"));
		
		this.setVisible(true);
	}
}
