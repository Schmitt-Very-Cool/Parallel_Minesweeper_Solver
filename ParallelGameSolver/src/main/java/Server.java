import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Server implements Runnable{
    public boolean acceptingClients = true;
    boolean solved = false;
    public Minefield minefield;
    int port;
    ArrayList<ObjectOutputStream> ooss;
    ArrayList<ObjectInputStream> oiss;
    ArrayList<Socket> clients;
    public Server(int port, Minefield mf){
        minefield = mf;
        this.port = port;
    }

    @Override
    public void run() {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(5000);
            System.out.println("Server started");
            clients = new ArrayList<>();
            while(acceptingClients){
                Socket clientSocket = null;
                boolean accepted = true;
                try {
                    clientSocket = serverSocket.accept();
                }catch (SocketTimeoutException e){
                    accepted = false;
                }
                if(accepted) {
                    System.out.println("Client connected: " + clientSocket);

                    clients.add(clientSocket);
                    Main.gui.updateConnections(clients.size());
                }
            }
            System.out.println("Beginning solution:");
            setUpObjectStreams();
            while(!solved) {
                sendTasksToClients();
                ArrayList<Cell> results = receiveResults();
                chooseNextMoves(results);
                Main.gui.mfp.repaint();
                Thread.sleep(2000);
                if(minefield.isComplete()){
                    solved = true;
                }
            }
            System.out.println("Solved!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpObjectStreams() {
        ooss = new ArrayList<>();
        oiss = new ArrayList<>();
        for(Socket s : clients){
            try {
                ooss.add(new ObjectOutputStream(s.getOutputStream()));
                oiss.add(new ObjectInputStream(s.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendTasksToClients(){
        //designate partitions of the field for each client
        //loop through clients and send them their lot.
        //await return of results
        //stitch results together for a full board
        ArrayList<Cell> cellsToPartition = minefield.getUncertainCells();
        if(cellsToPartition.size() < clients.size()){ //if there are fewer cells than clients, just send 1 cell each until I run out
            for(int i = 0; i < cellsToPartition.size(); i++){
                ArrayList<Cell> part = new ArrayList<>(cellsToPartition.subList(i,i+1));
                MinefieldPackage pckg = new MinefieldPackage();
                pckg.tasks = part;
                pckg.mf = minefield;
                try {
                    System.out.println("Sending: " + pckg);
                    ooss.get(i).writeUnshared(pckg);
                    ooss.get(i).reset();
                }catch (IOException e){
                    continue;
                }
            }
        }else{
            for(int i = 0; i < clients.size(); i++){
                int start = cellsToPartition.size() * i / clients.size();
                int end = cellsToPartition.size() * (i + 1) / clients.size();
                ArrayList<Cell> part = new ArrayList<>(cellsToPartition.subList(start, end));
                MinefieldPackage pckg = new MinefieldPackage();
                pckg.tasks = part;
                pckg.mf = minefield;
                try {
                    System.out.println("Sending: " + pckg);
                    ooss.get(i).writeUnshared(pckg);
                    ooss.get(i).reset();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    ArrayList<Cell> receiveResults() {
        ArrayList<Cell> results = new ArrayList<>();
        for (int i = 0; i < clients.size(); i++) {
            Socket client = clients.get(i);
            try {
                ArrayList<Cell> result = (ArrayList<Cell>) oiss.get(i).readObject();
                System.out.println("Received from client: " + result);
                results.addAll(result);
                // Break the loop after successfully reading from a client
                break;
            } catch (EOFException e) {
                System.out.println("EOFException");
                continue;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }
        }
        return results;
    }

    public void chooseNextMoves(ArrayList<Cell> results){
        //choose next move(s).
        //if unrevealed cell has 100% chance of being a bomb, flag.
        //if unrevealed cell has 100% change of not being a bomb, reveal.
        //if neither, choose least-likely bomb cell
        Cell safest = new Cell(-1,-1);
        safest.chanceOfBomb = 1;
        boolean chance = true;
        for(Cell c : results){
            minefield.field[c.x][c.y].chanceOfBomb = c.chanceOfBomb;
            if(c.chanceOfBomb == 0){
                try {
                    System.out.println("Revealing cell at (" + c.x + "," + c.y + ")");
                    minefield.reveal(c.x, c.y);
                    chance = false;
                }catch(RuntimeException e){
                    Main.gui.mfp.boom();
                    System.err.println(e);
                }
            }else if(c.chanceOfBomb == 1){
                System.out.println("Flagging cell at (" + c.x + "," + c.y + ")");
                minefield.field[c.x][c.y].isFlagged = true;
                chance = false;
            }
            if(safest.x == -1 || safest.chanceOfBomb > c.chanceOfBomb){
                safest = c;
            }
        }
        if(chance){
            System.out.println("Taking a chance (p = " + safest.chanceOfBomb + "): cell at (" + safest.x + "," + safest.y + ")");
            minefield.reveal(safest.x, safest.y);
        }
        minefield.updateBaseProbability();
    }
}
