# SLR201_PhilosopherProject
The philosopher project of the course SLR201 of Telecom. It aims to simulate the philosopher problem where philosophers think and take forks to eat.

The project contains two parts: the local one and the one via socket.

## Local version

To run the local version, you can simply run these two commands:
```
$ cd local
$ java philosophe.java
```
The results will be printed on the screen. You can see the sequence of philosophers' actions, also their starvation time and at the end of the output their total eating times.

Moreover, if you want the result to be saved in a file, you can change the last command line with this:
```
$ java philosophe.java 1
```

## Socket version

To run the socket version, you should first run this line:
```
$ cd socket
```
Then, open one terminal and run this line:
```
$ java serverPhilo.java
```
Open another terminal and run this line:
```
$ java clientPhilo.java
```
And you'll get the result.

Of course, there are two optional arguments in `clientPhilo.java`. The first one is the address of the server while the second one is whether you want to print the result in a file.
For the first argument, the default value is `localhost`. And the default value of the second argument is `0` which means that the result is shown in powershell.
For example, if you deploy the server code on lame21.enst.fr and you want to have the result saved in a file, then run this line:
```
$ java clientPhilo.java lame21.enst.fr 1
```
