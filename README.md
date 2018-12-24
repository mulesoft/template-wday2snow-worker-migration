
# Anypoint Template: Workday to ServiceNow Worker Migration	

<!-- Header (start) -->
Moves a large set of workers from Workday to ServiceNow. You can trigger this manually or programmatically with an HTTP call. 

The template has the ability to specify a filtering criterion and a desired course of action when a user already exists in the destination system. This template uses batch to efficiently process many records at a time.

![d2d63dc1-87a3-4dab-adc8-5217b796ac21-image.png](https://exchange2-file-upload-service-kprod.s3.us-east-1.amazonaws.com:443/d2d63dc1-87a3-4dab-adc8-5217b796ac21-image.png)
<!-- Header (end) -->

# License Agreement
This template is subject to the conditions of the <a href="https://s3.amazonaws.com/templates-examples/AnypointTemplateLicense.pdf">MuleSoft License Agreement</a>. Review the terms of the license before downloading and using this template. You can use this template for free with the Mule Enterprise Edition, CloudHub, or as a trial in Anypoint Studio. 
# Use Case
<!-- Use Case (start) -->
As a Workday admin I want to migrate workers from Workday to users in ServiceNow.

This template serves as a foundation for the process of migrating Workers from Workday instance to Users in ServiceNow, being able to specify filtering criteria and desired behavior if a user already exists in the destination system. 

As implemented, this template leverages the Mule batch module. The batch job is divided into Process and On Complete stages.
The template queries Workday for all the existing active workers that match the filter criteria. The criteria is based on manipulations starting from the given date. The last step of the Process stage creates or updates the users in ServiceNow based on if a user already exists in the system. Finally during the On Complete stage the template outputs statistics data into the console and sends a notification email with the results of the batch execution.
<!-- Use Case (end) -->

# Considerations
<!-- Default Considerations (start) -->

<!-- Default Considerations (end) -->

<!-- Considerations (start) -->
There are a couple of things you should take into account before running this template:

1. Workday e-mail uniqueness: The email can be repeated for two or more accounts (or missing). Therefore Workday accounts with duplicate emails are removed from processing.
2. Users in ServiceNow are matched with Workday Workers based on the `user_name`. If user_name is not specified, then the matching is based on the `email`, `first_name`, and `last_name`.
<!-- Considerations (end) -->

## ServiceNow Considerations

Here's what you need to know to get this template to work with ServiceNow.


### As a Data Destination

There are no considerations with using ServiceNow as a data destination.
## Workday Considerations

### As a Data Source

There are no considerations with using Workday as a data origin.

# Run it!
Simple steps to get this template running.
<!-- Run it (start) -->
When you run this template this is an example of the output you see after you browsing to the HTTP connector:

```
{
    "Message": "Batch Process initiated",
    "ID": "25cb73d0-5e82-11e8-a6fe-100ba905a441",
    "RecordCount": 2,
    "StartExecutionOn": "2018-05-23T14:09:30Z"
}
```
<!-- Run it (end) -->

## Running On Premises
In this section we help you run this template on your computer.
<!-- Running on premise (start) -->

<!-- Running on premise (end) -->

### Download Anypoint Studio and the Mule Runtime
If you are new to Mule, download this software:

- [Download Anypoint Studio](https://www.mulesoft.com/platform/studio)
- [Download Mule runtime](https://www.mulesoft.com/lp/dl/mule-esb-enterprise)

**Note:** Anypoint Studio requires JDK 8.
<!-- Where to download (start) -->

<!-- Where to download (end) -->

### Import a Template into Studio
In Studio, click the Exchange X icon in the upper left of the taskbar, log in with your Anypoint Platform credentials, search for the template, and click Open.
<!-- Importing into Studio (start) -->

<!-- Importing into Studio (end) -->

### Run on Studio
After you import your template into Anypoint Studio, follow these steps to run it:

1. Locate the properties file `mule.dev.properties`, in src/main/resources.
2. Complete all the properties required per the examples in the "Properties to Configure" section.
3. Right click the template project folder.
4. Hover your mouse over `Run as`.
5. Click `Mule Application (configure)`.
6. Inside the dialog, select Environment and set the variable `mule.env` to the value `dev`.
7. Click `Run`.

<!-- Running on Studio (start) -->

<!-- Running on Studio (end) -->

### Run on Mule Standalone
Update the properties in one of the property files, for example in mule.prod.properties, and run your app with a corresponding environment variable. In this example, use `mule.env=prod`. 
After this, to trigger the use case you just need to browse to the local HTTP connector with the port you configured in your file. If this is, for instance, `9090` then browse to: `http://localhost:9090/migrateworkers` and this  outputs a summary report and sends it in the email.

## Run on CloudHub
When creating your application in CloudHub, go to Runtime Manager > Manage Application > Properties to set the environment variables listed in "Properties to Configure" as well as the mule.env value.
<!-- Running on Cloudhub (start) -->
Once your app is all set up and started, if you choose `wdayworkermigration` as domain name to trigger the use case, you just need to browse to `http://wdayworkermigration.cloudhub.io/migrateworkers` and a report is sent to the email configured.
<!-- Running on Cloudhub (end) -->

### Deploying a Template in CloudHub
In Studio, right click your project name in Package Explorer and select Anypoint Platform > Deploy on CloudHub.
<!-- Deploying on Cloudhub (start) -->

<!-- Deploying on Cloudhub (end) -->

## Properties to Configure
To use this template, configure properties such as credentials, configurations, etc.) in the properties file or in CloudHub from Runtime Manager > Manage Application > Properties. The sections that follow list example values.

### Application Configuration
<!-- Application Configuration (start) -->

#### Application Configuration

- http.port `9090`
- page.size `100`
- migration.startDate `"2018-01-01T23:57:59"`

#### Workday Connector Configuration

- wday.username `joan`
- wday.tenant `acme_pt1`
- wday.password `joanPass123`
- wday.hostname `your_impl-cc.workday.com`

#### ServiceNow Connector 

- snow.user `snow_user1`
- snow.password `ExamplePassword881`
- snow.endpoint `https://instance.service-now.com`

#### SMTP Services Configuration

- smtp.host `smtp.gmail.com`
- smtp.port `587`
- smtp.user `sender@gmail.com`
- smtp.password `secret`

#### Email Details

- mail.from `users.report@mulesoft.com`
- mail.to `user@mulesoft.com`
- mail.subject `Users Migration Report`
<!-- Application Configuration (end) -->

# API Calls
<!-- API Calls (start) -->
There are no special considerations regarding API calls.
<!-- API Calls (end) -->

# Customize It!
This brief guide provides a high level understanding of how this template is built and how you can change it according to your needs. As Mule applications are based on XML files, this page describes the XML files used with this template. More files are available such as test classes and Mule application files, but to keep it simple, we focus on these XML files:

* config.xml
* businessLogic.xml
* endpoints.xml
* errorHandling.xml
<!-- Customize it (start) -->

<!-- Customize it (end) -->

## config.xml
<!-- Default Config XML (start) -->
This file provides the configuration for connectors and configuration properties. Only change this file to make core changes to the connector processing logic. Otherwise, all parameters that can be modified should instead be in a properties file, which is the recommended place to make changes.
<!-- Default Config XML (end) -->

<!-- Config XML (start) -->

<!-- Config XML (end) -->

## businessLogic.xml
<!-- Default Business Logic XML (start) -->
The functional aspect of this template is implemented in this XML file, directed by a flow responsible for executing the logic. For the purpose of this template, the *mainFlow* just executes the batch job which handles all its logic.
<!-- Default Business Logic XML (end) -->

<!-- Business Logic XML (start) -->

<!-- Business Logic XML (end) -->

## endpoints.xml
<!-- Default Endpoints XML (start) -->
This is the file where you find the inbound and outbound sides of your integration app.
This template has only an HTTP Listener as the way to trigger the use case.

**HTTP Listener Connector** - Start Report Generation

- `${http.port}` is set as a property to be defined either on a property file or in CloudHub environment variables.
- The path configured by default is `migrateworkers` and you are free to change for the one you prefer.
- The host name for all endpoints in your CloudHub configuration should be defined as `localhost`. CloudHub routes requests from your application domain URL to the endpoint.
- The endpoint is a *request-response* since as a result of calling it, the response is the total of workers synced and filtered by the criteria specified.
<!-- Default Endpoints XML (end) -->

<!-- Endpoints XML (start) -->

<!-- Endpoints XML (end) -->

## errorHandling.xml
<!-- Default Error Handling XML (start) -->
This file handles how your integration reacts depending on the different exceptions. This file provides error handling that is referenced by the main flow in the business logic.
<!-- Default Error Handling XML (end) -->

<!-- Error Handling XML (start) -->

<!-- Error Handling XML (end) -->

<!-- Extras (start) -->

<!-- Extras (end) -->
