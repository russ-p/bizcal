bizcal readme

author: martin.heinemann(at)tudor.lu
----------------------------
1) compile own bizcal lib
2) build calendar demo application


1) compile your own bizcal lib:
     ----------------------------------
     run ant tasks in the build.xml
     1. getlib
     2. compile
     3. jar
     
     there will be an "all" soon.
     
     
 2) build calendar demo application:
    -----------------------------------------
    the file build_calendar.xml builds a jar of the demo application that uses the bizcal library
    the main is located in 
    lu.tudor.santec.bizcal.CalendarDemo
    it uses some features of the library, but not all. It is just a demonstration.
    you can use it for your own applications. documentation follows....