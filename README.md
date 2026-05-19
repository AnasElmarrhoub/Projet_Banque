# 🏦 Système de Gestion Bancaire — INPT

> A fully object-oriented **Java 21** banking management system with **MySQL** persistence and a **JavaFX** graphical interface, developed as part of the Software Engineering curriculum at INPT (Institut National des Postes et Télécommunications).

---

## 📌 About The Project

This project simulates the core operations of a real-world banking system. It was designed and implemented from scratch with a focus on **clean architecture**, **object-oriented design principles**, and **database integration**.

The system handles two types of bank accounts — current accounts with configurable overdraft limits and savings accounts with automatic interest calculation — and supports the full lifecycle of banking operations: client registration, account management, deposits, withdrawals, and inter-account transfers. Every operation is persisted to a **MySQL** database and logged to both a journal file and individual receipt tickets, mirroring the behavior of real banking software.

The project also includes a **JavaFX desktop interface** that loads live data from the database and displays clients alongside their total aggregated balance.

### Key design decisions:
- **Abstraction & Polymorphism** — `Compte` is an abstract class; `ComptePrincipal` and `CompteEpargne` each override `retirer()` with their own business rules (overdraft ceiling vs. interest credit).
- **Custom exception hierarchy** — Domain errors (`MontantNegatifException`, `SoldeInsuffisantException`, `CompteInexistantException`) extend a base `BanqueException`, giving full control over error propagation.
- **Secure configuration** — Database credentials are never stored in source code; they are loaded at runtime from a local `db.properties` file (excluded from version control).
- **Resource safety** — All I/O streams and JDBC resources are managed with `try-with-resources` to prevent leaks.

---

## ✨ Features

| Feature | Details |
|---|---|
| 👤 **Client management** | Create and persist clients with full address information |
| 🏧 **Current account** | Withdrawals allowed up to a configurable overdraft limit |
| 💰 **Savings account** | Interest automatically credited to the balance before each withdrawal |
| 🔁 **Transfers** | Atomic inter-account transfers with full DB update and journal entry |
| 🗄️ **MySQL persistence** | All entities (clients, accounts, operations) stored and reloaded via JDBC |
| 🖥️ **JavaFX UI** | Desktop table view showing all clients and their total balance |
| 🧾 **Receipt tickets** | Per-operation `.txt` receipts generated automatically |
| 📋 **Journal log** | Timestamped append-only log of all significant banking events |

---

## 🗂️ Project Structure

```
Projet_banque/
├── src/
│   ├── Main.java                      # Entry point — full demo scenario
│   ├── Banque.java                    # Core banking logic (clients, accounts, transfers)
│   ├── Compte.java                    # Abstract base class for all account types
│   ├── ComptePrincipal.java           # Current account with overdraft support
│   ├── CompteEpargne.java             # Savings account with automatic interest
│   ├── Client.java                    # Client entity
│   ├── Adresse.java                   # Address value object
│   ├── Operation.java                 # Immutable operation record (deposit / withdrawal)
│   ├── ConnexionBD.java               # Singleton JDBC connection factory
│   ├── InterfaceBanque.java           # JavaFX graphical interface
│   ├── Journalisation.java            # File-based logging (journal + receipts)
│   ├── BanqueException.java           # Base checked exception
│   ├── MontantNegatifException.java   # Thrown on zero/negative amounts
│   ├── SoldeInsuffisantException.java # Thrown when balance limit is exceeded
│   └── CompteInexistantException.java # Thrown when an account is not found
├── db.properties.example              # ⚠ Template — copy to db.properties and configure
├── pom.xml                            # Maven build (Java 21, JavaFX 21, MySQL 8.3)
└── .gitignore
```

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- MySQL 8+

### 1. Clone the repository

```bash
git clone https://github.com/AnasElmarrhoub/Projet_Banque.git
cd Projet_Banque
```

### 2. Configure the database connection

```bash
cp db.properties.example db.properties
# Open db.properties and fill in your MySQL credentials
```

### 3. Create the MySQL schema

```sql
CREATE DATABASE BanqueINPT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE BanqueINPT;

CREATE TABLE clients (
    id          INT PRIMARY KEY,
    nom         VARCHAR(100) NOT NULL,
    prenom      VARCHAR(100) NOT NULL,
    rue         VARCHAR(200),
    ville       VARCHAR(100),
    code_postal VARCHAR(20)
);

CREATE TABLE comptes (
    numero             INT PRIMARY KEY,
    solde              DOUBLE       NOT NULL DEFAULT 0,
    type_compte        VARCHAR(20)  NOT NULL,  -- 'COURANT' or 'EPARGNE'
    plafond_decouvert  DOUBLE,
    taux_interet       DOUBLE,
    id_client          INT,
    FOREIGN KEY (id_client) REFERENCES clients(id)
);

CREATE TABLE operations (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    montant     DOUBLE      NOT NULL,
    type_op     VARCHAR(20) NOT NULL,
    date_op     DATETIME    NOT NULL,
    num_compte  INT,
    FOREIGN KEY (num_compte) REFERENCES comptes(numero)
);
```

### 4. Build and run

```bash
# Run the console demo
mvn compile exec:java -Dexec.mainClass="Main"

# Launch the JavaFX interface
mvn javafx:run
```

---

## 🏛️ Architecture & OOP Concepts Applied

```
Compte  (abstract)
├── ComptePrincipal   →  retirer() checks overdraft ceiling
└── CompteEpargne     →  retirer() credits interest before deducting

BanqueException  (checked)
├── MontantNegatifException
├── SoldeInsuffisantException
└── CompteInexistantException
```

| OOP Principle | Where applied |
|---|---|
| **Abstraction** | `Compte` defines the interface; concrete classes own the business rules |
| **Inheritance** | `ComptePrincipal` and `CompteEpargne` extend `Compte` |
| **Polymorphism** | `retirer()` behaves differently per account type |
| **Encapsulation** | All fields are `private`/`protected`; access only via getters |
| **Singleton** | `ConnexionBD` manages one shared JDBC connection |

---

## 🛠️ Tech Stack

![Java](https://img.shields.io/badge/Java-21-007396?style=flat&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21-informational?style=flat)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?style=flat&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3-C71A36?style=flat&logo=apachemaven&logoColor=white)

---

## 👤 Author

**Anas Elmarrhoub**  
Engineering Student at [INPT](https://www.inpt.ac.ma/) — Rabat, Morocco  
[GitHub](https://github.com/AnasElmarrhoub)

---

## 📄 License

This project was developed for educational purposes at INPT.