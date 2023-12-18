import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GUI extends JFrame {
    Minefield m;
    MineFieldPanel mfp;
    JLabel connections = new JLabel("Connections: 0");
    JButton start1 = new JButton("Start");
    JButton start2 = new JButton("Start");
    JPanel mainMenu = new JPanel();
    JLabel width = new JLabel("Width");
    JTextArea widthEntry = new JTextArea();
    JLabel height = new JLabel("Height");
    JTextArea heightEntry = new JTextArea();
    JLabel mines = new JLabel("Mines");
    JTextArea minesEntry = new JTextArea();
    JPanel connectionsPanel = new JPanel();
    public GUI(Minefield m){
        this.m = m;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Automatic Minesweeper");
        //start1.setEnabled(false);

        mainMenu.setLayout(new GridBagLayout());
        mainMenu.setPreferredSize(new Dimension(600,400));
        connectionsPanel.setPreferredSize(new Dimension(600,400));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weighty = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainMenu.add(width, gbc);
        gbc.gridx++;
        mainMenu.add(height, gbc);
        gbc.gridx++;
        mainMenu.add(mines, gbc);
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0,20,0,20);
        mainMenu.add(widthEntry, gbc);
        gbc.gridx++;
        mainMenu.add(heightEntry, gbc);
        gbc.gridx++;
        mainMenu.add(minesEntry, gbc);

        gbc.insets = new Insets(0,0,0,0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 1;
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 3;
        mainMenu.add(start1, gbc);
        add(mainMenu);

        connectionsPanel.setLayout(new GridBagLayout());
        gbc.gridy = 1;
        gbc.weighty = 0;
        connectionsPanel.add(connections,gbc);
        gbc.gridy++;
        gbc.weighty = 1;
        connectionsPanel.add(start2,gbc);

        pack();

        DocumentListener inputsVerifier = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {}
            @Override public void removeUpdate(DocumentEvent e) {}
            @Override
            public void changedUpdate(DocumentEvent e) {
                //start1.setEnabled(isOKToStart());
            }
        };

        widthEntry.getDocument().addDocumentListener(inputsVerifier);
        heightEntry.getDocument().addDocumentListener(inputsVerifier);
        minesEntry.getDocument().addDocumentListener(inputsVerifier);
        start1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(mainMenu);
                finalizeMinefield();
                add(connectionsPanel);
                pack();
            }
        });

        start2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(connectionsPanel);
                add(mfp);
                pack();
                Main.doneAcceptingClients();
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3) {
                    Main.step();
                }
            }
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
        });
    }

    public void updateConnections(int c){
        connections.setText("Connections: " + c);
    }

    public boolean isOKToStart(){
        try {
            int w = Integer.parseInt(widthEntry.getText());
            int h = Integer.parseInt(heightEntry.getText());
            int b = Integer.parseInt(minesEntry.getText());

            return (w >= 10 && h >= 10 && b > 0 && b < w*h-9);
        }catch(Exception e){
            return false;
        }
    }

    public boolean isOKToStart2(){
        int c = Integer.parseInt(connections.getText().split(" ")[1]);
        return c > 0;
    }

    public void finalizeMinefield(){
        Main.createMinefield(Integer.parseInt(widthEntry.getText()),
                Integer.parseInt(heightEntry.getText()),
                Integer.parseInt(minesEntry.getText()));
        mfp = new MineFieldPanel(m);
        m.start();
        mfp.repaint();
    }

    public void updateMinefield(Minefield m){
        this.m = m;
    }

}
