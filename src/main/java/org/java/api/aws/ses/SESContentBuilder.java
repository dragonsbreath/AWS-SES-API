package org.java.api.aws.ses;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;

import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;

/**
 * This class creates a content builder for creating message objects compatible with the AWS SES API.
 * 
 * @author Raghuram Challapalli
 */
public class SESContentBuilder {

	private final static Logger logger = Logger.getLogger(SESContentBuilder.class);
	private final static String htmlHeader = "<!DOCTYPE html><html lang=\"en\">"
	+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
	+ "<meta name=\"viewport\" content=\"width=device-width\"><title>New SESEmailMessage</title></head>"
	+ "<body><table<tr><td>";
	
	private final static String htmlFooter = "</td></tr></table></body></html>";
	private String footerText = "";
	
	/**
	 * This class creates a content builder for creating message objects compatible with the AWS SES API.
	 */
	public SESContentBuilder(){
		
	}
	
	/**
	 * Creates a destination object from an SESEmailMessage object.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @return SES Destination object that can be used by the AWS SES API.
	 */	
	protected Destination getDestination(SESEmailMessage email){		
		String toAddress = email.getTo();
		String ccAddress = email.getCc();
		String bccAddress = email.getBcc();		
		boolean ccNull = isInvalid(ccAddress);
		boolean bccNull = isInvalid(bccAddress);
		if(ccNull && bccNull){
			return new Destination().withToAddresses(Arrays.asList(toAddress.split(",")));
		}else if(ccNull && !bccNull){
			return new Destination().withToAddresses(Arrays.asList(toAddress.split(","))).withBccAddresses(Arrays.asList(bccAddress.split(",")));
		}else if(!ccNull && bccNull){
			return new Destination().withToAddresses(Arrays.asList(toAddress.split(","))).withCcAddresses(Arrays.asList(ccAddress.split(",")));
		}else if(!ccNull && !bccNull){
			return new Destination().withToAddresses(Arrays.asList(toAddress.split(","))).withCcAddresses(Arrays.asList(ccAddress.split(","))).withBccAddresses(Arrays.asList(bccAddress.split(",")));
		}
		return null;		
	}
	
	/**
	 * Validates each recipient address and adds them to the MimeMessage object.
	 * 
	 * @param message - MimeMessage object to which the recipients need to be added.
	 * @param email - SESEmailMessage object containing the required information.
	 * @return MimeMessage object with the recipient information.
	 * @throws MessagingException
	 */
	protected MimeMessage addRecipients(MimeMessage message, SESEmailMessage email) throws MessagingException{			
		for(String address : formatAddresses(email.getTo()))
			message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(address));
		if(null != email.getCc()){
			for(String address : formatAddresses(email.getCc()))
				message.setRecipients(javax.mail.Message.RecipientType.CC, InternetAddress.parse(address));
		}
		if(null != email.getBcc()){		
			for(String address : formatAddresses(email.getBcc()))
				message.setRecipients(javax.mail.Message.RecipientType.BCC, InternetAddress.parse(address));
		}
		return message;		
	}
	
	private List<String> formatAddresses(String address){
		List<String> addrArray = new ArrayList<String>();
		String[] addressArray = address.split(",");
		for(String addr : addressArray)
			addrArray.add(addr);		
		List<String> returnArray = new ArrayList<String>(addrArray);
		for(String addr : addrArray){
			if(isInvalid(addr))
				returnArray.remove(addr);
		}		
		return returnArray;
	}
	
	private boolean isInvalid(String address){		
		boolean result = false;
		try{
			InternetAddress emailAddress = new InternetAddress(address);
			emailAddress.validate();
			result = false;
		}catch(Exception e){
			result = true;
		}		
		return result;
	}
	
	/**
	 * Creates an AWS Message object from an SESEmailMessage Object.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @return SES Message Object that can be used by the AWS SES API.
	 */
	protected Message getAWSFormattedMessage(SESEmailMessage email){
		Message message = new Message();
		message.setSubject(this.createAWSContent(email.getSubject()));
		message.setBody(this.getAWSFormattedBody(email.getBody()));
		return message;
	}
	
	/**
	 * Creates an AWS Message object from an SESEmailMessage Object.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param displayName - Name to be displayed in the "From" section of the email.
	 * @param emailAddress - SESEmailMessage Id of the sender. This email Id has to be verified with AWS. This will override the default email Id.
	 * @return SES Message Object that can be used by the AWS SES API.
	 */
	protected Message getAWSFormattedMessage(SESEmailMessage email, String displayName, String emailAddress){
		this.createAdditionalFooter(displayName, emailAddress);
		Message message = new Message();
		message.setSubject(this.createAWSContent(email.getSubject()));
		message.setBody(this.getAWSFormattedBody(email.getBody()));
		return message;
	}
	
	/**
	 * Creates an AWS Message object from an SESEmailMessage Object.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param footerText - Customized footer text that will be added at the end of the message. Should be a properly formatted HTML string.
	 * @return SES Message Object that can be used by the AWS SES API.
	 */
	protected Message getAWSFormattedMessage(SESEmailMessage email, String footerText){
		if(null != footerText)
			this.footerText += footerText;
		Message message = new Message();
		message.setSubject(this.createAWSContent(email.getSubject()));
		message.setBody(this.getAWSFormattedBody(email.getBody()));
		return message;
	}
	
	/**
	 * Creates an AWS Message object from an SESEmailMessage Object.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param footerText - Customized footer text that will be added at the end of the message. Should be a properly formatted HTML string.
	 * @param displayName - Name to be displayed in the "From" section of the email.
	 * @param emailAddress - SESEmailMessage Id of the sender. This email Id has to be verified with AWS. This will override the default email Id.
	 * @return SES Message Object that can be used by the AWS SES API.
	 */
	protected Message getAWSFormattedMessage(SESEmailMessage email, String footerText, String displayName, String emailAddress){
		this.createAdditionalFooter(displayName, emailAddress);
		if(null != footerText)
			this.footerText += footerText;
		Message message = new Message();
		message.setSubject(this.createAWSContent(email.getSubject()));
		message.setBody(this.getAWSFormattedBody(email.getBody()));
		return message;
	}	
	
	/**
	 * Creates a MimeMultipart object from an SESEmailMessage Object.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @return MimeMultipart Object that can be used by the AWS SES API.
	 * @throws MessagingException
	 */
	protected MimeMultipart buildMimeBody(SESEmailMessage email) throws MessagingException{
		return buildMimeBody(email, null);
	}
	
	/**
	 * Creates a MimeMultipart object from an SESEmailMessage Object.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param footerText - Customized footer text that will be added at the end of the message. Should be a properly formatted HTML string.
	 * @return MimeMultipart Object that can be used by the AWS SES API.
	 * @throws MessagingException
	 */
	protected MimeMultipart buildMimeBody(SESEmailMessage email, String footerText) throws MessagingException{
		return buildMimeBody(email, footerText, null, null);
	}
	
	/**
	 * Creates a MimeMultipart object from an SESEmailMessage Object.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param displayName - Name to be displayed in the "From" section of the email.
	 * @param emailAddress - SESEmailMessage Id of the sender. This email Id has to be verified with AWS. This will override the default email Id.
	 * @return MimeMultipart Object that can be used by the AWS SES API.
	 * @throws MessagingException
	 */
	protected MimeMultipart buildMimeBody(SESEmailMessage email, String displayName, String emailAddress) throws MessagingException{
		return buildMimeBody(email, null, displayName, emailAddress);
	}
	
	/**
	 * Creates a MimeMultipart object from an SESEmailMessage Object.
	 * 
	 * @param email - SESEmailMessage object containing the required information.
	 * @param footerText - Customized footer text that will be added at the end of the message. Should be a properly formatted HTML string.
	 * @param displayName - Name to be displayed in the "From" section of the email.
	 * @param emailAddress - SESEmailMessage Id of the sender. This email Id has to be verified with AWS. This will override the default email Id.
	 * @return MimeMultipart Object that can be used by the AWS SES API.
	 * @throws MessagingException
	 */
	protected MimeMultipart buildMimeBody(SESEmailMessage email, String footerText, String displayName, String emailAddress) throws MessagingException{
		if((null != displayName) && (null != emailAddress))
			this.createAdditionalFooter(displayName, emailAddress);
		if(null != footerText)
			this.footerText += footerText;
		MimeMultipart msg = new MimeMultipart("mixed");
		MimeBodyPart wrap = new MimeBodyPart(); 
		wrap.setContent(addHTMLBody(email.getBody()));
		msg.addBodyPart(wrap);
		if((null != email.getFileList()) && (email.getFileList().size()>0))
			msg = addFileAttachments(msg, email.getFileList());
		return msg;
	}
	
	/**
	 * This method will check for HTML formatting and will add additional formatting as required.
	 * 
	 * @param rawBodyContent - String that shall be used as the body of the email message.
	 * @return String containing a formatted email message body.
	 */
	@SuppressWarnings("static-access")
	private String formatEmailBody(String rawBodyContent){		
		String bodyContent = Pattern.compile("[^\\p{ASCII}]").matcher(rawBodyContent).replaceAll(""); 
		if(!bodyContent.contains("<html>")){
			bodyContent = this.htmlHeader+bodyContent+this.htmlFooter;
		}
		bodyContent = addFooterText(bodyContent);
		return bodyContent;		
	}
	
	/**
	 * @param bodyContent - String that shall be used as the body of the email message. May or may not be HTML formatted.
	 * @return SES Body object that can be used by the AWS SES API.
	 */
	private Body getAWSFormattedBody(String bodyContent){		
		Body body = new Body();
		body.setHtml(createAWSContent(formatEmailBody(bodyContent)));
		return body;
	}
	
	/**
	 * Adds the footer text to the body of the email message.
	 * If footer is not defined, body is returned with no changes.
	 * 
	 * @param bodyContent - String that shall be used as the body of the email message.
	 * @return String with or without footer text added.
	 */
	private String addFooterText(String bodyContent){
		try{			
			return bodyContent+this.footerText;
		}catch(Exception e){
			logger.warn("No Footer Text defined!");
			return bodyContent;
		}		
	}	
	
	/**
	 * Creates a content object from a string. 
	 * Content objects can be used as subject or body of email message in the SES API.
	 * 
	 * @param contentText - String containing the desired text
	 * @return SES Content object that can be used by the AWS SES API.
	 */
	private Content createAWSContent(String contentText){
		Content content = new Content();
		content.setCharset("UTF-8");
		content.setData(contentText);						
		return content;
	}
	
	/**
	 * This will add an additional message line to the end of the message.
	 * This method is used only if the email address of the sender is different than the default email address, and if the email address of the sender is not verified with AWS.
	 * @param displayName
	 * @param emailAddress
	 */
	private void createAdditionalFooter(String displayName, String emailAddress){
		String content = "<html><body><p><strong>Important Information: </strong>"
		+ "This email was sent to you on behalf of "+WordUtils.capitalize(displayName)+". "
		+ "All replies to this email will be sent to "+emailAddress+".</p></body></html>";
		this.footerText += content;
	}
	
	private MimeMultipart addHTMLBody(String emailBody) throws MessagingException{
		MimeMultipart msg_body = new MimeMultipart("alternative");
		MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(formatEmailBody(emailBody),"text/html; charset=UTF-8");                
        msg_body.addBodyPart(htmlPart);        
		return msg_body;
	}
	
	private MimeMultipart addFileAttachments(MimeMultipart msg, List<File> fileList){        
        for(File file : fileList){
        	try{
        		MimeBodyPart att = new MimeBodyPart();
                DataSource fds = new FileDataSource(file);
                att.setDataHandler(new DataHandler(fds));
                att.setFileName(fds.getName());
                att.setHeader("Content-Type", fds.getContentType());
                msg.addBodyPart(att);
        	}catch(Exception e){
        		logger.error(e.getMessage());
        	}        	
        }
        return msg;
	}
}
