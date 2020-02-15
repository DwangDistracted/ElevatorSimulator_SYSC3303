ElevatorSimulator_SYSC3303
----------------------------------
This project will aim to implement a multithreaded simulation of an elevator system.

To Run
----------------------------------
ElevatorSim is an eclipse project. 

To Run, import the project into Eclipse Java and run from elevatorsim.Main.

The Unit Tests for the project are implemented in JUnit5. Run them by right clicking on the project and selecting Run As -> JUnit Test.

Authors - Group 1
----------------------------------
* Rahul Anilkumar (101038785) - Worked on Unit Tests, Requests, Documentation
* David Wang (101032271) - Worked on UDP Server, Scheduler State Machine, Sequence Diagram
* Michael Patsula (101043663) - Worked on Floor System, Requests
* Thomas Leung (101043255) - Worked on Unit Tests, UML Diagram, Requests
* Trevor Bivi (101045460) - Worked on Elevator System, Elevator State Machine, Scheduler Logic.


Packages
----------------------------------
elevatorsim - contains the Main Program Entry Point for the Elevator System

elevatorsim.common.requests - contains the Request POJOs that are used in the system. These POJOs contain information used by all the subsystems to communicate to one another

elevatorsim.constants - contains constants and enumerations used in the various subsystems. Also has a class that will create the DatagramPackets used for UDP communications between subsystems.

elevatorsim.elevator - contains the logic and model for the elevator subsystem

elevatorsim.floor - contains the logic and model for the floor subsystem

elevatorsim.scheduler - contains the logic for the scheduler subsystem

elevatorsim.server - contains the logic for a UDP Server capable of listening and sending. Each Subsystem will implement their own server logic based off the interfaces and abstract classes in this package.

elevatorsim.tests - contains the unit tests for ElevatorSim

elevatorsim.util - contains utility classes used in the systems to parse files, datagram packets, and requests

UML Class and Sequence Diagrams
----------------------------------
The UML diagrams can be found in the Diagrams/Iteration 2 directory

Sequence_Diagram.PNG - The sequence diagram depicting the interactions between threads in all three subsystems

Class_Diagram.PNG - The class diagram depicting the structure of the ElevatorSim project

State_Machine_Elevator.PNG - The class diagram depicting the state and state transitions for the Elevator

State_Machine_Scheduler.PNG - The class diagram depicting the state and state transitions for the Scheduler