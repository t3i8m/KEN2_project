package Game_Components.Board;

/**
 * This is a sample of how to run the game board in practice

 * I have made a simple board and just printed it out

 * This is a very simple implementation will work on improving on the side
 */
public class game_board_tester {
    public static void main(String[] args) {
        Game_Board gb1 = new Game_Board();

        gb1.fillBoard();


        String h = gb1.strMaker();
        System.out.println(h);
    }
}
