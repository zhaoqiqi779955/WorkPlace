package testForInet;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestInet {
    public static void main(String[] args) {
        try {
            InetAddress[] host=InetAddress.getAllByName("www.baidu.com");
           for(InetAddress h:host) {
               System.out.println(h);
           }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
