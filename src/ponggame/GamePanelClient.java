/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponggame;

/**
 *
 * @author daoxuanhieu
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static ponggame.GamePanelServer.MAX_SCORE;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author daoxuanhieu
 */
public class GamePanelClient extends JPanel implements Runnable {
    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH*(0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;
    static final int MAX_SCORE = 10;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball[] balls;
    int ballNum;
    Score score;
    String ipserver;
    //DatagramSocket client;
    Socket client;
    
    GamePanelClient(String ipserver, int ballNum) {
        this.ballNum = ballNum;
        balls = new Ball[ballNum];
        newPaddles();
        newBall();
        this.ipserver = ipserver; 
        score = new Score(GAME_WIDTH, GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void newBall() {
        random = new Random();
        for(int i=0; i<ballNum; i++) {
            balls[i] = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT-BALL_DIAMETER), BALL_DIAMETER, BALL_DIAMETER);
        }
    }
    public void newPaddles() {
        paddle1 = new Paddle(0, (GAME_HEIGHT/2)-(PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        paddle2 = new Paddle(GAME_WIDTH - PADDLE_WIDTH, (GAME_HEIGHT/2)-(PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT, 2);
    }
    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0,0,this);
    }
    public void draw(Graphics g) {
        paddle1.draw(g);
        paddle2.draw(g);
        for(int i=0; i<ballNum; i++) {
            balls[i].draw(g);
        }
        score.draw(g);
    }
    public void move() {
        paddle2.move();
    }
    public void checkCollision() {
        if(paddle2.y <= 0) {
            paddle2.y = 0;
        }
        if(paddle2.y >= (GAME_HEIGHT - PADDLE_HEIGHT)) {
            paddle2.y = GAME_HEIGHT - PADDLE_HEIGHT;
        }
        
    }
    public boolean checkWinner() {
        if(score.player1 == MAX_SCORE) {
            paddle1.result = "Thang";
            paddle2.result = "Thua";
            return true;
        }
        if(score.player2 == MAX_SCORE) {
            paddle2.result = "Thang";
            paddle1.result = "Thua";
            return true;
        }
        return false;
    }
    public void run() {
        try {
            long lastTime = System.nanoTime();
            double amountOfTicks = 60.0;
            double ns = 1000000000/amountOfTicks;
            double delta = 0;
            
            client = new Socket(ipserver, 1776);
            System.out.println("Da vao game");
            
            while(true) {
                long now = System.nanoTime();
                delta += (now-lastTime)/ns;
                lastTime = now;
                if(delta >= 1) { 
                    move();
                    checkCollision();
                    BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
                    DataInputStream dis = new DataInputStream(bis);
                    for(int i=0; i<ballNum; i++) {
                        balls[i].setLocation(dis.readInt(), dis.readInt());
                    }
                    paddle1.setLocation(dis.readInt(), dis.readInt());
                    score.player1 = dis.readInt();
                    score.player2 = dis.readInt();

                    BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
                    DataOutputStream dos = new DataOutputStream(bos);
                    dos.writeInt((int)paddle2.getX());
                    dos.writeInt((int)paddle2.getY());
                    bos.flush();
                    boolean stop = checkWinner();
                    repaint();
                    if(stop) gameThread.stop();
                    delta--;
                }
            }
        } catch (IOException ex) {
        }
    }
    public class AL extends KeyAdapter{
        public void keyPressed(KeyEvent e) {
            paddle1.keyPressed(e);
            paddle2.keyPressed(e);
        }
        public void keyReleased(KeyEvent e) {
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }
}

