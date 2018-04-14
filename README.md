# AWS-SES-API
A simplified Java Library for sending emails using the Amazon Simple Email Service.

<a class="github-buttons" href="#prerequisites" data-text="Prerequisites">Prerequisites</a> | <a class="github-buttons" href="#dependencies" data-text="Dependencies">Dependencies</a> | <a class="github-buttons" href="#usage" data-text="Usage">Usage</a> | <a class="github-buttons" href="#contributing" data-text="Contributing">Contributing</a>
   
## Introduction
### What is this API?
This API is a simplified Java library to send email messages using the Amazon Simple Email Service.

### What is Amazon Simple Email Service?
The official Amazon website states this:
> Amazon Simple Email Service (Amazon SES) is a cloud-based email sending service designed to help digital marketers 
and application developers send marketing, notification, and transactional emails. 
It is a reliable, cost-effective service for businesses of all sizes that use email to keep in contact with their customers.

Simplified, it is an email service that leverages Amazon's servers to send emails.

## Prerequisites
You will need to have an active AWS account. Official instructions by Amazon can be found [here](https://aws.amazon.com/ses/getting-started/). More detailed instructions can be found in the Wiki [here](https://github.com/dragonsbreath/AWS-SES-API/wiki/Configuring-AWS).

## Dependencies
### Amazon SES
This project depends on the Amazon SES repository. You can add the following maven repository dependency in your pom.xml.

```  
<!-- https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk-ses -->
<dependency>
  <groupId>com.amazonaws</groupId>
  <artifactId>aws-java-sdk-ses</artifactId>
  <version>1.11.299</version>
</dependency>
```

### Adding this API to your project
You need to add [JitPack](https://jitpack.io/) to your project as an additional repository. This will enable adding github projects as Maven dependencies. You can add other github projects too!

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

## Usage
Folowing code sample gives a simple use case for using this API:
````
import java.io.File;
import org.java.api.aws.ses.SESClientBuilder;
import org.java.api.aws.ses.SESEmailMessage;
import com.amazonaws.regions.Regions;

public Class AamazonSES{
    
    private final String defaultFromEmail = "youremail@example.com"; // This should be an AWS verified email address.
    private final String defaultFromName = "Your Name"; // Your name. This will be displayed in the "From" section of the email message.
    private final String awsAccessKeyId = "AWS Access Key Id"; // Needs to be obtained from AWS.
    private final String awsSecretAccessKey = "AWS Secret Access Key"; // Needs to be obtained from AWS.
    private final Regions awsRegion = Regions.US_WEST_2; // AWS region used for sending emails from your account.
    
    //This will initialize the AWS Client.
    SESClientBuilder awsClient = new SESClientBuilder(defaultFromName, defaultFromEmail, awsRegion, awsAccessKeyId, awsSecretAccessKey);		  
    
    //Use this method to compose your email message. You can also write your own wrapper class for this method.
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
    
    //This method will send your email message.
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

## Contributing
Contributions are always welcome. You can use the issues section to send feature requests. Pull requests are also welcome.


