# Bus Ticket Automation

Bus Ticket Automation is a **Java desktop application** developed using **Java Swing** and **MySQL**.  
The system allows passengers to view bus schedules and buy tickets, while managers can manage buses, routes, and reservations.

This project was developed as part of a **learning project to practice desktop application development, database integration, and basic software architecture.**

---

## Features

### User Management
- Two user roles: **Passenger** and **Manager**
- User registration and login system

### Ticket and Route System
- View available bus routes
- Display route information such as:
  - Departure location
  - Arrival location
  - Date and time
  - Ticket price

### Seat Reservation
- Select seats from the bus
- View available and reserved seats
- Purchase tickets for selected routes

### Bus Management (Manager)
- Add and manage buses
- Store bus information such as license plate and captain name

### User Interface
- Desktop interface built with **Java Swing**
- Uses the **Nimbus look and feel** for a cleaner UI

---

## Technologies Used

- **Programming Language:** Java  
- **UI Framework:** Java Swing  
- **Database:** MySQL  
- **Database Access:** JDBC  

---

## Project Structure

```
src/
 ├── main
 │    └── Main.java
 ├── gui
 ├── controllers
 ├── models
 └── utils
```

---

## Database Setup

To run the project locally:

1. Install and start **MySQL**.
2. Run the SQL script inside the project:

```
src/rename.sql
```

This will create the required tables such as:

- User  
- Bus  
- Dpt (Routes)  
- Seat  
- Booking  

---

## Database Configuration

Open the following file:

```
config.properties
```

Update the database connection settings according to your local MySQL setup:

```
db.url=jdbc:mysql://localhost:3306/database_name
db.user=root
db.password=your_password
```

---

## Running the Application

1. Open the project in an IDE such as **IntelliJ IDEA**, **Eclipse**, or **NetBeans**.
2. Run the following file:

```
src/main/Main.java
```

3. Register a new user or log in to start using the system.

---

## Author

Cihan Koç  
GitHub: https://github.com/cihankocc
