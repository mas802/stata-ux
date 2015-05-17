# stata-ux
A spring boot interface to the Stata statistics software

This is a proof-of-concept at the moment, adding a more touch friendly interface to Stata. 

Compile and run as any other spring boot application, eg with maven:

```
mvn spring-boot:ru
```

You will have to have Stata with the interact.ado installed (and preferably running the interact command):

```
net from https://dl.dropboxusercontent.com/u/12198759/ado/
net install interact
```
