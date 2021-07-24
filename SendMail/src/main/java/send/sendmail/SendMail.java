package send.sendmail;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author Vinoth N
 * Program to send to bulk emails
 */

public class SendMail {
	/*
	 * In this class we have two methods:
	 * i. main method - main task will be executed
	 * ii. mailHost - returns the host object of a Mail class, helps to choose the mail host
	 */
	
	public static void main(String[] args) throws Exception{//main method
		Mail mail;//Object mail of type Mail class
		mail= mailHost();//Assigns the object dynamically using mailHost() method
		mail.send();//invoke the send() method to send email from the host as per the mail object
		
	}
	
	public static Mail mailHost() throws Exception {
		/*
		 * Get class name at runtime and
		 * returns the object of type Mail
		 */
		System.out.print("Enter the host: ");
		String host=(new Scanner(System.in)).nextLine();//get the class name at run time
		Mail mail=(Mail)Class.forName("send.sendmail."+host).getDeclaredConstructor().newInstance();//Creates the object of Mail type
		return mail;
	}
	
}


abstract class Mail{
	/*
	 * It is an abstract contains 
	 * i. abstract method session() - to get the session of the user's chosen host
	 * ii. message() method - to insert subject, from address, and main content of the message and send it.
	 * iii. to() method - extracts to addresses from a text file and returns it as a property
	 * iv. send() method - used to manage the email. It will send organize to send email to each of the to addresses
	 */
	String from,password;
	abstract protected Session authenticate();//abstract method to choose the session for the host as per user wish
	private void message(String toperson, String toaddress) {
		/*
		 * Composition of message and regarding task is done in this method
		 */
		try {
			//MimeMessage object created with the help of host session
			MimeMessage message=new MimeMessage(authenticate());
			
			//Sender address is initialized using setFrom method
			message.setFrom(new InternetAddress(from));
			
			//Recipient address is set by setRecipient method
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toaddress));
			
			//Sets subject of the mail using setSubject method
			message.setSubject("Better way to Communicate");
			
			//Content of the mail composed with setText method
			message.setText("Dear "+toperson+"\n\nWelcome you to the world fo technology."
					+ " Do you have problem with send same mail "
					+ "to lots of people we have solution.\n\nYou can get a sample mail"
					+ "project form my GitHub.This is my "
					+ "URL: https://github.com/Vinoth170.\n\nThanks & Regards,\n"+from);
			
			//Mail will be send
			Transport.send(message);
			System.out.println("Message successfully sent to "+toaddress);
			
		}catch(MessagingException ex) {//When transport object fails to send mail catch block will be executed
//			ex.printStackTrace();//Helps to trace the error
			System.out.println("Could not send the mail");
		}
	}
	
	private Properties to(){
		/*
		 * Here recipients address is extracted from text file
		 */
		Properties addresses=new Properties();
		String key,value;
		File file=new File("src/main/java/send/mailaddress/ToAddress");//File object is created
		try{
			Scanner toaddress=new Scanner(file);
			while(toaddress.hasNextLine()){
					key=toaddress.nextLine();//Mail ID
					value=key.substring(0,key.lastIndexOf("@"));//Person
					addresses.put(key, value);
				}
			toaddress.close();
			}
		catch(IOException ex) {//IOException will caught when the input file doesn't exist
			ex.printStackTrace();
		}
		return addresses;//returns the addresses of recipients through properties object
	}
	
	public void send() {
		Properties addresses=to();
		
		Set set=addresses.entrySet();
		Iterator iter=set.iterator();
		
		//Send mail to each recipient with respect to the email addresses in the collection addresses
		while(iter.hasNext()) {
			Map.Entry<String,String> map=(Map.Entry<String, String>)iter.next();
			message(map.getValue(), map.getKey());
		}
	}
}


class Gmail extends Mail{
	/*
	 * Subclass of Mail class
	 * Implements authenticate() method - which returns the session for gmail
	 * Constructor assigns the from address and password for the session to get authenticate
	 */
	public Gmail() {//Gmail constructor to initialize the from and password
		from=System.getenv("USER_NAME");
		password=System.getenv("PASSWORD");
	}
	@Override
	protected Session authenticate() {//authenticate method to return session for gmail host
		String host="smtp.gmail.com";
		Properties prop=System.getProperties();
		prop.put("mail.smtp.host",host);
		prop.put("mail.smtp.port", "465");
		prop.put("mail.smtp.ssl.enable", "true");
		prop.put("mail.smtp.auth", "true");
		
		Session session =Session.getInstance(prop,new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});
		
		return session;
	}
}