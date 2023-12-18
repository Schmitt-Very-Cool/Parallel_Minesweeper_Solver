import java.awt.*;
import java.io.IOException;

public class Main {
    public static GUI gui;
    public static Server s;

    public static Minefield m;

    public static void main(String[] args) throws IOException {
        gui = new GUI(m);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui.setVisible(true);
            }
        });
        s = new Server(26920, m);
        new Thread(s).start();
    }

    public static void createMinefield(int w, int h, int b){
        m = new Minefield(w, h, b);
        gui.updateMinefield(m);
        s.minefield = m;
    }

    public static void doneAcceptingClients(){
        s.acceptingClients = false;
    }

    public static void step(){

    }
}
