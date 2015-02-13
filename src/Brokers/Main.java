package Brokers;

public class Main
{
	public static void main(String[] args) 
	{	
		//write correct ip
		//System.setProperty("java.rmi.server.hostname", "192.168.0.7"); 
		RegisterBrokerPage rbp = new RegisterBrokerPage();
		rbp.init();
	}
}
