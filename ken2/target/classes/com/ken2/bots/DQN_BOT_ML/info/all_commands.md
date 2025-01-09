## State Vector (BoardTransformation.java -> toVector()):
    // 0 - empty cell
    // 1 - WHITE RING
    // 2 - BLACK RING
    // 3 - WHITE COIN
    // 4 - BLACK COIN
    // LAST DIGIT:
        // 5 - white player turn
        // 6 - black player turn

## Actions State:
    // PLACE_RING
    // PLACE_CHIP
    // MOVE_RING
    // REMOVE_CHIP

## Rewards:
    // WIN(10),
    // CHIPS_IN_A_ROW(4),
    // FIVE_CHIPS_IN_A_ROW(6),
    // YOUR_RING_REMOVAL(10),
    // OPPONENT_RING_REMOVAL(-10),
    // SUCCESSSFUL_MOVE(1),
    // INVALID_MOVE(-100),
    // DRAW(5),
    // LOSE(-10);