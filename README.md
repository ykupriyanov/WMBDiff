# WMBDiff
GUI tool for comparison content of WebSphere Message Brokers and export data to Excel.

The program compares the contents of Execution Groups (EG) with the same names on different brokers.
Comparison is made by the name of bar file (excluding path) and modification date of the object in bar file.
When comparing names EG insensitive.
Analyzed only the currently running Message Flows.

> The program has an additional setting "TEMP_EG_SUFFIX" in config.xml.
> If it set on, for example, _TEMP then the program consider that the Execution Group (EG) with the suffix "_TEMP" is part of the EG without this suffix on the same broker. 
> Then program perform merge of content of these EGs before comparision.
>
> Merge rules:
> * Message Flow with same name are different object in "union EG";
> * Other types of objects (MsgSet, jar and so on): if two EGs contain the object with same name then in "union EG" will be added only object from *_TEMP EG.

##PREREQUISITES
###WMB 7.0 ConfigManagerProxy JAR
You must obtain following JAR file ConfigManagerProxy.jar from an IBM Message Broker Version 7.0 runtime installation.
ConfigManagerProxy.jar is using to connect to remote brokers and obtain information about their configuration.

For example, on platform Windows the ConfigManagerProxy.jar file is located in directory:
> C:\Program Files\IBM\MQSI\7.0\classes\

###IBM MQ classes for Java
You must obtain following required IBM MQ JAR files from an IBM MQ Series 7.0 installation:
* com.ibm.mq.jar
* com.ibm.mq.jmqi.jar

For example, on platform Windows these files may be located in directory:

> C:\Program Files (x86)\IBM\WebSphere MQ\java\lib\

###IBM JRE 6.0
You need a IBM JRE 6.0 or above.

##CONFIGURATION
In WMBDiff.bat you need change paths to jar files (com.ibm.mq.jar, com.ibm.mq.jmqi.jar, ConfigManagerProxy.jar) and
location of IBM JRE.

##EXECUTION
```sh
$ WMBDiff.bat
```
Then click on the Connect button and set connection parameters for remote message brokers whose contents you want to compare.

For export result of comparison into Excel file click on Export button.

##LICENSE
The WMBDiff tool is open-sourced software licensed under the Apache License, Version 2.0
