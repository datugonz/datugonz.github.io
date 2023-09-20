package Game3072Project;

import javax.swing.JButton;
import javax.xml.transform.OutputKeys;

/**
 * A class that keeps track of the movement of the numbers on the board
 */
public class Board {
//---------------------------------------------
//                 PROPERTIES    
//---------------------------------------------
    //the array of integers, the multiples of 3 for the gameboard
    private int[][] numbers;
    public int[][] getNumberArray(){ return numbers; }
    public void setNumberArray(int[][] numberArray){ numbers = numberArray; }
    
    //the array of JButtons for visual display of numbers array
    private JButton[][] buttons;
    public JButton[][] getButtonArray(){ return buttons; }
    public void setButtonArray(JButton[][] buttonArray){ buttons = buttonArray; }
    
    private int score;
    public int getScore(){ return score; }
    public void setScore(int newScore){ score = newScore; }
    
    private GamePages gamePage;
    public GamePages getGamePage(){ return gamePage; }
    public void setGamePage(GamePages game){ gamePage = game; }
    
//Static Variables
    //max and min for rows and columns on arrays
    public static final int MIN_SIZE = 0;
    public static final int MAX_SIZE = 4;
    
    //-1, 0, or 1, to be added to the current index to find what spot it should move to
    public static final int[] UP_MOVES = {-1,0};
    public static final int[] RIGHT_MOVES = {0,1};
    public static final int[] DOWN_MOVES = {1,0};
    public static final int[] LEFT_MOVES = {0,-1};
    
    //for keeping the direction codes cohesive and streamlined
    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    
//---------------------------------------------
//                CONSTRUCTORS    
//---------------------------------------------
    //default constructor
    public Board(){
        setNumberArray(new int[MAX_SIZE][MAX_SIZE]);
        setButtonArray(new JButton[MAX_SIZE][MAX_SIZE]);
    }
    
    //passes in the array of JButtons from main, makes a new int array
    public Board(JButton[][] buttonArray){
        setNumberArray(new int[MAX_SIZE][MAX_SIZE]);
        setButtonArray(buttonArray);
    }
    
    //passes in the array of ints from main, makes an empyu array of JButtons
    public Board(int[][] numberArray){
        setNumberArray(numberArray);
        setButtonArray(new JButton[MAX_SIZE][MAX_SIZE]);      
    }
    
    //full constructor
    public Board(int[][] numberArray, JButton[][] buttonArray){
        setNumberArray(numberArray);
        setButtonArray(buttonArray);     
    }

//---------------------------------------------
//                  METHODS    
//---------------------------------------------   
    //moves the entire board, based on direction
    public void move(int direction){
        //for each of numbers: 
            //I found that the for loop must work from the direction the sqaures are traveling 
            //- e.g. DOWN uses bottom to top rather than top to bottom - otherwise they won't
            //all move as far as they should
        switch(direction){
            case UP: //up goes top to bottom, left to right
                for(int ii = 0; ii<MAX_SIZE; ii++){ 
                    for(int jj = 0; jj<MAX_SIZE; jj++){
                        if(ii!=MIN_SIZE){
                            moveInt(direction, ii, jj);
                        }
                    }
                }
                break;
                
            case RIGHT: //right goes top to bottom, right to left
                for(int ii = 0; ii<MAX_SIZE; ii++){
                    for(int jj = MAX_SIZE-1; jj>=MIN_SIZE; jj--){
                        moveInt(direction, ii, jj);
                    }
                }
                break;
                
            case DOWN: //down goes bottom to top, left to right
                for(int ii = MAX_SIZE-1; ii>=MIN_SIZE; ii--){
                    for(int jj = 0; jj<MAX_SIZE; jj++){
                        moveInt(direction, ii, jj);
                    }
                }
                break;
                
            case LEFT: //left goes top to bottom, left to right
                for(int ii = 0; ii<MAX_SIZE; ii++){
                    for(int jj = 0; jj<MAX_SIZE; jj++){
                        moveInt(direction, ii, jj);
                    }
                }
                break;
                
            default:
                break;
        }
        generateOneTile();
    }    
               
    /**
     * Method to move an individual number. This method recursively calls itself until
     * the current number (not index) hits an edge or another number square.
     * @param direction up, right, down, or left, represented by static variables
     * @param ii current row index
     * @param jj current col index
     */
    public void moveInt(int direction, int ii, int jj){
        int[] moves = getMoves(direction);
        
        int row = ii + moves[0];
        int col = jj + moves[1];
        
        if(row >= MIN_SIZE && row < MAX_SIZE && col >= MIN_SIZE && col < MAX_SIZE){
            
            int adj = findAdjacent(direction, ii, jj);

            if(adj == numbers[ii][jj] && adj != 0){ //if they're equal
                combine(direction,adj,ii,jj);
            }
            else if(adj == 0){
                adj = numbers[ii][jj];
                numbers[ii][jj] = 0;
                numbers[ii + moves[0]][jj + moves[1]] = adj;
                moveInt(direction, ii + moves[0], jj + moves[1]);
            }
            updateColor(ii,jj);
            updateColor(row,col);
        }    
    }
            
    
    public int findAdjacent(int direction, int index1, int index2){
        int adjacent = -1; //switch to -1 to show it's out of bounds?
        
        //figure out which of the moves to use
        int[] moves = getMoves(direction);
        
        //find the row and column indexes for new location
        int row = index1 + moves[0];
        int col = index2 + moves[1];
        
        //get the integer value from that location and return it
        if(row >= MIN_SIZE && row < MAX_SIZE && col >= MIN_SIZE && col < MAX_SIZE){
            adjacent = numbers[row][col]; 
        }
        return adjacent;
    }
    
    public void combine(int direction, int value, int index1,int index2) 
    {
        int sum = 2*value;
        
        //figure out which of the moves to use
        int[] moves = getMoves(direction);
        
        //find the row and column indexes for new location
        int row = index1 + moves[0];
        int col = index2 + moves[1];
        
        numbers[row][col] = sum;
        numbers[index1][index2] = 0; //set previous square to empty
        
        score = score + sum; //add to score
        gamePage.updateScore(score);

   } 
    
    /**
     * Finds which of the _MOVES variables to use in movement calculations. This simplifies other methods.
     */
    public int[] getMoves(int direction){
        int[] moves = new int[2];
        //figure out which of the moves to use
        switch (direction) {
            case UP:
                moves = UP_MOVES;
                break;
            case RIGHT:
                moves = RIGHT_MOVES;
                break;
            case DOWN:
                moves = DOWN_MOVES;
                break;
            case LEFT:
                moves = LEFT_MOVES;
                break;
            default:
                break;
        }
        return moves;
    }
    
    public void generateTwoTiles(){
        
                //two open spots
        for(int i1 = 0; i1<2; i1++){
            //keep looking for open spots
            while(countOpenSpots()>0){
                
                //select random spots
                int ii = (int)(Math.random()*4);
                int jj = (int)(Math.random()*4);
                
                
                //variable three or six
                double threeOrSix = Math.random();
                if(threeOrSix>0.5){
                    threeOrSix = 3;
                }
                else{
                    threeOrSix = 6;
                }
                    //Enter three or six into an open spot
                    if(numbers[ii][jj]==0){
                        numbers[ii][jj]=(int)threeOrSix;
                        updateColor(ii,jj);
                        
                        //This breaks the while loop.
                        break;
                    }
            }
        }
    }
    public void generateOneTile(){
       
        
        //one open spot
        for(int i1 = 0; i1<1; i1++){
            //keep looking for open spots
            if(countOpenSpots()>0){
                
                while(true){
                //select random spots
                int ii = (int)(Math.random()*4);
                int jj = (int)(Math.random()*4);
                
                
                //variable three or six
                double threeOrSix = Math.random();
                if(threeOrSix>0.5){
                    threeOrSix = 3;
                }
                else{
                    threeOrSix = 6;
                }
                    //Enter three or six into an open spot
                    if(numbers[ii][jj]==0){
                        numbers[ii][jj]=(int)threeOrSix;
                        updateColor(ii,jj);
                        break;
                    }
                }
            }
            else{
                gamePage.gameOver();
            }
        }
    }
    /*
        This counts the open spots on the board.
        It is used in both the generateTwoTiles and generateOneTile.
    */
    public int countOpenSpots(){
        int count = 0; 
        
        //loops through the all array of arrays (numbers).
        for(int ii = 0; ii<MAX_SIZE; ii++){
            for(int jj = 0; jj<MAX_SIZE; jj++){
                if(numbers[ii][jj]==0){
                    count++;
                }
            }
        }
        return count; 
    }
    
    public void updateColor(int ii, int jj){
        int value = numbers[ii][jj];
        JButton button = buttons[ii][jj];
        
        button.setText("" + value);
        
        switch(value){
            case 3:
                button.setBackground(GamePages.Color3);
                break;
            case 6:
                button.setBackground(GamePages.Color6);
                break;
            case 12:
                button.setBackground(GamePages.Color12);
                break;
            case 24:
                button.setBackground(GamePages.Color24);
                break;
            case 48:
                button.setBackground(GamePages.Color48);
                break;
            case 96:
                button.setBackground(GamePages.Color96);
                break;
            case 192:
                button.setBackground(GamePages.Color192);
                break;
            case 384:
                button.setBackground(GamePages.Color384);
                break;
            case 768:
                button.setBackground(GamePages.Color768);
                break;
            case 1536:
                button.setBackground(GamePages.Color1536);
                break;
            case 3072:
                button.setBackground(GamePages.Color3072);
                
                //You Win!
                gamePage.win();
                break;
            default: //0 or otherwise
                button.setBackground(GamePages.normalBackground);
                button.setText("");
                break;
            
        }
        
    }
}
