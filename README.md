# Two-Tier Java GUI Database Client

*Secure Desktop Database Management with Role-Based Access Control and Real-Time Audit Logging*

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white) ![Swing](https://img.shields.io/badge/Swing-007396?style=for-the-badge&logo=java&logoColor=white) ![JDBC](https://img.shields.io/badge/JDBC-4479A1?style=for-the-badge)

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
- [Usage](#usage)
- [Security](#security)
- [Audit System](#audit-system)
- [Project Structure](#project-structure)
- [Technologies](#technologies)

## 🎯 Overview

A two-tier Java Swing desktop application that provides secure, multi-user database access through an intuitive GUI interface. Built with JDBC connectivity, the application enforces role-based privilege controls and maintains comprehensive audit logs of all database operations.

**Key Highlights:**
- 🔐 Secure credential validation before database access
- 👥 Role-based permission enforcement
- 📊 Real-time operations logging to dedicated audit database
- 🖥️ User-friendly Java Swing interface
- 📈 Query and update metrics tracking per user role

## ✨ Features

### 🖥️ Java Swing Desktop Interface
- Clean, intuitive GUI built with Java Swing components
- Real-time SQL query execution and result display
- Interactive data grid for viewing query results
- Responsive forms for database operations
- Error handling with user-friendly notifications

### 🔐 Secure Authentication System
- **Properties-based credential storage** for user management
- Pre-authentication validation before establishing database connections
- Encrypted credential verification (configurable)
- Session management for authenticated users
- Automatic logout on connection termination

### 👥 Role-Based Access Control (RBAC)
The application supports multiple user roles, each with distinct database privileges:

| User Role | Database Privileges | Typical Use Case |
|-----------|---------------------|------------------|
| **Root User** | Full CRUD access | Database administration |
| **Client User** | SELECT queries only | Data viewing and reporting |
| **Data Entry User** | INSERT, UPDATE operations | Data management |
| **Accountant User** | SELECT with financial focus | Financial reporting |

### 📊 Real-Time Audit Logging
- **Dedicated audit database** separate from application data
- Tracks all database operations with metadata:
  - Timestamp of operation
  - User role performing the action
  - Operation type (SELECT, INSERT, UPDATE, DELETE)
  - Query execution time
  - Number of rows affected
  - Success/failure status
- Enables compliance monitoring and security analysis
- Performance metrics for query optimization

### 🗃️ JDBC Database Connectivity
- Direct MySQL database connection via JDBC
- Prepared statements for SQL injection prevention
- Connection pooling for efficient resource management
- Transaction support for data integrity
- Exception handling with detailed error messages

## 🏗️ Architecture
```
┌─────────────────────────────────────────────────┐
│           Java Swing GUI Layer                  │
│  (LoginFrame, MainFrame, QueryPanel, Results)   │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│      Authentication & Authorization Layer       │
│    (Properties file validation, Role checking)  │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│            JDBC Connection Layer                │
│  (Connection management, Query execution)       │
└────────┬─────────────────────────┬───────────────┘
         │                         │
┌────────▼─────────┐      ┌────────▼─────────────┐
│  MySQL Database  │      │  MySQL Audit DB      │
│ (Application     │      │ (Operations Log)     │
│    Data)         │      │                      │
└──────────────────┘      └──────────────────────┘
```

## 🚀 Getting Started

### Prerequisites

Before running the application, ensure you have:

- **Java Development Kit (JDK)**: Version 8 or higher
```bash
  java -version  # Should show 1.8 or higher
```

- **MySQL Server**: Version 5.7+ or 8.0+
```bash
  mysql --version
```

- **MySQL JDBC Driver**: MySQL Connector/J
  - Download from: https://dev.mysql.com/downloads/connector/j/
  - Or use Maven/Gradle dependency management

- **IDE** (Optional): IntelliJ IDEA, Eclipse, or NetBeans

### Installation

1. **Clone the repository**:
```bash
git clone https://github.com/Angelo-Castellon/Two-Tier_Java_Application.git
cd Two-Tier_Java_Application
```

2. **Set up MySQL databases**:
```sql
-- Create the main application database
CREATE DATABASE project3;

-- Create the audit logging database
CREATE DATABASE operationslog;

-- Create necessary tables
USE project3;
-- Run your schema creation script here

USE operationslog;
CREATE TABLE IF NOT EXISTS operations_count (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_role VARCHAR(50) NOT NULL,
    num_queries INT DEFAULT 0,
    num_updates INT DEFAULT 0,
    last_operation TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_role (user_role)
);
```

3. **Configure properties files**:

Navigate to `src/Properties/` and configure the following files:

**`root.properties`** (Root user database connection):
```properties
MYSQL_DB_URL=jdbc:mysql://localhost:3306/project3
MYSQL_DB_USERNAME=root
MYSQL_DB_PASSWORD=your_root_password
```

**`client.properties`** (Client user database connection):
```properties
MYSQL_DB_URL=jdbc:mysql://localhost:3306/project3
MYSQL_DB_USERNAME=client
MYSQL_DB_PASSWORD=your_client_password
```

**`dataentryuser.properties`** (Data entry user connection):
```properties
MYSQL_DB_URL=jdbc:mysql://localhost:3306/project3
MYSQL_DB_USERNAME=dataentry
MYSQL_DB_PASSWORD=your_dataentry_password
```

**`accountant.properties`** (Accountant user connection):
```properties
MYSQL_DB_URL=jdbc:mysql://localhost:3306/project3
MYSQL_DB_USERNAME=accountant
MYSQL_DB_PASSWORD=your_accountant_password
```

**`operationslog.properties`** (Audit database connection):
```properties
MYSQL_DB_URL=jdbc:mysql://localhost:3306/operationslog
MYSQL_DB_USERNAME=root
MYSQL_DB_PASSWORD=your_root_password
```

4. **Create MySQL users with appropriate privileges**:
```sql
-- Root user (full access)
CREATE USER 'root'@'localhost' IDENTIFIED BY 'your_root_password';
GRANT ALL PRIVILEGES ON project3.* TO 'root'@'localhost';

-- Client user (read-only)
CREATE USER 'client'@'localhost' IDENTIFIED BY 'your_client_password';
GRANT SELECT ON project3.* TO 'client'@'localhost';

-- Data entry user (insert/update)
CREATE USER 'dataentry'@'localhost' IDENTIFIED BY 'your_dataentry_password';
GRANT SELECT, INSERT, UPDATE ON project3.* TO 'dataentry'@'localhost';

-- Accountant user (read-only with specific tables)
CREATE USER 'accountant'@'localhost' IDENTIFIED BY 'your_accountant_password';
GRANT SELECT ON project3.* TO 'accountant'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;
```

5. **Compile and run**:
```bash
# Compile the Java source files
javac -d bin -cp ".:lib/mysql-connector-java.jar" src/*.java

# Run the application
java -cp "bin:lib/mysql-connector-java.jar" Main
```

Or simply run from your IDE by opening the project and executing the main class.

### Configuration

**Security Best Practices:**

⚠️ **Important**: Never commit credentials to version control!

- Add `*.properties` to your `.gitignore` file
- Use environment variables for sensitive data in production
- Consider using encrypted property files (Jasypt)
- Restrict file permissions: `chmod 600 *.properties`

**JDBC Driver Setup:**
- Place `mysql-connector-java.jar` in the `lib/` directory
- Or add to project dependencies if using Maven/Gradle

## 💻 Usage

### Starting the Application

1. **Launch the application**:
   - Run the main Java class
   - Login window appears

2. **Select user role**:
   - Choose from dropdown: Root, Client, Data Entry, or Accountant
   - Each role corresponds to a properties file with specific credentials

3. **Enter credentials**:
   - Username and password are validated from the selected role's properties file
   - Connection established to MySQL upon successful authentication

### Performing Database Operations

**Executing Queries:**
```sql
-- Example SELECT query
SELECT * FROM suppliers WHERE status > 50;

-- Results display in the data grid
-- Operation logged to audit database with:
--   - User role
--   - Query execution time
--   - Number of rows returned
```

**Executing Updates:**
```sql
-- Example UPDATE statement
UPDATE products SET quantity = quantity + 100 WHERE product_id = 5;

-- Confirmation message shows rows affected
-- Audit log records:
--   - User role
--   - Update operation
--   - Rows modified
```

**Role-Based Restrictions:**

- **Client users**: Can only execute SELECT queries; UPDATE/INSERT/DELETE blocked
- **Data Entry users**: Can SELECT, INSERT, UPDATE; DELETE blocked
- **Accountant users**: Can only SELECT from specific financial tables
- **Root users**: Full access to all operations

### Viewing Audit Logs

Audit logs are stored in the `operationslog` database:
```sql
-- View operation counts by role
SELECT * FROM operations_count;

-- Sample output:
-- +----+-----------+-------------+--------------+---------------------+
-- | id | user_role | num_queries | num_updates  | last_operation      |
-- +----+-----------+-------------+--------------+---------------------+
-- |  1 | root      |          45 |           12 | 2024-11-09 10:30:15 |
-- |  2 | client    |         128 |            0 | 2024-11-09 10:25:42 |
-- |  3 | dataentry |          23 |           67 | 2024-11-09 10:28:33 |
-- +----+-----------+-------------+--------------+---------------------+
```

## 🔒 Security

### Authentication Flow
```
1. User selects role from dropdown
   ↓
2. Enters username and password
   ↓
3. Application reads corresponding properties file
   ↓
4. Credentials validated against properties
   ↓
5. JDBC connection established with role-specific privileges
   ↓
6. Main interface loads with appropriate permissions
   ↓
7. All operations logged to audit database
```

### Database-Level Security

Each MySQL user has specific GRANT privileges enforced at the database level:
```sql
-- Example: Client user cannot modify data
mysql> UPDATE products SET price = 100 WHERE id = 1;
ERROR 1142 (42000): UPDATE command denied to user 'client'@'localhost'
```

This provides **defense in depth**: even if application-level checks fail, database enforces permissions.

## 📊 Audit System

### Tracked Metrics

For each database operation, the audit system records:

| Metric | Description |
|--------|-------------|
| **User Role** | Which role performed the operation |
| **Operation Type** | Query (SELECT) or Update (INSERT/UPDATE/DELETE) |
| **Operation Count** | Cumulative queries and updates per role |
| **Timestamp** | When the last operation occurred |
| **Execution Time** | Query performance metrics (if implemented) |

### Audit Database Schema
```sql
CREATE TABLE operations_count (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_role VARCHAR(50) NOT NULL,
    num_queries INT DEFAULT 0,
    num_updates INT DEFAULT 0,
    last_operation TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
                   ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_role (user_role)
);
```

### Benefits of Audit Logging

- 📈 **Performance Monitoring**: Identify slow queries and optimize
- 🔍 **Security Analysis**: Detect unusual access patterns
- 📋 **Compliance**: Meet regulatory requirements for data access tracking
- 🐛 **Debugging**: Trace issues to specific user operations
- 📊 **Usage Analytics**: Understand how different roles use the system

## 📁 Project Structure
```
Two-Tier_Java_Application/
├── src/
│   ├── Main.java                    # Application entry point
│   ├── LoginFrame.java              # Login GUI
│   ├── MainFrame.java               # Main application window
│   ├── DatabaseConnection.java      # JDBC connection management
│   ├── QueryExecutor.java           # SQL execution logic
│   ├── AuditLogger.java             # Audit logging functionality
│   └── Properties/                  # Database connection configurations
│       ├── root.properties
│       ├── client.properties
│       ├── dataentryuser.properties
│       ├── accountant.properties
│       └── operationslog.properties
├── lib/
│   └── mysql-connector-java.jar     # JDBC driver
├── .gitignore                       # Exclude sensitive files
└── README.md
```

## 🛠️ Technologies

**Programming Language:**
- Java 8+

**GUI Framework:**
- Java Swing (`javax.swing`)
- AWT (Abstract Window Toolkit)

**Database:**
- MySQL 5.7+ / 8.0+
- JDBC 4.0+ (Java Database Connectivity)

**Libraries:**
- MySQL Connector/J (JDBC Driver)
- Java Properties API (Configuration Management)

**Development Tools:**
- Maven or Gradle (optional, for dependency management)
- Git (version control)

## 🎓 Course Information

**Course**: CNT 4714 - Enterprise Computing  
**Institution**: University of Central Florida  
**Project**: Two-Tier Database Client Application

## 📝 Future Enhancements

Potential improvements for future versions:

- [ ] Implement password encryption (BCrypt/Argon2)
- [ ] Add connection pooling (HikariCP/C3P0)
- [ ] Export query results to CSV/Excel formats
- [ ] Visual query builder for non-SQL users
- [ ] Real-time audit log viewer in GUI
- [ ] Database backup/restore functionality
- [ ] Dark mode theme support
- [ ] Multi-database support (PostgreSQL, Oracle)
- [ ] RESTful API for remote access
- [ ] Web-based interface alternative

## 📄 License

This project is for educational purposes as part of coursework at the University of Central Florida.

---

**Note**: This is an academic project demonstrating two-tier client-server architecture, JDBC connectivity, and role-based access control principles.
