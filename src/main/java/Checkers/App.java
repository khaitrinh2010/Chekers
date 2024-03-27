package Checkers;
import java.util.*;
import java.lang.Math;
//import org.reflections.Reflections;
//import org.reflections.scanners.Scanners;
import org.checkerframework.checker.units.qual.C;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.core.PFont;
import processing.event.MouseEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.awt.Font;
import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 48;
    //size of a cell
    public static int[][] checkerBoard = new int[8][8]; //when working with reference type, remember to use new keyword
    public static final int SIDEBAR = 0;
    public static final int BOARD_WIDTH = 8;
    public static final int[] BLACK_RGB = {181, 136, 99};
    public static final int[] WHITE_RGB = {240, 217, 181};
    public static final float[][][] coloursRGB = new float[][][] {
        //default - white & black
        {
                {WHITE_RGB[0], WHITE_RGB[1], WHITE_RGB[2]},
                {BLACK_RGB[0], BLACK_RGB[1], BLACK_RGB[2]}
        },
        //green
        {
                {105, 138, 76}, //when on white cell
                {105, 138, 76} //when on black cell
        },
        //blue
        {
                {196,224,232},
                {170,210,221}
        }
	};

    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE;

    public static final int FPS = 60;
    public int selectedRow = -1;
    public int selectedCol = -1;
    public int playerTurn = 1;  //1 for white, 2 for black
    public ArrayList<int[]> allPossibleMoves = new ArrayList<>();

    public HashMap<int[], Boolean> blueColor = new HashMap<>();
    public App() {
        
    }

    /**
     * Initialise the setting of the window size.
    */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

	@Override
    public void setup() {
        frameRate(FPS);

		//Set up the data structures used for storing data in the game
        for(int row = 0; row < 3; row ++){
            for(int col = 0; col < BOARD_WIDTH; col ++){
                if ((row + col)%2 == 1){
                    checkerBoard[row][col] = 1; //an white piece
                }
            }
        }
        for(int row = 5; row< BOARD_WIDTH; row ++){
            for(int col = 0; col < BOARD_WIDTH; col ++){
                if((row+col)%2 == 1){
                    checkerBoard[row][col] = 2; //indicates an black piece
                }
            }
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
    */

	@Override
    public void keyPressed(){

    }
    
    /**
     * Receive key released signal from the keyboard.
    */
	@Override
    public void keyReleased(){

    }

    @Override
    public void mousePressed(MouseEvent e) { //this is an event handler that gets called automatically
        //when the mouse is clicked
        //Check if the user clicked on a piece which is theirs - make sure only whoever's current turn it is, can click on pieces
		
		//TODO: Check if user clicked on an available move - move the selected piece there. 
		//TODO: Remove captured pieces from the board
		//TODO: Check if piece should be promoted and promote it
		//TODO: Then it's the other player's turn.
        //moouseX and mouseY are built in variable in processing library to keep track of the current coordinate
        //of the mouse that has been clicked relative to the window
        //0,0 is the top left, when you move to right x increases, when you move down, y increases
        int col = mouseX/CELLSIZE;
        int row = mouseY/CELLSIZE;

        if(checkerBoard[row][col] == playerTurn || checkerBoard[row][col] == playerTurn*10){ //choose a valid piece to move
            selectedRow = row; //these variables store the position of the piece that we are going to move
            selectedCol = col;
            allPossibleMoves = possibleMoves(selectedRow, selectedCol); //generate all the possible moves
            //for the current selected cell
        }
        if(checkerBoard[row][col] == 0 && selectedRow != -1 && selectedCol != -1){
            for(int[] moves: allPossibleMoves){
                if(row == moves[0] && col == moves[1]){ //found a new valid place to jump to
                    if(playerTurn == 1) {
//                        System.out.println(isValidMove(selectedRow, selectedCol, row, col));
//                        System.out.println(Math.abs(row - selectedRow) == 2);
//                        System.out.printf("%d, %d\n", selectedRow, selectedCol);
//                        System.out.printf("%d, %d\n", row, col);
                        if (Math.abs(row - selectedRow) == 2) {
                            int midRow = (row + selectedRow) / 2;
                            int midCol = (col + selectedCol) / 2;
                            if (checkerBoard[midRow][midCol] == 2 || checkerBoard[midRow][midCol] == 20) {
                                //System.out.println("Can capture");
                                checkerBoard[midRow][midCol] = 0; //captures
                            }
                        }
                        if(row == 7){
                            checkerBoard[row][col] = 10;
                        }
                        else {
                            if(checkerBoard[selectedRow][selectedCol] == 10){
                                checkerBoard[row][col] = 10;
                            }
                            else {
                                checkerBoard[row][col] = 1; //execute a move
                            }
                        }
                        playerTurn = 2;
                    }
                    else if(playerTurn == 2){
                        if (Math.abs(row - selectedRow) == 2) {
                            int midRow = (row + selectedRow) / 2;
                            int midCol = (col + selectedCol) / 2;
                            if (checkerBoard[midRow][midCol] == 1 || checkerBoard[midRow][midCol] == 10) {
                                checkerBoard[midRow][midCol] = 0;
                            }
                        }
                        if(row == 0){
                            checkerBoard[row][col] = 20;
                        }
                        else {
                            if(checkerBoard[selectedRow][selectedCol] == 20){
                                checkerBoard[row][col] = 20;
                            }
                            else {
                                checkerBoard[row][col] = 2; //execute a move
                            }
                        }
                        playerTurn = 1;
                    }
                    checkerBoard[selectedRow][selectedCol] = 0;
                    allPossibleMoves.clear(); //after finish a move
                    selectedCol = -1;
                    selectedRow = -1;
                    return; //return to avoid any further modification
                }
            }
        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    /**
     * Draw all elements in the game by current frame. 
    */
    public void drawWhiteKing(float x, float y){
        fill(255); //white
        stroke(0);
        strokeWeight(4);
        ellipse(x + CELLSIZE/2, y + CELLSIZE/2, CELLSIZE*0.8f, CELLSIZE*0.8f);
        noStroke();
        fill(255);
        stroke(0);
        strokeWeight(5);
        ellipse(x + CELLSIZE/2, y + CELLSIZE/2, CELLSIZE*0.45f, CELLSIZE*0.45f);
        noStroke();
        fill(255);
        ellipse(x + CELLSIZE/2, y + CELLSIZE/2, CELLSIZE*0.3f, CELLSIZE*0.3f);

    }
    public void drawBlackKing(float x, float y){
        fill(0); //white
        stroke(255);
        strokeWeight(4);
        ellipse(x + CELLSIZE/2, y + CELLSIZE/2, CELLSIZE*0.8f, CELLSIZE*0.8f);
        noStroke();
        fill(0);
        stroke(255);
        strokeWeight(5);
        ellipse(x + CELLSIZE/2, y + CELLSIZE/2, CELLSIZE*0.45f, CELLSIZE*0.45f);
        noStroke();
        fill(0);
        ellipse(x + CELLSIZE/2, y + CELLSIZE/2, CELLSIZE*0.3f, CELLSIZE*0.3f);

    }
	@Override
    public void draw() {
        this.noStroke();
        background(180);
		//draw the board
        //in processing, stroke, noStroke, and strokeWweight are used to control the borders of a shape
        //stroke takes the value of the color, strokeWeight is the thickness and noStroke disable thhe drawing of the stroke
        //for the afterward shapes

		for(int row = 0; row < BOARD_WIDTH; row ++){
            for(int col = 0; col < BOARD_WIDTH; col ++){
                int x = col*CELLSIZE;
                int y = row*CELLSIZE;

                if( (row + col) % 2 == 0){ //white cell
                    this.setFill(0, 0);
                    rect(x, y, CELLSIZE, CELLSIZE);
                }
                else if((row+col) % 2 == 1){ //black cell
                    setFill(0, 1);
                    rect(x, y, CELLSIZE, CELLSIZE);
                    if(checkerBoard[row][col] == 1){ //white piece
                        fill(255); //white
                        stroke(0);
                        strokeWeight(4);
                        ellipse(x + CELLSIZE/2, y + CELLSIZE/2, CELLSIZE*0.8f, CELLSIZE*0.8f);
                        noStroke();

                    } //white piece
                    else if(checkerBoard[row][col] == 2){ //black piece
                        fill(0); //black piece
                        stroke(255);
                        strokeWeight(4);
                        ellipse(x + CELLSIZE/2, y + CELLSIZE/2, CELLSIZE*0.8f, CELLSIZE*0.8f);
                        noStroke();
                    }
                    else if(checkerBoard[row][col] == 10){ //black piece
                        drawWhiteKing(x, y);
                    }
                    else if(checkerBoard[row][col] == 20){
                        drawBlackKing(x, y);
                    }
                }
            }

        }

        //draw the cell being selected
        if(selectedRow != -1 && selectedCol != -1 && (checkerBoard[selectedRow][selectedCol] == playerTurn || checkerBoard[selectedRow][selectedCol]==playerTurn*10)){ //display the selected piece in green color
            if(playerTurn == 1){
                this.setFill(1, 0); //green color
                rect(selectedCol * CELLSIZE, selectedRow * CELLSIZE, CELLSIZE, CELLSIZE);
                if(checkerBoard[selectedRow][selectedCol] == 1) {
                    fill(255);
                    stroke(0);
                    strokeWeight(4);
                    ellipse(selectedCol * CELLSIZE + CELLSIZE / 2, selectedRow * CELLSIZE + CELLSIZE / 2, CELLSIZE * 0.8f, CELLSIZE * 0.8f);
                    noStroke();
                }
                else if(checkerBoard[selectedRow][selectedCol] == 10){
                    drawWhiteKing(selectedCol*48, selectedRow*48);
                }
            }
            else if(playerTurn == 2) {
                this.setFill(1, 1);
                rect(selectedCol * CELLSIZE, selectedRow * CELLSIZE, CELLSIZE, CELLSIZE);
                if (checkerBoard[selectedRow][selectedCol] == 2) {
                    fill(0); //black piece
                    stroke(255);
                    strokeWeight(4);
                    ellipse(selectedCol * CELLSIZE + CELLSIZE / 2, selectedRow * CELLSIZE + CELLSIZE / 2, CELLSIZE * 0.8f, CELLSIZE * 0.8f);
                    noStroke();
                }
                else if(checkerBoard[selectedRow][selectedRow] == 20){
                    drawBlackKing(selectedCol*48, selectedRow*48);
                }
            }
        }

        ArrayList<int[]> copyList = new ArrayList<>(allPossibleMoves);
        if(selectedRow != -1 && selectedCol != -1 ) {
            if(checkerBoard[selectedRow][selectedCol] == playerTurn || checkerBoard[selectedRow][selectedCol] == playerTurn*10) {
                for (int i = 0; i < copyList.size(); i++) {
                    int[] moves = copyList.get(i);
                    int r = moves[0];
                    int c = moves[1];
                    this.setFill(2, 0);
                    rect(c * CELLSIZE, r * CELLSIZE, CELLSIZE, CELLSIZE);
                }
            }
        }
        System.out.println(isGameOver());
        if(isGameOver()){
            textSize(32);
            text("Game Over!!",20, 20);
            System.exit(0);
        }

		//draw highlighted cells

		//check if the any player has no more pieces. The winner is the player who still has pieces remaining

    }
	
	/**
     * Set fill colour for cell background
     * @param colourCode The colour to set
     * @param blackOrWhite Depending on if 0 (white) or 1 (black) then the cell may have different shades
     */


    public boolean isGameOver() {
        int countWhite = 0;
        int countBlack = 0;
        boolean whiteLeft = false;
        boolean blackLeft = false;
        for (int row = 0; row < BOARD_WIDTH; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (checkerBoard[row][col] == 1 || checkerBoard[row][col] == 10) {
                    countWhite += 1;
                    if (!possibleMoves(row, col).isEmpty()) {
                        whiteLeft = true;
                    }
                } else if (checkerBoard[row][col] == 2 || checkerBoard[row][col] == 20) {
                    countBlack += 1;
                    if (!possibleMoves(row, col).isEmpty()) {
                        blackLeft = true;
                    }
                }
            }
        }
        if (countWhite == 0) {
            return true;
        }
        else if(countBlack == 0){
            return true;
        }
        return false;
    }
	public void setFill(int colourCode, int blackOrWhite) {
		this.fill(coloursRGB[colourCode][blackOrWhite][0], coloursRGB[colourCode][blackOrWhite][1], coloursRGB[colourCode][blackOrWhite][2]);
	}
    public boolean valid(int row, int col){
        return (row < 8 && row >= 0 && col < 8 && col >= 0);
    }
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Implement this method to check if a move is legal according to the rules of
        // Checkers.
        if(toRow < 0|| toCol < 0 || toRow >= 8 || toCol >= 8){
            return false;
        }
        if(fromRow < 0|| fromCol < 0 || fromRow >= 8 || fromCol >= 8){
            return false;
        }
        boolean isKing = false;
        int piece = checkerBoard[fromRow][fromCol];

        //check whether the piece is the King or not
        if(piece == 10 || piece == 20){
            isKing = true;
        }

        //if not King, cannot moves backward
        if(!isKing){
            if(piece == 1 && toRow < fromRow){
                return false;
            }
            if(piece == 2 && toRow > fromRow){
                return false;
            }
        }

        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        //cannot move a empty cell
        if(piece == 0){
            return false;
        }

        //cannot jump to a non-empty cell
        if(checkerBoard[toRow][toCol] != 0){
            return false;
        }
        //only move diagonally
        return(rowDiff == colDiff && rowDiff <= 2) ;
    }
    public ArrayList<int[]> possibleMoves(int fromRow, int fromCol){
        boolean isKing = false;
        if(checkerBoard[fromRow][fromCol] == 10 || checkerBoard[fromRow][fromCol] == 20){
            isKing = true;
        }
        int[][] kingDirection = {{1,1}, {1, -1}, {-1,1}, {-1,-1}, {2,2}, {2,-2}, {-2,2},{-2,-2}};
        int[][] whiteDirection = {{1,1}, {1,-1}, {2,2}, {2,-2}};
        int [][] blackDirection = {{-1,1},{-1,-1}, {-2,2}, {-2,-2}};
        ArrayList<int[]> storePossibleMoves = new ArrayList<>();
        if(!isKing){
            if(playerTurn == 1){
                for(int i = 0;i <whiteDirection.length; i ++){
                    int dx = whiteDirection[i][0];
                    int dy = whiteDirection[i][1];
                    if(isValidMove(fromRow, fromCol,fromRow + dx, fromCol + dy)){
                        if(dx == 2) {
                            int midRow = fromRow + 1;
                            int midCol = fromCol + dy/2;
                            if(checkerBoard[midRow][midCol] != 0) {
                                int[] storing = {fromRow + dx, fromCol + dy};
                                storePossibleMoves.add(storing);
                            }
                        }
                        else{
                            int[] storing = {fromRow + dx, fromCol + dy};
                            storePossibleMoves.add(storing);
                        }
                    }

                }
            }
            else if(playerTurn == 2){
                for(int i = 0;i <blackDirection.length; i ++){
                    int dx = blackDirection[i][0];
                    int dy = blackDirection[i][1];
                    if(isValidMove(fromRow, fromCol,fromRow + dx, fromCol + dy)){
                        if(dx == -2) {
                            int midRow = fromRow - 1;
                            int midCol = fromCol + dy/2;
                            if(checkerBoard[midRow][midCol] != 0) {
                                int[] storing = {fromRow + dx, fromCol + dy};
                                storePossibleMoves.add(storing);
                            }
                        }
                        else{
                            int[] storing = {fromRow + dx, fromCol + dy};
                            storePossibleMoves.add(storing);
                        }
                    }
                }
            }
        }
        else{
            for(int i = 0;i <kingDirection.length; i ++){
                int dx = kingDirection[i][0];
                int dy = kingDirection[i][1];
                if(valid(fromRow + dx, fromCol + dy) && checkerBoard[fromRow+dx][fromCol+dy] == 0){
                    if(dx == -2 || dx == 2) {
                        int midRow = fromRow + dx/2;
                        int midCol = fromCol + dy/2;
                        if(checkerBoard[midRow][midCol] != 0) { //only can jump if there is a piece in the middle
                            int[] storing = {fromRow + dx, fromCol + dy};
                            storePossibleMoves.add(storing);
                        }
                    }
                    else{
                        int[] storing = {fromRow + dx, fromCol + dy};
                        storePossibleMoves.add(storing);
                    }
                }
            }
        }
        return storePossibleMoves;
    }

    public static void main(String[] args) {
        PApplet.main("Checkers.App");
    }
}
