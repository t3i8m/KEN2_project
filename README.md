
# Yinsh


This project is a Yinsh board game implemented with JavaFX. Yinsh is an abstract strategy board game, released in 2003, it is an example of a variation on the classic Connection subcategory of board games. The ultimate goal is to form uninterrupted lines of their chips. This project includes a custom UI to represent the board and game pieces.
## Prerequisites
This project requires;

- Java JDF 21 or higher
- JavaFX 22 or higher, added to the project dependencies
- Maven


## Installation

1. Clone the repository
```bash
git clone https://github.com/t3i8m/KEN2_project.git
```
2. Ensure you've got the necessary dependencies. If you are using Maven, the dependencies are already defined in the [pom.xml](https://github.com/t3i8m/KEN2_project/blob/main/ken2/pom.xml)

## Running the Game
### Through the terminal
1. Navigate to the Project Folder
```bash
cd <PATH-TO-PROJECT-FOLDER>
```
2. To run the game using Maven use the following command
```bash
mvn clean
```

### Through an IDE
To run the game through an IDE you need to run the [MainApp](https://github.com/t3i8m/KEN2_project/blob/main/ken2/src/main/java/com/ken2/ui/MainApp.java).


## Rules
### Gameplay Overview
The game is played in turns. Players take turns performing the following steps:

#### **Step 1: Place a Marker**
1. The player chooses one of their **rings**.
2. A marker (chip) of their color is placed inside the chosen ring and flipped to the player's color.

#### **Step 2: Move the Ring**
- The player then moves the chosen ring in a straight line to another empty intersection.
- The ring can move over any number of empty intersections but **cannot jump over other rings**.

#### **Flipping Markers**
- If a ring moves over one or more markers, **all the markers it jumps over get flipped** to the opposite color.
- The flipping happens only if the markers are in a straight line and continuous without any gaps.

#### **Forming a Row of Five**
- If a player forms a row of five consecutive markers of their color (horizontally, vertically, or diagonally):
  1. The player **removes those five markers** from the board.
  2. The player then **removes one of their rings** from the board.
- The removed markers are not returned to the game.
- Removing a ring is the main objective; once a player removes **three rings**, they win the game.

### **Game End**
- The game ends when a player successfully removes three of their own rings from the board.
- That player is declared the **winner**.

### **Special Rules and Details**
- **Move Restrictions**: You cannot move a ring if it would land on another ring. The movement must end on an empty intersection.
- **Double Rows**: If creating a row of five simultaneously creates more rows, you must first complete the actions for the primary row before handling the additional rows.
- **Tie Handling**: If both players remove the third ring during the same turn, the game results in a **tie**.