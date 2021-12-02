/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponggame;

import static ponggame.GameFrameClient.SCREEN_SIZE;
import ponggame.GamePanelClient.AL;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author daoxuanhieu
 */
public class GameFrameServer extends JFrame{
    static final int GAME_WIDTH = 1006;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH*(0.5555))+72;
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    GamePanelServer panel;
    GameFrameServer(int ballNum) throws RemoteException, MalformedURLException {
        panel = new GamePanelServer(ballNum);
        JPanel panel2 = new JPanel();
        JButton suspend = new JButton("Tam dung");
        JButton resume = new JButton("Tiep tuc");
        suspend.setBounds(0, 0, 80, 80);
        panel2.add(suspend);
        panel2.add(resume);
        this.add(panel2, BorderLayout.SOUTH);
        this.setPreferredSize(SCREEN_SIZE);
        this.add(panel);
        this.setTitle("Pong Game");
        this.setResizable(false);
        this.setBackground(Color.black);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        
        
        Registry rmiRegistry = LocateRegistry.createRegistry(1099); // cmd: rmiregistry (path)
        Handle hd = new Handle(panel);
        Naming.rebind("rmi://localhost/handle", hd); 
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    UnicastRemoteObject.unexportObject(rmiRegistry, true);
                    panel.server.close();
                    panel.gameThread.stop();
                    dispose();
                    System.out.println("Close game server");
                } catch (IOException ex) {
                    Logger.getLogger(GameFrameServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        suspend.addActionListener((e) -> {
            panel.gameThread.suspend();
            panel.requestFocus();
        });
        resume.addActionListener((e) -> {
            panel.gameThread.resume();
            panel.lastTime = System.nanoTime();
            panel.requestFocus();
        });
        
    }
//    public static void main(String args[]) throws RemoteException, MalformedURLException {
//        new GameFrameServer(1);
//    }

}
