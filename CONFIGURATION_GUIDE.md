# Habbo Scanner: Configuration Guide 🛠️

This guide provides a detailed walkthrough on how to configure the Habbo Scanner bot. 

## Prerequisites 💻

Before you start, make sure you have the following:

- G-Earth installed on your machine.
- MySQL installed and properly configured.
- Java Development Kit (JDK) installed on your machine.

## Configuration Files 🗂️

All configuration files are located in the `src/main/resources` directory. Here are the key configuration files you need to set up:

- `bot.properties`: Configures the bot's behavior.
- `command_description.properties`: Defines the commands that the bot can execute.
- `discord.properties`: Configures the Discord integration.
- `log4j.properties`: Configures the logging behavior.
- `message.properties`: Defines the messages that the bot can send.
- `mysql.properties`: Configures the MySQL database connection.
- `mysql_structure.sql`: Defines the structure of the MySQL database.

## Setting Up ⚙️

1. Import the SQL structure using the `mysql_structure.sql` file into your MySQL database.
2. Ensure that the MySQL settings in the `mysql.properties` file are correctly set to connect to your database.
3. Compile the Habbo Scanner extension as a .jar file and install it within your G-Earth.
4. Configure the `bot.properties` file according to your needs. Follow the comments in the file for guidance.

Now, you're all set! You have successfully configured the Habbo Scanner bot. Enjoy your enhanced Habbo experience! 🎉
