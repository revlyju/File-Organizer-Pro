# 📁 File Organizer Pro  
**Smart File Management with Undo Functionality**

## 🔹 Overview
**File Organizer Pro** is a Java desktop application built using **JavaFX** that helps users efficiently organize files within a selected directory. Files can be organized based on **file extensions** or **last modified date** (day / month / year).

A key highlight of the application is its **Undo functionality**, which allows users to revert the most recent organization action and restore files to their original locations, ensuring data safety and flexibility.


## ✨ Features
- Organize files by **file extension**
- Organize files by **date** (day / month / year)
- **Undo** last organization action
- Clean and interactive **JavaFX GUI**
- Safe file handling using structured logging


## 🛠 Technologies Used
- **Java (JDK 17+)**
- **JavaFX**
- **Gson** (for JSON-based undo tracking)
- **IntelliJ IDEA** (development environment)


## 🖥 System Requirements & Setup

### ✅ Requirements
- Java **JDK 17 or higher**
- **JavaFX SDK** compatible with your JDK
- Any Java-supported IDE (IntelliJ IDEA / Eclipse / VS Code)

### 🔧 JavaFX Configuration (VM Options)
Add the following VM options while running the project:

```bash
--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml
