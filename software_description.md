# Detailed Explanation of the Game Code

This documentation provides a detailed explanation of the Java classes involved in the game, focusing on their roles, methods, and interactions. The code is part of a game application with separate components for game logic and GUI.

## Table of Contents
1. [Overview](#overview)
2. [GameSimulation.java](#gamesimulationjava)
   - [Purpose](#purpose-1)
   - [Key Methods](#key-methods-1)
3. [GameState.java](#gamestatejava)
   - [Purpose](#purpose-2)
   - [Key Attributes](#key-attributes)
   - [Key Methods](#key-methods-2)
4. [GameEngine.java](#gameenginejava)
   - [Purpose](#purpose-3)
   - [Key Methods](#key-methods-3)
5. [MainApp.java](#mainappjava)
   - [Purpose](#purpose-4)
   - [GUI Components](#gui-components)
   - [Event Handling](#event-handling)
   - [Key Methods](#key-methods-4)
6. [How the Classes Interact](#how-the-classes-interact)

---

## Overview

The game application is structured into several Java classes:

- **GameSimulation.java**: Simulates all possible moves from a given disk position.
- **GameState.java**: Holds all the game state variables.
- **GameEngine.java**: Contains all the game logic and decision-making processes.
- **MainApp.java**: The GUI class that handles user interactions and displays the game board.

---

## GameSimulation.java

### Purpose
The `GameSimulation` class is responsible for simulating all possible moves from a given disk position on the game board. It calculates valid moves in all directions and helps in determining game logic, such as flipping coins and placing rings.

### Key Methods

- **Constructor**
  - `GameSimulation()`: Initializes the `allPossibleMoves` list, which will store possible moves.

- **startSimulation(Vertex[][] board, int xRingPosition, int yRingPosition)**
  - Simulates all possible moves from a given disk position.
  - Iterates through all possible directions and calculates moves along each diagonal.

- **getAllPossibleMoves()**
  - Returns the list of all possible moves calculated by the simulation.

- **getAllPossibleStartingRingPlaces(Vertex[][] board)**
  - Finds all available places to put a starting ring.
  - Iterates over the board and adds vertices without any play objects to the list.

- **getDirectionBetween(int startX, int startY, int targetX, int targetY)**
  - Determines the direction between two vertices.
  - Returns the `Direction` enum value that represents the direction from the start vertex to the target vertex.

- **simulateMove(Game_Board board, Vertex startVertex, Vertex targetVertex)**
  - Simulates a move from the given start to the target position.
  - Validates if the move is in a straight line and in a valid direction.
  - Returns a `Move` object if the move is valid.

- **flipCoins(ArrayList<Coin> coinFlips, Game_Board gameBoard)**
  - Flips the coins affected by a move.
  - Updates the game board accordingly.

---

## GameState.java

### Purpose
The `GameState` class holds all the variables needed by the game engine to manage the current state of the game. It tracks the number of rings, chips remaining, whose turn it is, and other stateful information.

### Key Attributes

- **ringsWhite**: Number of white rings remaining.
- **ringsBlack**: Number of black rings remaining.
- **chipsRemaining**: Total chips remaining in the game.
- **ringsPlaced**: Total number of rings placed on the board.
- **isWhiteTurn**: Boolean indicating if it's the white player's turn.
- **gameBoard**: Instance of `Game_Board`, representing the game board.
- **chipPlacement**: Boolean indicating if a chip placement is expected.
- **selectedRingVertex**: The vertex number of the selected ring.
- **selectedChipVertex**: The vertex number of the selected chip.
- **chipPlaced**: Boolean indicating if a chip has been placed in the current turn.
- **chipNumber**: List of chip vertex numbers.
- **chipRingVertex**: The vertex number where a chip was placed on a ring.

### Key Methods

- **Constructor**
  - `GameState()`: Initializes the game state with default values.

- **currentPlayerColor()**
  - Returns the color of the current player ("White" or "Black") based on `isWhiteTurn`.

- **updateRingCount(String playerColor)**
  - Decrements the ring count for the specified player color.

- **resetTurn()**
  - Toggles the turn to the next player.
  - Resets turn-specific variables.

---

## GameEngine.java

### Purpose
The `GameEngine` class contains all the game logic and decision-making processes. It interacts with `GameState` and `GameSimulation` to validate moves, update the game state, and provide methods for the GUI to interact with.

### Key Methods

- **Constructor**
  - `GameEngine()`: Initializes the `currentState`, `gameSimulation`, and vertex coordinates.

- **placeStartingRing(int vertex, String ringColor)**
  - Handles the logic for placing starting rings on the board.
  - Validates if the placement is valid.
  - Updates the game state accordingly.

- **availablePlacesForStartingRings()**
  - Returns a list of vertices where starting rings can be placed.

- **placeChip(int vertex, GraphicsContext gc)**
  - Handles the logic for placing a chip on the board.
  - Validates the placement based on game rules.
  - Updates the game state and the GUI.

- **checkPlaceRingVertex(int frmVertex, int toVertex, ArrayList<Integer> availableMoves)**
  - Validates if moving a ring from one vertex to another is allowed.
  - Checks if the target vertex is within the list of available moves.

- **possibleMoves(Vertex boardVertex)**
  - Calculates possible moves for a ring at a given vertex.
  - Uses `GameSimulation` to determine valid moves.

- **findClosestVertex(double xCoordinate, double yCoordinate)**
  - Determines the vertex number closest to the given screen coordinates.
  - Useful for translating mouse clicks to game actions.

- **showAlert(String title, String message)**
  - Displays an alert dialog with the specified title and message.
  - Used for informing the player about invalid moves or game events.

- **resetGame()**
  - Resets the game state and reinitializes the vertex coordinates.

---

## MainApp.java

### Purpose
The `MainApp` class is the main GUI application class. It handles user interactions, displays the game board, and communicates with the `GameEngine` to update the game state based on user actions.

### GUI Components
- **Canvas**: Used for drawing the game board and play objects (rings and chips).
- **ComboBox**: Allows the user to select between "Human Player" and "AI Player" for both white and black players.
- **Text**: Displays game information like turn indicators and remaining rings/chips.
- **Buttons**: "Reset" and "Undo" buttons to control the game flow.
- **Pane**: A container that holds the game board and allows for drawing indicators like possible moves.

### Event Handling
- **handleFieldClick(MouseEvent e, GraphicsContext gc)**
  - Handles mouse click events on the game board.
  - Determines which vertex was clicked.
  - Decides whether to place starting rings, chips, or move rings based on the game state.
  - Updates the GUI accordingly.

### Key Methods

- **start(Stage primaryStage)**
  - Sets up the GUI components.
  - Draws the initial game board.
  - Initializes event handlers for user interactions.

- **drawBoard(GraphicsContext gc)**
  - Draws the game board grid and lines.
  - Displays vertex numbers for debugging or informative purposes.

- **drawImage(Image img, int vertex, GraphicsContext gc)**
  - Draws an image (ring or chip) at the specified vertex on the canvas.

- **drawHighlighter(int vertex, boolean availability)**
  - Draws a circle indicator on the board to highlight possible moves or valid placement areas.
  - Green for available spots, red for unavailable.

- **removeCircleIndicators()**
  - Clears all the highlighter indicators from the board.

- **resetBoard()**
  - Resets the game to its initial state.
  - Clears the board and reinitializes game variables.

- **resetTurn()**
  - Updates the game state to switch to the next player's turn.
  - Updates the turn indicator text.

- **updateOnscreenText()**
  - Updates the GUI text elements to reflect the current game state.
  - Chips remaining.
  - Rings remaining for each player.

---

## How the Classes Interact

- **MainApp**: The entry point of the application. It sets up the GUI and handles user interactions.
- **GameEngine**: Acts as a mediator between the GUI and the game logic. It receives input from `MainApp` and updates the `GameState`.
- **GameState**: Holds the current state of the game, including the board, players' turns, and remaining pieces.
- **GameSimulation**: Used by the `GameEngine` to simulate possible moves and validate them according to the game rules.
