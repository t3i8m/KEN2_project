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
import com.ken2.engine.Direction;
import com.ken2.engine.Direction;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;
import com.ken2.ui.GameAlerts;
import com.ken2.utils.BotFactory;
import com.ken2.utils.Player;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.css.Rule;
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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.util.Duration;


import static com.ken2.ui.GameAlerts.showAlert;


public class MainApp extends Application {

    private GameEngine gameEngine;

    // ASSETS
    private Image ringBImage = new Image("file:ken2/assets/black ring.png");
    private static Image chipBImage = new Image("file:ken2/assets/black chip.png");
    private Image ringWImage = new Image("file:ken2/assets/white ring.png");
    private static Image chipWImage = new Image("file:ken2/assets/white chip.png");

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
    private Text whiteWinsText;
    private Text blackWinsText;
    private Text drawText;
    private Text gamesPlayedText;


    private Text turnIndicator = new Text("White's Turn");

    // COMBOBOXES
    private ComboBox whitePlayerComboBox;
    private ComboBox blackPlayerComboBox;
    

    private ArrayList<Integer> highlightedVertices;
    private int whiteScore = 0;
    private int blackScore = 0;
    // private boolean CheckPossibleChipsToRemove = true;
    // private boolean FirstClickOnCoin = true;
    private int ChipsRemoved = 5;
    private boolean CheckPossibleChipsToRemove = true;
    private boolean FirstClickOnCoin = true;
    private Circle[] whiteScoreCircle = new Circle[3];
    private Circle[] blackScoreCircle = new Circle[3];
//     List <Integer> nadoEtiChips  = new ArrayList<>();

    // BOT COMPONENTS
    // ArrayList<Bot> bots = new ArrayList<>();
    // Button botTurnButton;
    // private Bot whiteBot;
    // private Bot blackBot;

    // Player List
    private int currentPlayerIndex = 0;
    private Player whitePlayer;
    private Player blackPlayer;
    private int whiteWins = 0; 
    private int blackWins = 0; 
    private int draws = 0;
    private int gamesPlayed = 0;
    private boolean isGameOver=false;




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
                selectPlayer("White", (String)whitePlayerComboBox.getSelectionModel().getSelectedItem());
            } else {
                // System.out.println("game has already started");
                whitePlayerComboBox.getSelectionModel().select(oldValue);
            }
        });

        blackPlayerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (!isGameStarted) {
                selectPlayer("Black", (String)blackPlayerComboBox.getSelectionModel().getSelectedItem());
            } else {
                // System.out.println("game has already started");
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
        gamesPlayedText = new Text("Games Played: 0");
        gamesPlayedText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        root.add(gamesPlayedText, 5, 6);

        Text blackPlayerLabel = new Text();
        blackPlayerLabel.setText("Black");
        chipsRemainText = new Text();

        chipsBlackText = new Text();
        chipsWhiteText = new Text();
        ringBlackRemainingText = new Text();
        ringWhiteRemainingText = new Text();
        updateOnscreenText();

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> restartGame());
        drawText = new Text("Draws: 0");
        drawText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        root.add(drawText, 3, 6);


        whiteWinsText = new Text("White Wins: 0");
        whiteWinsText.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        blackWinsText = new Text("Black Wins: 0");
        blackWinsText.setFont(Font.font("Arial", FontWeight.BOLD, 14));

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
        root.add(whiteWinsText, 0, 6);
        root.add(blackWinsText, 7, 6);
        root.add(chipsWhiteText, 0, 5);
        root.add(chipsBlackText, 7, 5);
        //////////
        root.add(chipsRemainText,3,1);
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
        root.add(turnIndicator, 3, 7);

        root.add(startGameButton, 7, 7);
        primaryStage.setScene(scene);
        primaryStage.show();

        highlightedVertices = new ArrayList<>();

    }

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

    private void playerTurn() {
        if (isGameOver) {
            return;
        }
        Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;
        GraphicsContext gc = playObjectCanvas.getGraphicsContext2D();
        removeCircleIndicators();
        // System.out.println("Current Player Index: " + currentPlayerIndex);

        if (currentPlayer.isBot()) {
            // System.out.println("BOT");
            // System.out.println(currentPlayer.getName().toLowerCase());
            // TODO: remove for the adversial search
            // if(currentPlayer.getName().toLowerCase().equals("rulebased bot")){
            //     PauseTransition pause = new PauseTransition(Duration.seconds(0.001));
            //     pause.setOnFinished(event -> {
            //         botTurn(gc);
            //     });
            //     pause.play();
            // } else{
            //     botTurn(gc);
            // }
            PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
            pause.setOnFinished(event -> botTurn(gc));
            pause.play();
            // botTurn(gc);
        } else {
            if (gameEngine.currentState.ringsPlaced < 10) {
                if (gameEngine.currentState.ringsPlaced == 10) {
                    removeCircleIndicators();
                } else {
                    GUIavailablePlacesForStartRings(gc);
                }

                // System.out.println(currentPlayer.getColor() + " player's turn. Please make a move.");
            }
        }
    }


    private void switchPlayer() {
        gameEngine.currentState.isWhiteTurn = !gameEngine.currentState.isWhiteTurn;
        currentPlayerIndex = gameEngine.currentState.isWhiteTurn ? 0 : 1;
    }


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

        // switchPlayer();
        // playerTurn();
        updateOnscreenText();
    }
    List <Integer> nadoEtiChips  = new ArrayList<>();

    public void WinningCase(int vertex, GraphicsContext gc){

        if(isGameOver){
            restartGame();
        }
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
            // System.out.println(nadoEtiChips);
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
            //System.out.print("remove");
            // gameEngine.currentState.setAllPossibleCoinsToRemove(nadoEtiChips);
            Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;
            if(currentPlayer.isBot()){
                // Bot activeBot = currentPlayer.getBot();
                // ArrayList<Integer> allRemoveChips = activeBot.removeChips(gameEngine.currentState);
                
                // for (Integer vert : allRemoveChips){
                //     handleChipRemove(vert, gc);
                    
                //     gameEngine.currentState.chipNumber.remove(RemoveChipVertex);
                //     ChipsRemoved--;

                // }
            } else{
                handleChipRemove(vertex, gc);
                ChipsRemoved--;
                gameEngine.currentState.chipNumber.remove(RemoveChipVertex);

            }


            // handleChipAndRingPlacement(vertex, gc);
            // System.out.println(nadoEtiChips);

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
    }

    public void handleChipRemove(int vertex, GraphicsContext gc) {
        if (!gameEngine.getWinningChips().contains(vertex)) {
            // System.out.println("This chip is not part of the winning row.");
            // System.out.println("Winning Chips: " + winningChips);
            // System.out.println("Clicked Vertex: " + vertex);
            return;
        }
        Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;

        Vertex v = gameEngine.currentState.gameBoard.getVertex(vertex);
        String currColor = v.getCoin().getColour().toLowerCase();
        if (v != null && v.hasCoin() && v.getCoin().getColour().toLowerCase().equalsIgnoreCase(gameEngine.getWinningColor().toLowerCase())) {
            v.setPlayObject(null);
            gc.clearRect(gameEngine.vertexCoordinates[vertex][0] + pieceDimension / 4,
                    gameEngine.vertexCoordinates[vertex][1] + pieceDimension / 4, pieceDimension / 2, pieceDimension / 2);

            gameEngine.currentState.chipsRemaining+=1;
            chipsRemainText.setText("      Chips Remaining: " + gameEngine.currentState.chipsRemaining);

            chipsToRemove--;
            removeCircleIndicators();
            gameEngine.getWinningChips().remove(Integer.valueOf(vertex));
            // System.out.println("Chip removed. Remaining chips to remove: " + chipsToRemove);
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
/////Ne rabotaet
                List<Integer> allchips =gameEngine.getWinningChips();
                // System.out.println("asdsad"+allchips);
                // System.out.println(nadoEtiChips);
                if(allchips.size()>=5){
                    String choice = showOptionDialog("Select Chips to Remove",
                            "There are more than 5 chips in the winning row. Choose which set to keep:",
                            "First 5: " + allchips.subList(0, 5),
                            "Last 5: " + allchips.subList(allchips.size() - 5, allchips.size()));

                    if(choice.startsWith("First")){
                        gameEngine.setWinningChips(allchips.subList(0,5));
                    }else{
                        gameEngine.setWinningChips(allchips.subList(allchips.size()-5,allchips.size()));
                    }

                }
                // System.out.println("curr color "+currColor);

                if(currColor.equals(currentPlayer.getColor().toLowerCase())){
                    // System.out.println("curr color "+currColor);
                    resetTurn();

                }

            }

        } else {
            // System.out.println("No chip found at vertex " + vertex + " or color does not match.");
        }
    }
    public String showOptionDialog(String title, String header, String option1, String option2) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);

        ButtonType buttonTypeOne = new ButtonType(option1);
        ButtonType buttonTypeTwo = new ButtonType(option2);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeOne) {
            return option1;
        } else {
            return option2;
        }
    }



    public void handleWinningRing(int vertex, GraphicsContext gc) {
        // System.out.println("snshsbsbsbsb");
        
        // System.out.println("Clicked nnnasd "+vertex);
        Vertex v = gameEngine.currentState.gameBoard.getVertex(vertex);
        if (v != null && v.hasRing() && v.getRing().getColour().toLowerCase().equals(gameEngine.getWinningColor().toLowerCase())) {
            moveRingToThePanel(gameEngine.getWinningColor());
            v.setRing(null);
            gc.clearRect(gameEngine.vertexCoordinates[vertex][0], gameEngine.vertexCoordinates[vertex][1], pieceDimension, pieceDimension);
            ////CHIPS IN A ROW
            gameEngine.findAndSetAllWinningChips(gameEngine.getWinningColor());
            // System.out.println("Winning Chips Identified: " + gameEngine.getWinningChips());
            gameEngine.currentState.setAllPossibleCoinsToRemove(gameEngine.getWinningChips());
            if (gameEngine.getWinningChips().size() > 5){


            }
            // System.out.println("Clicked Vertex: " + vertex);


            gameEngine.setRingSelectionMode(false); // Exit ring selection mode
            gameEngine.setChipRemovalMode(true);
            chipsToRemove = 5;
            // chipsToRemove = winningChips.size();
            Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;

            // if (!currentPlayer.isBot()){
            //     showAlert("Select Chips to Remove", "Please select 5 chips of your color to remove from the board.");
            // }

        }

    }

    private void highlightRemovableRings(List<Integer> removableRings) {
        // removeCircleIndicators(); 
    
        for (Integer ringVertex : removableRings) {
            // System.out.println("qqqqqqqq");
            drawHighlighter(ringVertex, true); 
        }
    }

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
    
    

    private void moveRingToThePanel(String playerColor) {
        if (playerColor.equalsIgnoreCase("white")) {
            if (whiteScore < 3) {
                whiteScoreCircle[whiteScore].setStroke(activeScoreRingColor); // Make the ring appear active
                whiteScore++;

                // Check if White has won
                if (whiteScore == 3) {
                    this.isGameOver=true;
                    whiteWins++; 
                    updateWinsText();
                    showGameOverAlert("White wins!");
                    // showAlert("Game Over", "White wins!");
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
                    this.isGameOver=true;

                    blackWins++; 
                    updateWinsText();
                    showGameOverAlert("Black wins!");


                    // showAlert("Game Over", "Black wins!");
                    //endGame();
                    return;
                }
            }
        }
    }



    private void GUIplaceStartingRing(int vertex, GraphicsContext gc) {
        Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;

        String ringColor = currentPlayer.getColor();
        if (gameEngine.placeStartingRing(vertex, ringColor)) {

            Image ringImage = ringColor.equals("white") ? ringWImage : ringBImage;
            drawImage(ringImage, vertex, gc, true);
            // turnIndicator.setText(gameEngine.currentState.isWhiteTurn ? "White's Turn" : "Black's Turn");
            resetTurn();
            // System.out.println("now turn is" + ringColor);
            // playerTurn();
            // gameEngine.currentState.ringsPlaced+=1;
            // drawHighlighter(vertex,false);
        }
    }


    private void GUIavailablePlacesForStartRings(GraphicsContext gc) {

        ArrayList<Vertex> aVertices = gameEngine.availablePlacesForStartingRings();
        for (Vertex v : aVertices) {
            boolean isVnull = (v != null);
            if (isVnull) {
                drawHighlighter(v.getVertextNumber(), true);
            }
        }
    }


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

            if (Move_Valid[0] == 1){
               

                String color = gameEngine.currentState.currentPlayerColor();
                // gameEngine.checkWinning(vertex, color);
                // if (gameEngine.win(vertex, color)==false)
                // if()
                resetTurn();
                //resetTurn();
                // gameEngine.currentState.selectedChipVertex = -1;
                //removeCircleIndicators();
            }
        } else {
            GameAlerts.alertInvalidMove();

        }

    }


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

    private void highlightRemovableChips(List<Integer> removableChips) {
        removeCircleIndicators(); 
        for (Integer chipVertex : removableChips) {
            drawHighlighter(chipVertex, true); 
        }
    }
    

    private void moveRing(int fromVertex, int toVertex, GraphicsContext gc, int[] Move_Valid, Move currentMove) {
        try{
            if (gameEngine.isInChipRemovalMode()==false && gameEngine.isInRingRemovalMode()==false){
                Vertex sourceVertex = gameEngine.currentState.gameBoard.getVertex(fromVertex);
                Vertex targetVertex = gameEngine.currentState.gameBoard.getVertex(toVertex);
        
                // if (!highlightedVertices.contains(toVertex)) {
                //     GameAlerts.alertInvalidMove();
                //     Move_Valid[0] = 0;
                //     return;
                // }
        
                if (targetVertex.hasRing()) {
                    // System.out.println("FROM: "+sourceVertex.getVertextNumber());
                    // System.out.println("WANTS TO GO: "+targetVertex.getVertextNumber());
                    // System.out.println("MOVE WAS unsuccessfull!");
                    // GameAlerts.alertRingPlacement();
                    Move_Valid[0] = 0;
                    throw new IOException("This exception should be handled by custom handler.");

                    // botTurn(gc);
                    // return;
                }
        
                if (targetVertex.hasCoin()) {
                    // System.out.println("FROM: "+sourceVertex.getVertextNumber());
                    // System.out.println("WANTS TO GO: "+targetVertex.getVertextNumber());
                    // System.out.println("MOVE WAS unsuccessfull!");
                    // botTurn(gc);
                    // GameAlerts.alertPositionHasChip();
                    Move_Valid[0] = 0;
                    throw new IOException("This exception should be handled by custom handler.");
                    // return;
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
                        // Image chipImage = existingChip.getColour().toLowerCase().equals("white") ? chipWImage : chipBImage;
                        // gc.drawImage(chipImage, gameEngine.vertexCoordinates[fromVertex][0] + pieceDimension / 4,
                        //         gameEngine.vertexCoordinates[fromVertex][1] + pieceDimension / 4, pieceDimension / 2, pieceDimension / 2);
                    }
                    ArrayList<Coin> flippedCoins = currentMove.getFlippedCoins();
                    ArrayList<Vertex> vertexesOfFlippedCoins = new ArrayList<>();
        
                    if (!flippedCoins.isEmpty()) {
                        gameEngine.gameSimulation.flipCoins(flippedCoins, gameEngine.currentState.gameBoard);
                        for (Coin coinToFlip : flippedCoins) {
                            Vertex currVertex = gameEngine.currentState.gameBoard.getVertexByCoin(coinToFlip);
                            vertexesOfFlippedCoins.add(currVertex);
                            // Image chipImage = coinToFlip.getColour().toLowerCase().equals("white") ? chipWImage : chipBImage;
                            // gc.drawImage(chipImage, gameEngine.vertexCoordinates[currVertex.getVertextNumber()][0] + pieceDimension / 4,
                            //         gameEngine.vertexCoordinates[currVertex.getVertextNumber()][1] + pieceDimension / 4, pieceDimension / 2,
                            //         pieceDimension / 2);
                        }
                    }
        
                    targetVertex.setRing(ringToMove);
                    Image ringImage = ringToMove.getColour().toLowerCase().equals("white") ? ringWImage : ringBImage;
                    gc.drawImage(ringImage, gameEngine.vertexCoordinates[toVertex][0], gameEngine.vertexCoordinates[toVertex][1], pieceDimension, pieceDimension);
                    updateGameBoard(gameEngine.currentState.getGameBoard(), playObjectCanvas.getGraphicsContext2D());
    
                    gameEngine.currentState.chipRingVertex = -1;
                    gameEngine.currentState.chipsRemaining-=1;
                    gameEngine.currentState.chipPlaced = false;
                    gameEngine.currentState.selectedRingVertex = -1;
                    gameEngine.currentState.updateChipsRingCountForEach();
                    vertexesOfFlippedCoins.add(sourceVertex);
    
                    gameEngine.currentState.setVertexesOfFlippedCoins(vertexesOfFlippedCoins);
                    // System.out.println("CHIPS REAMINING: "+gameEngine.currentState.chipsRemaining);
                    if(gameEngine.currentState.chipsRemaining<=0){
                        showDrawAlert();
                    }
        
                    String color = gameEngine.currentState.isWhiteTurn ? "white" : "black";
                    // gameEngine.checkWinning(fromVertex, color);
                    // System.out.println(flippedCoins);
                    // System.out.println(gameEngine.currentState.gameBoard.strMaker());
                    // System.out.println("---------------------- STARTING FROM HERE ----------------------");
                    if (gameEngine.win(gameEngine.currentState.getVertexesOfFlippedCoins())){
                        String currPlayerColor = gameEngine.getWinningColor(); 
                        Player currentPlayer;
                        
                        List<Integer> removableRings = getRemovableRings();
                        // System.out.println(removableRings);
                        highlightRemovableRings(removableRings);
        
                        if (whitePlayer.getColor().toLowerCase().equalsIgnoreCase(currPlayerColor.toLowerCase())) {
                            currentPlayer = whitePlayer;
                        } else {
                            currentPlayer = blackPlayer;
                        }
        
                        // System.out.println("YOU CAN REMOVE ONE RING");
                        // Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;
                        if(currentPlayer.isBot()){
                            // System.out.println("BOT CAN REMOVE ONE RING");
                            
                            Bot activeBot = currentPlayer.getBot();
                                
    
    
                            Vertex vertexToRemoveBOT = activeBot.removeRing(gameEngine.currentState);
                            handleWinningRing(vertexToRemoveBOT.getVertextNumber(), gc);
                            // System.out.println(gameEngine.currentState.gameBoard.strMaker());
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
                            // removeCircleIndicators();
    
                        } else{
                            GameAlerts.alertRowCompletion(color);
        
                            // System.out.println("YOU CAN REMOVE ONE RING");
                        }
                    }
                    // gameEngine.currentState.setVertexesOfFlippedCoins(null);
        
                } else {
                    GameAlerts.alertNoRing();
                }
            }
        } catch(Exception ex){
            this.isGameOver=true;
            restartGame();
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

    /* Method to draw rings and coins
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



    private void removeCircleIndicators(){
        fieldPane.getChildren().removeIf(node -> node instanceof Circle);
    }



    private Circle makeScoreCircle() {
        Circle circle = new Circle(35);
        circle.setStroke(inactiveScoreRingColor);
        circle.setFill(null);
        circle.setStrokeWidth(10);

        return circle;
    }




    // /
    //  * created bot for the player if bot is selected
    //  * @param color
    //  * @param comboBox
    //  */

    private void botTurn(GraphicsContext gc) {
        if (isGameOver) {
            restartGame();
        }
        // if (whi) {
        //     return;
        // }
        // try{
            GameState gs = gameEngine.getGameState();
            Game_Board gameBoard = gs.gameBoard;
            Player currentPlayer = (currentPlayerIndex == 0) ? whitePlayer : blackPlayer;
    
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
                        // if(move==null){
                        //     return;
                        // }
        
                        vertexTo = gameBoard.getVertexNumberFromPosition(move.getXposition(), move.getYposition());
                        // System.out.println("from "+vertexFrom.getVertextNumber());
                        // System.out.println("to "+vertexTo);
                        // if(vertexTo==-1 || vertexFrom.getVertextNumber()==-1){
                            // while(true){
                            //     move = activeBot.makeMove(gameEngine.getGameState());
                            //     vertexFrom = move.getStartingVertex();
        
                            //     vertexTo = gameBoard.getVertexNumberFromPosition(move.getXposition(), move.getYposition());
                            //     if(vertexTo!=-1 & vertexFrom.getVertextNumber()!=-1){
                            //         break;
                            //     }
                            // }
                            int[] moveValid = {1};
                            
                            moveRing(vertexFrom.getVertextNumber(), vertexTo, gc, moveValid, move);
                    } catch(Exception ex){
                        ex.printStackTrace();
                        System.out.println(gameBoard.strMaker());
                        this.isGameOver=true;
                        restartGame();
                    }
                    
                    }
    
                    
                    // currentPlayerIndex = gameEngine.currentState.isWhiteTurn ? 0 : 1;
    
                }
                // System.out.println("NUMBER OF "+gs.ringsPlaced);
                // System.out.println("NUMBER BLACK "+gs.ringsBlack);
                // System.out.println("NUMBER WHITE "+gs.ringsWhite);
    
                if (move != null) {
                    // resetTurn();
                    // Platform.runLater(() -> resetTurn());
                    resetTurn();
                    // updateGameBoard(gameBoard, gc);
                    updateOnscreenText();
                    
                }
        // } catch(Throwable ex){
        //     // System.out.println(ex);
        //     // return;
        //     PauseTransition pause = new PauseTransition(Duration.seconds(1));
        //     pause.setOnFinished(event -> botTurn(gc));
        //     pause.play();
        //     // Platform.runLater(() ->             restartGame()
        //     // );

        // }
        
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


    private void resetTurn(){
        if (isGameOver) {
            return;
        }
        if (gameEngine.isInChipRemovalMode()==false && gameEngine.isInRingRemovalMode()==false){

            gameEngine.currentState.resetTurn();
            gameEngine.currentState.selectedChipVertex = -1;
            // currentPlayerIndex = gameEngine.currentState.isWhiteTurn ? 0 : 1;
            switchPlayer();
            chipsToRemove=5;
            // lastMoveStartVertex = null;
            // lastMoveEndVertex = null;
            updateStrengthIndicator();
            chipsRemainText.setText("      Chips Remaining: " + gameEngine.currentState.chipsRemaining);

            updateGameBoard(gameEngine.currentState.getGameBoard(), playObjectCanvas.getGraphicsContext2D());
            // System.out.println(gameEngine.currentState.isWhiteTurn);
            turnIndicator.setText(gameEngine.currentState.isWhiteTurn ? "White's Turn" : "Black's Turn");
            drawDashedLine(playObjectCanvas.getGraphicsContext2D());

            playerTurn();
            // Platform.runLater(() -> playerTurn());

        }
        
    }

    private void updateStrengthIndicator() {
        GraphicsContext gc = strengthIndicator.getGraphicsContext2D();
        GameState gs = gameEngine.getGameState();
        gc.clearRect(0, 0, strengthIndicator.getWidth(), strengthIndicator.getHeight());

//        int whiteStrength = gs.calculateStrength("white");
//        int blackStrength = gs.calculateStrength("black");

        double whiteStrength = gs.evaluate(gameEngine.currentState,gameEngine,"white");
        double blackStrength = gs.evaluate(gameEngine.currentState,gameEngine,"black");

        double totalStrength = whiteStrength + blackStrength;

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


    private void updateOnscreenText(){
        chipsWhiteText.setText("Chips White on board: " + gameEngine.currentState.chipsWhite);
        chipsBlackText.setText("Chips Black on board: " + gameEngine.currentState.chipsBlack);

        ringWhiteRemainingText.setText("White Rings Remaining: " + gameEngine.currentState.ringsWhite);
        ringBlackRemainingText.setText("Black Rings Remaining: " + gameEngine.currentState.ringsBlack);
    }

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
    
        System.out.println(stats.toString());
    }
    

    // private void restartGame() {
    //     this.isGameOver=false;
    //     // System.out.println("\n----------------------------");
    //     // System.out.println("Games Played: "+gamesPlayed);
    //     // System.out.println("Number of Black Wins"+whiteScore);
    //     // System.out.println("Number of White wins: "+blackScore);
    //     // System.out.println();
    //     // System.out.println("----------------------------\n");
    //     displayGameStatistics();
    //     isGameStarted = false; 

    //     startGameButton.setDisable(false); 
    //     whitePlayerComboBox.setDisable(false);
    //     blackPlayerComboBox.setDisable(false);
        
    //     gameEngine.resetGame();
    //     gameEngine.currentState.chipsRemaining = 51;  
        
    //     currentPlayerIndex = 0;
    //     // whiteScore = 0;
    //     // blackScore = 0;
    //     ChipsRemoved = 5;
    //     CheckPossibleChipsToRemove = true;
    //     FirstClickOnCoin = true;
    //     nadoEtiChips.clear();
    //     winningChips.clear();
        
    //     GraphicsContext gcP = playObjectCanvas.getGraphicsContext2D();
    //     gcP.clearRect(0, 0, playObjectCanvas.getWidth(), playObjectCanvas.getHeight());
    //     removeCircleIndicators();
        
    //     for (Circle circle : whiteScoreCircle) {
    //         circle.setStroke(inactiveScoreRingColor);
    //     }
    //     for (Circle circle : blackScoreCircle) {
    //         circle.setStroke(inactiveScoreRingColor);
    //     }

    //     whiteScoreCircle = new Circle[3];
    //     blackScoreCircle = new Circle[3];
    //     whiteScoreCircle[0] = makeScoreCircle();
    //     whiteScoreCircle[1] = makeScoreCircle();
    //     whiteScoreCircle[2] = makeScoreCircle();

    //     // Initialize black score rings
    //     blackScoreCircle[0] = makeScoreCircle();
    //     blackScoreCircle[1] = makeScoreCircle();
    //     blackScoreCircle[2] = makeScoreCircle();
    //     updateOnscreenText();  
    //     updateStrengthIndicator();  
    
    //     if (whitePlayer.isBot() && blackPlayer.isBot()) {
    //         // selectPlayer("White", whitePlayer.getName());
    //         // selectPlayer("Black", blackPlayer.getName());
    //         startGame(gcP);
    //     }
    // }

    private void restartGame() {
        displayGameStatistics();
        this.isGameOver = false;
        isGameStarted = false;
        startGameButton.setDisable(false);
        whitePlayerComboBox.setDisable(false);
        blackPlayerComboBox.setDisable(false);
        gameEngine.resetGame();
        gameEngine.currentState.chipsRemaining = 51;
        currentPlayerIndex = 0;
        whiteScore = 0;
        blackScore = 0;
        ChipsRemoved = 5;
        CheckPossibleChipsToRemove = true;
        FirstClickOnCoin = true;
        nadoEtiChips.clear();
        winningChips.clear();
        GraphicsContext gcP = playObjectCanvas.getGraphicsContext2D();
        gcP.clearRect(0, 0, playObjectCanvas.getWidth(), gcP.getCanvas().getHeight());
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
    
    

    private void showGameOverAlert(String message) {
        turnIndicator.setText(message);
        gamesPlayed++;  
        updateGamesPlayedText();

        if(whitePlayer.isBot() && blackPlayer.isBot()){
            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(event -> {
                restartGame();
            });
            pause.play();
            return;
            // restartGame();
            // return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(message);
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

    private void updateGamesPlayedText() {
        gamesPlayedText.setText("Games Played: " + gamesPlayed);
    }
    
    
    private void updateWinsText() {
        whiteWinsText.setText("White Wins: " + whiteWins);
        blackWinsText.setText("Black Wins: " + blackWins);
    }

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