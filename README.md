# cluster-dt066a

This does only work on different IP addresses, different machines

There's no need to setup ip addresses, The master program finds all active slaves on the same network. The slaves and master has a global udp communication link on port 9999 and then creates a separate udp link on individual ports. These private communication links are ran by different threads from Master node's perspective.

## Operation Order

- Import file (Master)
- Split (Master)
- Map (Slave)
- Reverse/Shuffle (Slave)
- Reduce (Slave) - This step is implemented a little bit wierd, since I basiacally have the answer already in the reverse state

![image](https://user-images.githubusercontent.com/43444902/69354735-e4ab4c00-0c80-11ea-85d6-e1e05f3fd083.png)

## LOGS

Every operation the master end is logged into different files, could be turned off.

## BUILDING

I used Apache ant to build the jars in console (on the RPi:s), since the rpi had lower compile version than my own computer.

### SLAVE BUILD AND RUN

in build.xml make sure that first line is
`<project name="Slave" default="main" basedir=".">`
and
`<property name="main-class" value="src.Main"></property>`

`java -jar Slave.jar <should log, leave this parameter if you dont want it to log>`

### MASTER BUILD AND RUN

in build.xml make sure that first line is
`<project name="Master" default="main" basedir=".">`
and
`<property name="main-class" value="main.Main"></property>`

to run the project make sure that Data folder is created with your testfiles, also make sure to create a folder LogFiles. THen run it like

`java -jar Master.jar <filepath> <lines per split> <message window> <max amount of reduce messages> <should make file, leave this if you want it to create log files>`

// pi perspective
![image](https://user-images.githubusercontent.com/43444902/69355146-9a769a80-0c81-11ea-860a-8fae77390435.png)
