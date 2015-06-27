set CLASSPATH="C:\Program Files (x86)\IBM\WebSphere MQ\java\lib\com.ibm.mq.jmqi.jar;C:\Program Files (x86)\IBM\WebSphere MQ\java\lib\com.ibm.mq.jar"
set CLASSPATH="C:\Program Files\IBM\MQSI\7.0\classes\ConfigManagerProxy.jar";%CLASSPATH%
set CLASSPATH=./target/wmbdiff-1.0.0-jar-with-dependencies.jar;%CLASSPATH%

rem "C:\Program Files\IBM\MQSI\7.0\jre16\bin\java" -cp %CLASSPATH% ru.wmbdiff.WMBDiff
"C:\Program Files (x86)\IBM\WebSphere MQ\java\jre\bin\java" -cp %CLASSPATH% ru.wmbdiff.WMBDiff


