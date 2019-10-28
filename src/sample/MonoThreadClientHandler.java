package sample;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * MonoThreadClientHandler class modified Runnable server to run Main
 * and for execution query the database
 */
public class MonoThreadClientHandler implements Runnable {

    /**
     * clientDialog for Socket
     */
    private static Socket clientDialog;
    /**
     * object database
     */
    private DB_project db_project = new DB_project();

    /**
     * This method used used for dialogue with a connected client
     * @param client Socket
     */
    public MonoThreadClientHandler(Socket client) {
        MonoThreadClientHandler.clientDialog = client;
    }

    @Override
    /**
     *Рere we read the data depending on the command using the parser and send it to the database
     */
    public void run() {
        try {
            DataOutputStream out = new DataOutputStream(clientDialog.getOutputStream());
            DataInputStream in = new DataInputStream(clientDialog.getInputStream());
            System.out.println("DataInputStream created");
            System.out.println("DataOutputStream  created");
            while (!clientDialog.isClosed()) {
                System.out.println("Server reading from channel");
                String entry = in.readUTF();
                System.out.println("READ from clientDialog message - " + entry);
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(entry);
                JSONObject jsonObject = (JSONObject) obj;
                String command = (String) jsonObject.get("comand");
                if (command.equals("enterUser")){
                    out.writeUTF(db_project.loginUser(entry));
                }
                if (command.equals("selectUser")){
                    out.writeUTF(db_project.selectColomUser(entry));
                }
                if (command.equals("registrUser")){
                    db_project.addColomUser(entry);
                }
                if (command.equals("lookUser")){
                    out.writeUTF(db_project.getColomUser(entry));
                }
                if (command.equals("deleteUser")){
                    db_project.deleteColomUser(entry);
                }
                if (command.equals("lookCost")){
                    out.writeUTF(db_project.selectIDCosts(entry));
                }
                if (command.equals("addBuy")){
                    db_project.addColomBuy(entry);
                }
                if (command.equals("lookBuy")){
                    out.writeUTF(db_project.getColomBuy(entry));
                }
                if (command.equals("deleteBuy")){
                    db_project.deleteColomBuy(entry);
                }
                if (command.equals("updateBuy")){
                    db_project.updateColumBuy(entry);
                }
                if (command.equals("lookReport")){
                    out.writeUTF(db_project.getColomBuyDate(entry));
                }
                if (command.equals("lookChart")){
                    out.writeUTF(db_project.getChart(entry));
                }
                if (command.equalsIgnoreCase("quit")) {
                    System.out.println("Client initialize connections suicide ...");
                    out.writeUTF("Server reply - " + entry + " - OK");
                    Thread.sleep(1);
                    break;
                }
                System.out.println("Server try writing to channel");
                out.writeUTF("Server reply - " + entry + " - OK");
                System.out.println("Server Wrote message to clientDialog.");
                out.flush();
            }
            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels.");
            in.close();
            out.close();
            clientDialog.close();
            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

