package org.java.api.aws.ses;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;

/**
 * This class creates a builder for the AWS SES Client. 
 * 
 * @author Raghuram Challapalli
 */
public class SESClientBuilder {

	
	final static Logger logger = Logger.getLogger(SESClientBuilder.class);
	SESRequestBuilder requestBuilder = null;
	private String defaultDisplayName;
	private String defaultEmailAddress;
	private Regions defaultRegion;
	private String accessKeyId;
	private String secretAccessKey;
	AmazonSimpleEmailService client = null;
	
	/**
	 * Defines the configuration parameters required for the AWS SES Client.
	 * 
	 * @param displayName  Name to be displayed in the From section of the email.  
	 * @param emailAddress  Email address to be displayed as the from email. This has to be a verified email with AWS.
	 * @param region  AWS Server region used to send emails using AWS SES.
	 * @param accessKeyId  AWS access key Id. This can be obtained from the AWS SES Console.
	 * @param secretAccessKey  AWS Secret Access Key. This can be obtained from the AWS SES Console.
	 */
	public SESClientBuilder(String displayName, String emailAddress, Regions region, String accessKeyId, String secretAccessKey){
		this.defaultDisplayName = displayName;
		this.defaultEmailAddress = emailAddress;
		this.defaultRegion = region;
		this.accessKeyId = accessKeyId;
		this.secretAccessKey = secretAccessKey;
		createDefaultCredentials();
		requestBuilder = new SESRequestBuilder(displayName, emailAddress);		
	}
	
	private void createDefaultCredentials(){
		BasicAWSCredentials credentials = new BasicAWSCredentials(this.accessKeyId, this.secretAccessKey);
		AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);		
		client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(defaultRegion).withCredentials(credentialsProvider).build(); 
	}
	
	/**
	 * Sends an email using the AWS SES API.
	 * 
	 * @param email  SESEmailMessage object containing the required information.
	 * @return true if the email was sent successfully; false otherwise
	 */
	public boolean sendEmail(SESEmailMessage email){
		if(email.getFileList().size()>0){
			try{				
	            SendRawEmailRequest rawEmailRequest = requestBuilder.buildAWSRawMessage(email, defaultDisplayName, defaultEmailAddress);
	            client.sendRawEmail(rawEmailRequest);
	            return true;
			}catch(Exception e){
				logger.error("SESEmailMessage Sending Failed! Error: "+e.getMessage());
				return false;				
			}
		}
		try{
			SendEmailRequest request = requestBuilder.createCustomMailRequest(email, defaultDisplayName, defaultEmailAddress);		      			      
			client.sendEmail(request);
			return true;
		}catch(Exception ex){
			logger.error("SESEmailMessage Sending Failed! Error: "+ex.getMessage());
		}
		return false;
	}
	
	/**
	 * Sends an email using the AWS SES API.
	 * 
	 * @param email  SESEmailMessage object containing the required information.
	 * @param footerText  Customized footer text that will be added at the end of the message. Should be a properly formatted HTML string.
	 * @return true if the email is sent successfully; false otherwise
	 */
	public boolean sendEmail(SESEmailMessage email, String footerText){
		if(email.getFileList().size()>0){
			try{				
	            SendRawEmailRequest rawEmailRequest = requestBuilder.buildAWSRawMessage(email, footerText,  defaultDisplayName, defaultEmailAddress);
	            client.sendRawEmail(rawEmailRequest);
	            return true;
			}catch(Exception e){
				logger.error("SESEmailMessage Sending Failed! Error: "+e.getMessage());
				return false;				
			}
		}
		try{
			SendEmailRequest request = requestBuilder.createCustomMailRequest(email, footerText, defaultDisplayName, defaultEmailAddress);		      			      
			client.sendEmail(request);
			return true;
		}catch(Exception ex){
			logger.error("SESEmailMessage Sending Failed! Error: "+ex.getMessage());
		}
		return false;
	}

	/**
	 * Sends an email using the AWS SES API.
	 * 
	 * @param email  SESEmailMessage object containing the required information.
	 * @param displayName  Name to be displayed in the "From" section of the email.
	 * @param emailAddress  Email address of the sender. This email address has to be a verified email with AWS. This will override the default email address.
	 * @return true if the email is sent successfully; false otherwise
	 */
	public boolean sendEmail(SESEmailMessage email, String displayName, String emailAddress){
		if(email.getFileList().size()>0){
			try{				
	            SendRawEmailRequest rawEmailRequest = requestBuilder.buildAWSRawMessage(email, displayName, emailAddress);
	            client.sendRawEmail(rawEmailRequest);
	            return true;
			}catch(Exception e){
				try{
					SendRawEmailRequest rawEmailRequest = requestBuilder.buildCustomAWSRawMessage(email, displayName, emailAddress);
		            client.sendRawEmail(rawEmailRequest);
		            return true;
				}catch(Exception es){
					logger.error("SESEmailMessage Sending Failed! Error: "+es.getMessage());
					return false;
				}				
			}
		}
		try{
			SendEmailRequest request = requestBuilder.createAWSMailRequest(email, displayName, emailAddress);		      			      
			client.sendEmail(request);
			return true;
		}catch(Exception e){
			logger.warn("Sender SESEmailMessage ID : "+emailAddress+" is not verified with AWS.");
			logger.warn("Sending SESEmailMessage using default AWS Credentials.");				
			try{
				SendEmailRequest request = requestBuilder.createCustomMailRequest(email, displayName, emailAddress);		      			      
				client.sendEmail(request);
				return true;
			}catch(Exception ex){
				logger.error("SESEmailMessage Sending Failed! Error: "+ex.getMessage());
			}			
		}
		return false;
	}
	
	/**
	 * Sends an email using the AWS SES API.
	 * 
	 * @param email  SESEmailMessage object containing the required information.
	 * @param footerText  Customized footer text that will be added at the end of the message. Should be a properly formatted HTML string.
	 * @param displayName  Name to be displayed in the "From" section of the email.
	 * @param emailAddress  Email address of the sender. This email address has to be a verified email with AWS. This will override the default email address.
	 * @return true if the email is sent successfully; false otherwise
	 */
	public boolean sendEmail(SESEmailMessage email, String footerText, String displayName, String emailAddress){		
		if(email.getFileList().size()>0){
			try{				
	            SendRawEmailRequest rawEmailRequest = requestBuilder.buildAWSRawMessage(email, footerText, displayName, emailAddress);
	            client.sendRawEmail(rawEmailRequest);
	            return true;
			}catch(Exception e){
				try{
					SendRawEmailRequest rawEmailRequest = requestBuilder.buildCustomAWSRawMessage(email, footerText, displayName, emailAddress);
		            client.sendRawEmail(rawEmailRequest);
		            return true;
				}catch(Exception es){
					logger.error("SESEmailMessage Sending Failed! Error: "+es.getMessage());
					return false;
				}				
			}
		}
		try{
			SendEmailRequest request = requestBuilder.createAWSMailRequest(email, footerText, displayName, emailAddress);		      			      
			client.sendEmail(request);
			return true;
		}catch(Exception e){
			logger.warn("Sender SESEmailMessage ID : "+emailAddress+" is not verified with AWS.");
			logger.warn("Sending SESEmailMessage using default AWS Credentials.");				
			try{
				SendEmailRequest request = requestBuilder.createCustomMailRequest(email, footerText, displayName, emailAddress);		      			      
				client.sendEmail(request);
				return true;
			}catch(Exception ex){
				logger.error("SESEmailMessage Sending Failed! Error: "+ex.getMessage());
			}			
		}
		return false;
	}	
	
}
