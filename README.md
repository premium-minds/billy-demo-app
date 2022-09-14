billy-demo-app
==============

# How to use

## Create database
```
createdb -h localhost -U postgres billy-demo
```

## Compile jar file
```
mvn package 
```

## Execute jar file
```
java -jar target/DummyApp.jar
```

## Alternatively execute directly from maven
```
mvn exec:java -Dexec.mainClass="dummyApp.app.App" 
```

## Single Invoice Demonstration with SAFT and PDF export
 ```
java -jar target/DummyApp.jar demo Portugal
 ```
or
```
mvn exec:java -Dexec.mainClass="dummyApp.app.App" -Dexec.args="demo"
```
will create a _saft.xml_, a _invoice*pdf_ and a _creditNote*pdf_
