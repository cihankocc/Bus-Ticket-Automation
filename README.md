# Bus Ticket Automation

This project is a desktop automation application based on **Java Swing** and **MySQL**, where passengers can view bus schedules and purchase tickets, while managers/company owners can manage these schedules and buses.

## 🚀 Features

- **User Management**: Two distinct role types - Passenger and Manager/Owner.
- **Registration & Login**: Secure user registration and login functionality.
- **Schedule (Route) Viewing**: Listing details such as departure, arrival, date, time, and ticket price for various routes.
- **Seat Selection & Reservation**: Tracking available, booked, or reserved seats for a particular route and the ability to purchase a ticket.
- **Bus Management**: Maintaining bus records with their license plate and captain information.
- **Modern Interface**: A clean and user-friendly interface designed using the Java Swing `Nimbus` look and feel.

## 🛠️ Built With

- **Language**: Java (Supports Java 9+ modular architecture)
- **User Interface**: Java Swing
- **Database**: MySQL
- **Database Driver**: JDBC

## 📋 Getting Started

To run the project on your local machine, please follow the steps below:

### 1. Database Setup
MySQL must be up and running for the application to work:
1. Start your local MySQL server.
2. Execute the `src/rename.sql` file provided in the repository to create the necessary tables (`User`, `Bus`, `Dpt`, `Seat`, `Booking`).

### 2. Database Connection Configuration
You need to configure the database credentials to establish a connection:
1. Open the `config.properties` file in your project's root directory.
2. Update properties like `db.url`, `db.user`, and `db.password` according to your local MySQL environment.

### 3. Running the Project
The application can be run using any Java-friendly IDE (like Eclipse, IntelliJ IDEA, or NetBeans):
1. Open the project directory through your IDE.
2. Locate and launch the `src/main/Main.java` file.
3. You can either log into the system with an existing account or register as a new user to start using the software.

## 📂 Project Structure

- `src/main` : The entry point of the project (`Main.java`).
- `src/gui` : Contains the UI components (Swing Frames and Panels).
- `src/controllers` : Connects the user interface to the business logic and handles database operations.
- `src/models` : Java models representing database tables (`User`, `Bus`, `Dpt`, etc.).
- `src/utils` : Contains utility and helper classes, such as the one managing database connections.

---

> This project was built for educational purposes, aimed at understanding the core methodologies of desktop automation applications.
