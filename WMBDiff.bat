set IBM_JRE6_PATH="C:\Program Files (x86)\IBM\WebSphere MQ\java\jre\bin\java"
rem IBM_JRE6_PATH="C:\Program Files\IBM\MQSI\7.0\jre16\bin\java"
set "MQ_JAVA_LIB_PATH=C:\Program Files (x86)\IBM\WebSphere MQ\java\lib"
set "MQSI_CMP_JAR=C:\Program Files\IBM\MQSI\7.0\classes\ConfigManagerProxy.jar"
set CLASSPATH=%MQ_JAVA_LIB_PATH%\com.ibm.mq.jmqi.jar;%MQ_JAVA_LIB_PATH%\com.ibm.mq.jar
set CLASSPATH=%MQSI_CMP_JAR%;%CLASSPATH%
set CLASSPATH=.;./target/wmbdiff-1.0.0-jar-with-dependencies.jar;%CLASSPATH%

%IBM_JRE6_PATH% -cp "%CLASSPATH%" ru.wmbdiff.WMBDiff


