/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pong3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author daoxuanhieu
 */
public class GameFrameClient extends JFrame {
    static final int GAME_WIDTH = 1006;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH*(0.5555))+72;
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    GamePanelClient panel;
    DataOutputStream roomDos;
    DefaultTableModel model;
    Thread th;
    GameFrameClient(String ipserver, DataOutputStream roomDos, DefaultTableModel model, JComboBox cb, int ballNum) {
        panel = new GamePanelClient(ipserver, ballNum);
        this.roomDos = roomDos;
        this.model = model;
        this.setPreferredSize(SCREEN_SIZE);
        JPanel panel2 = new JPanel();
        JButton suspend = new JButton("Tam dung");
        JButton resume = new JButton("Tiep tuc");
        suspend.setBounds(0, 0, 80, 80);
        panel2.add(suspend);
        panel2.add(resume);
        this.add(panel2, BorderLayout.SOUTH);
        this.add(panel);
        this.setTitle("Pong Game");
        this.setResizable(false);
        this.setBackground(Color.black);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    cb.enable();
                    model.setValueAt("Chua san sang", 1, 2);
                    roomDos.writeUTF("$notready");
                    panel.client.close();
                    dispose();
                    System.out.println("Close game client");
                } catch (IOException ex) {
                    Logger.getLogger(GameFrameClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        suspend.addActionListener((e) -> {
            try {
                HandleInf hd = (HandleInf) Naming.lookup("rmi://"+ipserver+"/handle");
                hd.suspend();
                panel.requestFocus();
            } catch (NotBoundException ex) {
                Logger.getLogger(GameFrameClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(GameFrameClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(GameFrameClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        resume.addActionListener((e) -> {
             try {
                HandleInf hd = (HandleInf) Naming.lookup("rmi://localhost/handle");
                hd.resume();
                panel.requestFocus();
            } catch (NotBoundException ex) {
                Logger.getLogger(GameFrameClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(GameFrameClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(GameFrameClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }


}
