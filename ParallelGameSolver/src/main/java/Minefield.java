import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Minefield implements Serializable{
    Cell[][] field;
    int w;
    int h;
    int b;
    double baseProbability = 1;
    public Minefield(int w, int h, int b){
        if(b > w*h-9){
            throw new RuntimeException("Too many mines!");
        }

        this.w = w;
        this.h = h;
        this.b = b;
        field = new Cell[w][h];
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                field[i][j] = new Cell(i,j);
            }
        }
    }

    public void start(){
        int x = (int)(Math.random()*w);
        int y = (int)(Math.random()*h);
        createField(x, y);
        reveal(x,y);
        updateBaseProbability();
    }

    public void reveal(int x, int y) {
        if(x < 0 || x >= field.length || y < 0 || y >= field[0].length){
            //OOB
            return;
        }
        if(field[x][y].isRevealed){
            //already revealed
            return;
        }
        field[x][y].reveal();
        if(field[x][y].publicNum == 0){
            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 1; j++){
                    reveal(x + i, y + j);
                }
            }
        }
    }

    public void createField(int startX, int startY){
        ArrayList<Point> bombLocations = new ArrayList<>();
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                bombLocations.add(new Point(startX + i, startY + j)); //OOB doesn't matter here
            }
        }
        for(int i = 0; i < b; i++){
            int x = (int)(Math.random()*w);
            int y = (int)(Math.random()*h);
            Point p = new Point(x,y);
            if(bombLocations.contains(p)){
                i--;
                continue;
            }
            bombLocations.add(p);
        }
        for(int i = 9; i < bombLocations.size(); i++){
            Point p = bombLocations.get(i);
            field[p.x][p.y].setHasBomb(true);
        }
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                setNum(i, j);
            }
        }
    }

    public void setNum(int x, int y){
        int n = 0;
        for(int i = Math.max(x-1, 0); i <= Math.min(x+1, w-1); i++){
            for(int j = Math.max(y-1, 0); j <= Math.min(y+1, h-1); j++){
                if(field[i][j].hasBomb()){
                    n++;
                }
            }
        }
        field[x][y].setNum(n);
    }

    public boolean isComplete(){
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                if(!field[i][j].isRevealed && !field[i][j].isFlagged){
                    return false;
                }
            }
        }
        return true;
    }

    public ArrayList<Cell> getUncertainCells(){
        ArrayList<Cell> cells = new ArrayList<>();
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                if(!field[i][j].isRevealed && !field[i][j].isFlagged) {
                    cells.add(field[i][j]);
                }
            }
        }
        return cells;
    }

    public int numRemainingBombs(){
        int n = b;
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                if(field[i][j].isFlagged){
                    --n;
                }
            }
        }
        return n;
    }

    /**
     * Updates the baseProbability field in the Minefield, which is used for determining the probability that any cell
     * which only borders unrevealed or flagged cells is a mine. This is done by calculating the number of remaining
     * bombs and dividing that by the number of unrevealed cells.
     * <p>
     * This probability could be further improved by removing the minimum number of bombs lying on unrevealed cells
     * which border revealed numbers, but such a calculation is computationally expensive for only a slight improvement
     * on the logic.
     */
    public void updateBaseProbability(){
        int numBombs = numRemainingBombs();
        double numEmpty = 0;
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                if(!field[i][j].isRevealed && !field[i][j].isFlagged){
                    numEmpty++;
                }
            }
        }
        baseProbability = numEmpty/numBombs;
    }
    public String toString(){
        String s = "";
        for(int i = 0; i < h; i++){
            for(int j = 0; j < w; j++){
                if(!field[j][i].isRevealed){
                    if(field[j][i].isFlagged){
                        s += "F";
                    }else{
                        s += "#";
                    }
                }else{
                    s += field[j][i].publicNum;
                }
            }
            s += "\n";
        }
        return s;
    }
}