![](https://s17.directupload.net/images/190317/g4777bij.png)

# ReformCloud 2 [![Discord](https://img.shields.io/discord/499666347337449472.svg?color=7289DA&label=discord)](https://discord.gg/uskXdVZ) [![CodeFactor](https://www.codefactor.io/repository/github/derklaro/reformcloud2/badge?s=1093a7711bb179b3fb6e48ffbb3e4c1315e5aada)](https://www.codefactor.io/repository/github/derklaro/reformcloud2)
ReformCloud 2 is a server management tool to control every network you are running. 
The cloud system is build to handle all sizes of networks such as network with 80 players or networks with 1000 players.

The cloud system supports java and bedrock edition.

### Currently support java versions:
| Version Name                        | Version ID      |
|-------------------------------------|-----------------|
| Bungeecord (Waterfall, Hexacord...) | 1.7.10 - 1.14.4 |
| Velocity                            | 1.7.10 - 1.14.4 |
| Waterdog                            | 1.8 - 1.14.4    |
| Spigot (Paper, Taco, Torch...)      | 1.7.10 - 1.14.4 |
| Sponge                              | 1.7.10 - 1.12.2 |
| Akarin                              | 1.12.2          |
| GlowStone                           | 1.7.9 - 1.12.2  |

### Currently supported bedrock versions:
| Version Name  |                     
|---------------|
| NukkitX       |
| WaterDog      |

**Give a try and leave a review!**

# Run ReformCloud2 the first time
## System requirements

 - 1 GB Memory
 - 1 CPU Core
 - Java 8, 9, **~~10~~ (Java 10 is not supported anymore)** or 11
 
## Startup
The startup is very easy. Just start the cloud system using the following command:
```
java -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:CompileThreshold=100 -Xmx512m -Xms256m -jar runner.jar
```

The runner is now going to download the needed libraries, so **make sure you have an internet connection** 
during the first startup! Please answer all questions which the runner is going to ask you!

After the setup: **start using reformcloud2!**

# Developer Information
## Want to contribute?
[**Open Issue**](https://github.com/derklaro/reformcloud2/issues/new)

[**Fork Project**](https://github.com/derklaro/reformcloud2/fork)

## Open Source Libraries
| Library                                                             | Author                                               | License                                                                                    |
|---------------------------------------------------------------------|------------------------------------------------------|--------------------------------------------------------------------------------------------|
| [Netty](https://github.com/netty/netty/)                            | [The Netty Project](https://github.com/netty)        | [Apache Licence 2.0](https://github.com/netty/netty/blob/4.1/LICENSE.txt)                  |
| [Gson](https://github.com/google/gson/)                             | [Google](https://github.com/google/)                 | [Apache License 2.0](https://github.com/google/gson/blob/master/LICENSE)                   |
| [JLine 2](https://github.com/jline/jline2/)                         | [JLine](https://github.com/jline/)                   | [The 2-Clause BSD License](https://github.com/jline/jline2/blob/master/LICENSE.txt)        |
| [Reflections](https://github.com/ronmamo/reflections/)              | [ronmamo](https://github.com/ronmamo/)               | No Licence                                                                                 |
| [MongoDB Java Driver](https://github.com/mongodb/mongo-java-driver) | [MongoDB](https://github.com/mongodb/)               | [Apache License 2.0](https://github.com/mongodb/mongo-java-driver/blob/master/LICENSE.txt) |
| [MySQL connector Java](https://github.com/mysql/mysql-connector-j)  | [MySQL](https://github.com/mysql/)                   | [Self written Licence](https://github.com/mysql/mysql-connector-j/blob/release/8.0/LICENSE)|
| [Snake Yml](https://github.com/lsst-camera-dh/snakeyaml/)           | [lsst-camera-dh](https://github.com/lsst-camera-dh/) | [Apache License 2.0](https://github.com/lsst-camera-dh/snakeyaml/blob/master/LICENSE.txt)  |

## Build this project
```
git clone https://github.com/derklaro/reformcloud2.git
cd reformcloud2/
mvn clean package
```