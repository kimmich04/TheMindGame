# The Mind Game

This is a JavaFX implementation of "The Mind Game". Follow the instructions below to set up your environment and run the game.

## Prerequisites

- Java Development Kit (JDK) version 23
- Eclipse IDE for Java Developers

## Installation Instructions

### Step 1: Install Java JDK 23

1. Download the JDK 23 from the official [Oracle website](https://www.oracle.com/java/technologies/javase-jdk23-downloads.html).
2. Follow the installation instructions for your operating system.
3. Verify the installation by opening a terminal or command prompt and typing: java -version
Ensure it shows Java version 23.

### Step 2: Install Eclipse IDE
1. Download Eclipse IDE for Java Developers from the Eclipse official website.
2. Follow the installation instructions for your operating system.
3. Launch Eclipse after installation is complete.

### Step 3: Set Up the Project in Eclipse
1. Open Eclipse and create a new Java project:
- Go to File -> New -> Java Project.
Enter the project name (e.g., TheMindGame) and click Finish.
2. Add JavaFX library to your project:
- Right-click on your project in the Project Explorer.
- Go to Build Path -> Add Libraries....
- Select User Library and click Next.
- Click User Libraries... and then New....
- Name the new library JavaFX23 and click OK.
- Click Add External JARs... and navigate to the JavaFX SDK folder you downloaded earlier.
- Select all the JAR files in the lib folder and click Open.
- Click OK to add the library.
3. Configure the JavaFX library:
- Right-click on your project and go to Build Path -> Configure Build Path.
- Go to the Libraries tab, select JavaFX23, and click Edit....
- Set the Native library location to the bin directory of your JavaFX SDK.
4. Import the game source code:
- Copy the source files into the src folder of your project in Eclipse.
5. Add the VM arguments:
- Right-click on your project and go to Run As -> Run Configurations....
- Select your project under Java Application, and go to the Arguments tab.
- In the VM arguments section, add:
> --module-path /path/to/javafx-sdk-23/lib --add-modules javafx.controls,javafx.fxml

### Step 4: Run the Game:
- Right-click on your project and go to Run As -> Java Application.
- Select the main class (e.g., GameClient1) and click Run.
The game should now start, and you can enjoy playing "The Mind Game"!

### Notes
- Ensure all images and resources are correctly placed in the /images folder in your project directory.
- If you encounter any issues, double-check the paths and configurations.
- Feel free to customize the game and add new features!
Enjoy the game!
