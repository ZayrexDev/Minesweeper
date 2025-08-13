# Minesweeper

A simple, feature-rich Minesweeper CLI game written in Java.

> **Note:**  
> This README was AI-generated, cause I'm too lazy KEKW.

---

## Features

- **Fully Interactive Terminal UI**: Move with arrow keys, reveal cells, and flag mines using your keyboard.
- **Customizable Board Size and Mine Count**: Choose your preferred grid size and number of mines before each game.
- **Colorful Display**: Uses [Jansi](https://github.com/fusesource/jansi) and [JLine](https://github.com/jline/jline3) for colored output and smooth terminal control.
- **Classic Minesweeper Mechanics**: Automatic flood-fill for empty spaces, accurate flagging, and win/lose detection.
- **Performance**: Optimized rendering for a responsive experience even on large boards.

---

## Demo

![Demo GIF](https://github.com/user-attachments/assets/0a9e4013-1752-4694-8aca-de59aa219542)

---

## Controls

| Key       | Action                        |
|-----------|------------------------------|
| Arrow Keys| Move cursor                   |
| `z`       | Reveal (open) cell            |
| `x`       | Flag or unflag a cell         |

---

## Getting Started

### Prerequisites

- Java 17 or newer (for record types and recent Java features)
- Dependencies: [Jansi](https://github.com/fusesource/jansi) and [JLine](https://github.com/jline/jline3)

You can add dependencies via Maven or Gradle, or download jars manually.

### Running the Game

1. **Clone the repository:**
   ```sh
   git clone https://github.com/ZayrexDev/Minesweeper.git
   cd Minesweeper
   ```

2. **Compile and run:**
   ```sh
   javac -cp "path/to/jansi.jar:path/to/jline.jar" Main.java
   java -cp ".:path/to/jansi.jar:path/to/jline.jar" Main
   ```

   Replace `path/to/jansi.jar` and `path/to/jline.jar` with the actual paths to the Jansi and JLine jar files.

3. **Follow the on-screen prompts** to set your board size and mine count, then play!

---

## License

This project is licensed under the MIT License.
