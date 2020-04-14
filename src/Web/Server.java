package Web;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame implements Runnable {
    ServerSocket serverSocket;
    JTabbedPane tabbedPane=null;
    public Server() {
       super.setTitle("服务器");
       setSize(600,600);
        tabbedPane=new JTabbedPane(JTabbedPane.LEFT);
        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            serverSocket = new ServerSocket(8888);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public static void main(String[] args) {

        Server server = new Server();
        new Thread(server).start();
    }

    public void run() {
        while (true) {
            try {
                Socket socketAtServer = serverSocket.accept();
                System.out.println("已与" + socketAtServer.getInetAddress() + " 端口号：" + socketAtServer.getPort() + "连接！");
                tabbedPane.add("客户："+socketAtServer.getInetAddress() + " 端口号：" + socketAtServer.getPort(),new Handler(socketAtServer).getPane());
                setVisible(true);
            } catch (Exception e) {
                System.out.println(e);
            }


        }

    }
}

class Handler {//用于处理每一个客户端请求
    DisplayPane displayPane = null;
    Socket socket = null;
    BufferedWriter writer = null;
    BufferedReader reader = null;
    public Handler(Socket soc) {
        socket = soc;
        displayPane = new DisplayPane(this);
        InetAddress inetAddress=soc.getInetAddress();

        displayPane.setClinet("与客户: "+inetAddress.getHostAddress()+" 端口号: "+socket.getPort());
        displayPane.setState(true);
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            displayPane.setState(false);
//            e.printStackTrace();
        }
        listen();

    }

    public void listen() {//接听消息

        new Thread() {
            public void run() {
                try {
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (true) {
                        String str = reader.readLine();
                        if (str != null)
                            displayPane.ShowMessage("客户端："+str);
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    displayPane.setState(false);

                }
            }
        }.start();


    }

    public void send(String str) {//发送消息
        try {
            writer.write(str);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JPanel getPane() {
        return displayPane.getPane();
    }
}
