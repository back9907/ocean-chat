
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author back
 */
public class Sever {

    private BSTree tree;

    private SimpleDateFormat sdf;

    private int port;

    private boolean keepGoing;

    public Sever(int port) {
        this.port = port;
        sdf = new SimpleDateFormat("HH:mm:ss");
        tree = new BSTree();
    }

    public void start() {
        keepGoing = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (keepGoing) {
                System.out.println("Sever waiting for clients on port " + port + " .");
                Socket socket = serverSocket.accept();

                if (!keepGoing) {
                    break;
                }

                ClientThread t = new ClientThread(socket);
                Node<ClientThread> clientNode= new Node<>(t.userId,t);
                tree.insert(clientNode);
                t.start();
            }

//            try {
//                serverSocket.close();
//                for (int i = 0; i < al.size(); ++i) {
//                    ClientThread ct = al.get(i);
//                    try {
//                        ct.inputStream.close();
//                        ct.outputStream.close();
//                        ct.socket.close();
//                    } catch (IOException e) {
//
//                    }
//                }
//            } catch (IOException e) {
//                System.out.println("Exception closing the sever and clients:" + e);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        keepGoing = false;
        try {
            new Socket("localhost", port);
        } catch (Exception e) {

        }
    }

    private synchronized void sendMessage(String message, int destination){
        String time = sdf.format(new Date());
        String messageLf = time + "#" + message;
        System.out.println(messageLf);


        ClientThread ct = (ClientThread) tree.find(destination).thread;

        if (!ct.writeMsg(messageLf)) {
//            al.remove(destination);
            System.out.println("Disconnected Client " + ct.userId + " removed from list.");
        }
    }

//    private synchronized void broadcast(String message) {
//
//        String time = sdf.format(new Date());
//        String messageLf = time + " " + message + "\n";
//
//        System.out.println(messageLf);
//
//        for (int i = al.size(); --i >= 0; ) {
//            ClientThread ct = al.get(i);
//
//            if (!ct.writeMsg(messageLf)) {
//                al.remove(i);
//                System.out.println("Disconnected Client " + ct.userId + " removed from list.");
//            }
//        }
//    }

//    synchronized void remove(int id) {
//        for (int i = 0; i < al.size(); i++) {
//            ClientThread ct = al.get(i);
//            if (ct.userId == id) {
//                al.remove(i);
//                return;
//            }
//        }
//    }

    public static void main(String[] args) {
        Sever sever = new Sever(4242);
        sever.start();
    }

    class ClientThread extends Thread {

        Socket socket;
        ObjectInputStream inputStream;
        ObjectOutputStream outputStream;

        long userId;
        String date;

        ClientThread(Socket socket) throws IOException {

            this.socket = socket;
            System.out.println("Thread trying to create Object Input/Output Stream");
            try {
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());


                userId = Long.parseLong(inputStream.readObject().toString());
                System.out.println(userId + " just connect.");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            date = new Date() + "\n";
        }

        @Override
        public void run() {

            boolean keepGoing = true;
            while (keepGoing) {
                try {
                    String message = inputStream.readObject().toString();
                    String[] messageInfo = message.split("#");
                    sendMessage(message,Integer.parseInt(messageInfo[1]));
                } catch (IOException e) {
                    System.out.println(userId + "Exception reading Stream: " + e);
                    break;
                } catch (ClassNotFoundException e) {
                    break;
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }

            }
        }

        private void close() {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
            }

            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
            }

            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {}
        }

        private boolean writeMsg(String msg){
            if (!socket.isConnected()) {
                close();
                return false;
            }

            try{
                outputStream.writeObject(msg);
            }catch (IOException e){
                System.out.println("Error sending message to " + userId);
                System.out.println(e);
            }
            return true;
        }
    }
}

