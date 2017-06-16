This desktop chat app is for learning purpose only.

It was developed to apply TCP network communication terminology while learning it in a network course at Islamic University of Gaza,
under the supervision of associate professor Dr. Aiman Abu Samra.

The App was implemented in java and have the following features: 

1-	Multiple simultaneous clients (Multithread), and each client have a unique ID.

2-	Any client can get a list of all other connected clients from the server in any time.

3-	Creating group tracked by the server, any client can see the list of all available group and enroll to them.

4-	Any client can see the list of all users enrolled to a specific group.

5-	Allow Single message between users, Broadcast message and Group message.

6-	The list of online users, available groups and enrolled users in specific group are always up to date, without user engagement.

All of these feature is work perfectly.

There was a very low load in evaluating the UI of the project from the supervisor (Some of student commit the commands using Print Console), that's why we didn't work hard on it,
but we've made a pretty cool one using Scene Builder.

haven't it ^"

![Alt text](https://drive.google.com/open?id=0B6-hfcTZET2ZSmtsV0lZRVV3RUE?raw=true)



The main purpose of the project is to ensure that you've understand the main concept of TCP connecting in networking,
so our app is simple and need some improvement, for example:

1- We should provide a separate screen for logging, allowing users to enter their nick name, profile picture and so on.

This is an important step if you want to develop a chat app for non-learning purpose, and prevent unique user name problem.

2- For unique group name problem, we have a solution for it, but we didn't provide it in the code, if we decided to upgrade the code, we will include it.

The project has included comments that describe most of steps taken while developing it, also we didn't
remove the trace code (PrintStream in the console) founded in many methods,
they're helpful to understand the code and debug it.feel free to delete them!

Feel free to upgrade the code, and incorporate with us!

This project was done with my dear classmate Momen Zaqout (momenzaqout@gmail.com).

Important Note: 

You should run the server class before running client class.

To save any change in the code, you should run whole project and not the server code (ignore the exception occurred because the server code is not running).

