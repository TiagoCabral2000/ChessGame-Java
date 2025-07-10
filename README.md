# ♟ Chess Game - Java ♟

A JavaFX-based chess game developed for the Advanced Programming course (2024/2025).  
The goal was to build a complete and interactive chess application using **Object-Oriented Programming** principles and several **Java design patterns**.


---

## 🧠 Overview

This project simulates a real chess game, including special moves like **castling**, **promotion**, and **en passant**.  
It includes a modern JavaFX interface, game saving/loading, and support for multiple languages and audio feedback.

Most importantly, the project was designed using **clean architecture** and several well-known **Java design patterns**.

---

## 🧩 Design Patterns Used

We applied the following design patterns to follow Java best practices and keep the project modular and maintainable:

### ✔️ MVC (Model-View-Controller)
Used as the main architecture to separate:
- **Model**: game logic (`ChessGame`, `Player`, `Board`)
- **View**: JavaFX interface (`RootPane`, `BoardCanvas`)
- **Controller**: UI event handling and user input (`ChessGameManager` as facade)

### ✔️ Facade
The class `ChessGameManager` works as the **only access point** between the UI and the game logic, hiding complexity from the interface.

### ✔️ Command
Used to implement **Undo/Redo** functionality.  
Classes like `MoveCommand`, `CommandManager`, and the `ICommand` interface follow the Command pattern.

### ✔️ Factory Method
All chess pieces (`King`, `Queen`, `Bishop`, etc.) are created using a **PieceFactory** class, centralizing object creation.

### ✔️ Singleton
The `ModelLog` class uses this pattern to ensure there's only **one instance** for logging game events.

### ✔️ Multiton
The `ImageManager` class manages images efficiently using a **Multiton**, caching one instance per piece type.

### ✔️ Observer
Used so that the UI **automatically updates** when the game state changes, through `PropertyChangeListener` and `PropertyChangeSupport`.

---

## ✅ Main Features

- Full chess rules and legal move validation
- Castling, promotion, and en passant supported
- Undo / Redo support
- Load and save games (text, CSV, and binary)
- JavaFX UI with multiple windows
- Highlight possible moves (learning mode)
- Audio feedback (multi-language)
- Piece image management with caching
- Game history and logs
- JavaDoc documentation
- Unit testing included

---

## 🛠 Technologies

- Java 17+
- JavaFX
- JUnit
- JavaDoc

