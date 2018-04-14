# AWS-SES-API
A simplified Java Library for sending emails using the Amazon Simple Email Service.

<a class="github-buttons" 
   href="#dependencies"
   title="Dependencies"
   data-icon="octicon-mark-github"
   data-size="large"
   data-text="Dependencies"
   aria-label="{{ aria label }}">Dependencies</a>
   
## Introduction
### What is this API?
This API is a simplified Java library to send email messages using the Amazon Simple Email Service.

### What is Amazon Simple Email Service?
The official Amazon website states this:
> Amazon Simple Email Service (Amazon SES) is a cloud-based email sending service designed to help digital marketers 
and application developers send marketing, notification, and transactional emails. 
It is a reliable, cost-effective service for businesses of all sizes that use email to keep in contact with their customers.

Simplified, it is an email service that leverages Amazon's servers to send emails.

### Dependencies
This project depends on the amazon ses repository. You can add the following maven repository dependency in your pom.xml.

```  
<dependency>
  <groupId>com.amazonaws</groupId>
  <artifactId>aws-java-sdk-ses</artifactId>
  <version>1.11.299</version>
</dependency>
```

### Adding this API to your project
You need to add JitPack to your project as an additional repository. This will enable adding github projects as Maven dependencies.

```
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```

To add this API as a maven dependency, use the following code:

```
<dependency>
  <groupId>com.github.dragonsbreath</groupId>
  <artifactId>aws-ses-api</artifactId>
  <version>1.0.1.beta</version>
</dependency>
```

### Usage
Folowing code sample gives a simple use case for using this API:
````
import java.io.File;
import org.java.api.aws.ses.SESClientBuilder;
import org.java.api.aws.ses.SESEmailMessage;
import com.amazonaws.regions.Regions;

public Class AamazonSES{
    
    private final String defaultFromEmail = "youremail@example.com";
    private final String defaultFromName = "Your Name";
    private final String awsAccessKeyId = "AWS Access Key Id";
    private final String awsSecretAccessKey = "AWS Secret Access Key";
    
    SESClientBuilder awsClient = new SESClientBuilder(defaultFromName, defaultFromEmail, Regions.US_EAST_1, awsAccessKeyId, awsSecretAccessKey);	
	  
    public SESEmailMessage composeMessage(){
        SESEmailMessage message = new SESEmailMessage();
        message.setTo(""); // Comma separated list of emails.
        message.setCc(""); // Optional CC. Set to null if not required. Or ignore it completely.
        message.setBcc(""); // Optional BCC. Set to null if not required. Or ignore it completely.
        message.setSubject(""); // Subject of your message.
        message.setBody(""); // String with your message content. Also accepts HTML content.
        message.setFileList(someFileList); // Collection of java.io.File objects. These will be sent as attachments.
        return message;    
    }
    
    public boolean sendEmail(SESEmailMessage message){
        if(awsClient.sendEmail(message))
          return true;
        else
          return false;
    }
    
    //If you want to add a nice HTML footer to your message.
    public boolean sendEmailWithFooter(SESEmailMessage message, String extraFooter){
        if(awsClient.sendEmail(message, extraFooter))
          return true;
        else
          return false;
    }
    
    //If you want to add a different reply-to address to your message.
    public boolean sendEmailWithFooter(SESEmailMessage message, String senderName, String senderEmailAddress){
        if(awsClient.sendEmail(message, senderName, senderEmailAddress))
          return true;
        else
          return false;
    }
    
    //If you want to add a nice HTML footer, and a different reply-to address to your message.
    public boolean sendEmailWithFooter(SESEmailMessage message, String extraFooter, String senderName, String senderEmailAddress){
        if(awsClient.sendEmail(message, extraFooter, senderName, senderEmailAddress))
          return true;
        else
          return false;
    }
    
}

````

### Contributing
Contributions are always welcome. You can use the issues section to send feature requests. Pull requests are also welcome.

### License

