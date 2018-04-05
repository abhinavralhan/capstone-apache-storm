# capstone-apache-storm

To create a jar with Dependencies for storm to execute :

>  mvn assembly:assembly -DdescriptorId=jar-with-dependencies
>  storm jar target/TwitterWork-0.0.1-SNAPSHOT-jar-with-dependencies.jar twtst.TwitterTopology
