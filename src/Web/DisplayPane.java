package Web;

import netscape.javascript.JSException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.http.WebSocket;


public class DisplayPane  {
   Handler handler;
    JTextArea record;//消息记录
    JTextArea inputMessage;//输入消息区域
    JLabel clinet;//显示当前用户信息
    JButton confirm;//确认发送按钮
    JLabel state;//显示当前连接状态
    JPanel panel;
    public DisplayPane(Handler handler) throws HeadlessException {
        int x=30;
        this.handler=handler;
        clinet=new JLabel();
        clinet.setPreferredSize(new Dimension(250,30));

        state=new JLabel("未连接");
        state.setPreferredSize(new Dimension(100,30));

        JPanel head=new JPanel();
        head.setLayout(new FlowLayout(FlowLayout.LEFT));
        head.setPreferredSize(new Dimension(400,50));
        head.add(clinet);
        //head.add(Box.createHorizontalStrut(50));
        head.add(state);

        record=new JTextArea("消息记录...\n");;
        JScrollPane scrollPane=new JScrollPane(record,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.setPreferredSize(new Dimension(350,400));
        scrollPane.setBorder(new LineBorder(Color.black));

//        JScrollPane scrollPane=new JScrollPane(record,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        inputMessage=new JTextArea();
        inputMessage.setPreferredSize(new Dimension(300,80));
        JPanel MessagePane=new JPanel();
        MessagePane.setLayout(new FlowLayout(FlowLayout.LEFT));
        MessagePane.setPreferredSize(new Dimension(400,100));

        //确定按钮
        JButton ok=new JButton("Enter");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {//发送消息
                String str=inputMessage.getText();
                handler.send(str);
                record.append("服务器："+str+'\n');
                inputMessage.setText("");
            }
        });

        Box h1=Box.createHorizontalBox();
        h1.add(inputMessage);
        h1.add(Box.createHorizontalStrut(8));
        h1.add(ok) ;

        MessagePane.add(h1);
        panel=new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(MessagePane,BorderLayout.SOUTH);
        panel.add(head,BorderLayout.NORTH);
        panel.add(scrollPane,BorderLayout.CENTER);
        //pack();

    }

    public void ShowMessage(String content)//显示消息记录
    {
            record.append(content+'\n');
    }
    public  void setState(boolean b)//设置状态
    {
        if(b==true) state.setText("状态：已连接");
        else state.setText("状态：未连接");
    }
    public void setClinet( String str)
    {
        clinet.setText(str);
    }
    public JPanel getPane()
    {
        return panel;
    }

}
