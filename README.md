# stata-ux
A spring boot interface to the Stata statistics software

This is a proof-of-concept at the moment, adding a more touch friendly 
interface to Stata. 

Compile and run as any other spring boot application, eg with maven:

```
mvn spring-boot:run
```

You will have to have Stata with the interact.ado installed (and preferably 
running the interact command):

```
net from https://dl.dropboxusercontent.com/u/12198759/ado/
net install interact
```

## License
Stata-ux is Open Source software released under the
[http://www.apache.org/licenses/LICENSE-2.0.html|Apache 2.0 license].