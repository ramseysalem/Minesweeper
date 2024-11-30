# Minesweeper Game - README

## Overview
This project is a Java implementation of the classic **Minesweeper** game. It provides an interactive game experience using `javalib.impworld` for rendering the game board and managing user interactions. The game includes various features such as different difficulty levels, a timer, a click score, and dynamic mine-count updates.

---

## Features

### Core Gameplay
- **Cell Representation**: Each cell can be a mine, flagged, or revealed.
- **Mine Placement**: Mines are randomly distributed across the board.
- **Neighbor Linking**: Cells are aware of their neighbors for mine counting and flood-fill logic.
- **User Interaction**: 
  - **Left-Click**: Reveals a cell.
  - **Right-Click**: Flags or unflags a cell.
  - **Key Input**: Start the game, reset, or change difficulty levels.

### Difficulty Levels
- Easy: Small grid with fewer mines.
- Medium: Moderate grid and mine count.
- Hard: Large grid with many mines.

### Timer
- Tracks time elapsed during gameplay.

### End Conditions
- **Win**: Flag all mines correctly and reveal non-mine cells.
- **Loss**: Click on a mine.

---

## Classes and Responsibilities

### 1. `Cell`
- Represents a single cell in the game.
- Tracks its state (mine, flagged, revealed).
- Manages its neighbors and calculates the number of adjacent mines.
- Draws its visual representation.

### 2. `Timer`
- Handles game timing, including starting, stopping, and resetting.

### 3. `MinesweeperGame`
- Manages the overall game state.
- Handles board initialization, mine placement, and user input.
- Manages win/loss conditions and generates game visuals.

### 4. `ExamplesMinesweeper`
- Contains test cases for the game's features and behaviors.
- Validates functionalities such as board initialization, mine placement, cell linking, and game logic.

---

## How to Play

1. **Start the Game**
   - Press `Enter` to start after selecting a difficulty level:
     - Press `1`: Easy
     - Press `2`: Medium
     - Press `3`: Hard
2. **Gameplay**
   - Left-click to reveal cells.
   - Right-click to flag/unflag cells.
3. **Win/Loss**
   - Flag all mines correctly to win.
   - Reveal a mine to lose.

---

## Testing
The project includes robust testing through the `ExamplesMinesweeper` class. Key test scenarios:
- Board initialization and cell linking.
- Correct mine placement.
- Cell revealing and flood-fill logic.
- Game end conditions (win/loss).
- Timer functionality.

Run the `testBigBang` method to start a live test of the game.

---

## Known Enhancements (Extra Credit)
- Implemented:
  - Difficulty levels.
  - Timer and click-based score tracking.
  - Remaining mine count.
- Potential Improvements:
  - Visual feedback for flagged cells.
  - Advanced scoring system.

---

## Dependencies
- `javalib.impworld` for game world management.
- `tester` library for unit testing.

---

## How to Run
1. Compile and run the `MinesweeperGame` class.
2. Use `testBigBang` to initialize and play the game interactively.
