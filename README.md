billy-demo-app
==============

# How to use

## create database
```
createdb -h localhost -U postgres billy-demo
```

## compile jar file
```
mvn package 
```

## execute jar file
```
java -jar target/DummyApp-0.0.1-SNAPSHOT.jar
```

## alternatively execute directly from maven
```
mvn exec:java -Dexec.mainClass="dummyApp.app.App" 
```