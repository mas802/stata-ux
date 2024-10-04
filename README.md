# stata-ux
A spring boot interface to the Stata statistics software

This is a proof-of-concept at the moment, adding a more touch friendly 
interface to Stata.

(!) currently pom.xml is disabled due to security issues, proceed with caution

## Run

The released jar file is a self contained spring:boot application. For full 
operation it of course requires a Stata installation with the accompaning 
interact command installed.

```
net from https://raw.githubusercontent.com/mas802/stata-ux/master/ado/
net install interact
```

The interface can be reached under: 

```
http://localhost:8080/
```

You need to supply a password for the user  ```user``` which can be found in 
the log files or set in ```application.properties```.

You can enter most stata commands (maybe with the exception of help/man). 
Stata-ux will offer suggestions based on the commands in the history and the 
variables in the opened data file as well as to complete paths and file names.

There are a number of special commands that start with a /:

```/edit "<do file>"``` opens a ace based editor with this do file.

```/help term``` opens a new browser window redirected to Stata's online help 
with the term provided

```/graph [name]``` opens a new browsers window with the current (or specified)
graph in png format

```/est``` opens a new browser window with an standard estout 
(needs to be installed) html of the latest estimation(s)

## Develop

Compile and run as any other spring boot application, eg with maven:

```
mvn spring-boot:run
```

I use eclipse and have commited the nessesary files to quickly load the project.


## License
Stata-ux is Open Source software released under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
