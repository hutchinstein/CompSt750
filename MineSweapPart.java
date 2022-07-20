import javax.swing.*;
import javax.swing.plaf.synth.SynthScrollBarUI;

import java.awt.*;
import java.awt.event.*;

import java.util.Random;
import java.util.ArrayList;

public class MineSweapPart extends JFrame
{
  /**** SETTING CONSTANTS ****/
  private static final long serialVersionUID = 1L;
  private static final int WINDOW_HEIGHT = 760;
  private static final int WINDOW_WIDTH = 760;
  private static final int TOTAL_MINES = 16;
  
  private static int guessedMinesLeft = TOTAL_MINES;
  private static int actualMinesLeft = TOTAL_MINES;

  private static final String INITIAL_CELL_TEXT = "";
  private static final String UNEXPOSED_FLAGGED_CELL_TEXT = "@";
  private static final String EXPOSED_MINE_TEXT = "M";
  
  // visual indication of an exposed MyJButton
  private static final Color EXPOSED_CELL_BACKGROUND_COLOR = Color.lightGray;
  // colors used when displaying the getStateStr() String
  private static final Color EXPOSED_CELL_FOREGROUND_COLOR_MAP[] = {Color.lightGray, Color.blue, Color.green, Color.cyan, Color.yellow, 
                                           Color.orange, Color.pink, Color.magenta, Color.red, Color.red};
  
  // holds the "number of mines in perimeter" value for each MyJButton 
  private static final int MINEGRID_ROWS = 16;
  private static final int MINEGRID_COLS = 16;
  private int[][] mineGrid = new int[MINEGRID_ROWS][MINEGRID_COLS];

  private static final int NO_MINES_IN_PERIMETER_MINEGRID_VALUE = 0;
  private static final int ALL_MINES_IN_PERIMETER_MINEGRID_VALUE = 8;
  private static final int IS_A_MINE_IN_MINEGRID_VALUE = 9;
  private static final int EXPOSED_CELL = -1;
  
  private boolean running = true;
  
  ArrayList<Integer> mineCoordinates = new ArrayList<Integer>();
  ArrayList<Integer> flaggedCells = new ArrayList<Integer>();

  public MineSweapPart()
  {
    this.setTitle("MineSweap                                                         " + 
                  MineSweapPart.guessedMinesLeft +" Mines left");
    this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    this.setResizable(false);
    this.setLayout(new GridLayout(MINEGRID_ROWS, MINEGRID_COLS, 0, 0));
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);

    // set the grid of MyJbuttons
    this.createContents();
    
    // place MINES number of mines in sGrid and adjust all of the "mines in perimeter" values
    this.setMines();
    
    this.setVisible(true);
  }

  public void createContents()
  {
    for (int mgr = 0; mgr < MINEGRID_ROWS; ++mgr)
    {  
      for (int mgc = 0; mgc < MINEGRID_COLS; ++mgc)
      {  
        // set sGrid[mgr][mgc] entry to 0 - no mines in it's perimeter
        this.mineGrid[mgr][mgc] = NO_MINES_IN_PERIMETER_MINEGRID_VALUE; 
        
        // create a MyJButton that will be at location (mgr, mgc) in the GridLayout
        MyJButton but = new MyJButton(INITIAL_CELL_TEXT, mgr, mgc); 
        
        // register the event handler with this MyJbutton
        but.addActionListener(new MyListener());
        
        // add the MyJButton to the GridLayout collection
        this.add(but);
      }  
    }
  }

  // begin nested private class
  private class MyListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if ( running )
      {
        // used to determine if ctrl or alt key was pressed at the time of mouse action
        int mod = event.getModifiers();
        MyJButton mjb = (MyJButton)event.getSource();
        
        // is the MyJbutton that the mouse action occurred in flagged
        boolean flagged = mjb.getText().equals(MineSweapPart.UNEXPOSED_FLAGGED_CELL_TEXT);
        
        // is the MyJbutton that the mouse action occurred in already exposed
        boolean exposed = mjb.getBackground().equals(EXPOSED_CELL_BACKGROUND_COLOR);
       
        // flag a cell : ctrl + left click
        if ( !flagged && !exposed && (mod & ActionEvent.CTRL_MASK) != 0 )
        {
          mjb.setText(MineSweapPart.UNEXPOSED_FLAGGED_CELL_TEXT);
          --MineSweapPart.guessedMinesLeft;
          flaggedCells.add(getLinearizedValueOfCell(mjb.ROW, mjb.COL));
          if(MineSweapPart.guessedMinesLeft == 0){
            
            // No guesses remain, game ends.  exposeCell will display all
            // mine locations.
            running = false;
            exposeCell(mjb);
          }
          
          // if the MyJbutton that the mouse action occurred in is a mine
          if ( mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_MINEGRID_VALUE )
          {
            --MineSweapPart.actualMinesLeft;
            if(MineSweapPart.actualMinesLeft == 0){
              running = false;
              exposeCell(mjb);
            }
            // what else do you need to adjust?
            // could the game be over?
          }
          setTitle("MineSweap                                                         " + 
                   MineSweapPart.guessedMinesLeft +" Mines left");
        }
       
        // unflag a cell : alt + left click
        else if ( flagged && !exposed && (mod & ActionEvent.ALT_MASK) != 0 )
        {
          mjb.setText(INITIAL_CELL_TEXT);
          ++MineSweapPart.guessedMinesLeft;
          flaggedCells.remove(indexOf(flaggedCells, getLinearizedValueOfCell(mjb.ROW, mjb.COL)));
         
          // if the MyJbutton that the mouse action occurred in is a mine
          if ( mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_MINEGRID_VALUE )
          {
            ++MineSweapPart.actualMinesLeft;
            // what else do you need to adjust?
            // could the game be over?
          }
          setTitle("MineSweap                                                         " + 
                    MineSweapPart.guessedMinesLeft +" Mines left");
        }
     
        // expose a cell : left click
        else if ( !flagged && !exposed )
        {
          exposeCell(mjb);
        }  
      }
    }
    
    public int indexOf(ArrayList<Integer> flaggedCells, int targetCell) {
      for(int i = 0; i < flaggedCells.size(); i++){
        if(flaggedCells.get(i) == targetCell){
          return i;
        }
      }
      return 0;
    }

    public void exposeCell(MyJButton mjb)
    {
      if ( !running ){
        for(int linearizedValue : mineCoordinates){
          MyJButton mjbn = (MyJButton)mjb.getParent().getComponent(linearizedValue);
          if(flaggedCells.contains(linearizedValue)){
            mjbn.setBackground(Color.green);
          }
          else{
            mjbn.setForeground(Color.red);
            mjbn.setBackground(EXPOSED_CELL_BACKGROUND_COLOR);
          }
          mjbn.setText(getGridValueStr(mjbn.ROW, mjbn.COL));
        }
        return;
      }


      //If running is true the following block will run
      mjb.setBackground(EXPOSED_CELL_BACKGROUND_COLOR);
      mjb.setForeground(EXPOSED_CELL_FOREGROUND_COLOR_MAP[mineGrid[mjb.ROW][mjb.COL]]);
      mjb.setText(getGridValueStr(mjb.ROW, mjb.COL));
      
      // if the MyJButton that was just exposed is a mine
      if ( mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_MINEGRID_VALUE )
      {  
        // NEED TO DISPLAY LOCATION OF MINES -- SOMEHOW FLAG MINES THAT WE FOUND
        // what else do you need to adjust?
        // could the game be over?

        // Stopping the game and sending this back to exposeCell to display all mine locations
        running = false;
        exposeCell(mjb);
        return;
      }
      
      // if the MyJButton that was just exposed has no mines in its perimeter
      if ( mineGrid[mjb.ROW][mjb.COL] == NO_MINES_IN_PERIMETER_MINEGRID_VALUE)
      {
        // To prevent churning over cells that have already been exposed, setting them to -1.
        // This helps to prevent it from ping-ponging back and forth between cells it has already checked
        // and move on to discover new cells.
        mineGrid[mjb.ROW][mjb.COL] = EXPOSED_CELL;

        // Expose cell to the left
        try{
          MyJButton mjbn = (MyJButton)mjb.getParent().getComponent(getLinearizedValueOfCell(mjb.ROW, mjb.COL) - 1);
          if(mineGrid[mjbn.ROW][mjbn.COL] != IS_A_MINE_IN_MINEGRID_VALUE && mjbn.getText() == INITIAL_CELL_TEXT){
            exposeCell(mjbn);
          }
        }catch(Exception e){
        }
        // Expose cell to the right 
        try{
          MyJButton mjbn = (MyJButton)mjb.getParent().getComponent(getLinearizedValueOfCell(mjb.ROW, mjb.COL) + 1);
          if(mineGrid[mjbn.ROW][mjbn.COL] != IS_A_MINE_IN_MINEGRID_VALUE && mjbn.getText() == INITIAL_CELL_TEXT){
            exposeCell(mjbn);
          }
        }catch(Exception e){
        }
        // Expose cell below current cell
        try{
          MyJButton mjbn = (MyJButton)mjb.getParent().getComponent(getLinearizedValueOfCell(mjb.ROW, mjb.COL) + 16);
          if(mineGrid[mjbn.ROW][mjbn.COL] != IS_A_MINE_IN_MINEGRID_VALUE && mjbn.getText() == INITIAL_CELL_TEXT){
            exposeCell(mjbn);
          }
        }catch(Exception e){
        }
        // Expose cell above current cell
      try{
          MyJButton mjbn = (MyJButton)mjb.getParent().getComponent(getLinearizedValueOfCell(mjb.ROW, mjb.COL) - 16);
          if(mineGrid[mjbn.ROW][mjbn.COL] != IS_A_MINE_IN_MINEGRID_VALUE && mjbn.getText() == INITIAL_CELL_TEXT){
            exposeCell(mjbn);
          }
        }catch(Exception e){
        }
      }
    }


    public static int getLinearizedValueOfCell(int row, int col){
      int linearValue = (row * MINEGRID_ROWS) + col;
      return linearValue;
    }
  }
  // end nested private class


  public static void main(String[] args)
  {
    new MineSweapPart();
  }

  
  //************************************************************************************************

  // Generate random coordinates for mines to be placed in
  public static int generateCoordinate(){
    Random random = new Random();
    int coordinate = random.nextInt(16);
    return coordinate;
  }

  public int minesInPerimeter(int row, int col){
    int minesInPerimeter = 0;
    for(int row_neighbor = (row - 1); row_neighbor < (row + 2); row_neighbor++){
            for(int col_neighbor = (col - 1); col_neighbor < (col + 2); col_neighbor++){
              try{
                if( this.mineGrid[row_neighbor][col_neighbor] == IS_A_MINE_IN_MINEGRID_VALUE ){
                  if(!(row == row_neighbor && col == col_neighbor)){
                    minesInPerimeter++;
                  }
                }
              }catch(Exception e){
              }
            } 
        }
    return minesInPerimeter;
  }

  // place MINES number of mines in sGrid and adjust all of the "mines in perimeter" values
  private void setMines()
  {
    // your code here ...
    // placing mines in TOTAL_MINES locations
    int minesToBePlaced = TOTAL_MINES;
    while(minesToBePlaced > 0){
      int x = generateCoordinate();
      int y = generateCoordinate();
      // Checking if the value = 0 prevents assigning a cell to be a mine
      // when it already is one.  
      if(this.mineGrid[x][y] == 0){
        this.mineGrid[x][y] = IS_A_MINE_IN_MINEGRID_VALUE;
        this.mineCoordinates.add(MyListener.getLinearizedValueOfCell(x, y));
        --minesToBePlaced;
      }
    }

    // update each cell with total number of mines touching it
    for(int row = 0; row < MINEGRID_ROWS; row++){
      for(int col = 0; col < MINEGRID_COLS; col++){
        if(this.mineGrid[row][col] != IS_A_MINE_IN_MINEGRID_VALUE){
          this.mineGrid[row][col] = minesInPerimeter(row, col);
        }
      }
    }
  }
  
  private String getGridValueStr(int row, int col)
  {
    // no mines in this MyJbutton's perimeter
    if ( this.mineGrid[row][col] == NO_MINES_IN_PERIMETER_MINEGRID_VALUE )
      return INITIAL_CELL_TEXT;
    
    // 1 to 8 mines in this MyJButton's perimeter
    else if ( this.mineGrid[row][col] > NO_MINES_IN_PERIMETER_MINEGRID_VALUE && 
              this.mineGrid[row][col] <= ALL_MINES_IN_PERIMETER_MINEGRID_VALUE )
      return "" + this.mineGrid[row][col];
    
    // this MyJButton in a mine
    else // this.mineGrid[row][col] = IS_A_MINE_IN_GRID_VALUE
      return MineSweapPart.EXPOSED_MINE_TEXT;
  }
  
}
