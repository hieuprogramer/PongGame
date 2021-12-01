/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pong3;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author daoxuanhieu
 */
public class ServerList extends javax.swing.JFrame{
    
    public final static int SERVERPORT = 1772;
    public final static int CLIENTPORT = 1773;
    public final static int BUFFER_SIZE = 1024;
    public final static String MSG_SERVER_IDENTIFY = "I am server";
    public final static String MSG_CLIENT_FIND_SERVER = "Are you server";

    private static DatagramSocket UDPclient;
    private static byte[] buffer;
    private static DatagramPacket pkdp;
    
    Thread th;
    
    public ServerList() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        th = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    final byte[] request = MSG_CLIENT_FIND_SERVER.getBytes();
                    UDPclient = new DatagramSocket(CLIENTPORT);
                    //clientds.setSoTimeout(60000);
                    buffer = new byte[BUFFER_SIZE];
                    pkdp = new DatagramPacket(buffer, BUFFER_SIZE);


                    // gửi broadcast để tìm kiếm server
                    DatagramPacket outpkdp = new DatagramPacket(request, request.length,
                            InetAddress.getByName("255.255.255.255"), SERVERPORT);
                    UDPclient.send(outpkdp);
                    System.out.println("Client is running");

                    while (true) {
                        try {
                            UDPclient.receive(pkdp);
                            System.out.println("Tim thay 1 server");
                            String mess = new String(buffer);

                            // Nếu đây là thông điệp từ server
                            if (mess.contains(MSG_SERVER_IDENTIFY)) {
                                System.out.printf("SERVER IP: %s\n",
                                        pkdp.getAddress().getHostAddress());
                                String[] svrow = mess.split("#");
                                DefaultTableModel model = (DefaultTableModel) ServerTbl.getModel();
                                model.addRow(new Object[]{svrow[1], svrow[2],pkdp.getAddress().getHostAddress()});
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(RoomClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (SocketException ex) {
                    System.out.println("Dung quet server");
                } catch (UnknownHostException ex) {
                    Logger.getLogger(RoomClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(RoomClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        th.start();

        ServerTbl.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = ServerTbl.rowAtPoint(evt.getPoint());
                int col = ServerTbl.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    String ip = ServerTbl.getModel().getValueAt(row, 2).toString();
                    JTextField username = new JTextField();
                    JTextField password = new JPasswordField();
                    Object[] message = {
                        "Player name:", username,
                        "Room password:", password
                    };
                    int option = JOptionPane.showConfirmDialog(rootPane, message, "Login", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        if(!username.getText().isEmpty()) {
                            try {
                                Socket sc = new Socket(ip,1775);
                                System.out.println("Da ket noi toi "+ip+" port 1775");
                                DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
                                dos.writeUTF(password.getText());
                                DataInputStream dis = new DataInputStream(sc.getInputStream());
                                String res = dis.readUTF();
                                if(res.equals("ok")) {
                                    RoomClient rc = new RoomClient(username.getText(), ip);
                                    rc.setVisible(true);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Sai mat khau");
                                }
                                sc.close();
                                System.out.println("Dong ket noi 1776");
                            } catch (IOException ex) {
                                Logger.getLogger(ServerList.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else JOptionPane.showMessageDialog(null, "Ten khong duoc de trong");
                    } else {
                        System.out.println("Login canceled");
                    }
                }
            }
        });
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                UDPclient.close();
                th.stop();
                System.out.println("Close UDP client");
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        ServerTbl = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ServerTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ten phong", "Chu phong", "IP"
            }
        ));
        jScrollPane1.setViewportView(ServerTbl);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServerList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerList().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable ServerTbl;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
