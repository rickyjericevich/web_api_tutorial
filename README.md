# Web API Tutorial

<!-- ## Table of Contents -->

## Introduction
This repo contains the instructions and code that you will use to learnt the basics of creating and deploying a simple REST API.
It has the following structure:
1. rest_api_server folder
2. web_ui folder
3. rest_api_client folder

We will go through each folder in this order, and in each folder we will add the code required to create and use simple REST API.

Here are a few resources that you may need to understand the concepts in this tutotial:
- Linux terminal basics: [video](https://youtu.be/IVquJh3DXUA?si=XDidkpJCJ0F6G5LV)
- REST API basics: [video](https://youtu.be/-mN3VyJuCjM?si=SLyE8mTkFxFJFKR-)

### rest_api_server folder
This folder is a java project (that uses gradle and spring boot) that we will use to create a simple REST API server. We will add the code required to make the server work.

### web_ui folder
This folder contains a simple HTML web page that we will use to interact with our REST API server. All the code is already added (except for a few variables that we need to change), so we will just run it and see how it works.

### rest_api_client folder
This folder contains a java project that we will use to interact with our REST API server. We will add the code required to make the app work.

## 1. Update the REST API Server

### 1.1 Pull the code from github onto your computer

git clone https://github.com/rickyjericevich/web_api_tutorial.git

You may need to [install git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) first on your pc.

### 1.2 Add the code
At a minimum for this tutorial, you have to implement the following:
- An ArrayList of Message objects to store all messages in memory
- A GET endpoint that returns all messages
- A POST endpoint that adds a new message to the ArrayList

Use whatever resources you need to help you. I found ChatGPT to be the easiest, although it skips over some deeper concepts. Enter this prompt into ChatGPT and see what it returns:
```
I am implementing a rest API in java spring boot. The API will allow a user to POST a message of the form {"username": "the user's name", "message": "the user's message"}. It will also allow a user to get all messages sent by all users. The api will use port 42069.

Please show me how to implement the following:
- An ArrayList of Message objects to store all messages in memory
- A GET endpoint that returns all messages
- A POST endpoint that adds a new message to the ArrayList
```

### 1.3 Test it locally
To test it locally, you need to run the code on your computer, and then send requests to it from your computer. Make sure java 17 is installed and that your terminal is in the directory where the gradlew file is located

Build the project to a jar file:
./gradlew build

If the build is unsuccessfull, read the error output to see what went wrong. It's likely that java isn't configured on your computer properly, or there are mistakes in the code.'

Once built, run the jar file:

java -jar build/libs/rest_api_tutorial-0.0.1.jar

If it starts successfully, it will be running in the terminal. You can always stop it by pressing ctrl+c, and re-run using the same command again.

Now that the API server is running, you can interact with. One way to do this is is to open a browser and type in the following url:
http://localhost:42069/messages/test

The browser essentially sends a GET request to the API server using this url, and the API server responds with the list of messages. Check your API server's logs to make sure that the browser request was received. You wont see any message data in the browser as we have not yet sent any POST requests to the API server to create messages. You can use the browser to send POST requests, but it's a bit more complicated.

Another way to test is to open the web_ui/index.html file in the browser. Once open, also open the console logs (ctrl+shift+i) to make sure that it is correctly getting the messages from the API server. You can also use it to send POST requests to the API server to create messages.

Make sure you thoroughly test your API server locally before moving on to the next step.

## 2. Deploy the REST API Server code to a server
We now want to run our API server code on this server so that it is accessible to the world. 

### 2.1 Create a server in the cloud using Digital Ocean
Sign up for Digital Ocean
Verify your email address

Create a new doplet
- choose the san francisco region
- Choose the Ubuntu image
- Under CPU options select the Regular option and the $4/month option
- Create a password that you will use to log in to the server via the terminal
- change the hostname of your droplet to web-api-tutorial-yourname

When the droplet is created, copy its IP address. From here on, we will refer to your server IP address as 
```
ip.ad.dre.ss
```

From the terminal, ping your server to make sure it's running
```
ping ip.ad.dre.ss
```

ssh into it. Give it the password that you created when you created the droplet
```
ssh root@ip.ad.dre.ss
```

Now you can explore the server using the terminal commands that you learned in the Linux basics video. When you're done, return to the home folder:
```
cd ~
```

### 2.2 Run the code on the server

To do this, there are a few steps you need to complete:
1. Update the linux packages on the server
2. [Install java](https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-on-ubuntu-22-04) on the server
3. Copy the jar to the server
4. Run the code on the server

#### 2.2.1 Update the linux packages on the server
```
sudo apt update
```

#### 2.2.2 Install java on the server
We want to install the [JDK](https://www.digitalocean.com/community/tutorials/difference-jdk-vs-jre-vs-jvm) as it contains the JRE and JVM.

Type `javac` into the terminal, and linux will give you options on how to install the JDK. Run the following:

```
apt install openjdk-17-jdk-headless
```
After pressing enter, you will be asked if you want to continue. Type y and press enter. If it asks you which services must be restarted, navigate to each option using your arrow keys and select all of them using the space bar. Then press enter to restart them.

Verify that java is installed properly
```
javac --version
```

```
java --version
```

#### 2.2.3 Copy the jar to the server
You could pull the code from github onto the server, but that will mean that you will have to first push your updated code to the repo, which is a bit more complicated. It's easier to just [copy the jar file to the server](https://linuxize.com/post/how-to-use-scp-command-to-securely-transfer-files/).

In your local terminal, cd into the rest_api_server directory and run the following command:
```
scp build/libs/rest_api_server-0.0.1.jar root@ip.ad.dre.ss:~/
```

Now if you use the terminal that is ssh'd into the remote server, you'll see the jar file in the home directory of the root user on the server
```
cd ~ && l
```

### 2.3 Test it

Run the jar file on the server just as you did locally
```
java -jar rest_api_tutorial-0.0.1.jar
```

The API server will run exactly as it did locally. Now you can test it in exactly the same way as you did before using the browser or web_ui, but replace localhost with the server IP address. For example, in the browser, type in the following url:
```
http://ip.ad.dre.ss:42069/messages/test
``` 

## 3. Update the Web UI

The html file needs to be updated with your server's IP address so that it can send requests to the API server on the remote server.

### 3.1 Update the IP address in the web UI code

Open index.html in your text/code eitor of choice, find the IP variable and replace it with your server's IP address

### 3.2 Test it
See the console logs for any errors. If there are no errors, you should be able to submit messages to your remote API server and see them in the browser.

## 4. Deploy the Web UI to the server

### 4.1 Copy the html file to the server
In your local terminal, cd into the web_ui directory and scp the file to the server
```
scp index.html root@147.182.235.242:~
```

### 4.2 Run a web server on the server
We need to run a program called a *web server* on our server. This is a program that will *serve* (ie send) the html to anyone/anything that requests it. The quickest way to set this up is to use python (another programming language, like java). In the remote terminal, cd into the same folder as the html file (should be the home folder: ~) and run the following command:
```
python3 -m http.server 80
```

We tell the web server program to serve files through port 80. This allows users to access the html file from the browser without having to specify the port in the url, since browsers automatically use this port when no port is specified in the url

Now if you put only your server's IP into the browser, you will see your html file, and the console logs will show that it is correctly getting the messages from the API server.

## 4.3 Test it
Test the web UI as you have done before. You can even test from a different device like your phone, since the web UI is now accessible to the world.

## 4.4 Comments
You will notice that if you close the terminal session for the web server, you will no longer be able to access the web page. Similarly, if you close the terminal session for the API server, you will no longer be able to send messages to the API server. This is because the programs are running in the terminal session, and when the session is closed, the programs are stopped. If you want to keep the programs running even after the terminal is closed, use the [nohup](https://askubuntu.com/a/222855) command:
```
nohup java -jar rest_api_tutorial-0.0.1.jar &
```
```
nohup python3 -m http.server 80 &
```

The only way to then stop the programs is to find their process ids (pids) and kill them:
```
ps -ef | grep "java -jar rest_api_tutorial-0.0.1.jar"
kill -9 <pid>
```
```
ps -ef | grep "python3 -m http.server 80"
kill -9 <pid>
```

## 5. Update the REST API Client

The android project needs to be updated with your server's IP address so that it can send requests to the API server on the remote server.

### 5.1 Update the IP address in the android code

Open MainActivity.java in Android Studio, find the IP variable and replace it with your server's IP address

### 5.2 Test it
Run it on a simulated android device, or on an actual android device by connecting itto your PC via USB. See the logs for any errors. If there are no errors, you should be able to submit messages to your remote API server and see them in the app.

## 5.3 Build the apk
Build the apk file and copy it to your phone. You can do this by clicking on the hammer icon in Android Studio, or by running the following command in the terminal:
```
./gradlew assembleDebug
```

The built apk file will be located in the following directory inside the project folder:
```
app/build/outputs/apk/debug/app-debug.apk
```

Copy the file to the project folder and at the same timed rename it to rest_api_client.apk:
```
cp app/build/outputs/apk/debug/app-debug.apk rest_api_client.apk
```

You will now see rest_api_client.apk in the project folder
```
ls
```

## 6 Deploy the app to the server

### 6.1 Copy the apk file to the server
In your local terminal, cd into the apk's folder and scp the file to the server
```
scp rest_api_client.apk root@147.182.235.242:~
```

### 6.2 Download the apk from anywhere
The web server program that we ran to serve the index.html file can also be used to download files from the server. Any file that is in the same folder as the index.html file on the server can be downloaded to any device that request that file. To do this, make sure the web server is running on the server as described above, and then in your browser, type in the server url followed by the file that you would like to download. For example, to download the apk file, type in the following url:
```
http://ip.ad.dre.ss:42069/rest_api_client.apk
```

You can now download, install and run this app on any android device.

## 6.3 Test it
Download the app on an android phone, install it an run it.