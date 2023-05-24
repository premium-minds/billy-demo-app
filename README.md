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
mvn -B dependency:copy-dependencies

# using classpath
java -cp "target/DummyApp-0.0.1-SNAPSHOT.jar:target/dependency/*" dummyApp.app.App

# using modulepath and classpath
java -p "target/DummyApp-0.0.1-SNAPSHOT.jar:target/dependency/billy-core-8.0.1.jar:target/dependency/billy-core-jpa-8.0.1.jar:target/dependency/billy-gin-8.0.1.jar:target/dependency/billy-spain-8.0.1.jar:target/dependency/billy-portugal-8.0.1.jar:target/dependency/guice-5.1.0.jar:target/dependency/guice-persist-5.1.0.jar" \
    -cp "target/dependency/*" \
    -m DummyApp/dummyApp.app.App
```

## Alternatively execute directly from maven
```
mvn exec:java -Dexec.mainClass="dummyApp.app.App" 
```

## Single Invoice Demonstration with SAFT and PDF export
 ```
java -jar target/DummyApp.jar demo portugal
 ```
or
```
mvn exec:java -Dexec.mainClass="dummyApp.app.App" -Dexec.args="demo"
```
will create a _saft.xml_, an _invoice*.pdf_ and a _creditNote*.pdf_
