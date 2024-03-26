# Habbo Scanner: Configuration Guide 🛠️

This guide provides a detailed walkthrough on how to configure the Habbo Scanner bot. 

## Prerequisites 💻

Before you start, make sure you have the following:

- G-Earth installed on your machine.
- G-Earth.jar file placed in the `src/lib` directory.
- MySQL installed and properly configured.
- Java Development Kit (JDK) installed on your machine.

## Configuration Files 🗂️

All configuration files are located in the `src/main/resources` directory. Here are the key configuration files you need to set up:

- `bot.properties`: Configures the bot's behavior.
- `command_description.properties`: Defines the commands that the bot can execute.
- `discord.properties`: Configures the Discord integration.
- `log4j.properties`: Configures the logging behavior.
- `message.properties`: Defines the messages that the bot can send.
- `mysql.properties`: Configures the MySQL dao connection.

## Setting Up ⚙️

1. Navigate in the `database_structure` folder and import every sql file to your database.
2. Populate the `items_timeline` and `items.sql` tables in your MySQL database. 

   - The `items_timeline` table is mandatory for dating furni with the timeline. It has the following structure:
     - `date`: The date associated with the furni.
     - `id`: The ID of the furni.
     - `type`: The type of the furni, either 'Floor' or 'Wall'.
     
     You need to assign an ID and date to each furni and specify whether it's a 'Floor' or 'Wall' type.

   - The `items.sql` table is optional but useful for the "follow, furni_info" function where the bot comes to provide information about a furni. It has the following structure:
     - `classname`: The classname of the furni.
     - `category`: The category of the furni.
     - `type`: The type of the furni, either 'Floor' or 'Wall'.
     - `seen_pieces`: The number of pieces of this furni seen.
     
     Ideally, you should periodically update the data in this table.

3. Ensure that the MySQL settings in the `mysql.properties` file are correctly set to connect to your dao.
4. Compile the Habbo Scanner extension as a .jar file and install it within your G-Earth.
5. Configure the `bot.properties` file according to your needs. Follow the comments in the file for guidance.

Now, you’re all set! You have successfully configured the Habbo Scanner bot. Enjoy exploring the comprehensive set of Habbo data! 🎉
