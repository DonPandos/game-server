# game-server

To run this project you just need to run target/TestGameServer-0.0.1-SNAPSHOT-jar-with-dependencies.jar. To do it run the following command from terminal in the src folder:
*java -jar TestGameServer-0.0.1-SNAPSHOT-jar-with-dependencies.jar*  

You have to have JDK installed on your machine.  
Server is running on 8080 port, to access endpoint you should make HTTP request against localhost:8080  

There are 2 endpoints in the project: /api/game/rooms and /api/game/start  

GET /api/game/rooms - returns a list of machines (list of mac adresses)  
POST /api/game/start - starts a new game. If the game was successfully started - returns true, otherwise false. In this request you need to pass request body. Example of request body:
```
{
    "roomMac":"86:9b:16:91:cb:7a",
    "timeout": 60,
    "result": 0,
    "graspPower": 0,
    "topPower": 0,
    "movePower": 0,
    "maxPower": 0,
    "topHeight": 0,
    "lineLength": 0,
    "xSpeed": 0,
    "ySpeed": 0,
    "zSpeed": 0
}
```

Mocked Machine is created for demonstration purposes. To test game creation you need to run SimpleServer app from this repository: https://github.com/xuebaodev/wawaji. In case this server is run - you will get 'true' response. If server is not run - you will get false. If you try to end request with mac that doesn't exist in collection - you will get an error.
