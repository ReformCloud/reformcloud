![](https://s17.directupload.net/images/190317/g4777bij.png)

# ReformCloud 2 [![Discord](https://img.shields.io/discord/499666347337449472.svg?color=7289DA&label=discord)](https://discord.gg/uskXdVZ) [![CodeFactor](https://www.codefactor.io/repository/github/ReformCloud/reformcloud2/badge?s=1093a7711bb179b3fb6e48ffbb3e4c1315e5aada)](https://www.codefactor.io/repository/github/ReformCloud/reformcloud2) [![Build Status](https://travis-ci.com/ReformCloud/reformcloud2.svg?token=DsMrJCyqH6BCtUu5ax94&branch=stable)](https://travis-ci.com/ReformCloud/reformcloud2) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/systems.reformcloud.reformcloud2/reformcloud2-executor-api/badge.svg)](https://mvnrepository.com/artifact/systems.reformcloud.reformcloud2/reformcloud2-executor-api)

ReformCloud is a cloud system programmed and optimized for all sizes of networks. 
The cloud system provides a huge api to access all internal functions, group, processes etc. It's made
for **synchronized as well as asynchronous programming** and is integrated into the main executor **Node**
 but as well into the apis for **Velocity / BungeeCord / Spigot / Sponge / Nukkit etc**.
So the development work to integrate something like a private server system into the cloud is not that much work 
and get be made easily by every developer who spends 5 minutes to read **the documentation of the api**.
ReformCloud is basically just an application to start and manage the minecraft servers and proxies. 
If you need more features like ingame commands or a permission system reformcloud also provides these 
things as an external module. ReformCloud is build to flexible manage your **minecraft servers and proxies
which are internally called "processes"**. The processes can get started bases on a **group** which are
**not sorted by the version**. A group can have **multiple** templates and **all of them can have other versions**.
If you need **more than one template** for a group to start or need a **path inclusion** you can easily create
them in the group file and the cloud will copy the paths or templates at the next startup of a process.

### Currently supported minecraft-java-edition versions:
| Version Name                        | Version ID                   | Recommended Java Version     |
|-------------------------------------|------------------------------|------------------------------|
| Bungeecord (Waterfall, Hexacord...) | 1.8 - 1.16.1                 | Java 11                      |
| Velocity                            | 1.8 - 1.16.1                 | Java 11                      |
| Waterdog                            | 1.8 - 1.15.2                 | Java 8                       | 
| Spigot & Paper                      | 1.8 - 1.16.1                 | <1.12 Java 8 / >1.12 Java 11 |
| Torch                               | 1.8.8, 1.9.4, 1.12.2         | Java 8                       |
| Taco                                | 1.8.8, 1.11.2, 1.12.2        | Java 8                       |
| Hose                                | 1.8.8, 1.9.4, 1.10.2, 1.11.2 | Java 8                       |
| Tuinity                             | 1.15.2                       | Java 11                      |
| Glowstone                           | 1.10.2, 1.12.2               | Java 8                       |
| Sponge (Forge)                      | 1.10.2 - 1.12.2 (V 7.2.2)    | Java 8                       |
| Sponge (Vanilla)                    | 1.11.2, 1.12.2 (V 7.2.2)     | Java 8                       |
| Akarin                              | 1.12.2                       | Java 8                       |

### Currently supported minecraft-pocket-edition versions:
| Version Name  | Recommended Java Version |                
|---------------|--------------------------|
| NukkitX       | Java 8                   |
| WaterDog      | Java 8                   |

# Run ReformCloud2 the first time
## System requirements

 - 1 GB Memory
 - 1 CPU Core
 - A little bit of space on the hard disk

## Supported Java Versions
 
 - Java 7 and lower  : **NOT SUPPORTED**
 - Java 8 and higher : **SUPPORTED**
 - Java 11           : **RECOMMENDED**
 
## Startup

You can download the latest release version from the [CI](https://ci.reformcloud.systems/job/reformcloud/job/reformcloud2/job/stable/lastStableBuild/artifact/reformcloud2-runner/target/runner.jar)
or as a zip file from the [download server](https://dl.reformcloud.systems/latest/ReformCloud2-latest.zip).

Just save the file named as `runner.jar` in the folder you want to run the cloud in and start the runner using:
```
java -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:CompileThreshold=100 -Xmx512m -Xms256m -jar runner.jar
```

The runner is now going to download the needed libraries for the runtime, so **make sure you have an internet connection** 
during the first startup!

## Node

The node will ask six questions:
 1) Firstly you have to provide the name of the new node which ***is not used already in the cluster***
 2) After this the node needs the host on which the servers and proxies should get bound to
 3) Then the node asks for the ***internal*** network port (default `1809`)
 4) And then the node asks for the ***internal*** web port (default `2008`)
 5) Then you have to provide the connection key for other nodes. I you want to setup a cluster and already
 have a node in this cluster copy the key from the other node located in `NODE_DIR/reformcloud/files/.connection/connection.json`.
 If you want to generate a random connection key type `gen`

# Found a bug or have a proposal?
Please
[**open an issue**](https://github.com/ReformCloud/reformcloud2/issues/new)
and ***describe the bug/proposal as detailed as possible*** and **look into your email if we have replied to your issue
and answer upcoming questions**.

# Support our work
If you like reformcloud and want to support our work you can **star** :star2: the project, leave a (positive)
review on [SpigotMC](https://www.spigotmc.org/resources/reformcloud-v2.63950/) or join our 
[Discord](https://discord.gg/uskXdVZ).

But the best support for our work is very simple: ***use the cloud system!***

# Developer Information
## Want to contribute?
You can simply 
[**fork the project**](https://github.com/ReformCloud/reformcloud2/fork)
make the changes you want to add and create a **pull request**. If your pull request got approved and merged
you will get added to the list of contributors.

## Open Source Libraries
| Library                                                             | Author                                                 | License                                                                                                       |
|---------------------------------------------------------------------|--------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| [Netty](https://github.com/netty/netty/)                            | [The Netty Project](https://github.com/netty)          | [Apache Licence 2.0](https://github.com/netty/netty/blob/4.1/LICENSE.txt)                                     |
| [Gson](https://github.com/google/gson/)                             | [Google](https://github.com/google/)                   | [Apache License 2.0](https://github.com/google/gson/blob/master/LICENSE)                                      |
| [JLine 3](https://github.com/jline/jline3/)                         | [JLine](https://github.com/jline/)                     | [The 3-Clause BSD License](https://github.com/jline/jline3/blob/master/LICENSE.txt)                           |
| [Reflections](https://github.com/ronmamo/reflections/)              | [ronmamo](https://github.com/ronmamo/)                 | [Do What The F*ck You Want To Public License](https://github.com/ronmamo/reflections/blob/master/COPYING.txt) |
| [MongoDB Java Driver](https://github.com/mongodb/mongo-java-driver) | [MongoDB](https://github.com/mongodb/)                 | [Apache License 2.0](https://github.com/mongodb/mongo-java-driver/blob/master/LICENSE.txt)                    |
| [MySQL connector Java](https://github.com/mysql/mysql-connector-j)  | [MySQL](https://github.com/mysql/)                     | [Self written Licence](https://github.com/mysql/mysql-connector-j/blob/release/8.0/LICENSE)                   |
| [H2](https://github.com/h2database/h2database/)                     | [h2database](https://github.com/h2database/)           | [Dual Licenced (MPL 2.0/EPL 1.0)](https://github.com/h2database/h2database/blob/master/LICENSE.txt)           |
| [RethinkDB](https://github.com/rethinkdb/rethinkdb)                 | [rethinkdb](https://github.com/rethinkdb)              | [Apache License 2.0](https://github.com/rethinkdb/rethinkdb/blob/next/LICENSE)                                |
| [Maven-Wrapper](https://github.com/takari/maven-wrapper)            | [takari](https://github.com/takari/)                   | [Apache License 2.0](https://github.com/takari/maven-wrapper/blob/master/LICENSE.txt)                         |
| [HikariCP](https://github.com/brettwooldridge/HikariCP)             | [brettwooldridge](https://github.com/brettwooldridge/) | [Apache License 2.0](https://github.com/brettwooldridge/HikariCP/blob/dev/LICENSE)                            |

## Build this project
```
git clone https://github.com/ReformCloud/reformcloud2.git
cd reformcloud2/
mvn clean package
```

## Maven
**Repository:**
ReformCloud2 is available in the [central repository](https://search.maven.org/search?q=reformcloud).
Because of this you don't need to provide any repository.

**Dependency:**
```xml
    <dependency>
        <groupId>systems.reformcloud.reformcloud2</groupId>
        <!-- replace with needed artifact for example 'reformcloud2-executor' or 'reformcloud2-default-application-permissions' -->
        <artifactId>reformcloud2-executor-api</artifactId>
        <version>2.10.1-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
```

## Contributors
***Thanks to all these wonderful people***

<table>
    <tr>
        <td align="center">
            <img src="https://avatars3.githubusercontent.com/u/40468651?s=460&v=4" width="100px;" alt=""/>
            <br />
                <sub><b>Pasqual K. (derklaro)</b></sub>
                <br />
                <sub><b>Project owner</b></sub>
            <br/>
        </td>
        <td align="center">
        <img src="https://avatars0.githubusercontent.com/u/51173477?s=400&v=4" width="100px;" alt=""/>
            <br />
                <sub><b>Lukas B. (Lvkas_)</b></sub>
                <br />
                <sub><b>Project developer</b></sub>
            <br/>
        </td>
        <td align="center">
        <img src="https://avatars0.githubusercontent.com/u/40271530?s=400&v=4" width="100px;" alt=""/>
            <br />
                <sub><b>JanDragon</b></sub>
                <br />
                <sub><b>Contributor</b></sub>
            <br/>
        </td>
    </tr>
</table>
