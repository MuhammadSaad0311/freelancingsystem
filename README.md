# Freelancing System 🚀

A robust, modern Java-based application designed for freelancers and project managers to streamline workflows, manage projects, and handle secure payments.

## ✨ Key Features

- **📊 Comprehensive Project Management**: Track projects from inception to completion.
- **👥 Multi-User Support**: Roles for Admin and Employees with specific permission levels.
- **🕒 Timelog & Work tracking**: Precision time tracking for employees assigned to projects.
- **🛡️ Escrow System**: Secure transaction management with milestone-based releases.
- **📑 Digital Invoicing**: Generate professional PDF invoices (powered by OpenPDF).
- **🎨 Modern UI**: Sleek, responsive interface built with FlatLaf (Light Theme).
- **🗄️ Reliable Storage**: Persistence layer powered by SQLite for lightweight yet powerful data management.

## 🛠️ Tech Stack

- **Language**: Java 17
- **Build System**: Maven
- **Database**: SQLite (JDBC)
- **UI Framework**: Java Swing + [FlatLaf](https://github.com/JFormDesigner/FlatLaf)
- **PDF Generation**: [OpenPDF](https://github.com/LibrePDF/OpenPDF)
- **Logging**: SLF4J

## 🚀 Getting Started

### Prerequisites

- **Java JDK 17** or higher.
- **Apache Maven** installed.

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/HassanRaza214/freelancing-system.git
   cd freelancing-system
   ```

2. Build the project using Maven:
   ```bash
   mvn clean install
   ```

### Running the Application

Run the application using the following Maven command:

```bash
mvn exec:java -Dexec.mainClass="com.freelancer.system.Main"
```

## 📁 Project Structure

```text
freelancing-system/
├── src/main/java/com/freelancer/system/
│   ├── Main.java          # Entry point
│   ├── db/                # Database management logic
│   ├── model/             # Data models (Project, User, Milestone, etc.)
│   ├── service/           # Internal business logic
│   ├── ui/                # Swing UI components and dialogs
│   └── util/              # Utility classes
├── libs/                  # External dependencies (if any)
├── pom.xml                # Maven configuration
└── freelancer.db          # SQLite database file
```

## 🤝 Contributing

Contributions are welcome! If you'd like to improve the Freelancing System, please follow these steps:

1. Fork the project.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Developed with ❤️ by [Muhammad Saad](https://github.com/MuhammadSaad0311)
