package com.ken2.ui;

import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.PlayObj;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.bots.Bot;
import com.ken2.bots.BotAbstract;
import com.ken2.bots.AlphaBetaBot.AlphaBetaBot;
import com.ken2.bots.RuleBased.RuleBasedBot;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;
import com.ken2.utils.BotFactory;
import com.ken2.utils.Player;

import javafx.application.Application;
import javafx.css.Rule;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
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
import javafx.animation.PauseTransition;
import javafx.util.Duration;


import static com.ken2.ui.GameAlerts.showAlert;


public class MainApp extends Application {

    private GameEngine gameEngine;

    // ASSETS
    private Image ringBImage = new Image("file:ken2\\assets\\black ring.png");
    private static Image chipBImage = new Image("file:ken2\\assets\\black chip.png");
    private Image ringWImage = new Image("file:ken2\\assets\\white ring.png");
    private static Image chipWImage = new Image("file:ken2\\assets\\white chip.png");

    // COMPONENTS
    private static int fieldDimension = 470;
    private static int pieceDimension = 37;

    private Color inactiveScoreRingColor = Color.rgb(200, 200, 200);
    private Color activeScoreRingColor = Color.rgb(90, 150, 220);

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

    private Text turnIndicator = new Text("White's Turn");

    // COMBOBOXES
    private ComboBox whitePlayerComboBox;
    private ComboBox blackPlayerComboBox;

    private ArrayList<Integer> highlightedVertices;
    private int whiteScore = 0;
    private int blackScore = 0;
    private int ChipsRemoved = 5;
    private Circle[] whiteScoreCircle = new Circle[3];
    private Circle[] blackScoreCircle = new Circle[3];
    private int chipsToRemove = 0;


    // BOT COMPONENTS
    // ArrayList<Bot> bots = new ArrayList<>();
    // Button botTurnButton;
    // private Bot whiteBot;
    // private Bot blackBot;

    // Player List
    private int currentPlayerIndex = 0;
    private Player whitePlayer;
    private Player blackPlayer;

    // FLAGS
    private boolean isGameStarted = false;
    private Vertex lastMoveStartVertex = null;
    private Vertex lastMoveEndVertex = null;


    // BUTTONS
    private Button startGameButton;


    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage primaryStage) throws Exception {

        gameEngine = new GameEngine();

        primaryStage.setTitle("Compare Players with Yinsh");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);

        root = new GridPane();
        root.setPadding(new Insets(5, 5, 5, 5));
        root.setVgap(10);
        root.setHgap(10);

        Scene scene = new Scene(root, 800, 600, Color.GRAY);
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

        whitePlayerComboBox.getSelectionModel().selectFirst();

        blackPlayerComboBox = new ComboBox<>();
        blackPlayerComboBox.getItems().add("Human Player");
        blackPlayerComboBox.getItems().add("RuleBased Bot");
        blackPlayerComboBox.getItems().add("AlphaBeta Bot");

        blackPlayerComboBox.getSelectionModel().selectFirst();

        whitePlayerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (!isGameStarted) {
                selectPlayer("White", whitePlayerComboBox);
            } else {
                System.out.println("game has already started");
                whitePlayerComboBox.getSelectionModel().select(oldValue);
            }
        });

        blackPlayerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (!isGameStarted) {
                selectPlayer("Black", blackPlayerComboBox);
            } else {
                System.out.println("game has already started");
                blackPlayerComboBox.getSelectionModel().select(oldValue);
            }
        });


        strengthIndicator = new Canvas(fieldDimension, 20);
        root.add(strengthIndicator, 1, 0, 5, 1);
        updateStrengthIndicator();

        startGameButton = new Button("Start Game");
        startGameButton.setOnAction(e -> startGame(gcP));

        Text whitePlayerLabel = new Text();
        whitePlayerLabel.setText("White");

        Text blackPlayerLabel = new Text();
        blackPlayerLabel.setText("Black");
        chipsRemainText = new Text();

        chipsBlackText = new Text();
        chipsWhiteText = new Text();
        ringBlackRemainingText = new Text();
        ringWhiteRemainingText = new Text();
        updateOnscreenText();

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> resetBoard(gcP));

        Button undoButton = new Button("Undo");
        //undoButton.setOnAction(e -> undoToLastState());

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


        root.add(chipsWhiteText, 0, 5);
        root.add(chipsBlackText, 7, 5);
        //////////
        root.add(chipsRemainText,3,5);
//////////////////////////
        root.add(ringWhiteRemainingText, 1, 1);
        root.add(ringBlackRemainingText, 5, 1);
        root.add(scoreRingeW1, 0, 2);
        root.add(scoreRingeW2, 0, 3);
        root.add(scoreRingeW3, 0, 4);
        root.add(scoreRingeB1, 7, 2);
        root.add(scoreRingeB2, 7, 3);
        root.add(scoreRingeB3, 7, 4);
        // root.add(chipsRemainText, 4, 0);
       // root.add(ringWhiteRemainingText, 1, 0);
     //   root.add(ringBlackRemainingText, 5, 0);
//        root.add(scoreRingeW1, 0, 2);
//        root.add(scoreRingeW2, 0, 3);
//        root.add(scoreRingeW3, 0, 4);
//        root.add(scoreRingeB1, 7, 2);
//        root.add(scoreRingeB2, 7, 3);
//        root.add(scoreRingeB3, 7, 4);
        root.add(whiteScoreCircle[0], 0, 2);
        root.add(whiteScoreCircle[1], 0, 3);
        root.add(whiteScoreCircle[2], 0, 4);

        root.add(blackScoreCircle[0], 7, 2);
        root.add(blackScoreCircle[1], 7, 3);
        root.add(blackScoreCircle[2], 7, 4);

        root.add(resetButton, 0, 7);
        root.add(undoButton, 1, 7);
        root.add(turnIndicator, 3, 7);

        root.add(startGameButton, 7, 7);
        primaryStage.setScene(scene);
        primaryStage.show();

        highlightedVertices = new ArrayList<>();

    }

    /**
     * Handle the game start button
     * // * @param gc GraphicsContext
     */
    private void startGame(GraphicsContext gcP) {
        this.isGameStarted = true;
        if (whitePlayer == null) {
            whitePlayer = new Player("White");
        }
        if (blackPlayer == null) {
            blackPlayer = new Player("Black");
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

    private void playerTurn() {

        Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;
        GraphicsContext gc = playObjectCanvas.getGraphicsContext2D();
        removeCircleIndicators();
        System.out.println("Current Player Index: " + currentPlayerIndex);

        if (currentPlayer.isBot()) {
            System.out.println("BOT");
            // TODO: remove for the adversial search
            PauseTransition pause = new PauseTransition(Duration.seconds(0.4));
            pause.setOnFinished(event -> {
                botTurn(gc);
            });
            pause.play();
            // botTurn(gc);
        } else {
            if (gameEngine.currentState.ringsPlaced < 10) {
                if (gameEngine.currentState.ringsPlaced == 10) {
                    removeCircleIndicators();
                } else {
                    GUIavailablePlacesForStartRings(gc);
                }

                System.out.println(currentPlayer.getColor() + " player's turn. Please make a move.");
            }
        }
    }

    /**
     * switch current player
     * // * @param color
     * // * @param comboBox
     */
    private void switchPlayer() {
        gameEngine.currentState.isWhiteTurn = !gameEngine.currentState.isWhiteTurn;
        currentPlayerIndex = gameEngine.currentState.isWhiteTurn ? 0 : 1;
    }


    /**
     * combobox handler
     *
     * @param color
     * @param comboBox
     */
    private void selectPlayer(String color, ComboBox<String> comboBox) {
        String selectedItem = comboBox.getSelectionModel().getSelectedItem();
        BotFactory botFactory = new BotFactory();

        if ("Human Player".equals(selectedItem)) {
            if (color.equalsIgnoreCase("White")) {
                whitePlayer = new Player(color);
            } else {
                blackPlayer = new Player(color);
            }
        } else {
            Bot bot = botFactory.getBot(selectedItem, color);
            if (color.equalsIgnoreCase("White")) {
                whitePlayer = new Player(color, bot);
            } else {
                blackPlayer = new Player(color, bot);
            }
        }
    }

    /**
     * Handle mouse clicks
     *
     * @param e  MouseEvent
     * @param gc GraphicsContext
     */
    private void handleFieldClick(MouseEvent e, GraphicsContext gc) {

        int vertex = gameEngine.findClosestVertex(e.getX(), e.getY());
        Vertex v = gameEngine.currentState.gameBoard.getVertex(vertex);
        if (vertex < 0) return;
        if (gameEngine.GetWinningRing()) {
            System.out.print("jjsjsjs");
            handleWinningRing(vertex, gc);
            Integer RemoveChipVertex = vertex;
            if (gameEngine.currentState.chipNumber.contains(RemoveChipVertex)) {
                System.out.print("remove");
                handleChipRemove(vertex, gc);
                // handleChipAndRingPlacement(vertex, gc);
                gameEngine.currentState.chipNumber.remove(RemoveChipVertex);
                ChipsRemoved--;
                if (ChipsRemoved == 0) {
                    gameEngine.setWinningRing(false);
                    gameEngine.setChipRemovalMode(false);
                    ChipsRemoved = 5;
                }
            }
            return;

        }
        if (!gameEngine.GetWinningRing() && !gameEngine.isInChipRemovalMode()) {
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

        // switchPlayer();
        // playerTurn();
        updateOnscreenText();
    }

    public void handleChipRemove(int vertex, GraphicsContext gc) {
        Vertex v = gameEngine.currentState.gameBoard.getVertex(vertex);
        if (v != null && v.hasCoin() && v.getCoin().getColour().equalsIgnoreCase(gameEngine.getWinningColor())) {
            v.setPlayObject(null);
            gc.clearRect(gameEngine.vertexCoordinates[vertex][0] + pieceDimension / 4,
                    gameEngine.vertexCoordinates[vertex][1] + pieceDimension / 4, pieceDimension / 2, pieceDimension / 2);

            gameEngine.currentState.chipsRemaining++;
            chipsToRemove--;
            if (chipsToRemove == 0) {
                gameEngine.setChipRemovalMode(false);
                gameEngine.setWinningColor("");
                showAlert("Continue Game", "5 chips removed. The game continues.");
                gameEngine.setTurnToWinningPlayer();
            }
            chipsRemainText.setText("Chips Remaining: " + gameEngine.currentState.chipsRemaining);

        } else {
            System.out.println("No chip found at vertex " + vertex + " or color does not match.");
        }
    }


    public void handleWinningRing(int vertex, GraphicsContext gc) {
        System.out.print("snshsbsbsbsb");
        Vertex v = gameEngine.currentState.gameBoard.getVertex(vertex);
        if (v != null && v.hasRing() && v.getRing().getColour().equalsIgnoreCase(gameEngine.getWinningColor())) {
            moveRingToThePanel(gameEngine.getWinningColor());
            v.setRing(null);
            gc.clearRect(gameEngine.vertexCoordinates[vertex][0], gameEngine.vertexCoordinates[vertex][1], pieceDimension, pieceDimension);

            gameEngine.setRingSelectionMode(false); // Exit ring selection mode
            gameEngine.setChipRemovalMode(true);
            chipsToRemove = 5;
            showAlert("Select Chips to Remove", "Please select 5 chips of your color to remove from the board.");

        }

    }

    private void moveRingToThePanel(String playerColor) {
        if (playerColor.equalsIgnoreCase("white")) {
            if (whiteScore < 3) {
                whiteScoreCircle[whiteScore].setStroke(activeScoreRingColor); // Make the ring appear active
                whiteScore++;


                // Check if White has won
                if (whiteScore == 3) {
                    showAlert("Game Over", "White wins!");
                    //endGame();
                    return;
                }
            }
        } else {
            if (blackScore < 3) {
                blackScoreCircle[blackScore].setStroke(activeScoreRingColor); // Make the ring appear active
                blackScore++;

                // Check if Black has won
                if (blackScore == 3) {
                    showAlert("Game Over", "Black wins!");
                    //endGame();
                    return;
                }
            }
        }
    }


    /**
     * To place rings at the start of the game
     *
     * @param vertex vertex number
     */
    private void GUIplaceStartingRing(int vertex, GraphicsContext gc) {
        Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;

        String ringColor = currentPlayer.getColor();
        if (gameEngine.placeStartingRing(vertex, ringColor)) {

            Image ringImage = ringColor.equals("white") ? ringWImage : ringBImage;
            drawImage(ringImage, vertex, gc, true);
            // turnIndicator.setText(gameEngine.currentState.isWhiteTurn ? "White's Turn" : "Black's Turn");
            resetTurn();
            System.out.println("now turn is" + ringColor);
            // playerTurn();
            // gameEngine.currentState.ringsPlaced+=1;
            // drawHighlighter(vertex,false);
        }
    }

    /**
     * Displays available vertices for the starting rings to be placed
     *
     * @param gc GraphicsContext
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
     * Handles game logic between chips and rings placement
     *
     * @param vertex vertex clicked
     * @param gc     GraphicsContext
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

            moveRing(gameEngine.currentState.selectedChipVertex, vertex, gc, Move_Valid, currentMove);

            if (Move_Valid[0] == 1) {

                String color = gameEngine.currentState.currentPlayerColor();
                gameEngine.checkWinning(vertex, color);
                if (!gameEngine.win(vertex, color))
                    resetTurn();
                //resetTurn();
                // gameEngine.currentState.selectedChipVertex = -1;
                //removeCircleIndicators();
            }
        } else {
            GameAlerts.alertInvalidMove();

        }

    }

    /**
     * Highlight the possible moves of the ring
     *
     * @param possibleMoves posiible moves
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
     * Logic for the Move ring method
     *
     * @param fromVertex
     * @param toVertex
     * @param gc
     * @param Move_Valid
     * @param currentMove // * @param possibleMoves
     */
    private void moveRing(int fromVertex, int toVertex, GraphicsContext gc, int[] Move_Valid, Move currentMove) {
        Vertex sourceVertex = gameEngine.currentState.gameBoard.getVertex(fromVertex);
        Vertex targetVertex = gameEngine.currentState.gameBoard.getVertex(toVertex);

        // if (!highlightedVertices.contains(toVertex)) {
        //     GameAlerts.alertInvalidMove();
        //     Move_Valid[0] = 0;
        //     return;
        // }

        if (targetVertex.hasRing()) {
            GameAlerts.alertRingPlacement();
            Move_Valid[0] = 0;
            return;
        }

        if (targetVertex.hasCoin()) {
            GameAlerts.alertPositionHasChip();
            Move_Valid[0] = 0;
            return;
        }

        Move_Valid[0] = 1;
        gameEngine.placeChip(fromVertex, gc);

        Ring ringToMove = (Ring) sourceVertex.getRing();
        if (ringToMove != null) {
            sourceVertex.setRing(null);
            this.lastMoveStartVertex = sourceVertex;
            this.lastMoveEndVertex = targetVertex;
            gc.clearRect(gameEngine.vertexCoordinates[fromVertex][0], gameEngine.vertexCoordinates[fromVertex][1], pieceDimension, pieceDimension);

            if (sourceVertex.hasCoin()) {
                Coin existingChip = (Coin) sourceVertex.getCoin();
                // Image chipImage = existingChip.getColour().toLowerCase().equals("white") ? chipWImage : chipBImage;
                // gc.drawImage(chipImage, gameEngine.vertexCoordinates[fromVertex][0] + pieceDimension / 4,
                //         gameEngine.vertexCoordinates[fromVertex][1] + pieceDimension / 4, pieceDimension / 2, pieceDimension / 2);
            }

            if (!currentMove.getFlippedCoins().isEmpty()) {
                gameEngine.gameSimulation.flipCoins(currentMove.getFlippedCoins(), gameEngine.currentState.gameBoard);

                for (Coin coinToFlip : currentMove.getFlippedCoins()) {
                    Vertex currVertex = gameEngine.currentState.gameBoard.getVertexByCoin(coinToFlip);
                    // Image chipImage = coinToFlip.getColour().toLowerCase().equals("white") ? chipWImage : chipBImage;
                    // gc.drawImage(chipImage, gameEngine.vertexCoordinates[currVertex.getVertextNumber()][0] + pieceDimension / 4,
                    //         gameEngine.vertexCoordinates[currVertex.getVertextNumber()][1] + pieceDimension / 4, pieceDimension / 2,
                    //         pieceDimension / 2);
                }
            }

            targetVertex.setRing(ringToMove);
            Image ringImage = ringToMove.getColour().toLowerCase().equals("white") ? ringWImage : ringBImage;
            gc.drawImage(ringImage, gameEngine.vertexCoordinates[toVertex][0], gameEngine.vertexCoordinates[toVertex][1], pieceDimension, pieceDimension);

            gameEngine.currentState.chipRingVertex = -1;
            gameEngine.currentState.chipPlaced = false;
            gameEngine.currentState.selectedRingVertex = -1;
            gameEngine.currentState.updateChipsRingCountForEach();

            System.out.println(gameEngine.currentState.gameBoard.strMaker());
        } else {
            GameAlerts.alertNoRing();
        }
    }


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
     * Draw the game board for the GUI
     * @param gc GraphicsContext object for drawing the board
     */
    private void drawBoard(GraphicsContext gc) {
        //gc.drawImage(boardImage, 0, 0, fieldDimension, fieldDimension);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

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

        // // for demo
        // gc.drawImage(ringBImage, vertexCoordinates[39][0], vertexCoordinates[39][1], pieceDimension, pieceDimension);
        // gc.drawImage(ringWImage, vertexCoordinates[61][0], vertexCoordinates[61][1], pieceDimension, pieceDimension);
        // gc.drawImage(chipBImage, vertexCoordinates[43][0], vertexCoordinates[43][1], pieceDimension, pieceDimension);
        // gc.drawImage(chipWImage, vertexCoordinates[41][0], vertexCoordinates[41][1], pieceDimension, pieceDimension);

        // //hit box
        // gc.setStroke(Color.MAGENTA);
        // gc.strokeLine(vertexCoordinates[40][0]+pieceDimension/2 - 10, vertexCoordinates[40][1]+pieceDimension/2 + 10,
        // vertexCoordinates[40][0]+pieceDimension/2 + 10, vertexCoordinates[40][1]+pieceDimension/2 + 10);
        // gc.strokeLine(vertexCoordinates[40][0]+pieceDimension/2 - 10, vertexCoordinates[40][1]+pieceDimension/2 - 10,
        // vertexCoordinates[40][0]+pieceDimension/2 + 10, vertexCoordinates[40][1]+pieceDimension/2 - 10);
        // gc.strokeLine(vertexCoordinates[40][0]+pieceDimension/2 + 10, vertexCoordinates[40][1]+pieceDimension/2 - 10,
        // vertexCoordinates[40][0]+pieceDimension/2 + 10, vertexCoordinates[40][1]+pieceDimension/2 + 10);
        // gc.strokeLine(vertexCoordinates[40][0]+pieceDimension/2 - 10, vertexCoordinates[40][1]+pieceDimension/2 - 10,
        // vertexCoordinates[40][0]+pieceDimension/2 - 10, vertexCoordinates[40][1]+pieceDimension/2 + 10);

        //vertex numbers
        for (int i = 0 ; i < 85 ; i++){
            gc.setFill(Color.BLACK);
            gc.fillText(""+i, vertexCoordinates[i][0] + 10 + pieceDimension/2, vertexCoordinates[i][1] + 5 + pieceDimension/2);
        }
    }

    /**
     * Method to draw rings and coins
     * @param img image to draw
     * @param vertex vertex to draw image at
     * @param gc GraphicsContext
     */
        private void drawImage(Image img, int vertex, GraphicsContext gc, boolean resol) {
            int x = gameEngine.vertexCoordinates[vertex][0];
            int y = gameEngine.vertexCoordinates[vertex][1];
            int dimension = resol ? pieceDimension : pieceDimension / 2;
        
            if (!resol) {
                x += pieceDimension / 4;
                y += pieceDimension / 4;
            }
        
            gc.drawImage(img, x, y, dimension, dimension);
        }
    

    /**
     * Draws the highlighter for the available moves
     * @param vertex vertex number
     * @param availability true for green and false for red
     */
    public void drawHighlighter( int vertex, boolean availability){
        if (availability){highlightedVertices.add(vertex);}
        Color highLighterColor = (availability) ? Color.GREEN : Color.RED;
        Circle availableCircle = new Circle();
        availableCircle.setCenterX(gameEngine.getcoordinates(vertex)[0] + pieceDimension / 2);
        availableCircle.setCenterY(gameEngine.getcoordinates(vertex)[1] + pieceDimension / 2);
        availableCircle.setRadius(7);
        availableCircle.setFill(highLighterColor);
        fieldPane.getChildren().add(availableCircle);
    }


    /**
     * Removes the highlighters
     */
    private void removeCircleIndicators(){
        fieldPane.getChildren().removeIf(node -> node instanceof Circle);
    }


    /**
     * Draw the inactive score circles
     * @return a circle object
     */
    private Circle makeScoreCircle() {
        Circle circle = new Circle(35);
        circle.setStroke(inactiveScoreRingColor);
        circle.setFill(null);
        circle.setStrokeWidth(10);

        return circle;
    }

    /**
     * Reset the Board for a new game
     */
    private void resetBoard(GraphicsContext gc) {
        System.out.println();
        System.out.println(whitePlayerComboBox.getValue());
        System.out.println(blackPlayerComboBox.getValue());

        // clear all chips
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // check player selection
        selectPlayer("White", whitePlayerComboBox);
        selectPlayer("Black", blackPlayerComboBox);
        
        startGameButton.setDisable(false);
        
        // get a new game
        gameEngine.resetGame();

        // update highlights
        GUIavailablePlacesForStartRings(gc);
        

    }

    // /**
    //  * created bot for the player if bot is selected
    //  * @param color
    //  * @param comboBox
    //  */

    private void botTurn(GraphicsContext gc) {
        GameState gs = gameEngine.getGameState();
        Game_Board gameBoard = gs.gameBoard;
        Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;
    
        Bot activeBot = currentPlayer.getBot();
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
                move = activeBot.makeMove(gameEngine.getGameState());
                Vertex vertexFrom = move.getStartingVertex();

                int vertexTo = gameBoard.getVertexNumberFromPosition(move.getXposition(), move.getYposition());
                System.out.println("from "+vertexFrom.getVertextNumber());
                System.out.println("to "+vertexTo);
                int[] moveValid = {1};
                moveRing(vertexFrom.getVertextNumber(), vertexTo, gc, moveValid, move);
                // currentPlayerIndex = gameEngine.currentState.isWhiteTurn ? 0 : 1;

            }
            System.out.println("NUMBER OF "+gs.ringsPlaced);
            System.out.println("NUMBER BLACK "+gs.ringsBlack);
            System.out.println("NUMBER WHITE "+gs.ringsWhite);

            if (move != null) {
                resetTurn();  
                

    
                // updateGameBoard(gameBoard, gc);
                updateOnscreenText();
            }}
            
        }

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
     * reset the turn for the next player
     */
    private void resetTurn(){
        gameEngine.currentState.resetTurn();
        gameEngine.currentState.selectedChipVertex = -1; 
        // currentPlayerIndex = gameEngine.currentState.isWhiteTurn ? 0 : 1;
        switchPlayer();
        // lastMoveStartVertex = null;
        // lastMoveEndVertex = null;
        updateStrengthIndicator();
        updateGameBoard(gameEngine.currentState.getGameBoard(), playObjectCanvas.getGraphicsContext2D());
        System.out.println(gameEngine.currentState.isWhiteTurn);
        turnIndicator.setText(gameEngine.currentState.isWhiteTurn ? "White's Turn" : "Black's Turn");
        drawDashedLine(playObjectCanvas.getGraphicsContext2D());

        playerTurn();
    }

    private void updateStrengthIndicator() {
        GraphicsContext gc = strengthIndicator.getGraphicsContext2D();
        GameState gs = gameEngine.getGameState();
        gc.clearRect(0, 0, strengthIndicator.getWidth(), strengthIndicator.getHeight());
    
        int whiteStrength = gs.calculateStrength("white");
        int blackStrength = gs.calculateStrength("black");
        int totalStrength = whiteStrength + blackStrength;
        
        double whiteRatio = totalStrength == 0 ? 0.5 : (double) whiteStrength / totalStrength;
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
     * Updates onscreen text
     */
    private void updateOnscreenText(){
        chipsWhiteText.setText("Chips White on board: " + gameEngine.currentState.chipsWhite);
        chipsBlackText.setText("Chips Black on board: " + gameEngine.currentState.chipsBlack);

        ringWhiteRemainingText.setText("White Rings Remaining: " + gameEngine.currentState.ringsWhite);
        ringBlackRemainingText.setText("Black Rings Remaining: " + gameEngine.currentState.ringsBlack);
    }

    
    public static void main(String[] args) {
        launch(args);
    }

}