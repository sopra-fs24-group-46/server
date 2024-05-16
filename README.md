<h1 align="center">
<br>
GWÜSST-Server
<br>
</h1>


## Introduction
How good do you and your peers know Switzerland's most famous landscapes? Would you be able to show your knowledge and win against your friends? Gwüsst is an exciting game that allows players to compete against each other in guessing a certain location from Switzerland's most famous landscapes, such as mountains or hills. This is the back-end component of our project. The front-end component can be found [here](https://github.com/sopra-fs24-group-46/client).

## Technologies 
The back-end of this project is written in Java, and utilizes the Spring Boot framework. Persistence (TODO, how is it achieved? with JPA?). In addition, the communication between the server and client is achieved through REST.

## High Level Components
The Gamecontroller manages the game with the REST API methods. Moreover, Game is the main component that manages the gameflow in the project. In addition, the logic of the whole game is given by the GameEngine component.

## Launch & Deployment

To assist a new developer joining our team, we've outlined the essential steps to get started with our application. Here are the commands needed to build, run, and test the project locally:

### Build

To build the project, use the following command:

```bash
./gradlew build
```

This command will compile the source code, run the tests needed, and package the application.

### Run

To run the project locally, run the following command:


```bash
./gradlew bootRun
```

This command will launch the application on your local machine, enabling you to interact with it. To confirm the server is running, visit localhost:8080 in your browser.


### Test

To run the tests for this project, use this command:


```bash
./gradlew test
```

This command will execute the test suite and provide feedback on the application's reliability and functionality.

## Authors and Acknowledgment


SoPra Group 46 2024 consists of [Serafin Schoch](https://github.com/S3r4f1n), [Dominic Häfliger](https://github.com/Dhaefli),
[Rolando Villaseñor](https://github.com/RoVi80), [Daniel Odhiambo](https://github.com/DarthDanAmesh), and [Leandra Häfeli](https://github.com/Laendi22). 

We would like to thank our teaching assistant [Cedric von Rauscher](https://github.com/cedric-vr) for his support throughout the semester. We also like to thank GeoAdmin for providing its API. This semester has been both intriguing and challenging, providing us with valuable opportunities for growth. We've acquired extensive knowledge, not only in coding but also in teamwork and project execution. We would like to as well thank the whole SoPra 2024 team for the effort, we appreciate the opportunity and the experience gained from this project.
## License

GNU GPLv3
