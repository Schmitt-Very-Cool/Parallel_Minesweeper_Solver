import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MineFieldPanel extends JPanel {
    boolean boom = false;
    Minefield mf;
    Map<Integer, Color> colorMap = new HashMap<>();
    public MineFieldPanel(Minefield m){
        mf = m;
        setPreferredSize(new Dimension(25*(mf.w+4), 25*(mf.h+4)));
        setBackground(Color.YELLOW);
        colorMap.put(1, Color.BLUE);
        colorMap.put(2, Color.GREEN);
        colorMap.put(3, Color.RED);
        colorMap.put(4, new Color(0x000088));
        colorMap.put(5, new Color(0x5c3400));
        colorMap.put(6, Color.CYAN);
        colorMap.put(7, Color.BLACK);
        colorMap.put(8, Color.GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(int i = 0; i < mf.w; i++){
            for(int j = 0; j < mf.h; j++){
                if(!mf.field[i][j].isRevealed){
                    g.setColor(Color.WHITE);
                    g.fillRect(50 + i*25, 50 + j*25, 25, 25);
                    if(!(boom && mf.field[i][j].hasBomb()) && mf.field[i][j].isFlagged){
                        g.setColor(Color.BLACK);
                        g.fillRect(50 + i*25 + 5, 50 + j*25 +2, 3, 21);
                        g.setColor(Color.RED);
                        int[] flagXs = {50+i*25+8, 50+i*25+8, 50+i*25+20};
                        int[] flagYs = {50+j*25+2, 50+j*25+13, 50+j*25+8};
                        g.fillPolygon(new Polygon(flagXs, flagYs, 3));
                    }
                    if(boom && mf.field[i][j].hasBomb()){
                        g.setColor(Color.RED);
                        g.fillRect(50 + i*25, 50 + j*25, 25, 25);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setColor(Color.BLACK);
                        g2d.fillRect(50+i*25+2, 50+j*25+11,21,3);
                        g2d.fillRect(50+i*25+11,50+j*25+2,3,21);
                        int[] downCrossXs = {50+i*25+4,50+i*25+6,50+i*25+21,50+i*25+19};
                        int[] downCrossYs = {50+j*25+6,50+j*25+4,50+j*25+19,50+j*25+21};
                        g2d.fillPolygon(downCrossXs,downCrossYs,4);
                        int[] upCrossYs = {50+j*25+19,50+j*25+21,50+j*25+6,50+j*25+4};
                        g2d.fillPolygon(downCrossXs, upCrossYs, 4);
                        int[] octagonXs = {50+i*25+6,50+i*25+8,50+i*25+12,50+i*25+17,50+i*25+19,50+i*25+17,50+i*25+13,50+i*25+8};
                        int[] octagonYs = {50+j*25+12,50+j*25+8,50+j*25+6,50+j*25+8,50+j*25+13,50+j*25+17,50+j*25+19,50+j*25+17};
                        g2d.fillArc(i+13,j+13,7,7,0,360);
                    }
                }else{
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(50 + i*25, 50 + j*25, 25, 25);
                    int n = mf.field[i][j].publicNum;
                    if(n > 0){
                        g.setColor(colorMap.get(n));
                        g.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
                        int width = g.getFontMetrics().stringWidth(String.valueOf(n));
                        g.drawString(String.valueOf(n), 63+i*25-width/2,70+j*25);
                    }
                }
            }
        }

        g.setColor(Color.BLACK);
        for(int i = 0; i <= mf.w; i++){
            g.drawLine(50 + i*25, 50, 50 + i*25, getHeight()-50);
        }
        for(int i = 0; i <= mf.h; i++){
            g.drawLine(50, 50+i*25, getWidth()-50, 50+i*25);
        }

        g.setFont(new Font(Font.DIALOG,Font.BOLD,20));
        g.drawString("Bombs Remaining: " + mf.numRemainingBombs(), 10,getHeight()-10);
    }


    public void boom(){
        boom = true;
        repaint();
    }
}
