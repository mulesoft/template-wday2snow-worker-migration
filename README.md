
# Anypoint Template: Workday to ServiceNow Worker Migration	

<!-- Header (start) -->

<!-- Header (end) -->

# License Agreement
This template is subject to the conditions of the <a href="https://s3.amazonaws.com/templates-examples/AnypointTemplateLicense.pdf">MuleSoft License Agreement</a>. Review the terms of the license before downloading and using this template. You can use this template for free with the Mule Enterprise Edition, CloudHub, or as a trial in Anypoint Studio. 
# Use Case
<!-- Use Case (start) -->
As a Workday admin I want to migrate workers from Workday to users in ServiceNow.

This Anypoint template serves as a foundation for the process of migrating Workers from Workday instance to Users in ServiceNow, being able to specify filtering criteria and desired behavior if a user already exists in the destination system. 

As implemented, this template leverages the Mule batch module.
The batch job is divided into Process and On Complete stages.
The template queries Workday for all the existing active workers that match the filter criteria. The criteria is based on manipulations starting from the given date.
The last step of the Process stage will create or update the users in ServiceNow based on if a user already exists in the system.
Finally during the On Complete stage the template will both output statistics data into the console and send a notification e-mail with the results of the batch execution.
<!-- Use Case (end) -->

# Considerations
<!-- Default Considerations (start) -->

<!-- Default Considerations (end) -->

<!-- Considerations (start) -->
There are a couple of things you should take into account before running this template:

1. **Workday e-mail uniqueness**: The e-mail can be repeated for two or more accounts (or missing). Therefore Workday accounts with duplicate emails will be removed from processing.
2. Users in ServiceNow are matched with Workday Workers based on the `user_name`. If user_name is not specified then the matching is based on the `email`, `first_name` and `last_name`.
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
In any of the ways you would like to run this template this is an example of the output you'll see after browse toing the HTTP connector:

{
    "Message": "Batch Process initiated",
    "ID": "25cb73d0-5e82-11e8-a6fe-100ba905a441",
    "RecordCount": 2,
    "StartExecutionOn": "2018-05-23T14:09:30Z"
}
<!-- Run it (end) -->

## Running On Premises
In this section we help you run this template on your computer.
<!-- Running on premise (start) -->

<!-- Running on premise (end) -->

### Where to Download Anypoint Studio and the Mule Runtime
If you are new to Mule, download this software:

+ [Download Anypoint Studio](https://www.mulesoft.com/platform/studio)
+ [Download Mule runtime](https://www.mulesoft.com/lp/dl/mule-esb-enterprise)

**Note:** Anypoint Studio requires JDK 8.
<!-- Where to download (start) -->

<!-- Where to download (end) -->

### Importing a Template into Studio
In Studio, click the Exchange X icon in the upper left of the taskbar, log in with your Anypoint Platform credentials, search for the template, and click Open.
<!-- Importing into Studio (start) -->

<!-- Importing into Studio (end) -->

### Running on Studio
After you import your template into Anypoint Studio, follow these steps to run it:

+ Locate the properties file `mule.dev.properties`, in src/main/resources.
+ Complete all the properties required as per the examples in the "Properties to Configure" section.
+ Right click the template project folder.
+ Hover your mouse over `Run as`.
+ Click `Mule Application (configure)`.
+ Inside the dialog, select Environment and set the variable `mule.env` to the value `dev`.
+ Click `Run`.
<!-- Running on Studio (start) -->

<!-- Running on Studio (end) -->

### Running on Mule Standalone
Update the properties in one of the property files, for example in mule.prod.properties, and run your app with a corresponding environment variable. In this example, use `mule.env=prod`. 
After this, to trigger the use case you just need to browse to the local HTTP connector with the port you configured in your file. If this is, for instance, `9090` then you should browse to: `http://localhost:9090/migrateworkers` and this will output a summary report and send it in the e-mail.

## Running on CloudHub
When creating your application in CloudHub, go to Runtime Manager > Manage Application > Properties to set the environment variables listed in "Properties to Configure" as well as the mule.env value.
<!-- Running on Cloudhub (start) -->
Once your app is all set up and started, supposing you choose `wdayworkermigration` as domain name to trigger the use case, you just need to browse to `http://wdayworkermigration.cloudhub.io/migrateworkers` and report will be sent to the e-mail configured.
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
+ http.port `9090`
+ page.size `100`
+ migration.startDate `"2018-01-01T23:57:59"`

#### Workday Connector configuration
+ wday.username `joan`
+ wday.tenant `acme_pt1`
+ wday.password `joanPass123`
+ wday.hostname `your_impl-cc.workday.com`

#### ServiceNow Connector 
+ snow.user `snow_user1`
+ snow.password `ExamplePassword881`
+ snow.endpoint `https://instance.service-now.com`

#### SMTP Services configuration
+ smtp.host `smtp.gmail.com`
+ smtp.port `587`
+ smtp.user `sender@gmail.com`
+ smtp.password `secret`

#### Mail details
+ mail.from `users.report@mulesoft.com`
+ mail.to `user@mulesoft.com`
+ mail.subject `Users Migration Report`
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
* errorHandling.xml<!-- Customize it (start) -->

<!-- Customize it (end) -->

## config.xml
<!-- Default Config XML (start) -->
This file provides the configuration for connectors and configuration properties. Only change this file to make core changes to the connector processing logic. Otherwise, all parameters that can be modified should instead be in a properties file, which is the recommended place to make changes.<!-- Default Config XML (end) -->

<!-- Config XML (start) -->

<!-- Config XML (end) -->

## businessLogic.xml
<!-- Default Business Logic XML (start) -->
Functional aspect of the template is implemented on this XML, directed by one flow responsible of executing the logic.
For the pourpose of this particular template the *mainFlow* just executes the Batch Job which handles all the logic of it.<!-- Default Business Logic XML (end) -->

<!-- Business Logic XML (start) -->

<!-- Business Logic XML (end) -->

## endpoints.xml
<!-- Default Endpoints XML (start) -->
This is the file where you will find the inbound and outbound sides of your integration app.
This Template has only an HTTP Listener as the way to trigger the use case.

**HTTP Listener Connector** - Start Report Generation

+ `${http.port}` is set as a property to be defined either on a property file or in CloudHub environment variables.
+ The path configured by default is `migrateworkers` and you are free to change for the one you prefer.
+ The host name for all endpoints in your CloudHub configuration should be defined as `localhost`. CloudHub will then route requests from your application domain URL to the endpoint.
+ The endpoint is a *request-response* since as a result of calling it the response will be the total of Workers synced and filtered by the criteria specified.<!-- Default Endpoints XML (end) -->

<!-- Endpoints XML (start) -->

<!-- Endpoints XML (end) -->

## errorHandling.xml
<!-- Default Error Handling XML (start) -->
This file handles how your integration reacts depending on the different exceptions. This file provides error handling that is referenced by the main flow in the business logic.<!-- Default Error Handling XML (end) -->

<!-- Error Handling XML (start) -->

<!-- Error Handling XML (end) -->

<!-- Extras (start) -->

<!-- Extras (end) -->
