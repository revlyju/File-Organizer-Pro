File Organizer Pro — Smart File Management with Undo Functionality
Abstract / Summary
The project File Organizer Pro is a desktop-based Java application designed to simplify file management using intelligent organization methods. Built with JavaFX for the graphical interface and Java I/O for backend logic, it automatically organizes files within a folder based on their file type (extension) or modification date (day, month, or year).
A key feature is the Undo option, which allows users to revert the last organization action, restoring files to their original locations. This ensures data safety and flexibility for users. The project demonstrates the integration of modern GUI design with backend logic, focusing on efficiency, usability, and data integrity.

Introduction
Managing large numbers of files on a computer can become confusing and time-consuming. Users often struggle to locate files spread across multiple folders, leading to inefficiency and clutter.
This project aims to solve this problem through an automated file organization system that sorts files intelligently while maintaining an intuitive user experience. By using JavaFX for the frontend and Java’s File API for backend logic, the application enables smooth interaction and effective file handling.
The inclusion of an Undo feature ensures that users can safely reverse their actions, making the system reliable and user-friendly.

Literature Review
Various file management systems and utilities exist, such as Windows File Explorer and third-party tools like CCleaner or Files by Google. However, most tools lack custom sorting control and undo capabilities in local file organization.
Research on automation in file management emphasizes the importance of user control, data safety, and efficient categorization (Source: IEEE papers on Intelligent File Management, 2023).
This project builds upon these principles, combining automation with reversible actions, ensuring a balance between convenience and safety — a feature not commonly found in typical file organizers.

Objectives
The primary objectives of File Organizer Pro are:
To design a Java-based application that automatically organizes files in a selected directory.
To provide multiple organization methods — by file extension and by date (day/month/year).
To develop a Graphical User Interface (GUI) using JavaFX for ease of use.
To implement an Undo feature that restores files to their original structure after an organization action.
To ensure smooth performance and data security during all operations.

Methodology
The project was developed using the following tools and techniques:
Programming Language: Java
Framework: JavaFX (for GUI)
Libraries Used: Gson (for JSON file handling and undo tracking)
Development Environment: IntelliJ IDEA

Process:
The user selects a target folder using a directory chooser.
The app lists files and sorts them based on the selected criteria (extension/date).
Before sorting, a JSON log records each file’s original and new location.
The Undo function reads this JSON log to restore all files to their original paths if the user requests it.
All interactions are handled through JavaFX buttons such as Organize Files and Undo Last Action.
This modular approach ensures easy scalability and maintenance.

Conclusion / Significance
File Organizer Pro enhances productivity by automating tedious file organization tasks while preserving user control through its undo mechanism. It demonstrates how modern Java applications can combine intelligent automation with data safety in an elegant GUI.
The project holds practical value for both individual users and organizations dealing with large datasets, offering a foundation for future work such as AI-based categorization, cloud integration, or file tagging systems.
