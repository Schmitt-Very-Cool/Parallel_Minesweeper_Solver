import java.io.Serializable;

public class Cell implements Serializable {
    private boolean hasBomb = false;
    public boolean isRevealed = false;
    public boolean isFlagged = false;
    private int num;
    public int publicNum;
    public double chanceOfBomb = 1;
    public int x;
    public int y;
    public Cell(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void setHasBomb(boolean b){
        hasBomb = b;
    }

    public void setNum(int n){
        num = n;
    }
    public int reveal() throws RuntimeException {
        if(hasBomb){
            throw new RuntimeException("Boom!");
        }
        chanceOfBomb = 0;
        publicNum = num;
        isRevealed = true;
        return publicNum;
    }

    public boolean hasBomb(){
        return hasBomb;
    }
}
