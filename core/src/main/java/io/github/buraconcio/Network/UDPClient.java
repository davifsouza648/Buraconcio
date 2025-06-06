// package io.github.buraconcio.Network;

// import java.io.*;
// import java.net.*;

// import io.github.buraconcio.Objects.Player;
// import io.github.buraconcio.Utils.Constants;
// import io.github.buraconcio.Utils.PlayerManager;
// import io.github.buraconcio.Utils.UdpPackage;

// public class UDPClient {
    
//     private DatagramSocket UDPsocket;
//     private DatagramPacket PlayerPackage;

//     public void startUDPClient(){
//         Thread thread = new Thread(() -> connect());
//         thread.setDaemon(true);
//         thread.start();
//     }

//     byte[] sendData = new byte[1024];
//     byte[] receiveData = new byte[1024];

//     public void connect(){

//         try{

//             UDPsocket = new DatagramSocket();

//             ByteArrayOutputStream bos = new ByteArrayOutputStream();
//             ObjectOutputStream out = new ObjectOutputStream(bos);

//             //para teste

//             UdpPackage package = new UdpPackage(PlayerManager.getInstance().getLocalPlayer().getId(), 0, 0)

//             out.writeObject(PlayerManager.getInstance().getLocalPlayer());
//             out.flush();

//             sendData = bos.toByteArray();

//             UDPsocket = new DatagramSocket();
//             InetAddress address = InetAddress.getByName(Constants.IP);
//             PlayerPackage = new DatagramPacket(sendData, sendData.length, address, Constants.PORT);

//             UDPsocket.send(PlayerPackage);
//             System.out.println("enviamos o player local");

//             UDPsocket.close();
//         }

//     }



// }
