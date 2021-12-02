/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponggame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author daoxuanhieu
 */
public class RoomServer extends javax.swing.JFrame {

    /**
     * Creates new form Room
     */
    public final static int SERVERPORT = 1772;
    public final static int CLIENTPORT = 1773;
    public final static int BUFFER_SIZE = 1024;
    public final static String MSG_SERVER_IDENTIFY = "I am server";
    public final static String MSG_CLIENT_FIND_SERVER = "Are you server";

    private static DatagramSocket UDPserver;
    private static byte[] buffer;
    private static DatagramPacket pkdp;
    
    private DefaultTableModel model;
    private String playerName;
    private String roomName;
    private String roomPwd="";
    
    private ServerSocket TCPserver;
    private Socket TCPsc;
    private ServerSocket TCPserverAuth;
    private DataInputStream dis;
    private DataOutputStream dos;
    
    private Thread th1;
    private Thread th2;
    private Thread th3;
    public RoomServer(String name, String roomname, String roompwd) {
        initComponents();
        this.setTitle(roomname+" : "+roompwd);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.playerName = name;
        this.roomName = roomname;
        this.roomPwd = roompwd;
        model = (DefaultTableModel) playerTbl.getModel();
        model.addRow(new Object[]{name, "Chu phong", "San sang"});
        th1 = new Thread(new Runnable() {
            public void run() {
                try {
                    //server
                    
                    buffer = new byte[BUFFER_SIZE];
                    UDPserver = new DatagramSocket(SERVERPORT);
                    pkdp = new DatagramPacket(buffer, BUFFER_SIZE);
                    System.out.println("UDP Server is running on 1772");
                    while (true) {
                        UDPserver.receive(pkdp);
                        String mess = new String(buffer);
                        // kiem tra tin nhan den tu client
                        if (mess.contains(MSG_CLIENT_FIND_SERVER)) {
                            System.out.printf("Request SERVER from IP: %s\n",pkdp.getAddress().getHostAddress());

                            // gui thong bao xac nhan day la server
                            String message = MSG_SERVER_IDENTIFY+"#"+roomName+"#"+playerName;
                            byte[] response = message.getBytes();
                            DatagramPacket outpkdp = new DatagramPacket(response, response.length,
                                    pkdp.getAddress(), CLIENTPORT);
                            UDPserver.send(outpkdp);
                        }
                    }
                } catch (SocketException ex) {
                    Logger.getLogger(RoomServer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(RoomServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        th1.start();
        // khởi tạo action listener, thêm nó khi cần và bỏ khi không cần
        // để tranh lỗi tự kích hoạt action listener
        ActionListener cbal = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // TODO add your handling code here:
                    String nums = ballNumCb.getSelectedItem().toString();
                    dos.writeUTF("$ball-"+nums);
                    //System.out.println(nums);
                } catch (IOException ex) {
                    Logger.getLogger(RoomServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        ballNumCb.addActionListener(cbal);
        th2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        TCPserver = new ServerSocket(1774);
                        System.out.println("TCP Server running on 1774");                    
                        TCPsc = TCPserver.accept();
                        System.out.println("Client da ket noi");
                        dis = new DataInputStream(TCPsc.getInputStream());
                        dos = new DataOutputStream(TCPsc.getOutputStream());
                        String clname = dis.readUTF();
                        model.addRow(new Object[]{clname, "Khach", "Chua san sang"});
                        dos.writeUTF(playerName);
                        while(true) {
                            String rmessage = dis.readUTF();
                            if(rmessage.equals("$ready")) {
                                model.setValueAt("San sang", 1, 2);
                                ballNumCb.disable();
                                //chatView.append("\nDoi thu da san sang");
                            } else if(rmessage.equals("$notready")) {
                                model.setValueAt("Chua san sang", 1, 2);
                                ballNumCb.enable();
                                //chatView.append("\nDoi thu chua san sang");
                            } else if(rmessage.contains("$ball")) {
                                String[] nums = rmessage.split("-");
                                ballNumCb.removeActionListener(cbal);
                                ballNumCb.setSelectedItem(nums[1]);
                                ballNumCb.addActionListener(cbal);
                            } else {
                                    chatView.append(rmessage);
                            }
                        }

                    } catch (EOFException ex) {
                        System.out.println("Client ngat ket noi");
                        if(model.getRowCount()==2)model.removeRow(1);
                    } catch (IOException ex) { 
                        Logger.getLogger(RoomServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        TCPserver.close();
                    } catch (IOException ex) {
                        Logger.getLogger(RoomServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        th2.start();
        th3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TCPserverAuth = new ServerSocket(1775);
                    System.out.println("TCP server is running on 1775");
                    while(true) {
                        Socket cl = TCPserverAuth.accept();
                        DataInputStream dis = new DataInputStream(cl.getInputStream());
                        DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                        String req = dis.readUTF();
                        if(req.equals(roomPwd)) {
                            dos.writeUTF("ok");
                        } else {
                            dos.writeUTF("reject");
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("Dong server");
                }
            }
        });
        th3.start();
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                th1.stop();
                th2.stop();
                UDPserver.close();
                try {
                    TCPserverAuth.close();
                    TCPserver.close();
                } catch (IOException ex) {
                    Logger.getLogger(RoomServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Close server");
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
        playerTbl = new javax.swing.JTable();
        startGameBtn = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        chatView = new javax.swing.JTextArea();
        chatTf = new javax.swing.JTextField();
        chatBtn = new javax.swing.JButton();
        kickBtn = new javax.swing.JButton();
        ballNumCb = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        playerTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ten nguoi choi", "Chuc vu", "Trang thai"
            }
        ));
        jScrollPane1.setViewportView(playerTbl);

        startGameBtn.setText("Bat dau");
        startGameBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startGameBtnActionPerformed(evt);
            }
        });

        chatView.setColumns(20);
        chatView.setRows(5);
        jScrollPane2.setViewportView(chatView);

        chatBtn.setText("Chat");
        chatBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chatBtnActionPerformed(evt);
            }
        });

        kickBtn.setText("Kick");
        kickBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kickBtnActionPerformed(evt);
            }
        });

        ballNumCb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3" }));
        ballNumCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ballNumCbActionPerformed(evt);
            }
        });

        jLabel1.setText("Balls");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(startGameBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(48, 48, 48))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(ballNumCb, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)))
                        .addComponent(kickBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chatTf, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(chatBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(startGameBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(kickBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ballNumCb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chatBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chatTf, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chatBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chatBtnActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            String smessage = chatTf.getText();
            switch(smessage) {
                case "$start":
                    //chatView.append("\n"+ playerName +": Da bat dau");
                    if(model.getValueAt(1, 2).toString().equals("San sang")){
                        int n = Integer.parseInt(ballNumCb.getSelectedItem().toString());
                        GameFrameServer gfs = new GameFrameServer(n);
                        dos.writeUTF(smessage);
                    } else JOptionPane.showMessageDialog(null, "Nguoi choi chua san sang");
                    break;
                default:
                    smessage = "\n"+ playerName+": "+ smessage;
                    chatView.append(smessage);
                    dos.writeUTF(smessage);
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(RoomClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        chatTf.setText("");
    }//GEN-LAST:event_chatBtnActionPerformed

    private void startGameBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startGameBtnActionPerformed
        // TODO add your handling code here:
        if(model.getValueAt(1, 2).toString().equals("San sang")) {
            try {
                int n = Integer.parseInt(ballNumCb.getSelectedItem().toString());
                GameFrameServer gfs = new GameFrameServer(n);
                dos.writeUTF("$start");
            } catch (IOException ex) {
                Logger.getLogger(RoomServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nguoi choi chua san sang");
        }
    }//GEN-LAST:event_startGameBtnActionPerformed

    private void kickBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kickBtnActionPerformed
        try {
            // TODO add your handling code here:
            int row = playerTbl.getSelectedRow();
            if(row==1) {
                model.removeRow(1);
                TCPsc.close();
            } else JOptionPane.showMessageDialog(rootPane, "Khong the kich");
        } catch (IOException ex) {
            Logger.getLogger(RoomServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_kickBtnActionPerformed

    private void ballNumCbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ballNumCbActionPerformed
        
    }//GEN-LAST:event_ballNumCbActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(RoomServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(RoomServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(RoomServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(RoomServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//        //</editor-fold>
//        //new GameFrameServer();
//        new RoomServer("oke", "oke", "oke").setVisible(true);
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ballNumCb;
    private javax.swing.JButton chatBtn;
    private javax.swing.JTextField chatTf;
    private javax.swing.JTextArea chatView;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton kickBtn;
    private javax.swing.JTable playerTbl;
    private javax.swing.JButton startGameBtn;
    // End of variables declaration//GEN-END:variables


}
