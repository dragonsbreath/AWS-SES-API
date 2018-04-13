package com.aws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;

/**
 * @author Raghuram Challapalli
 */
public class SESRequestBuilder {

	SESContentBuilder contentBuilder = new SESContentBuilder();
	private String displayName;
	private String emailAddress;
	
	/**
	 * Defines the default values to be used while sending emails.
	 * 
	 * @param displayName - Name to be displayed as the sender in the "From" section of the email.
	 * @param emailAddress - SESEmailMessage Id that will be used to send emails. This has to be verified with AWS.
	 */
	public SESRequestBuilder(String displayName, String emailAddress){
		this.displayName = displayName;
		this.emailAddress = emailAddress;
	}

	/**
	 * Creates a properly formatted SESEmailMessage Request object that can be sent using the AWS SES API.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param displayName - Name to be displayed in the "From" section of the email.
	 * @param emailAddress - SESEmailMessage Id of the sender. This email Id has to be verified with AWS. This will override the default email Id.
	 * @return SendEmailRequest object that has all the required information.
	 */
	protected SendEmailRequest createAWSMailRequest(SESEmailMessage email, String displayName, String emailAddress){
		contentBuilder = new SESContentBuilder();
		SendEmailRequest request = new SendEmailRequest();
		request.setDestination(contentBuilder.getDestination(email));
		request.setMessage(contentBuilder.getAWSFormattedMessage(email));
		request.setSource(this.getFormattedAddress(displayName, emailAddress));		
		return request;
	}
	
	/**
	 * Creates a properly formatted SESEmailMessage Request object that can be sent using the AWS SES API.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param footerText - Customized footer text that will be added at the end of the message. Should be a properly formatted HTML string.
	 * @param displayName - Name to be displayed in the "From" section of the email.
	 * @param emailAddress - SESEmailMessage Id of the sender. This email Id has to be verified with AWS. This will override the default email Id.
	 * @return SendEmailRequest object that has all the required information.
	 */
	protected SendEmailRequest createAWSMailRequest(SESEmailMessage email, String footerText, String displayName, String emailAddress){
		contentBuilder = new SESContentBuilder();
		SendEmailRequest request = new SendEmailRequest();
		request.setDestination(contentBuilder.getDestination(email));
		request.setMessage(contentBuilder.getAWSFormattedMessage(email, footerText));
		request.setSource(this.getFormattedAddress(displayName, emailAddress));		
		return request;
	}
	
	/**
	 * Creates a properly formatted SESEmailMessage Request object that can be sent using the AWS SES API.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param displayName - Name to be displayed in the "From" section of the email.
	 * @param emailAddress - SESEmailMessage Id of the sender. This email Id has to be verified with AWS. This will override the default email Id.
	 * @return SendEmailRequest object that has all the required information.
	 */
	protected SendEmailRequest createCustomMailRequest(SESEmailMessage email, String displayName, String emailAddress){
		if(emailAddress.equals(this.emailAddress))
			return createAWSMailRequest(email, displayName, emailAddress);
		
		contentBuilder = new SESContentBuilder();
		SendEmailRequest request = new SendEmailRequest();
		request.setDestination(contentBuilder.getDestination(email));
		request.setMessage(contentBuilder.getAWSFormattedMessage(email, displayName, emailAddress));
		request.setReplyToAddresses(Arrays.asList(this.getFormattedAddress(displayName, emailAddress)));
		request.setSource(this.getFormattedAddress(this.displayName, this.emailAddress));		
		return request;
	}
	
	/**
	 * Creates a properly formatted SESEmailMessage Request object that can be sent using the AWS SES API.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param footerText - Customized footer text that will be added at the end of the message. Should be a properly formatted HTML string.
	 * @param displayName - Name to be displayed in the "From" section of the email.
	 * @param emailAddress - SESEmailMessage Id of the sender. This email Id has to be verified with AWS. This will override the default email Id.
	 * @return SendEmailRequest object that has all the required information.
	 */
	protected SendEmailRequest createCustomMailRequest(SESEmailMessage email, String footerText, String displayName, String emailAddress){
		if(emailAddress.equals(this.emailAddress))
			return createAWSMailRequest(email, footerText, displayName, emailAddress);
		
		contentBuilder = new SESContentBuilder();
		SendEmailRequest request = new SendEmailRequest();
		request.setDestination(contentBuilder.getDestination(email));
		request.setMessage(contentBuilder.getAWSFormattedMessage(email, footerText, displayName, emailAddress));
		request.setReplyToAddresses(Arrays.asList(this.getFormattedAddress(displayName, emailAddress)));
		request.setSource(this.getFormattedAddress(this.displayName, this.emailAddress));		
		return request;
	}
		
	/**
	 * Creates a properly formatted SESEmailMessage Request object that can be sent using the AWS SES API.
	 * <p>
	 * This message should be used if there are any attachments that need to be sent.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @return SendRawEmailRequest object that has all the required information.
	 * @throws IOException 
	 */
	protected SendRawEmailRequest buildAWSRawMessage(SESEmailMessage email) throws AddressException, MessagingException, IOException{
		contentBuilder = new SESContentBuilder();
		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage message = new MimeMessage(session);
		message = contentBuilder.addRecipients(message, email);
		message.setSubject(email.getSubject(), "UTF-8");
        message.setFrom(new InternetAddress(this.emailAddress, this.displayName));
        message.setContent(contentBuilder.buildMimeBody(email));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		message.writeTo(outputStream);
        RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
        return new SendRawEmailRequest(rawMessage);
	}
	
	/**
	 * Creates a properly formatted SESEmailMessage Request object that can be sent using the AWS SES API.
	 * <p>
	 * This message should be used if there are any attachments that need to be sent.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param footerText - Customized footer text that will be added at the end of the message. Should be a properly formatted HTML string.
	 * @return SendRawEmailRequest object that has all the required information.
	 * @throws IOException 
	 */
	protected SendRawEmailRequest buildAWSRawMessage(SESEmailMessage email, String footerText) throws AddressException, MessagingException, IOException{
		contentBuilder = new SESContentBuilder();
		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage message = new MimeMessage(session);
		message = contentBuilder.addRecipients(message, email);
		message.setSubject(email.getSubject(), "UTF-8");
        message.setFrom(new InternetAddress(this.emailAddress, this.displayName));
        message.setContent(contentBuilder.buildMimeBody(email, footerText));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		message.writeTo(outputStream);
        RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
        return new SendRawEmailRequest(rawMessage);
	}
	
	/**
	 * Creates a properly formatted SESEmailMessage Request object that can be sent using the AWS SES API.
	 * <p>
	 * This message should be used if there are any attachments that need to be sent.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param displayName - Name to be displayed in the "From" section of the email.
	 * @param emailAddress - SESEmailMessage Id of the sender. This email Id has to be verified with AWS. This will override the default email Id.
	 * @return SendRawEmailRequest object that has all the required information.
	 * @throws IOException 
	 */
	protected SendRawEmailRequest buildAWSRawMessage(SESEmailMessage email, String displayName, String emailAddress) throws AddressException, MessagingException, IOException{
		contentBuilder = new SESContentBuilder();
		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage message = new MimeMessage(session);
		message = contentBuilder.addRecipients(message, email);
		message.setSubject(email.getSubject(), "UTF-8");
        message.setFrom(new InternetAddress(emailAddress, displayName));
        message.setContent(contentBuilder.buildMimeBody(email));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		message.writeTo(outputStream);
        RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
        return new SendRawEmailRequest(rawMessage);
	}	
	
	/**
	 * Creates a properly formatted SESEmailMessage Request object that can be sent using the AWS SES API.
	 * <p>
	 * This message should be used if there are any attachments that need to be sent.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param footerText - Customized footer text that will be added at the end of the message. Should be a properly formatted HTML string.
	 * @param displayName - Name to be displayed in the "From" section of the email.
	 * @param emailAddress - SESEmailMessage Id of the sender. This email Id has to be verified with AWS. This will override the default email Id.
	 * @return SendRawEmailRequest object that has all the required information.
	 * @throws IOException 
	 */
	protected SendRawEmailRequest buildAWSRawMessage(SESEmailMessage email, String footerText, String displayName, String emailAddress) throws AddressException, MessagingException, IOException{
		contentBuilder = new SESContentBuilder();
		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage message = new MimeMessage(session);
		message = contentBuilder.addRecipients(message, email);
		message.setSubject(email.getSubject(), "UTF-8");
        message.setFrom(new InternetAddress(emailAddress, displayName));
        message.setContent(contentBuilder.buildMimeBody(email, footerText));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		message.writeTo(outputStream);
        RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
        return new SendRawEmailRequest(rawMessage);
	}
	
	/**
	 * Creates a properly formatted SESEmailMessage Request object that can be sent using the AWS SES API.
	 * <p>
	 * This message should be used if there are any attachments that need to be sent.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param displayName - Name to be displayed in the "From" section of the email.
	 * @param emailAddress - SESEmailMessage Id of the sender. This email Id has to be verified with AWS. This will override the default email Id.
	 * @return SendRawEmailRequest object that has all the required information.
	 * @throws IOException 
	 */
	protected SendRawEmailRequest buildCustomAWSRawMessage(SESEmailMessage email, String displayName, String emailAddress) throws AddressException, MessagingException, IOException{
		if(emailAddress.equals(this.emailAddress))
			return buildAWSRawMessage(email, displayName, emailAddress);
		
		contentBuilder = new SESContentBuilder();
		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage message = new MimeMessage(session);
		message = contentBuilder.addRecipients(message, email);
		message.setSubject(email.getSubject(), "UTF-8");
        message.setFrom(new InternetAddress(this.emailAddress, this.displayName));        
        message.setContent(contentBuilder.buildMimeBody(email, displayName, emailAddress));
        message.setReplyTo(InternetAddress.parse(emailAddress));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		message.writeTo(outputStream);
        RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
        return new SendRawEmailRequest(rawMessage);
	}
	
	/**
	 * Creates a properly formatted SESEmailMessage Request object that can be sent using the AWS SES API.
	 * <p>
	 * This message should be used if there are any attachments that need to be sent.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param footerText - Customized footer text that will be added at the end of the message. Should be a properly formatted HTML string.
	 * @param displayName - Name to be displayed in the "From" section of the email.
	 * @param emailAddress - SESEmailMessage Id of the sender. This email Id has to be verified with AWS. This will override the default email Id.
	 * @return SendRawEmailRequest object that has all the required information.
	 * @throws IOException 
	 */
	protected SendRawEmailRequest buildCustomAWSRawMessage(SESEmailMessage email, String footerText, String displayName, String emailAddress) throws AddressException, MessagingException, IOException{
		if(emailAddress.equals(this.emailAddress))
			return buildAWSRawMessage(email, footerText, displayName, emailAddress);
		
		contentBuilder = new SESContentBuilder();
		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage message = new MimeMessage(session);
		message = contentBuilder.addRecipients(message, email);
		message.setSubject(email.getSubject(), "UTF-8");
		message.setFrom(new InternetAddress(this.emailAddress, this.displayName));
        message.setContent(contentBuilder.buildMimeBody(email, footerText, displayName, emailAddress));
        message.setReplyTo(InternetAddress.parse(emailAddress));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		message.writeTo(outputStream);
        RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
        return new SendRawEmailRequest(rawMessage);
	}
	
	private String getFormattedAddress(String name, String email){
		return "\""+name+"\" <"+email+">";
	}
}
