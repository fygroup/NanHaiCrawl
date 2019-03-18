package landsea.com.util;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/02/26 15:10
 */
public class MudpSrv {

    int port = 6789;

    public void sendMessage(String msg, MulticastSocket socket) throws IOException {
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(ostream);
        dataStream.writeUTF(msg);
        dataStream.close();

        byte[] data = ostream.toByteArray();

        InetAddress address = InetAddress.getByName("230.3.3.3");
        System.out.println(address);
        socket.joinGroup(address);
        DatagramPacket dp = new DatagramPacket(data, data.length, address,port);
        socket.send(dp);
    }

    public void getMessage(MulticastSocket socket) throws IOException{
        byte[] bs = new byte[1000];
        DatagramPacket packet = new DatagramPacket(bs, bs.length);
        socket.receive(packet);

        DataInputStream istream = new DataInputStream(new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength()));

        String msg = istream.readUTF();

        System.out.println(msg);
    }

    public static void main(String args[]) throws IOException{
        while (true){
            MudpSrv srv = new MudpSrv();
            MulticastSocket socket = new MulticastSocket(srv.port);
            srv.getMessage(socket);
        }

    }

}
