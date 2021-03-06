/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponggame;

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
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author daoxuanhieu
 */
public class RoomClient extends javax.swing.JFrame {

    /**
     * Creates new form Room
     */
    DefaultTableModel model;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String namePlayer;
    private boolean ready= false;
    private Socket cl;
    private Thread th;
    public RoomClient(String name, String ip) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.namePlayer = name;
        model = (DefaultTableModel) roomClTbl.getModel();
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
        th = new Thread(new Runnable() {
            public void run() {
                try {
                    cl = new Socket(ip, 1774);
                    System.out.println("Da ket noi server "+ip);
                    dos = new DataOutputStream(cl.getOutputStream());
                    dis = new DataInputStream(cl.getInputStream());
                    dos.writeUTF(name);
                    String playerServerName = dis.readUTF();
                    model.addRow(new Object[] {playerServerName, "Chu phong", "San sang"});
                    model.addRow(new Object[] {namePlayer, "Khach", " Chua san sang"});
                    while(true) {
                        String rmessage = dis.readUTF();
                        if(rmessage.equals("$start")) {
                            int n = Integer.parseInt(ballNumCb.getSelectedItem().toString());
                            GameFrameClient gfc = new GameFrameClient(ip, dos, model, ballNumCb, n);
                        } else if(rmessage.contains("$ball")) {
                            String[] nums = rmessage.split("-");
                            ballNumCb.removeActionListener(cbal);
                            ballNumCb.setSelectedItem(nums[1]);
                            ballNumCb.addActionListener(cbal);
                        } else  {
                            chatView.append(rmessage);
                        }
                    }
                } catch (EOFException ex) {
                    JOptionPane.showMessageDialog(rootPane, "Ban bi kick");
                    setVisible(false);
                    dispose();
                } catch (IOException ex) {
                    Logger.getLogger(RoomServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        th.start();
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    cl.close();
                    th.stop();
                } catch (IOException ex) {
                    Logger.getLogger(RoomClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Close client");
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
        roomClTbl = new javax.swing.JTable();
        readyBtn = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        chatView = new javax.swing.JTextArea();
        chatTf = new javax.swing.JTextField();
        chatBtn = new javax.swing.JButton();
        notReadyBtn = new javax.swing.JButton();
        ballNumCb = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        roomClTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ten nguoi choi", "Chuc vu", "Trang thai"
            }
        ));
        jScrollPane1.setViewportView(roomClTbl);

        readyBtn.setText("San sang");
        readyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readyBtnActionPerformed(evt);
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

        notReadyBtn.setText("Chua san sang");
        notReadyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notReadyBtnActionPerformed(evt);
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
                        .addComponent(readyBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(ballNumCb, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(49, 49, 49)))
                        .addComponent(notReadyBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ballNumCb, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(notReadyBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(readyBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chatBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chatTf, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chatBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chatBtnActionPerformed
        try {
            // TODO add your handling code here:
            String message = chatTf.getText();
            switch(message) {
                case "$ready":
                    model.setValueAt("San sang", 1, 2);
                    //chatView.append("\n"+ namePlayer+": Da san sang");
                    break;
                case "$notready":
                    model.setValueAt("Chua san sang", 1, 2);
                    //chatView.append("\n"+ namePlayer+": Chua san sang");
                    break;
                default:
                    message = "\n"+ namePlayer+": "+ message;
                    chatView.append(message);
                    break;
            }
            dos.writeUTF(message);
        } catch (IOException ex) {
            Logger.getLogger(RoomClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        chatTf.setText("");
    }//GEN-LAST:event_chatBtnActionPerformed

    private void readyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readyBtnActionPerformed
        try {
            model.setValueAt("San sang", 1, 2);
            //chatView.append("\n"+ namePlayer+": Da san sang");
            dos.writeUTF("$ready");
            ballNumCb.disable();
        } catch (IOException ex) {
            Logger.getLogger(RoomClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_readyBtnActionPerformed

    private void notReadyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notReadyBtnActionPerformed
        // TODO add your handling code here:
        try {
            model.setValueAt("Chua san sang", 1, 2);
            //chatView.append("\n"+ namePlayer+": Chua san sang");
            dos.writeUTF("$notready");
            ballNumCb.enable();
        } catch (IOException ex) {
            Logger.getLogger(RoomClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_notReadyBtnActionPerformed

    private void ballNumCbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ballNumCbActionPerformed
        // TODO add your handling code here:
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
//            java.util.logging.Logger.getLogger(RoomClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(RoomClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(RoomClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(RoomClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//        //</editor-fold>
//        //</editor-fold>
//        //</editor-fold>
//
//        new RoomClient("oke","192.168.255.1").setVisible(true);
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ballNumCb;
    private javax.swing.JButton chatBtn;
    private javax.swing.JTextField chatTf;
    private javax.swing.JTextArea chatView;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton notReadyBtn;
    private javax.swing.JButton readyBtn;
    private javax.swing.JTable roomClTbl;
    // End of variables declaration//GEN-END:variables

   

}
