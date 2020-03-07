# ElevatorSimulator_SYSC3303
This project will aim to implement a multithreaded simulation of an elevator system

## To Run
ElevatorSim is an eclipse project. 

To Run, import the project into Eclipse Java and run the programs in the following order.
1. Run the SchedulerProcess.java
2. Run the ElevatorProcess.java
3. Run the FloorSystemProcess.java

For running on three different computers Please do the following:
1. Import the project into Eclipse on each computer 
2. For each computer go into the elevatorsim.constants package and edit the SCHEDULER_IP constant value in the NetworkConstants.java file.
   The value should be changed to the ip address of the computer which you will run the main for the SchedulerProcess.java class.
3. After changing this value on each computer run the programs in the following order:
	1. Run the SchedulerProcess.java on computer 1
	2. Run the ElevatorProcess.java on computer 2
	3. Run the FloorSystemProcess.java on computer 3

The Unit Tests for the project are implemented in JUnit5. Run them by right clicking on the project and selecting Run As -> JUnit Test.

## Authors
* Rahul Anilkumar (101038785) - Worked on Unit Tests, Requests, Documentation, Message Classes, File Parser, Scheduling
* David Wang (101032271) - Worked on UDP Server, Scheduler State Machine, Scheduler, Sequence Diagram, Documentation, Startup Logic
* Michael Patsula (101043663) - Worked on Floor System, Requests, Floor UDP Requests, Documentation
* Thomas Leung (101043255) - Worked on Unit Tests, UML Diagram, Requests, Scheduler Class
* Trevor Bivi (101045460) - Worked on Elevator System, Elevator State Machine, Scheduler Logic, Unit Test

## Packages
elevatorsim - contains the Main Program Entry Point for the Elevator System

elevatorsim.common.requests - contains the Request POJOs that are used in the system. These POJOs contain information used by all the subsystems to communicate to one another

elevatorsim.constants - contains constants and enumerations used in the various subsystems. Also has a class that will create the DatagramPackets used for UDP communications between subsystems.

elevatorsim.elevator - contains the logic and model for the elevator subsystem

elevatorsim.floor - contains the logic and model for the floor subsystem

elevatorsim.scheduler - contains the logic for the scheduler subsystem

elevatorsim.server - contains the logic for a UDP Server capable of listening and sending. Each Subsystem will implement their own server logic based off the interfaces and abstract classes in this package.

elevatorsim.tests - contains the unit tests for ElevatorSim

elevatorsim.util - contains utility classes used in the systems to parse files, datagram packets, and requests

## UML Class and Sequence Diagrams
The UML diagrams can be found in the Diagrams/Iteration 3 directory

Class_Diagram.PNG - The class diagram depicting the structure of the ElevatorSim project

Sequence and State Machine Diagrams are unchanged from Iteration 2 and can be viewed in the Diagrams/Iteration 2 directory

Iteration 3 Concurrency Changes from Iteration 2.pdf - Describes the changes to scheduler re: concurrency between iteration 2 and 3