# capstone-apache-storm

Download repo

> git clone https://github.com/abhinavralhan/capstone-apache-storm/

To create a jar with Dependencies for storm to execute :

> mvn clean install

>  mvn assembly:assembly -DdescriptorId=jar-with-dependencies

>  storm jar target/TwitterWork-0.0.1-SNAPSHOT-jar-with-dependencies.jar twtst.TwitterTopology

> ![Demo](https://twitter.com/i/status/994479691779067904)
