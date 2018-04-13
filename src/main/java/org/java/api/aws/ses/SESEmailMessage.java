package org.java.api.aws.ses;

import java.io.File;
import java.util.List;

/**
 * This is the POJO class used by the AWS SES API. All classes and methods in the API use this class for communication.
 * 
 * @author Raghuram Challapalli
 *
 */
public class SESEmailMessage {
		
	private String to;
	private String cc;
	private String bcc;
	private String subject;
	private String body;
	private List<byte[]> rawFileList;
	private List<File> fileList;
	
	/**
	 * This is the POJO class used by the AWS SES API. All classes and methods in the API use this class for communication.
	 */
	public SESEmailMessage(){
		
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @return the cc
	 */
	public String getCc() {
		return cc;
	}

	/**
	 * @param cc the cc to set
	 */
	public void setCc(String cc) {
		this.cc = cc;
	}

	/**
	 * @return the bcc
	 */
	public String getBcc() {
		return bcc;
	}

	/**
	 * @param bcc the bcc to set
	 */
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the rawFileList
	 */
	public List<byte[]> getRawFileList() {
		return rawFileList;
	}

	/**
	 * @param rawFileList the rawFileList to set
	 */
	public void setRawFileList(List<byte[]> rawFileList) {
		this.rawFileList = rawFileList;
	}

	/**
	 * @return the fileList
	 */
	public List<File> getFileList() {
		return fileList;
	}

	/**
	 * @param fileList the fileList to set
	 */
	public void setFileList(List<File> fileList) {
		this.fileList = fileList;
	}
	
	
}
