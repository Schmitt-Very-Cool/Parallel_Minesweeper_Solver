import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Main {
    private static String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 26920;

    public static void main(String[] args) {
        if(args.length > 0){
            SERVER_ADDRESS = args[0];
        }
        System.out.println("connecting to " + SERVER_ADDRESS);
        try (Socket socket = new Socket(InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());){
            System.out.println("Connected!");
            while(true) {
                // Receive tasks from the server
                try {
                    Object input;
                    System.out.println("Trying to read from server");
                    input = ois.readObject();
                    MinefieldPackage pckg = (MinefieldPackage) input;
                    System.out.println("Received data from server!");
                    evaluate(pckg);
                    oos.writeUnshared(pckg.tasks);
                    oos.reset();
                }catch(EOFException e){
                    System.out.println("EOF hit");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Evaluates the probability that each assigned cell is a bomb.
     *
     * @param task the list of assigned cells and the minesweeper puzzle for reference
     * @return the task with all the evaluated probabilities.
     */
    private static MinefieldPackage evaluate(MinefieldPackage task) {
        // Perform Minesweeper evaluation logic here
        // Return the result
        System.out.println("Evaluating:\n" + task.mf);
        for(Cell c : task.tasks){
            c.chanceOfBomb = evaluateCell(c.x, c.y, task.mf);
        }
        return task;
    }

    /**
     * Evaluates a cell by looking at all the immediately surrounding cells, and determines the likelihood that this
     * cell is a mine.
     * <p>
     * Could be further improved by checking for larger patterns, such as the classic 1-2-1 implying a bomb-free space
     * by the 2.
     *
     * @param x x coordinate of the cell
     * @param y y coordinate of the cell
     * @param mf minesweeper puzzle for reference
     * @return likelihood that this cell is a mine.
     */
    private static double evaluateCell(int x, int y, Minefield mf) {
        double chanceOfBomb = 0;
        int numEvaluated = 0;
        boolean done = false;
        for(int i = -1; i <= 1; i++){
            if(done) {
                break;
            }
            for(int j = -1; j <= 1; j++){ //for every cell in a 3x3 around the cell in question
                if(done) {
                    break;
                }
                try{
                    if(!mf.field[x+i][y+j].isRevealed){
                        continue; //skip if no number
                    }
                    //the probability is equal to the highest result of countMinesByNumber, or zero if one of the
                    //countMinesByNumber returns zero instead.
                    double oneProbability = countMinesByNumber(x+i, y+j, mf);
                    numEvaluated++;
                    if(oneProbability == 0.0){
                        chanceOfBomb = 0;
                        done = true;
                    }else {
                        chanceOfBomb = Math.max(oneProbability, chanceOfBomb);
                    }
                }catch(ArrayIndexOutOfBoundsException e){ //skip if out of bounds
                    continue;
                }
            }
        }
        if(numEvaluated == 0){
            //this cell is not adjacent to any revealed cells. The probability it is a bomb is equal to the base
            //probability in the Minefield object. Check there for more details on how this is determined.
            chanceOfBomb = mf.baseProbability;
        }
        return chanceOfBomb;
    }

    /**
     * Counts the number of flagged cells and hidden cells around this cell to determine the chance that one of the
     * hidden cells is a mine. This method is only called on revealed cells with a number on them, next to at least
     * one unflagged, unrevealed cell.
     *
     * @param x x coordinate of the cell in question
     * @param y y coordinate of the cell in question
     * @param mf minesweeper puzzle for reference
     * @return chance of unflagged, unrevealed cell adjacent to this being a mine.
     */
    private static double countMinesByNumber(int x, int y, Minefield mf) {
        int num = mf.field[x][y].publicNum;
        int numMines = 0;
        int numHidden = 0;
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                try{
                    if(mf.field[x+i][y+j].isFlagged){
                        numMines++;
                    }else if(!mf.field[x+i][y+j].isRevealed){
                        numHidden++;
                    }
                }catch(ArrayIndexOutOfBoundsException e){
                    continue;
                }
            }
        }
        if(num - numMines == 0){
            return 0;
        }
        if(num - numMines == numHidden){
            return 1;
        }
        return ((double)(num - numMines))/numHidden;
    }
}
