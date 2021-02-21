package Web.testForInet;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InquireHost {
    public static void main(String[] args) {
        try {
            InetAddress host=InetAddress.getByName(args[0]);
            System.out.println(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
