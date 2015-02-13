package HouseholdMeter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class HouseholdActivatePage extends JFrame
{
	private JTextField idField;
	private JButton activate;
	
	public HouseholdActivatePage()
	{
		this.idField = new JTextField(10);
		this.activate = new JButton("Activate");
		
		this.setTitle("Meter Activation");
		this.setSize(300, 300);
	}
	
	public void init()
	{
		JPanel panel = new JPanel();
		this.setContentPane(panel);
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		panel.add(new JLabel("Please input your meter id\n(make it up)"), c);
		c.gridy = 1;
		panel.add(this.idField, c);
		c.gridy = 2;
		panel.add(this.activate, c);
		
		activate.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				new MeterHomePage(idField.getText()).init();
				dispose();
			}
		});
		
		this.setVisible(true);
	}
}
