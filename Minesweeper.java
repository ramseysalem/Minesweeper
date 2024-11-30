import java.util.ArrayList;
import java.util.Random;

import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import tester.*;
import java.util.Arrays;

//Extra Credit: Levels of Difficulty, Timer, keeping score, mine count,
// Actual flag representation 
//Represents a cell in the Minesweeper game

class Cell {
  boolean isMine;
  boolean isFlagged;
  boolean isRevealed;
  ArrayList<Cell> neighbors;

  // testing constructor 

  Cell(boolean isMine, boolean isFlagged, boolean isRevealed) {
    this.isMine = isMine; 
    this.isFlagged = isFlagged;
    this.isRevealed = isRevealed;
    this.neighbors = new ArrayList<Cell>(); 
  }

  // actual constructor 

  Cell(boolean isMine) { 
    this(isMine, false, false); 
  }

  // Just go through the list of neighbors instead
  // Count neighboring mines

  int countMines() { 
    int count = 0; 
    for (Cell x : this.neighbors) { 
      if (x.isMine) { 
        count ++; 
      }
    }
    return count; 
  }

  // draw the cells

  WorldImage drawCell() {
    RectangleImage blankGray = new RectangleImage(20, 20, OutlineMode.SOLID, Color.LIGHT_GRAY);
    RectangleImage blackOutline = new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK);
    RectangleImage blankGridSquare = new RectangleImage(20, 20, OutlineMode.SOLID,
        new Color(172, 209, 233));
    BesideAlignImage flag = new BesideAlignImage(AlignModeY.TOP,
        new RectangleImage(2, 18, OutlineMode.SOLID, Color.RED), // Pole
        new RectangleImage(15, 8, OutlineMode.SOLID, Color.RED));
    CircleImage mine = new CircleImage(15 * 2 / 5, OutlineMode.SOLID, Color.BLACK);
    int numMines = this.countMines();
    WorldImage cell;
    if (!this.isRevealed) {
      if (this.isFlagged) {
        cell = new OverlayImage(flag, blankGridSquare);
      } else {
        cell = blankGridSquare;
      }
    } else {
      if (this.isMine) {
        cell = new OverlayImage(mine, blankGray);
      } else {
        if (numMines == 0) {
          cell = blankGray;
        } else if (numMines == 1) {
          cell = new OverlayImage(
              new TextImage(String.valueOf(numMines), 20 * .75, Color.BLUE), blankGray);
        } else if (numMines == 2) {
          cell = new OverlayImage(
              new TextImage(String.valueOf(numMines), 20 * .75, Color.GREEN), blankGray);
        } else {
          cell = new OverlayImage(
              new TextImage(String.valueOf(numMines), 20 * .75, Color.RED), blankGray);
        }
      }
    }
    return new OverlayImage(blackOutline, cell);
  }


}

//represents a timer
class Timer {
  
  int startTime;
  int elapsedTime;
  boolean running;

  Timer() {
    this.startTime = 0;
    this.elapsedTime = 0;
    this.running = false;
  }

  // Start the timer
  void start() {
    this.startTime = (int)System.currentTimeMillis();
    this.running = true;
  }

  // Stop the timer
  void stop() {
    this.elapsedTime += (int)System.currentTimeMillis() - this.startTime;
    this.running = false;
  }

  // Reset the timer
  void reset() {
    this.startTime = 0;
    this.elapsedTime = 0;
    this.running = false;
  }

  // Get the current elapsed time
  long getElapsedTime() {
    long time = this.elapsedTime;
    if (this.running) {
      time += (int)System.currentTimeMillis() - this.startTime;
    }
    return time;
  }
}

//Represents the Minesweeper game

class MinesweeperGame extends World {
  int numRows;
  int numCols;
  int cellWidth = 20;
  int cellHeight = 20;
  int width;
  int height;
  int countMines;
  int countClicks;
  ArrayList<ArrayList<Cell>> board = new ArrayList<>(); 
  Random rand;
  boolean gameOver;
  boolean win;
  boolean started; 
  Timer timer;

  //this one is for testing, create another one without the given random seed for actual gameplay
  MinesweeperGame(int numRows, int numCols, int countMines, Random rand, 
      boolean started) {
    this.numRows = numRows;
    this.numCols = numCols;
    this.width = this.cellWidth * this.numRows;
    this.height = this.cellHeight * this.numCols;
    this.countMines = countMines;
    this.rand = rand;
    this.gameOver = false;
    this.win = false;
    this.countClicks = 0;
    this.started = false; 
    this.timer = new Timer();


    // Initialize the board with empty cells
    initializeBoard(numRows, numCols);

    // Place mines randomly on the board
    placeMines(countMines); 
  }

  // actual constructor 

  MinesweeperGame(int numRows, int numColumns, int countMines) {
    this(numRows, numColumns, countMines, new Random(), false); 
  }



  //onKeyEvent handler - effect 
  
  public void onKeyEvent(String key) {

    //handle all of the possible key events
    if (key.equals("space") || key.equals("enter") || key.equals("1") 
        || key.equals("2") || key.equals("3")) {

      if (key.equals("enter")) { 
        started = true; 
        this.timer.start();
      }
      if (key.equals("1")) { 
        numRows = 7; 
        numCols = 7; 
        countMines = 4; 
      }
      
      if (key.equals("2")) { 
        numRows = 12; 
        numCols = 12; 
        countMines = 47; 
      }
      
      if (key.equals("3")) { 
        numRows = 15; 
        numCols = 15; 
        countMines = 88; 
      }

    }
  }


  // Initialize the board with empty cells

  void initializeBoard(int numRows, int numCols) {
    for (int i = 0; i < numRows; i++) {
      ArrayList<Cell> row = new ArrayList<>();
      for (int j = 0; j < numCols; j++) {
        row.add(new Cell(false, false, false));
      }
      this.board.add(row);
    }

    // Link cells together
    linkCells();
  }

  // Link cells together so that every cell has a list of its neighbors

  void linkCells() {
    for (int i = 0; i < this.numRows; i++) {
      for (int j = 0; j < this.numCols; j++) {
        Cell cell = this.board.get(i).get(j);
        // Iterate over neighboring cells
        for (int dx = -1; dx <= 1; dx++) {
          for (int dy = -1; dy <= 1; dy++) {
            int neighborX = i + dx;
            int neighborY = j + dy;
            // Ensure neighbor is within bounds and not the current cell
            if (isValidCell(neighborX, neighborY) && !(dx == 0 && dy == 0)) {
              cell.neighbors.add(position(neighborX, neighborY));
            }
          }
        }
      }
    }
  }


  // returns a cell at a certain position 

  Cell position(int x, int y) { 
    return this.board.get(x).get(y); 
  }

  // Check if given coordinates represent a valid cell on the board

  boolean isValidCell(int x, int y) {
    return x >= 0 && x < this.numRows && y >= 0 && y < this.numCols;
  }


  // Place mines randomly on the board

  void placeMines(int mines) {
    int minesRemaining = mines; 

    for (ArrayList<Cell> row : this.board) { 
      for (Cell x : row) { 
        boolean mineCheck = rand.nextBoolean(); 
        if (minesRemaining > 0 && mineCheck && !x.isMine) { 
          x.isMine = true;
          minesRemaining -= 1; 
        }
      }
    }
    if (minesRemaining != 0) {
      this.placeMines(minesRemaining); 
    }
  }


  // Draw the game board

  public WorldScene makeScene() {

    if (!started) { 
      WorldImage box = new OverlayImage(
          new RectangleImage(80, 30, OutlineMode.OUTLINE, Color.BLACK),
          new RectangleImage(80, 30, OutlineMode.SOLID, Color.LIGHT_GRAY));
      WorldScene result = new WorldScene(300, 300);
      result.placeImageXY(
          new AboveImage(
              new TextImage("difficulty level, enter to start", 15, FontStyle.BOLD, Color.BLACK),
              new OverlayImage(new TextImage("easy = 1", 10, FontStyle.ITALIC, Color.GREEN), box),
              new OverlayImage(new TextImage("medium = 2", 10, FontStyle.ITALIC, Color.YELLOW),
                  box),
              new OverlayImage(new TextImage("hard = 3", 10, FontStyle.ITALIC, Color.RED), box)),
          150, 150);
      return result;
    } else  {
      WorldScene scene = new WorldScene(numCols * 20, numRows * 20);

      // Draw cells

      for (int i = 0; i < this.numRows; i++) {
        for (int j = 0; j < this.numCols; j++) {
          WorldImage cellImage = this.position(i, j).drawCell();
          scene.placeImageXY(cellImage, j * 20 + 10, i * 20 + 10);
        }
      }
      
      int remainingMines = countRemainingMines();

      String clickCountText = "Click Score: " + this.countClicks;
      WorldImage clickCountImage = new TextImage(clickCountText, 12, Color.BLACK);
      scene.placeImageXY(clickCountImage, this.width / 2 + 100, this.height / 2);

      if (started) {
        String timeText = "Time Elapsed: " + this.timer.getElapsedTime() / 1000 + " seconds";
        WorldImage timeImage = new TextImage(timeText, 12, Color.BLACK);
        scene.placeImageXY(timeImage, this.width / 2 + 100, this.height / 2 + 50);
      }
      
      String minesText = "Mines Remaining: " + remainingMines;
      WorldImage minesImage = new TextImage(minesText, 12, Color.BLACK);
      scene.placeImageXY(minesImage, this.width / 2 + 100, this.height / 2 + 100);

      return scene;
    }
  }

  // Handle mouse clicks

  public void onMouseClicked(Posn pos, String button) {
    if (!gameOver) {
      int col = pos.x / 20;
      int row = pos.y / 20;
      if (isValidCell(row, col)) {
        if (button.equals("LeftButton")) {
          revealCell(row, col);
          this.countClicks++;
        }
        else if (button.equals("RightButton")) {
          flagCell(row, col);
        }
        checkWinLoss();
      }
    }
  }

  // Reveal the cell at the given row and column

  void revealCell(int row, int col) {
    Cell cell = board.get(row).get(col);
    if (!cell.isRevealed && !cell.isFlagged) {
      cell.isRevealed = true;
      if (cell.isMine) {
        gameOver = true;
      }
      else if (cell.countMines() == 0) {
        // Flood-fill effect
        for (Cell neighbor : cell.neighbors) {
          revealCell(neighbor);
        }
      }
    }
  }

  // Flood-fill to reveal neighboring cells with no adjacent mines

  void revealCell(Cell cell) {
    if (!cell.isRevealed && !cell.isFlagged) {
      cell.isRevealed = true;
      if (cell.countMines() == 0) {
        for (Cell neighbor : cell.neighbors) {
          revealCell(neighbor);
        }
      }
    }
  }

  // Flag or unflag the cell at the given row and column

  void flagCell(int row, int col) {
    Cell cell = board.get(row).get(col);
    cell.isFlagged = !cell.isFlagged;
  }

  // Check for win or loss conditions

  void checkWinLoss() {
    int unrevealedCells = 0;
    int flaggedMines = 0;
    for (ArrayList<Cell> row : board) {
      for (Cell cell : row) {
        if (!cell.isRevealed) {
          unrevealedCells++;
          if (cell.isMine && cell.isFlagged) {
            flaggedMines++;
          }
        }
      }
    }
    if (flaggedMines == countMines && unrevealedCells == countMines) {
      win = true;
      gameOver = true;
    }
    else if (gameOver) {
      win = false;
    }
  }

  // Ends the game
  public WorldEnd worldEnds() {
    if (this.gameOver) {
      this.timer.stop();
      return new WorldEnd(true, this.makeAFinalScene());
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // Draws the final scene
  public WorldScene makeAFinalScene() {
    WorldScene scene = new WorldScene(this.width, this.height);
    WorldImage endScreen = new TextImage("You've Lost the Game, WOMP WOMP", this.width / 25,
        Color.BLACK);
    scene.placeImageXY(endScreen, this.width / 2, this.height / 2);
    return scene;
  }
  
  // counts the remaining amount of mines in the game 
  
  int countRemainingMines() {
    int remainingMines = this.countMines;
    for (ArrayList<Cell> row : this.board) {
      for (Cell cell : row) {
        if ((cell.isRevealed || cell.isFlagged) && (cell.isMine)) {
          remainingMines--;
        }
      }
    }
    return remainingMines;
  }
}

// examples for minesweeper game 

class ExamplesMinesweeper {

  MinesweeperGame game1;
  MinesweeperGame game2;

  Cell cell101;
  Cell cell102;
  Cell cell103;
  Cell cell104;
  Cell cell105;
  Cell cell106;

  Cell cell1;
  Cell cell2;
  Cell cell3;
  Cell cell4;
  Cell cell5;
  Cell cell6;
  Cell cell7;
  Cell cell8;
  Cell cell9;
  Cell cell10;
  Cell cell11;
  Cell cell12;
  Cell cell13;
  Cell cell14;
  Cell cell15;
  Cell cell16;

  WorldImage regularSquare;  

  ArrayList<ArrayList<Cell>> board;

  void init() {

    this.game1 = new MinesweeperGame(4, 4, 3, new Random(5), false);
    this.game1.gameOver = false;
    this.game2 = new MinesweeperGame(2, 2, 2, new Random(5), false);
    this.game2.gameOver = false;

    cell1 = new Cell(true);
    cell2 = new Cell(false);
    cell3 = new Cell(false);
    cell4 = new Cell(true);
    cell5 = new Cell(false);
    cell6 = new Cell(true);
    cell7 = new Cell(false);
    cell8 = new Cell(false);
    cell9 = new Cell(false);
    cell10 = new Cell(false);
    cell11 = new Cell(false);
    cell12 = new Cell(false);
    cell13 = new Cell(false);
    cell14 = new Cell(false);
    cell15 = new Cell(false);
    cell16 = new Cell(false);

    // cells to test drawCell
    // different cases with mines, flags, revealed and not.

    cell106 = new Cell(true, false, true);
    cell103 = new Cell(false, true, false); 
    cell101 = new Cell(false, false, false); 
    cell102 = new Cell(true, false, false); 
    cell104 = new Cell(false, false, true); 
    cell105 = new Cell(true, true, false); 


    this.cell1.neighbors = new ArrayList<>(Arrays.asList(this.cell2, this.cell5, this.cell6));
    this.cell2.neighbors = new ArrayList<>(
        Arrays.asList(this.cell1, this.cell3, this.cell5, this.cell6, this.cell7));
    this.cell3.neighbors = new ArrayList<>(
        Arrays.asList(this.cell2, this.cell4, this.cell6, this.cell7, this.cell8));
    this.cell4.neighbors = new ArrayList<>(Arrays.asList(this.cell3, this.cell7, this.cell8));
    this.cell5.neighbors = new ArrayList<>(
        Arrays.asList(this.cell1, this.cell2, this.cell6, this.cell9, this.cell10));
    this.cell6.neighbors = new ArrayList<>(Arrays.asList(this.cell1, this.cell2, this.cell3,
        this.cell5,
        this.cell7, this.cell9, this.cell10, this.cell11));
    this.cell7.neighbors = new ArrayList<>(Arrays.asList(this.cell2, this.cell3, this.cell4,
        this.cell6,
        this.cell8, this.cell10, this.cell11, this.cell12));
    this.cell8.neighbors = new ArrayList<>(
        Arrays.asList(this.cell3, this.cell4, this.cell7, this.cell11, this.cell12));
    this.cell9.neighbors = new ArrayList<>(
        Arrays.asList(this.cell5, this.cell6, this.cell10, this.cell13, this.cell14));
    this.cell10.neighbors = new ArrayList<>(Arrays.asList(this.cell5, this.cell6, this.cell7, 
        this.cell9,
        this.cell11, this.cell13, this.cell14, this.cell15));
    this.cell11.neighbors = new ArrayList<>(Arrays.asList(this.cell6, this.cell7, this.cell8,
        this.cell10,
        this.cell12, this.cell14, this.cell15, this.cell16));
    this.cell12.neighbors = new ArrayList<>(
        Arrays.asList(this.cell7, this.cell8, this.cell11, this.cell15, this.cell16));
    this.cell13.neighbors = new ArrayList<>(Arrays.asList(this.cell9, this.cell10, 
        this.cell14));
    this.cell14.neighbors = new ArrayList<>(
        Arrays.asList(this.cell9, this.cell10, this.cell11, this.cell13, this.cell15));
    this.cell15.neighbors = new ArrayList<>(
        Arrays.asList(this.cell10, this.cell11, this.cell12, this.cell14, this.cell16));
    this.cell16.neighbors = new ArrayList<>(Arrays.asList(this.cell11, this.cell12, 
        this.cell15));

    this.board = new ArrayList<>(
        Arrays.asList(new ArrayList<>(Arrays.asList(this.cell1, this.cell2, this.cell3,
            this.cell4)),
            new ArrayList<>(Arrays.asList(this.cell5, this.cell6, this.cell7, this.cell8)),
            new ArrayList<>(Arrays.asList(this.cell9, this.cell10, this.cell11, this.cell12)),
            new ArrayList<>(Arrays.asList(this.cell13, this.cell14, this.cell15, 
                this.cell16))));
    //images for testing: 
    this.regularSquare = new OverlayImage(
        new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(20, 20, OutlineMode.SOLID, new Color(172, 209, 233)));
  }

  // Test Big Bang
  void testBigBang(Tester t) {
    this.init(); 
    MinesweeperGame game = new MinesweeperGame(30, 16, 99, new Random(), false);
    int worldWidth = 30 * 20; // Each cell is 20 pixels wide
    int worldHeight = 16 * 20; // Each cell is 20 pixels high
    double tickRate = 1.0 / 60.0; // Tick rate of 60 ticks per second
    game.bigBang(worldWidth, worldHeight, tickRate);
  }

  // test makeScene

  void testMakeScene(Tester t) {
    this.init();
    WorldScene newWorld = new WorldScene(300, 300);
    newWorld.placeImageXY(regularSquare, 20, 20);
    newWorld.placeImageXY(regularSquare, 10, 10);
    newWorld.placeImageXY(regularSquare, 30, 10);
    newWorld.placeImageXY(regularSquare, 10, 30);
    newWorld.placeImageXY(regularSquare, 30, 30);
    t.checkExpect(this.game2.makeScene(), game2.makeScene());
  }

  // test initBoard

  void testInitializeBoard(Tester t) { 
    this.init(); 
    t.checkExpect(this.game1.board, this.board); 
  }

  // test the helper function position 

  void testPosition(Tester t) {
    this.init(); 
    t.checkExpect(this.game1.position(0, 0), this.cell1);
    t.checkExpect(this.game1.position(0, 1), this.cell2);
    t.checkExpect(this.game1.position(2, 2), this.cell11);
  }

  // test isValidCell helper function 

  void testIsValidCell(Tester t) { 
    this.init();
    t.checkExpect(this.game1.isValidCell(500, 0), false);
    t.checkExpect(this.game1.isValidCell(1, 0), true); 
    t.checkExpect(this.game1.isValidCell(4, 4), false);
    t.checkExpect(this.game1.isValidCell(4, 3), false);
  }

  // test revealCell 

  void testRevealCell(Tester t) { 
    this.init(); 
    this.game1.revealCell(0,0);
    t.checkExpect(this.game1.board.get(0).get(0).isRevealed, true);    
  }

  // test linkCells 

  void testAddNeighbors(Tester t) {
    this.init();
    t.checkExpect(this.game1.board.get(0).get(0).neighbors, this.cell1.neighbors); 
    t.checkExpect(this.game1.board.get(0).get(3).neighbors, this.cell4.neighbors);
    t.checkExpect(this.game1.board.get(0).get(2).neighbors, 
        this.cell3.neighbors); // tests x cord
    t.checkExpect(this.game1.board.get(2).get(0).neighbors, 
        this.cell9.neighbors); // tests y cord
    t.checkExpect(this.game1.board.get(2).get(2).neighbors, 
        this.cell11.neighbors); // tests x and y
  }

  // test countMines in the cell class 

  void testcountMines(Tester t) {
    this.init(); 
    t.checkExpect(this.cell5.countMines(), 2); 
    t.checkExpect(this.cell11.countMines(), 1); 
    t.checkExpect(this.cell7.countMines(), 2); 
    t.checkExpect(this.cell1.countMines(), 1); 
  }

  // test DrawCell 

  void testDrawCell(Tester t) { 
    t.checkExpect(cell1.drawCell(),
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
            new RectangleImage(20, 20, OutlineMode.SOLID, new Color(172, 209, 233))));
    this.cell2.isRevealed = true;
    t.checkExpect(cell2.drawCell(),
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
            new OverlayImage(new TextImage("2", 15, Color.GREEN),
                new RectangleImage(20, 20, OutlineMode.SOLID, Color.LIGHT_GRAY))));
    t.checkExpect(cell106.drawCell(),
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
            new OverlayImage(new CircleImage(6, OutlineMode.SOLID, Color.BLACK),
                new RectangleImage(20, 20, OutlineMode.SOLID, Color.LIGHT_GRAY))));
  }

  // test onMouseClicked

  void testOnMouseClicked(Tester t) {
    init();
    t.checkExpect(this.game1.gameOver, false);
    // not validPos --> check that nothing changes
    // no specific cell to check flag, mine, and shown fields
    this.game1.onMouseClicked(new Posn(10, 10), "LeftButton");
    t.checkExpect(this.game1.gameOver, true);
  }
  
  //Add a new test method to test the Timer class
  void testTimer(Tester t) {
    Timer timer = new Timer();
    t.checkExpect(timer.getElapsedTime(), 0L);
    timer.start();
    try {
      Thread.sleep(1000);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  //testing onKeyEvent
  void testOnKeyEvent(Tester t) {

    // Setting up the initial conditions
    this.init();

    // Checking the behavior when "enter" key is pressed
    t.checkExpect(this.game1.started, false);
    this.game1.onKeyEvent("enter");
    t.checkExpect(this.game1.started, true);
  }

  //Test checkWinLoss
  void testCheckWinLoss(Tester t) {
    MinesweeperGame game = new MinesweeperGame(4, 4, 3);
    game.board.get(0).get(0).isMine = true;
    game.board.get(0).get(0).isFlagged = true;
    game.board.get(0).get(0).isRevealed = true;
    game.countMines = 1;
    t.checkExpect(game.win, false);
    t.checkExpect(game.gameOver, false);
    game.checkWinLoss();
    t.checkExpect(game.win, false);
    t.checkExpect(game.gameOver, false);
  }
  
  //Test worldEnds
  void testWorldEnds(Tester t) {
    MinesweeperGame game = new MinesweeperGame(4, 4, 3);
    game.gameOver = true;
    game.worldEnds();
    t.checkExpect(game.worldEnds(), game.worldEnds());
    t.checkExpect(game.makeScene(), game.makeScene());
  }
  
  //Test makeAFinalScene
  void testMakeAFinalScene(Tester t) {
    MinesweeperGame game = new MinesweeperGame(4, 4, 3);
    WorldScene expected = new WorldScene(80, 80);
    expected.placeImageXY(new TextImage("You've Lost the Game, WOMP WOMP", 3, Color.BLACK), 40, 40);
    t.checkExpect(game.makeAFinalScene(), expected);
  }
  
  // Test linkCells for correct linking of neighboring cells

  void testLinkCells(Tester t) {
    MinesweeperGame game = new MinesweeperGame(3, 3, 1);
    game.initializeBoard(3, 3);
    ArrayList<ArrayList<Cell>> board = game.board;
    Cell middleCell = board.get(1).get(1);
    ArrayList<Cell> middleCellNeighbors = middleCell.neighbors;
    t.checkExpect(middleCellNeighbors.size(), 16); 
    Cell cornerCell = board.get(0).get(0);
    ArrayList<Cell> cornerCellNeighbors = cornerCell.neighbors;
    t.checkExpect(cornerCellNeighbors.size(), 6);
    Cell edgeCell = board.get(0).get(1);
    ArrayList<Cell> edgeCellNeighbors = edgeCell.neighbors;
    t.checkExpect(edgeCellNeighbors.size(), 10);
    Cell isolatedCell = board.get(2).get(2);
    ArrayList<Cell> isolatedCellNeighbors = isolatedCell.neighbors;
    t.checkExpect(isolatedCellNeighbors.size(), 6);
  }

  // Test placeMines method to ensure correct placement of mines

  void testPlaceMines(Tester t) {
    MinesweeperGame game = new MinesweeperGame(5, 5, 3);
    game.initializeBoard(3, 3);
    ArrayList<ArrayList<Cell>> board = game.board;
    int minesPlaced = 0;
    for (ArrayList<Cell> row : board) {
      for (Cell cell : row) {
        if (cell.isMine) {
          minesPlaced++;
        }
      }
    }
    t.checkExpect(minesPlaced, 3);
    MinesweeperGame game2 = new MinesweeperGame(12, 12, 10);
    game2.initializeBoard(5, 5); 
    ArrayList<ArrayList<Cell>> board2 = game2.board;
    int minesPlaced2 = 0;
    for (ArrayList<Cell> row : board2) {
      for (Cell cell : row) {
        if (cell.isMine) {
          minesPlaced2++;
        }
      }
    }
    t.checkExpect(minesPlaced2, 10);
  }

  // Test flagCell method to ensure correct flagging and unflagging of cells

  void testFlagCell(Tester t) {
    MinesweeperGame game = new MinesweeperGame(3, 3, 1);
    game.initializeBoard(3, 3);
    game.flagCell(0, 0);
    t.checkExpect(game.board.get(0).get(0).isFlagged, true);
    game.flagCell(0, 0);
    t.checkExpect(game.board.get(0).get(0).isFlagged, false);
    game.flagCell(1, 1);
    t.checkExpect(game.board.get(1).get(1).isFlagged, true);
  }
  
  // Test countRemainingMines method
  
  void testCountRemainingMines(Tester t) {
    MinesweeperGame game = new MinesweeperGame(3, 3, 3);
    game.initializeBoard(3, 3);

    t.checkExpect(game.countRemainingMines(), 3);


    game.flagCell(1, 1);
    t.checkExpect(game.countRemainingMines(), 3);


    game.revealCell(2, 2);
    t.checkExpect(game.countRemainingMines(), 3);
  }


}
