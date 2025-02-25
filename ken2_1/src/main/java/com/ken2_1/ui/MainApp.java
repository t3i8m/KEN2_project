package com.ken2_1.ui;

import com.ken2_1.Game_Components.Board.Coin;
import com.ken2_1.Game_Components.Board.Game_Board;
import com.ken2_1.Game_Components.Board.PlayObj;
import com.ken2_1.Game_Components.Board.Ring;
import com.ken2_1.Game_Components.Board.Vertex;
import com.ken2_1.bots.Bot;
import com.ken2_1.bots.DQN_BOT_ML.utils.Reward;
import com.ken2_1.engine.Direction;
import com.ken2_1.engine.GameEngine;
import com.ken2_1.engine.GameState;
import com.ken2_1.engine.Move;
import com.ken2_1.utils.BotFactory;
import com.ken2_1.utils.Player;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.util.Duration;


public class MainApp extends Application {

    private GameEngine gameEngine;

    // ASSETS
    private Image ringBImage = new Image("file:ken2_1/assets/black ring.png");
    private static Image chipBImage = new Image("file:ken2_1/assets/black chip.png");
    private Image ringWImage = new Image("file:ken2_1/assets/white ring.png");
    private static Image chipWImage = new Image("file:ken2_1/assets/white chip.png");

    // COMPONENTS
    private static int fieldDimension = 470;
    private static int pieceDimension = 37;

    private Color inactiveScoreRingColor = Color.rgb(100, 60, 0);

    private GridPane root;
    private Canvas playObjectCanvas;
    private Pane fieldPane;
    private Canvas strengthIndicator;

    // TEXTS
    private Text ringBlackRemainingText;
    private Text ringWhiteRemainingText;
    private Text chipsWhiteText;
    private Text chipsBlackText;
    private Text chipsRemainText;
    private Text whiteWinsText;
    private Text blackWinsText;
    private Text drawText;
    private Text gamesPlayedText;
    private GameState newState; 
    private GameState previuosState;
    private double reward = 0.0;


    private Text turnIndicator = new Text("White's Turn");

    // COMBOBOXES
    private ComboBox whitePlayerComboBox;
    private ComboBox blackPlayerComboBox;
    

    private ArrayList<Integer> highlightedVertices;
    private int whiteScore = 0;
    private int blackScore = 0;
    private int ChipsRemoved = 5;
    private boolean CheckPossibleChipsToRemove = true;
    private boolean FirstClickOnCoin = true;
    private Circle[] whiteScoreCircle = new Circle[3];
    private Circle[] blackScoreCircle = new Circle[3];
    // Player List
    private int currentPlayerIndex = 0;
    private Player whitePlayer;
    private Player blackPlayer;
    private int whiteWins = 0; 
    private int blackWins = 0; 
    private int draws = 0;
    private int gamesPlayed = 0;
    private boolean isGameOver=false;
    private Move currMove;

    // FLAGS
    private boolean isGameStarted = false;
    private Vertex lastMoveStartVertex = null;
    private Vertex lastMoveEndVertex = null;

    // BUTTONS
    private Button startGameButton;
    ///OTHER
    private List<Integer> winningChips = new ArrayList<>();
    private Direction direction;
    private int chipsToRemove = 5;
    /**
     * Sets up the game stage and initializes UI components.
     *
     * @param primaryStage The primary stage for this application.
     * @throws Exception if an error occurs during initialization.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage primaryStage) throws Exception {

        gameEngine = new GameEngine();
        previuosState = gameEngine.currentState.clone();

        primaryStage.setTitle("Yinsh Game");
        primaryStage.setWidth(950);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);

        root = new GridPane();
        root.setPadding(new Insets(5, 5, 5, 5));
        root.setVgap(10);
        root.setHgap(10);

        root.setStyle("-fx-background-image: url('file:ken2_1/assets/light_bg.jpg'); " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center;");

        Scene scene = new Scene(root, 1010, 600, Color.GRAY);
        Canvas gameBoardCanvas = new Canvas();

        gameBoardCanvas.setWidth(fieldDimension);
        gameBoardCanvas.setHeight(fieldDimension);

        this.playObjectCanvas = new Canvas();

        playObjectCanvas.setWidth(fieldDimension);
        playObjectCanvas.setHeight(fieldDimension);


        GraphicsContext gcB = gameBoardCanvas.getGraphicsContext2D();
        GraphicsContext gcP = playObjectCanvas.getGraphicsContext2D();

        drawBoard(gcB);
        playObjectCanvas.setOnMouseClicked((MouseEvent e) -> {
            if (isGameStarted) {
                handleFieldClick(e, gcP);
            }
        });

        // info elements
        whitePlayerComboBox = new ComboBox<>();
        whitePlayerComboBox.getItems().add("Human Player");
        whitePlayerComboBox.getItems().add("RuleBased Bot");
        whitePlayerComboBox.getItems().add("AlphaBeta Bot");
        whitePlayerComboBox.getItems().add("DQN Bot");
        whitePlayerComboBox.getItems().add("ML Alpha Beta");


        whitePlayerComboBox.getSelectionModel().selectFirst();
        whitePlayerComboBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #D7CCC8, #8D6E63);" + // Dark brown gradient
                        "-fx-text-fill: white;" + // White text
                        "-fx-font-weight: bold;" + // Bold text
                        "-fx-font-size: 12px;" + // Text size
                        "-fx-border-radius: 5;" + // Rounded corners
                        "-fx-background-radius: 5;" + // Rounded background
                        "-fx-padding: 5 10;" + // Padding
                        "-fx-cursor: hand;" // Pointer cursor
        );

        blackPlayerComboBox = new ComboBox<>();
        blackPlayerComboBox.getItems().add("Human Player");
        blackPlayerComboBox.getItems().add("RuleBased Bot");
        blackPlayerComboBox.getItems().add("AlphaBeta Bot");
        blackPlayerComboBox.getItems().add("DQN Bot");
        blackPlayerComboBox.getItems().add("ML Alpha Beta");


        blackPlayerComboBox.getSelectionModel().selectFirst();
        blackPlayerComboBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #D7CCC8, #8D6E63);" + // Dark brown gradient
                        "-fx-text-fill: white;" + // White text
                        "-fx-font-weight: bold;" + // Bold text
                        "-fx-font-size: 12px;" + // Text size
                        "-fx-border-radius: 5;" + // Rounded corners
                        "-fx-background-radius: 5;" + // Rounded background
                        "-fx-padding: 5 10;" + // Padding
                        "-fx-cursor: hand;" // Pointer cursor
        );

        whitePlayerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (!isGameStarted) {
                selectPlayer("White", (String)whitePlayerComboBox.getSelectionModel().getSelectedItem());
            } else {
                whitePlayerComboBox.getSelectionModel().select(oldValue);
            }
        });

        blackPlayerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (!isGameStarted) {
                selectPlayer("Black", (String)blackPlayerComboBox.getSelectionModel().getSelectedItem());
            } else {
                blackPlayerComboBox.getSelectionModel().select(oldValue);
            }
        });


        Button helpButton = new Button("?");
        helpButton.setStyle(
                "-fx-background-color: #0073e6; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 15; " +
                        "-fx-padding: 5;"
        );
        helpButton.setOnAction(e -> showGameRules());
        root.add(helpButton, 8, 0);



        strengthIndicator = new Canvas(fieldDimension, 20);
        root.add(strengthIndicator, 1, 0, 5, 1);
        updateStrengthIndicator();

        startGameButton = new Button("Start Game");
        startGameButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #D7CCC8, #8D6E63);" + // Dark brown gradient
                        "-fx-text-fill: white;" + // White text
                        "-fx-font-weight: bold;" + // Bold text
                        "-fx-font-size: 14px;" + // Text size
                        "-fx-border-radius: 5;" + // Rounded corners
                        "-fx-background-radius: 5;" + // Rounded background
                        "-fx-padding: 5 10;" + // Padding
                        "-fx-cursor: hand;" // Pointer cursor
        );
        startGameButton.setOnAction(e -> startGame(gcP));

        Text whitePlayerLabel = new Text();
        whitePlayerLabel.setText("WHITE SIDE");
        whitePlayerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        gamesPlayedText = new Text("Games Played: 0");
        gamesPlayedText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        root.add(gamesPlayedText, 4, 6);

        Text blackPlayerLabel = new Text();
        blackPlayerLabel.setText("BLACK SIDE ");
        blackPlayerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20)); // Adjust size (20)
        chipsRemainText = new Text();

        chipsBlackText = new Text();
        chipsWhiteText = new Text();
        ringBlackRemainingText = new Text();
        ringWhiteRemainingText = new Text();
        updateOnscreenText();

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> restartGame());
        resetButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #D7CCC8, #8D6E63);" + // Dark brown gradient
                        "-fx-text-fill: white;" + // White text
                        "-fx-font-weight: bold;" + // Bold text
                        "-fx-font-size: 14px;" + // Text size
                        "-fx-border-radius: 5;" + // Rounded corners
                        "-fx-background-radius: 5;" + // Rounded background
                        "-fx-padding: 5 10;" + // Padding
                        "-fx-cursor: hand;" // Pointer cursor
        );

        drawText = new Text("Draws: 0");
        drawText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        root.add(drawText, 3, 6);


        whiteWinsText = new Text("White Wins: 0");
        whiteWinsText.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        blackWinsText = new Text("Black Wins: 0");
        blackWinsText.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        turnIndicator.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        fieldPane = new Pane();
        fieldPane.setPrefSize(fieldDimension, fieldDimension);
        root.add(fieldPane, 1, 1, 5, 5);

        // score elements
        Circle scoreRingeW1 = makeScoreCircle();
        Circle scoreRingeW2 = makeScoreCircle();
        Circle scoreRingeW3 = makeScoreCircle();
        Circle scoreRingeB1 = makeScoreCircle();
        Circle scoreRingeB2 = makeScoreCircle();
        Circle scoreRingeB3 = makeScoreCircle();

        // Initialize white score rings
        whiteScoreCircle[0] = makeScoreCircle();
        whiteScoreCircle[1] = makeScoreCircle();
        whiteScoreCircle[2] = makeScoreCircle();

        // Initialize black score rings
        blackScoreCircle[0] = makeScoreCircle();
        blackScoreCircle[1] = makeScoreCircle();
        blackScoreCircle[2] = makeScoreCircle();

        root.add(whitePlayerLabel, 0, 0);
        root.add(blackPlayerLabel, 7, 0);
        root.add(whitePlayerComboBox, 0, 1);
        root.add(blackPlayerComboBox, 7, 1);
        root.add(gameBoardCanvas, 1, 1, 5, 5);
        root.add(playObjectCanvas, 1, 1, 5, 5);
        root.add(whiteWinsText, 0, 6);
        root.add(blackWinsText, 7, 6);
        root.add(chipsWhiteText, 0, 5);
        root.add(chipsBlackText, 7, 5);
        chipsRemainText = new Text("Chips Remaining number: 51 ");
        chipsRemainText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        root.add(chipsRemainText,1,6);
        ringWhiteRemainingText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        ringWhiteRemainingText.setText("Black Rings Placed: 0");
        GridPane.setMargin(ringWhiteRemainingText, new Insets(0, 0, -60, 20));
        root.add(ringWhiteRemainingText, 1, 0);


        ringBlackRemainingText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        ringBlackRemainingText.setText("Black Rings Placed: 0");
        GridPane.setMargin(ringBlackRemainingText, new Insets(0, 0, -60, -90));
        root.add(ringBlackRemainingText, 4, 0);

        root.add(scoreRingeW1, 0, 2);
        root.add(scoreRingeW2, 0, 3);
        root.add(scoreRingeW3, 0, 4);
        root.add(scoreRingeB1, 7, 2);
        root.add(scoreRingeB2, 7, 3);
        root.add(scoreRingeB3, 7, 4);
        root.add(whiteScoreCircle[0], 0, 2);
        root.add(whiteScoreCircle[1], 0, 3);
        root.add(whiteScoreCircle[2], 0, 4);

        root.add(blackScoreCircle[0], 7, 2);
        root.add(blackScoreCircle[1], 7, 3);
        root.add(blackScoreCircle[2], 7, 4);

        root.add(resetButton, 0, 7);
        root.add(turnIndicator, 3, 7);

        root.add(startGameButton, 7, 7);
        primaryStage.setScene(scene);
        primaryStage.show();

        highlightedVertices = new ArrayList<>();

    }
    private void showGameRules() {
        Alert rulesAlert = new Alert(Alert.AlertType.INFORMATION);
        rulesAlert.setTitle("Game Rules");
        rulesAlert.setHeaderText("Yinsh Gameplay Rules");

        rulesAlert.setContentText(
                "Gameplay Overview\n" +
                        "The game is played in turns. Players take turns performing the following steps:\n\n" +

                        "Step 1: Place a Marker\n" +
                        "1. The player chooses one of their rings.\n" +
                        "2. A marker (chip) of their color is placed inside the chosen ring and flipped to the player's color.\n\n" +

                        "Step 2: Move the Ring\n" +
                        "- The player then moves the chosen ring in a straight line to another empty intersection.\n" +
                        "- The ring can move over any number of empty intersections but cannot jump over other rings.\n\n" +

                        "Flipping Markers\n" +
                        "- If a ring moves over one or more markers, all the markers it jumps over get flipped to the opposite color.\n" +
                        "- The flipping happens only if the markers are in a straight line and continuous without any gaps.\n\n" +

                        "Forming a Row of Five\n" +
                        "- If a player forms a row of five consecutive markers of their color (horizontally, vertically, or diagonally):\n" +
                        "  1. The player removes those five markers from the board and put them back in the pool.\n" +
                        "  2. The player then removes one of their rings from the board.\n" +
                        "- The removed markers are not returned to the game.\n" +
                        "- Removing a ring is the main objective; once a player removes three rings, they win the game.\n\n" +

                        "Game End\n" +
                        "- The game ends when a player successfully removes three of their own rings from the board.\n" +
                        "- That player is declared the winner.\n\n" +

                        "Special Rules and Details\n" +
                        "- Move Restrictions: You cannot move a ring if it would land on another ring. The movement must end on an empty intersection.\n" +
                        "- Double Rows: If creating a row of five simultaneously creates more rows, you must first complete the actions for the primary row before handling the additional rows.\n" +
                        "- Tie Handling: If both players remove the third ring during the same turn, the game results in a tie."
        );

        rulesAlert.setResizable(true);
        rulesAlert.getDialogPane().setMinWidth(600);
        rulesAlert.getDialogPane().setMinHeight(400);

        rulesAlert.showAndWait();
    }
    /**
     * Starts the game by initializing the game state and disabling controls.
     *
     * @param gcP The graphics context for drawing objects.
     */

    private void startGame(GraphicsContext gcP) {
        this.isGameStarted = true;
        if (whitePlayer == null) {
            whitePlayer = new Player("White", "Human");
        }
        if (blackPlayer == null) {
            blackPlayer = new Player("Black", "Human");
        }
        startGameButton.setDisable(true);
        whitePlayerComboBox.setDisable(true);
        blackPlayerComboBox.setDisable(true);
        gameEngine.resetGame();
        gcP.clearRect(0, 0, gcP.getCanvas().getWidth(), gcP.getCanvas().getHeight());
        updateOnscreenText();

        currentPlayerIndex = 0;

        playerTurn();
    }
    /**
     * Handles the logic for each player's turn in the game.
     */
    private void playerTurn() {
        if (isGameOver) {
            return;
        }
        Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;
        GraphicsContext gc = playObjectCanvas.getGraphicsContext2D();
        removeCircleIndicators();
        // System.out.println("Current Player Index: " + currentPlayerIndex);

        if (currentPlayer.isBot()) {
            PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
            pause.setOnFinished(event -> botTurn(gc));
            pause.play();

        } else {
            if (gameEngine.currentState.ringsPlaced < 10) {
                if (gameEngine.currentState.ringsPlaced == 10) {
                    removeCircleIndicators();
                } else {
                    GUIavailablePlacesForStartRings(gc);
                }
            }
        }
    }

    /**
     * Switches the current player's turn.
     */
    private void switchPlayer() {
        gameEngine.currentState.isWhiteTurn = !gameEngine.currentState.isWhiteTurn;
        currentPlayerIndex = gameEngine.currentState.isWhiteTurn ? 0 : 1;
    }
    /**
     * Selects a player based on their color and type (human or bot).
     *
     * @param color The color of the player ("White" or "Black").
     * @param selectedItem The type of player ("Human Player" or bot type).
     */

    private void selectPlayer(String color, String selectedItem) {
        BotFactory botFactory = new BotFactory();

        if ("Human Player".equals(selectedItem)) {
            if (color.equalsIgnoreCase("White")) {
                whitePlayer = new Player(color, "Human");
            } else {
                blackPlayer = new Player(color, "Human");
            }
        } else {
            Bot bot = botFactory.getBot(selectedItem, color);
            if (color.equalsIgnoreCase("White")) {
                whitePlayer = new Player(color, bot, selectedItem);
            } else {
                blackPlayer = new Player(color, bot, selectedItem);
            }
        }
    }
    /**
     * Handles mouse click events on the game board.
     *
     * @param e The mouse event triggering the click.
     * @param gc The graphics context for rendering changes.
     */

    private void handleFieldClick(MouseEvent e, GraphicsContext gc) {

        int vertex = gameEngine.findClosestVertex(e.getX(), e.getY());
        Vertex v = gameEngine.currentState.gameBoard.getVertex(vertex);
        if (vertex < 0) return;
        if (gameEngine.GetWinningRing()) {
            WinningCase(vertex, gc);
            return;
        }

        if (!gameEngine.GetWinningRing() && !gameEngine.isInChipRemovalMode() ) {
            if (gameEngine.currentState.ringsPlaced < 10) {
                GUIplaceStartingRing(vertex, gc);
                GUIavailablePlacesForStartRings(gc);
                if (gameEngine.currentState.ringsPlaced == 10) {
                    removeCircleIndicators();
                }
            } else {
                removeCircleIndicators();
                handleChipAndRingPlacement(vertex, gc);
            }
        }
        updateOnscreenText();
    }
    List <Integer> nadoEtiChips  = new ArrayList<>();
    /**
     * Handles the logic for the winning case.
     *
     * @param vertex The vertex where the winning condition is triggered.
     * @param gc The graphics context for drawing.
     */
    public void WinningCase(int vertex, GraphicsContext gc){
        try{
            handleWinningRing(vertex, gc);
            String WinningColor = gameEngine.getWinningColor();
            if(CheckPossibleChipsToRemove){
                this.winningChips = new ArrayList<>(gameEngine.getWinningChips());
                for (Integer chip : this.winningChips){
                    for(Direction direction: Direction.values()) {
                        int ChipCounter = gameEngine.countChipsInOneDirection(chip, WinningColor, direction.getDeltaX(), direction.getDeltaY());
                        if (ChipCounter >= 4) {
                            if (!nadoEtiChips.contains(chip)) {
                                nadoEtiChips.add(chip);
                            }
                            for (Integer thisChips : gameEngine.getChipsInDirection(chip, WinningColor, direction.getDeltaX(), direction.getDeltaY())) {
                                if (!nadoEtiChips.contains(thisChips)) {
                                    nadoEtiChips.add(thisChips);
                                }
                            }
                        }
                    }
                }
                highlightRemovableChips(nadoEtiChips);
                CheckPossibleChipsToRemove = false;
            }

            Integer RemoveChipVertex = vertex;
            if (gameEngine.currentState.chipNumber.contains(RemoveChipVertex) && nadoEtiChips.contains(RemoveChipVertex)) {
                if(FirstClickOnCoin){
                    for(Direction direction: Direction.values()) {
                        int ChipCounter = gameEngine.countChipsInOneDirection(vertex, WinningColor, direction.getDeltaX(), direction.getDeltaY());
                        if (ChipCounter >= 4) {
                            nadoEtiChips.clear();
                            nadoEtiChips.addAll(gameEngine.getChipsInDirection(vertex, WinningColor, direction.getDeltaX(), direction.getDeltaY()));
                            nadoEtiChips = new ArrayList<>(nadoEtiChips.subList(0, 4));
                            FirstClickOnCoin = false;
                        }
                    }
                    if(FirstClickOnCoin) return;
                }
                Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;
                if(currentPlayer.isBot()){
                } else{
                    handleChipRemove(vertex, gc);
                    ChipsRemoved--;
                    gameEngine.currentState.chipNumber.remove(RemoveChipVertex);

                }

                if (ChipsRemoved == 0) {
                    gameEngine.setWinningRing(false);
                    gameEngine.setChipRemovalMode(false);
                    ChipsRemoved = 5;
                    CheckPossibleChipsToRemove = true;
                    FirstClickOnCoin = true;
                    nadoEtiChips.clear();
                    removeCircleIndicators();
                }
            }
        } catch(Exception ex){
            restartGame();
        }
        
    }
    /**
     * Handles the removal of a chip from the board.
     *
     * @param vertex The vertex where the chip is located.
     * @param gc The graphics context for rendering updates.
     */
    public void handleChipRemove(int vertex, GraphicsContext gc) {
        if (!gameEngine.getWinningChips().contains(vertex)) {
            return;
        }
        Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;

        Vertex v = gameEngine.currentState.gameBoard.getVertex(vertex);
        String currColor = v.getCoin().getColour().toLowerCase();
        if (v != null && v.hasCoin() && v.getCoin().getColour().toLowerCase().equalsIgnoreCase(gameEngine.getWinningColor().toLowerCase())) {
            v.setPlayObject(null);
            gc.clearRect(
                    gameEngine.vertexCoordinates[vertex][0] + pieceDimension  / 10,
                    gameEngine.vertexCoordinates[vertex][1] + pieceDimension  / 10 ,pieceDimension, pieceDimension
            );
            gameEngine.currentState.chipsRemaining+=1;

            chipsToRemove--;
            removeCircleIndicators();
            gameEngine.getWinningChips().remove(Integer.valueOf(vertex));
            List<Integer> adjacentVertices = gameEngine.getAdjacentVertices(vertex);
            List<Integer> validRemovableChips = new ArrayList<>();
            for (int adjVertex : adjacentVertices) {
                if (winningChips.contains(adjVertex)) {
                    Vertex adjV = gameEngine.currentState.gameBoard.getVertex(adjVertex);
                    if (adjV != null && adjV.hasCoin() 
                            && adjV.getCoin().getColour().equalsIgnoreCase(gameEngine.getWinningColor())) {
                        validRemovableChips.add(adjVertex);
                    }
                }
            }
            highlightRemovableChips(validRemovableChips);


            if (chipsToRemove <= 0) {
                gameEngine.setChipRemovalMode(false);
                gameEngine.setRingSelectionMode(false);
                gameEngine.setWinningColor("");
                winningChips.clear();
                gameEngine.getWinningChips().clear();

                if(gameEngine.currentState.getCurrentColor().toLowerCase().equals(currentPlayer.getColor().toLowerCase())){
                    resetTurn();

                }

            }

        } else {
        }
    }
    /**
     * Handles the removal of a winning ring from the board.
     *
     * @param vertex The vertex where the ring is located.
     * @param gc The graphics context for rendering updates.
     */
    public void handleWinningRing(int vertex, GraphicsContext gc) {
        Vertex v = gameEngine.currentState.gameBoard.getVertex(vertex);
        if (v != null && v.hasRing() && v.getRing().getColour().toLowerCase().equals(gameEngine.getWinningColor().toLowerCase())) {
            moveRingToThePanel(gameEngine.getWinningColor());
            v.setRing(null);
            gc.clearRect(gameEngine.vertexCoordinates[vertex][0], gameEngine.vertexCoordinates[vertex][1], pieceDimension, pieceDimension);
            ////CHIPS IN A ROW
            gameEngine.findAndSetAllWinningChips(gameEngine.getWinningColor());
            gameEngine.currentState.setAllPossibleCoinsToRemove(gameEngine.getWinningChips());
            if (gameEngine.getWinningChips().size() > 5){
               // GameAlerts.


            }
            gameEngine.setRingSelectionMode(false); // Exit ring selection mode
            gameEngine.setChipRemovalMode(true);
            Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;

            chipsToRemove = 5;

        }

    }
    /**
     * Highlights removable rings on the board.
     *
     * @param removableRings A list of vertices where rings can be removed.
     */
    private void highlightRemovableRings(List<Integer> removableRings) {
        for (Integer ringVertex : removableRings) {
            drawHighlighter(ringVertex, true); 
        }
    }
    /**
     * Retrieves a list of removable rings for the current player.
     *
     * @return A list of vertex indices of removable rings.
     */
    private List<Integer> getRemovableRings() {
        List<Integer> removableRings = new ArrayList<>();
        Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;

        for (Integer vertex : gameEngine.getWinningRings( gameEngine.getWinningColor().toLowerCase())) {
            Vertex v = gameEngine.currentState.gameBoard.getVertex(vertex);
            if (v != null && v.hasRing() 
                    && v.getRing().getColour().equalsIgnoreCase(gameEngine.getWinningColor())) {
                removableRings.add(vertex);
            }
        }
        return removableRings;
    }
    /**
     * Moves the ring to the player's score panel and checks for a win condition.
     *
     * @param playerColor The color of the player ("white" or "black").
     */
    private void moveRingToThePanel(String playerColor) {
        if (playerColor.equalsIgnoreCase("white")) {
            if (whiteScore < 3) {
                whiteScoreCircle[whiteScore].setStroke(Color.WHITE);
                whiteScore++;

                // Check if White has won
                if (whiteScore == 3) {
                    this.isGameOver=true;
                    whiteWins++; 
                    gameEngine.currentState.winner = "white";

                    updateWinsText();
                    GameAlerts.alertGameEnd("White player wins!");
                }
            }
        } else {
            if (blackScore < 3) {
                blackScoreCircle[blackScore].setStroke(Color.BLACK); // Make the ring appear active
                blackScore++;

                // Check if Black has won
                if (blackScore == 3) {
                    this.isGameOver=true;
                    gameEngine.currentState.winner = "black";
                    blackWins++; 
                    updateWinsText();
                    GameAlerts.alertGameEnd("Black player wins!");
                    return;
                }
            }
        }
    }
    /**
     * Places the starting ring on the board.
     *
     * @param vertex The vertex where the ring is placed.
     * @param gc The graphics context for rendering.
     */
    private void GUIplaceStartingRing(int vertex, GraphicsContext gc) {
        Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;

        String ringColor = currentPlayer.getColor();
        if (gameEngine.placeStartingRing(vertex, ringColor)) {

            Image ringImage = ringColor.equals("white") ? ringWImage : ringBImage;
            drawImage(ringImage, vertex, gc, true);
            resetTurn();

        }
    }
    /**
     * Highlights available places for starting rings.
     *
     * @param gc The graphics context for rendering.
     */
    private void GUIavailablePlacesForStartRings(GraphicsContext gc) {

        ArrayList<Vertex> aVertices = gameEngine.availablePlacesForStartingRings();
        for (Vertex v : aVertices) {
            boolean isVnull = (v != null);
            if (isVnull) {
                drawHighlighter(v.getVertextNumber(), true);
            }
        }
    }
    /**
     * Handles chip and ring placement during gameplay.
     *
     * @param vertex The selected vertex on the board.
     * @param gc The graphics context for rendering.
     */
    private void handleChipAndRingPlacement(int vertex, GraphicsContext gc) {
        Vertex boardVertex = gameEngine.currentState.gameBoard.getVertex(vertex);
        // If the player clicks on their own ring
        if (boardVertex.hasRing() && boardVertex.getRing().getColour().equals(gameEngine.currentState.currentPlayerColor())) {
            gameEngine.currentState.selectedChipVertex = vertex;
            removeCircleIndicators();
            ArrayList<Move> possibleMoves = gameEngine.possibleMoves(boardVertex);
            highlightPossibleMoves(possibleMoves);
        }
        // Else if the player has already selected a ring to move
        else if (gameEngine.currentState.selectedChipVertex != -1) {
            int[] Move_Valid = new int[1];
            Vertex selectedVertex = gameEngine.currentState.gameBoard.getVertex(gameEngine.currentState.selectedChipVertex);

            Move currentMove = gameEngine.gameSimulation.simulateMove(gameEngine.currentState.gameBoard,
                    gameEngine.currentState.gameBoard.getVertex(gameEngine.currentState.selectedChipVertex),
                    gameEngine.currentState.gameBoard.getVertex(vertex));

            ArrayList<Move> normalMove = gameEngine.possibleMoves(selectedVertex);
            boolean isValidMove = normalMove.stream().anyMatch(move -> {
                Vertex targetVertex = move.getTargetVertex(gameEngine);
                return targetVertex != null && targetVertex.getVertextNumber()==vertex;
            });
            if(!isValidMove){
                GameAlerts.alertInvalidMove();
                return;
            }

            moveRing(gameEngine.currentState.selectedChipVertex, vertex, gc, Move_Valid, currentMove);

            if (Move_Valid[0] == 1){
                String color = gameEngine.currentState.currentPlayerColor();
                resetTurn();

            }
        } else {
            GameAlerts.alertInvalidMove();

        }

    }
    /**
     * Highlights possible moves by drawing indicators on the board.
     *
     * @param possibleMoves The list of possible moves.
     */
    private void highlightPossibleMoves(ArrayList<Move> possibleMoves) {
        highlightedVertices.clear();
        for (Move move : possibleMoves) {
            int vertexNumber = gameEngine.currentState.gameBoard.getVertexNumberFromPosition(move.getXposition(), move.getYposition());
            if (vertexNumber == -1) {
                continue;
            }
            drawHighlighter(vertexNumber, true);
        }
    }
    /**
     * Highlights removable chips by drawing indicators on them.
     *
     * @param removableChips The list of removable chip vertices.
     */
    private void highlightRemovableChips(List<Integer> removableChips) {
        removeCircleIndicators(); 
        for (Integer chipVertex : removableChips) {
            drawHighlighter(chipVertex, true); 
        }
    }


    /**
     * Moves a ring from one vertex to another and updates the game state.
     *
     * @param fromVertex The starting vertex.
     * @param toVertex The destination vertex.
     * @param gc The graphics context for rendering.
     * @param Move_Valid An array indicating if the move is valid.
     * @param currentMove The current move being executed.
     */
    private void moveRing(int fromVertex, int toVertex, GraphicsContext gc, int[] Move_Valid, Move currentMove) {
        try{
            if (gameEngine.isInChipRemovalMode()==false && gameEngine.isInRingRemovalMode()==false){
                currMove = currentMove;

                Vertex sourceVertex = gameEngine.currentState.gameBoard.getVertex(fromVertex);
                Vertex targetVertex = gameEngine.currentState.gameBoard.getVertex(toVertex);
        
                if (targetVertex.hasRing()) {

                    Move_Valid[0] = 0;

                }
        
                if (targetVertex.hasCoin()) {
                    Move_Valid[0] = 0;
                }
        
                Move_Valid[0] = 1;
                gameEngine.placeChip(fromVertex);
                Ring ringToMove = (Ring) sourceVertex.getRing();

                if (ringToMove != null) {
                    sourceVertex.setRing(null);
                    this.lastMoveStartVertex = sourceVertex;
                    this.lastMoveEndVertex = targetVertex;
                    gc.clearRect(gameEngine.vertexCoordinates[fromVertex][0], gameEngine.vertexCoordinates[fromVertex][1], pieceDimension, pieceDimension);
        
                    if (sourceVertex.hasCoin()) {
                        Coin existingChip = (Coin) sourceVertex.getCoin();

                    }
                    ArrayList<Coin> flippedCoins = currentMove.getFlippedCoins();
                    ArrayList<Vertex> vertexesOfFlippedCoins = new ArrayList<>();
                    if (!flippedCoins.isEmpty()) {
                        for (Coin coinToFlip : flippedCoins) {

                            Vertex currVertex = gameEngine.currentState.gameBoard.getVertex(coinToFlip.getVertex());
                            if(currVertex==null){
                                
                                Vertex newVert = gameEngine.currentState.gameBoard.getVertex(coinToFlip.getVertex());
                                if(!vertexesOfFlippedCoins.contains(newVert)){
                                    vertexesOfFlippedCoins.add(newVert);
                                }
                                continue;
                            }

                            if(!vertexesOfFlippedCoins.contains(currVertex)){
                                vertexesOfFlippedCoins.add(currVertex);
                            }

                        }
                        gameEngine.gameSimulation.flipCoinsByVertex(vertexesOfFlippedCoins, gameEngine.currentState.gameBoard);

                    }

                    targetVertex.setRing(ringToMove);
                    Image ringImage = ringToMove.getColour().toLowerCase().equals("white") ? ringWImage : ringBImage;
                    gc.drawImage(ringImage, gameEngine.vertexCoordinates[toVertex][0], gameEngine.vertexCoordinates[toVertex][1], pieceDimension, pieceDimension);
                    updateGameBoard(gameEngine.currentState.getGameBoard(), playObjectCanvas.getGraphicsContext2D());
                    // System.out.println("AFTER:"+gameEngine.currentState.gameBoard.strMaker());

                    gameEngine.currentState.chipRingVertex = -1;
                    gameEngine.currentState.chipsRemaining-=1;
                    gameEngine.currentState.chipPlaced = false;
                    gameEngine.currentState.selectedRingVertex = -1;
                    gameEngine.currentState.updateChipsRingCountForEach();
                    if(!vertexesOfFlippedCoins.contains(sourceVertex)){
                        vertexesOfFlippedCoins.add(sourceVertex);

                    }
                    gameEngine.currentState.setVertexesOfFlippedCoins(vertexesOfFlippedCoins);
                    if(gameEngine.currentState.chipsRemaining<=0){
                        showDrawAlert();
                    }
        
                    String color = gameEngine.currentState.isWhiteTurn ? "white" : "black";
                    if (gameEngine.win(gameEngine.currentState.getVertexesOfFlippedCoins())){
                        String currPlayerColor = gameEngine.getWinningColor(); 
                        Player currentPlayer;
                        List<Integer> removableRings = getRemovableRings();
                        highlightRemovableRings(removableRings);
        
                        if (whitePlayer.getColor().toLowerCase().equalsIgnoreCase(currPlayerColor.toLowerCase())) {
                            currentPlayer = whitePlayer;
                        } else {
                            currentPlayer = blackPlayer;
                        }
                        if(currentPlayer.isBot()){
                            Bot activeBot = currentPlayer.getBot();
                            Vertex vertexToRemoveBOT = activeBot.removeRing(gameEngine.currentState);
                            handleWinningRing(vertexToRemoveBOT.getVertextNumber(), gc);
                            ArrayList<Integer> allRemoveChips = activeBot.removeChips(gameEngine.currentState);
                            highlightRemovableChips(allRemoveChips);
                            for (Integer vert : allRemoveChips){
                                handleChipRemove(vert, gc);
                            }
    
                            gameEngine.setWinningRing(false);
                            gameEngine.setChipRemovalMode(false);
                            ChipsRemoved = 5;
                            CheckPossibleChipsToRemove = true;
                            FirstClickOnCoin = true;
                            nadoEtiChips.clear();
    
                        } else{
                            GameAlerts.alertRowCompletion(color);
                        }
                    }
                } else {
                    GameAlerts.alertNoRing();
                }
            }
        } catch(Exception ex){

            this.isGameOver=true;
            restartGame();
        }
        
        }
    /**
     * Draws a dashed line indicating the last move.
     *
     * @param gc The graphics context for rendering.
     */
    private void drawDashedLine(GraphicsContext gc) {
        if (lastMoveStartVertex == null || lastMoveEndVertex == null) {
            return;
        }
        int[] startCoords = gameEngine.getVertexCoordinates()[lastMoveStartVertex.getVertextNumber()];
        int[] endCoords = gameEngine.getVertexCoordinates()[lastMoveEndVertex.getVertextNumber()];
        if (gameEngine.currentState.isWhiteTurn) {
            gc.setStroke(Color.BLUE);
        } else {
            gc.setStroke(Color.RED);
        }
        gc.setLineDashes(10);
        gc.setLineWidth(4);
        gc.strokeLine(
                startCoords[0] + pieceDimension / 2,
                startCoords[1] + pieceDimension / 2,
                endCoords[0] + pieceDimension / 2,
                endCoords[1] + pieceDimension / 2
        );
    }

    /**
     * Draws the game board with various lines connecting vertices and vertex numbers.
     *
     * @param gc The graphics context used for rendering.
     */
    private void drawBoard(GraphicsContext gc) {

        gc.setStroke(Color.rgb(0, 70, 0));
        gc.setLineWidth(3);

        int[][] vertexCoordinates = gameEngine.getVertexCoordinates();

        // up slant lines
        gc.strokeLine(vertexCoordinates[4][0]+ pieceDimension /2, vertexCoordinates[4][1]+pieceDimension/2,
                vertexCoordinates[28][0]+pieceDimension/2, vertexCoordinates[28][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[0][0]+pieceDimension/2, vertexCoordinates[0][1]+pieceDimension/2,
                vertexCoordinates[47][0]+pieceDimension/2, vertexCoordinates[47][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[1][0]+pieceDimension/2, vertexCoordinates[1][1]+pieceDimension/2,
                vertexCoordinates[57][0]+pieceDimension/2, vertexCoordinates[57][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[2][0]+pieceDimension/2, vertexCoordinates[2][1]+pieceDimension/2,
                vertexCoordinates[66][0]+pieceDimension/2, vertexCoordinates[66][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[3][0]+pieceDimension/2, vertexCoordinates[3][1]+pieceDimension/2,
                vertexCoordinates[74][0]+pieceDimension/2, vertexCoordinates[74][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[9][0]+pieceDimension/2, vertexCoordinates[9][1]+pieceDimension/2,
                vertexCoordinates[75][0]+pieceDimension/2, vertexCoordinates[75][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[10][0]+pieceDimension/2, vertexCoordinates[10][1]+pieceDimension/2,
                vertexCoordinates[81][0]+pieceDimension/2, vertexCoordinates[81][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[18][0]+pieceDimension/2, vertexCoordinates[18][1]+pieceDimension/2,
                vertexCoordinates[82][0]+pieceDimension/2, vertexCoordinates[82][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[27][0]+pieceDimension/2, vertexCoordinates[27][1]+pieceDimension/2,
                vertexCoordinates[83][0]+pieceDimension/2, vertexCoordinates[83][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[37][0]+pieceDimension/2, vertexCoordinates[37][1]+pieceDimension/2,
                vertexCoordinates[84][0]+pieceDimension/2, vertexCoordinates[84][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[56][0]+pieceDimension/2, vertexCoordinates[56][1]+pieceDimension/2,
                vertexCoordinates[80][0]+pieceDimension/2, vertexCoordinates[80][1]+pieceDimension/2);

        // down slant lines
        gc.strokeLine(vertexCoordinates[47][0]+pieceDimension/2, vertexCoordinates[47][1]+pieceDimension/2,
                vertexCoordinates[74][0]+pieceDimension/2, vertexCoordinates[74][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[28][0]+pieceDimension/2, vertexCoordinates[28][1]+pieceDimension/2,
                vertexCoordinates[81][0]+pieceDimension/2, vertexCoordinates[81][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[19][0]+pieceDimension/2, vertexCoordinates[19][1]+pieceDimension/2,
                vertexCoordinates[82][0]+pieceDimension/2, vertexCoordinates[82][1]+pieceDimension/2);


        gc.strokeLine(vertexCoordinates[11][0]+pieceDimension/2, vertexCoordinates[11][1]+pieceDimension/2,
                vertexCoordinates[83][0]+pieceDimension/2, vertexCoordinates[83][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[4][0]+pieceDimension/2, vertexCoordinates[4][1]+pieceDimension/2,
                vertexCoordinates[84][0]+pieceDimension/2, vertexCoordinates[84][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[5][0]+pieceDimension/2, vertexCoordinates[5][1]+pieceDimension/2,
                vertexCoordinates[79][0]+pieceDimension/2, vertexCoordinates[79][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[0][0]+pieceDimension/2, vertexCoordinates[0][1]+pieceDimension/2,
                vertexCoordinates[80][0]+pieceDimension/2, vertexCoordinates[80][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[1][0]+pieceDimension/2, vertexCoordinates[1][1]+pieceDimension/2,
                vertexCoordinates[73][0]+pieceDimension/2, vertexCoordinates[73][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[2][0]+pieceDimension/2, vertexCoordinates[2][1]+pieceDimension/2,
                vertexCoordinates[65][0]+pieceDimension/2, vertexCoordinates[65][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[3][0]+pieceDimension/2, vertexCoordinates[3][1]+pieceDimension/2,
                vertexCoordinates[56][0]+pieceDimension/2, vertexCoordinates[56][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[10][0]+pieceDimension/2, vertexCoordinates[10][1]+pieceDimension/2,
                vertexCoordinates[37][0]+pieceDimension/2, vertexCoordinates[37][1]+pieceDimension/2);

        // // vertical lines
        gc.strokeLine(vertexCoordinates[0][0]+pieceDimension/2, vertexCoordinates[0][1]+pieceDimension/2,
                vertexCoordinates[3][0]+pieceDimension/2, vertexCoordinates[3][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[4][0]+pieceDimension/2, vertexCoordinates[4][1]+pieceDimension/2,
                vertexCoordinates[10][0]+pieceDimension/2, vertexCoordinates[10][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[11][0]+pieceDimension/2, vertexCoordinates[11][1]+pieceDimension/2,
                vertexCoordinates[18][0]+pieceDimension/2, vertexCoordinates[18][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[19][0]+pieceDimension/2, vertexCoordinates[19][1]+pieceDimension/2,
                vertexCoordinates[27][0]+pieceDimension/2, vertexCoordinates[27][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[28][0]+pieceDimension/2, vertexCoordinates[28][1]+pieceDimension/2,
                vertexCoordinates[37][0]+pieceDimension/2, vertexCoordinates[37][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[38][0]+pieceDimension/2, vertexCoordinates[38][1]+pieceDimension/2,
                vertexCoordinates[46][0]+pieceDimension/2, vertexCoordinates[46][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[47][0]+pieceDimension/2, vertexCoordinates[47][1]+pieceDimension/2,
                vertexCoordinates[56][0]+pieceDimension/2, vertexCoordinates[56][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[57][0]+pieceDimension/2, vertexCoordinates[57][1]+pieceDimension/2,
                vertexCoordinates[65][0]+pieceDimension/2, vertexCoordinates[65][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[66][0]+pieceDimension/2, vertexCoordinates[66][1]+pieceDimension/2,
                vertexCoordinates[73][0]+pieceDimension/2, vertexCoordinates[73][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[74][0]+pieceDimension/2, vertexCoordinates[74][1]+pieceDimension/2,
                vertexCoordinates[80][0]+pieceDimension/2, vertexCoordinates[80][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[81][0]+pieceDimension/2, vertexCoordinates[81][1]+pieceDimension/2,
                vertexCoordinates[84][0]+pieceDimension/2, vertexCoordinates[84][1]+pieceDimension/2);
        //vertex numbers
        for (int i = 0 ; i < 85 ; i++){
            gc.setFill(Color.BLACK);
            gc.fillText(""+i, vertexCoordinates[i][0] + 10 + pieceDimension/2, vertexCoordinates[i][1] + 5 + pieceDimension/2);
        }
    }
    /**
     * Draws rings and coins on the board.
     *
     * @param img The image to draw.
     * @param vertex The vertex where the image is drawn.
     * @param gc The graphics context used for rendering.
     * @param resol Whether to apply resolution.
     */
    private void drawImage(Image img, int vertex, GraphicsContext gc, boolean resol) {
        int x = gameEngine.vertexCoordinates[vertex][0];
        int y = gameEngine.vertexCoordinates[vertex][1];
        

        gc.drawImage(img, x, y, pieceDimension, pieceDimension);
    }
    /**
     * Draws a highlighter circle around the specified vertex.
     *
     * @param vertex The vertex to highlight.
     * @param availability Indicates if the vertex is available.
     */
    public void drawHighlighter( int vertex, boolean availability){
        if (availability){highlightedVertices.add(vertex);}
        Color highLighterColor = (availability) ? Color.GREEN : Color.RED;
        Circle availableCircle = new Circle();
        availableCircle.setCenterX(gameEngine.getcoordinates(vertex)[0]  + pieceDimension / 2);
        availableCircle.setCenterY(gameEngine.getcoordinates(vertex)[1]-13 + pieceDimension / 2);
        availableCircle.setRadius(12);
        availableCircle.setFill(highLighterColor);
        fieldPane.getChildren().add(availableCircle);
    }
    /**
     * Removes all circle indicators from the board.
     */
    private void removeCircleIndicators(){
        fieldPane.getChildren().removeIf(node -> node instanceof Circle);
    }
    /**
     * Creates a score circle used for displaying player scores.
     *
     * @return A newly created score circle.
     */
    private Circle makeScoreCircle() {
        Circle circle = new Circle(35);
        circle.setStroke(inactiveScoreRingColor);
        circle.setFill(null);
        circle.setStrokeWidth(10);

        return circle;
    }
    /**
     * Handles the bot's turn by making a move and updating the game state.
     *
     * @param gc The graphics context for rendering.
     */
    private void botTurn(GraphicsContext gc) {
        if (isGameOver) {
           // restartGame();

            return;
        }

        if (isGameOver || gameEngine.isInChipRemovalMode() || gameEngine.isInRingRemovalMode()) {
            return; 
        }

        try{
            GameState gs = gameEngine.getGameState().clone();
            Game_Board gameBoard = gs.gameBoard;
            Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;
            
            Game_Board prevGameBoard = gs.getGameBoard().clone();   
            GameState prevGameStateNew = prevGameBoard.createStatesFromBoards(prevGameBoard);
            Bot activeBot = currentPlayer.getBot();
            Vertex vertexFrom;
            int vertexTo;
            Move move = null;
            if (activeBot != null) {
    
                if (gs.ringsPlaced < 10) {

                    move = activeBot.makeMove(gameEngine.getGameState());
                    int chosenVertexNumber = gameBoard.getVertexNumberFromPosition(move.getXposition(), move.getYposition());
                    GUIplaceStartingRing(chosenVertexNumber, gc);
    
                    gameEngine.currentState.resetTurn();
                    gameEngine.currentState.selectedChipVertex = -1;
                    currentPlayerIndex = gameEngine.currentState.isWhiteTurn ? 0 : 1;
                } else {
                    try{
                        move = activeBot.makeMove(gameEngine.getGameState());
                        vertexFrom = move.getStartingVertex();

                        vertexTo = gameBoard.getVertexNumberFromPosition(move.getXposition(), move.getYposition());
                            int[] moveValid = {1};
                            
                            moveRing(vertexFrom.getVertextNumber(), vertexTo, gc, moveValid, move);
                    } catch(Exception ex){
                        ex.printStackTrace();
                        this.isGameOver=true;
                        restartGame();
                    }
                    
                    }
    
                }

                if (move != null) {
                    newState = gameEngine.currentState.clone();
                    resetTurn();
                    updateOnscreenText();

                }
        } catch(Throwable ex){

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> botTurn(gc));
            pause.play();


        }
        
        }
    /**
     * Updates the game board by rendering all rings and coins.
     *
     * @param game_Board The current state of the game board.
     * @param gc The graphics context for rendering.
     */
    private void updateGameBoard(Game_Board game_Board, GraphicsContext gc){

        //clear canvas

        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        //run through every vertex and put image there if there is an image
        for (int i = 0; i <85;i++){
            Vertex vertex = game_Board.getVertex(i);

            //vertex has ring
            if (vertex.hasRing()){
                PlayObj ring = vertex.getRing();

                String colour = ring.getColour().toLowerCase();

                if (colour.equals("white")){

                    drawImage(ringWImage, i, gc, true);
                } else {
                    drawImage(ringBImage, i, gc, true);
                    // gameEngine.currentState.updateRingCount(colour);

                }
            }

            //vertex has coin
            if (vertex.hasCoin()){
                PlayObj coin = vertex.getCoin();

                String colour = coin.getColour().toLowerCase();

                if (colour.equals("white")){
                    drawImage(chipWImage, i, gc, false);
                } else {
                    drawImage(chipBImage, i, gc, false);
                }

            }

        }

    }
    /**
     * Resets the game turn and updates the UI.
     */

    private void resetTurn(){
       
        if (isGameOver) {
            newState = gameEngine.currentState.clone();
            Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;
    
            // System.out.println("\n"+"Move " +currentPlayer.getColor().toLowerCase());
            // reward = Reward.calculateReward(gameEngine, previuosState, currMove, newState, currentPlayer.getColor().toLowerCase());
            // System.out.println("TOTAL REWARD = " + reward+" BOT color: "+currentPlayer.getColor());
    
            previuosState = gameEngine.currentState.clone();
            return;
        }
        if (gameEngine.isInChipRemovalMode()==false && gameEngine.isInRingRemovalMode()==false){
            // System.out.println("BEFORE reset "+gameEngine.currentState.getCurrentColor()+" "+gameEngine.currentState.isWhiteTurn);
            newState = gameEngine.currentState.clone();
            Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;
    
            // System.out.println("\n"+"Move " +currentPlayer.getColor().toLowerCase());
            // reward = Reward.calculateReward(gameEngine, previuosState, currMove, newState, currentPlayer.getColor().toLowerCase());
            // System.out.println("TOTAL REWARD = " + reward+" BOT color: "+currentPlayer.getColor());
    
            previuosState = gameEngine.currentState.clone();
    

            gameEngine.currentState.resetTurn();
            gameEngine.currentState.selectedChipVertex = -1;
            switchPlayer();
            // System.out.println("AFTER reset "+gameEngine.currentState.getCurrentColor()+" "+gameEngine.currentState.isWhiteTurn);

            chipsToRemove=5;
            updateStrengthIndicator();
            chipsRemainText.setText("      Chips Remaining: " + gameEngine.currentState.chipsRemaining);
                
            updateGameBoard(gameEngine.currentState.getGameBoard(), playObjectCanvas.getGraphicsContext2D());
            // System.out.println(gameEngine.currentState.isWhiteTurn);
            turnIndicator.setText(gameEngine.currentState.isWhiteTurn ? "White's Turn" : "Black's Turn");
            drawDashedLine(playObjectCanvas.getGraphicsContext2D());

            playerTurn();
        }else{
            // System.out.println(gameEngine.isInChipRemovalMode());
            // System.out.println(gameEngine.isInRingRemovalMode());
        }
        
    }
    /**
     * Updates the strength indicator visualization.
     */

    private void updateStrengthIndicator() {
        GraphicsContext gc = strengthIndicator.getGraphicsContext2D();
        GameState gs = gameEngine.getGameState();
        gc.clearRect(0, 0, strengthIndicator.getWidth(), strengthIndicator.getHeight());
    
        double whiteStrength;
        double blackStrength;
        String currentPlayer = gameEngine.currentState.isWhiteTurn ? "white" : "black";
    
        if (currentPlayer.equals("white")) {
            whiteStrength = gs.evaluate(gameEngine.currentState, gameEngine, "white") + reward;
            blackStrength = gs.evaluate(gameEngine.currentState, gameEngine, "black");
        } else {
            whiteStrength = gs.evaluate(gameEngine.currentState, gameEngine, "white");
            blackStrength = gs.evaluate(gameEngine.currentState, gameEngine, "black") + reward;
        }
    
        double totalStrength = whiteStrength + blackStrength;
        double whiteRatio = (totalStrength == 0) ? 0.5 : whiteStrength / totalStrength;
        double blackRatio = 1 - whiteRatio;
    
        double width = strengthIndicator.getWidth();
        double height = strengthIndicator.getHeight();
    
        gc.setFill(Color.RED);
        gc.fillRect(0, 0, width * whiteRatio, height);
    
        gc.setFill(Color.BLUE);
        gc.fillRect(width * whiteRatio, 0, width * blackRatio, height);
    
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(width / 2, 0, width / 2, height);
    }


    /**
     * Updates the on-screen text elements.
     */
    private void updateOnscreenText(){
        chipsWhiteText.setText("Chips White on board: " + gameEngine.currentState.chipsWhite);
        chipsWhiteText.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        chipsBlackText.setText("Chips Black on board: " + gameEngine.currentState.chipsBlack);
        chipsBlackText.setFont(Font.font("Arial", FontWeight.BOLD, 16));


        ringWhiteRemainingText.setText("White Rings Placed: " + gameEngine.currentState.ringsWhite);
        ringWhiteRemainingText.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        ringBlackRemainingText.setText("Black Rings Placed: " + gameEngine.currentState.ringsBlack);
        ringBlackRemainingText.setFont(Font.font("Arial", FontWeight.BOLD, 16));

    }
      /**
        * Displays game statistics, including total games played, wins, losses, and draws.
        */
    private void displayGameStatistics() {
        StringBuilder stats = new StringBuilder();
    
        stats.append("\n=======================================================\n");
        stats.append("GAME STATISTICS\n");
        stats.append("=======================================================\n");
        stats.append(String.format("Total Games Played: %d\n", gamesPlayed));
    
        stats.append(String.format("\nWhite Player %s:\n", (String)whitePlayer.getName()));
        stats.append(String.format("    Wins: %d\n", whiteWins));
        stats.append(String.format("    Losses: %d\n", gamesPlayed - whiteWins - draws));
        stats.append(String.format("    Draws: %d\n", draws));
        stats.append(String.format("    Winning Rate: %.2f%%\n",
                (gamesPlayed > 0 ? (double) whiteWins / gamesPlayed * 100 : 0)));
    
        stats.append(String.format("\nBlack Player %s:\n", (String)blackPlayer.getName()));
        stats.append(String.format("    Wins: %d\n", blackWins));
        stats.append(String.format("    Losses: %d\n", gamesPlayed - blackWins - draws));
        stats.append(String.format("    Draws: %d\n", draws));
        stats.append(String.format("    Winning Rate: %.2f%%\n",
                (gamesPlayed > 0 ? (double) blackWins / gamesPlayed * 100 : 0)));
    
        stats.append("\nLast Game:\n");
        stats.append(String.format("    Winner: %s\n", (whiteScore == 3 ? "White" : blackScore == 3 ? "Black" : "Draw")));
    
        stats.append("\nScore Summary:\n");
        stats.append(String.format("    White Rings Captured: %d\n", whiteScore));
        stats.append(String.format("    Black Rings Captured: %d\n", blackScore));
    
        stats.append("=======================================================\n");
        stats.append("\n");
        stats.append("=======================================================\n");
    
        // System.out.println(stats.toString());
    }
    /**
     * Restarts the game, resetting game state and UI elements.
     */
    private void restartGame() {
        gamesPlayed++;
        updateGamesPlayedText();
        displayGameStatistics();
        this.isGameOver = false;
        isGameStarted = false;

       // gameEngine.currentState.winner = null;
        gameEngine.currentState.chipsRemaining = 51;
        chipsRemainText.setText("      Chips Remaining: " + gameEngine.currentState.chipsRemaining);
        startGameButton.setDisable(false);
        whitePlayerComboBox.setDisable(false);
        blackPlayerComboBox.setDisable(false);

        gameEngine.resetGame();
        gameEngine.currentState = gameEngine.getGameState();
        gameEngine.currentState.chipsRemaining = 51;
        currentPlayerIndex = 0;
        whiteScore = 0;
        blackScore = 0;
        ChipsRemoved = 5;
        CheckPossibleChipsToRemove = true;
        FirstClickOnCoin = true;
        nadoEtiChips.clear();
        winningChips.clear();
        highlightedVertices.clear();
        turnIndicator.setText("White's Turn");
        GraphicsContext gcP = playObjectCanvas.getGraphicsContext2D();
        gcP.clearRect(0, 0, playObjectCanvas.getWidth(), playObjectCanvas.getHeight());
        removeCircleIndicators();
        for (Circle circle : whiteScoreCircle) {
            circle.setStroke(inactiveScoreRingColor);
        }
        for (Circle circle : blackScoreCircle) {
            circle.setStroke(inactiveScoreRingColor);
        }


        updateOnscreenText();
        updateStrengthIndicator();
        if (whitePlayer.isBot() && blackPlayer.isBot()) {
            PauseTransition pause = new PauseTransition(Duration.seconds(5));
            pause.setOnFinished(event ->            startGame(gcP));
            pause.play();

        }
    }
    /**
     * Updates the number of games played in the UI.
     */
    private void updateGamesPlayedText() {
        gamesPlayedText.setText("Games Played: " + gamesPlayed);
    }

    /**
     * Updates the number of wins for each player in the UI.
     */
    private void updateWinsText() {
        whiteWinsText.setText("White Wins: " + whiteWins);
        blackWinsText.setText("Black Wins: " + blackWins);
    }
    /**
     * Shows an alert message when the game ends in a draw.
     */
    private void showDrawAlert() {
        gamesPlayed++;  
        updateGamesPlayedText();
        turnIndicator.setText("It's a draw!");
        draws++; 
        drawText.setText("Draws: " + draws);
        if(whitePlayer.isBot() && blackPlayer.isBot()){
            restartGame();
            return;
        }
    
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over - Draw");
        alert.setHeaderText("It's a draw!");
        alert.setContentText("Would you like to restart the game?");
    
        ButtonType restartButtonType = new ButtonType("Restart");
        ButtonType exitButtonType = new ButtonType("Exit");
    
        alert.getButtonTypes().setAll(restartButtonType, exitButtonType);
    
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == restartButtonType) {
            restartGame();
        } else {
            System.exit(0);
        }
    }
    

    public static void main(String[] args) {
        launch(args);
    }

}