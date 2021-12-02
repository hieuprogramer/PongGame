/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponggame;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
/**
 *
 * @author daoxuanhieu
 */
public class GamePanelServer extends JPanel implements Runnable {
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
    long lastTime;
    //DatagramSocket server;
    ServerSocket server;
    GamePanelServer(int ballNum) {
        this.ballNum = ballNum;
        balls = new Ball[ballNum];
        newPaddles();
        newBall();
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
        paddle1.move();
        //paddle2.move();
        for(int i=0; i<ballNum; i++) {
            balls[i].move();
        }
    }
    public void checkCollision() {
        for(int i=0; i<ballNum; i++) {
            if(balls[i].y <= 0) {
                balls[i].setYDirection(-balls[i].yVelocity);
            }
            if(balls[i].y >= GAME_HEIGHT-BALL_DIAMETER) {
                balls[i].setYDirection(-balls[i].yVelocity);
            }

            if(balls[i].intersects(paddle1)) {
                balls[i].xVelocity = Math.abs(balls[i].xVelocity);
                balls[i].setXDirection(balls[i].xVelocity);
                balls[i].setYDirection(balls[i].yVelocity);
            }
            if(balls[i].intersects(paddle2)) {
                balls[i].xVelocity = Math.abs(balls[i].xVelocity);
                balls[i].setXDirection(-balls[i].xVelocity);
                balls[i].setYDirection(balls[i].yVelocity);
            }

            if(balls[i].x <= 0) {
                if(score.player2<MAX_SCORE) score.player2++;
                //newPaddles();
                random = new Random();
                balls[i] = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT-BALL_DIAMETER), BALL_DIAMETER, BALL_DIAMETER);
                System.out.println("Player 2: "+score.player2);
            }
            if(balls[i].x >= GAME_WIDTH - BALL_DIAMETER) {
                if(score.player1<MAX_SCORE) score.player1++;
                random = new Random();
                balls[i] = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT-BALL_DIAMETER), BALL_DIAMETER, BALL_DIAMETER);
                System.out.println("Player 1: "+score.player1);
            }
        }
        
        if(paddle1.y <= 0) {
            paddle1.y = 0;
        }
        if(paddle1.y >= (GAME_HEIGHT - PADDLE_HEIGHT)) {
            paddle1.y = GAME_HEIGHT - PADDLE_HEIGHT;
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
            lastTime = System.nanoTime();
            double amountOfTicks = 60.0;
            double ns = 1000000000/amountOfTicks;
            double delta = 0;
            
            server = new ServerSocket(1776);
            System.out.println("Game dang chay tren port 1776");
            Socket sc = server.accept();
            System.out.println("Thanh cong");
                        
            while(true) {
                long now = System.nanoTime();
                delta += (now-lastTime)/ns;
                lastTime = now;
                if(delta >= 1) {
                    //System.out.println(delta +" "+lastTime+" "+now);
                    move();                    
                    checkCollision();
                    BufferedOutputStream bos = new BufferedOutputStream(sc.getOutputStream());
                    DataOutputStream dos = new DataOutputStream(bos);
                    for(int i=0; i<ballNum; i++) {
                        dos.writeInt((int)balls[i].getX());
                        dos.writeInt((int)balls[i].getY());
                    }
                    dos.writeInt((int)paddle1.getX());
                    dos.writeInt((int)paddle1.getY());
                    dos.writeInt(score.player1);
                    dos.writeInt(score.player2);
                    bos.flush();
                    
                    BufferedInputStream bis = new BufferedInputStream(sc.getInputStream());
                    DataInputStream dis = new DataInputStream(bis);
                    paddle2.setLocation(dis.readInt(), dis.readInt());
                    
                    boolean stop = checkWinner();
                    repaint();
                    if(stop) gameThread.stop();
                    delta--;
                }
            }
        } catch (IOException ex) {
            System.out.println("Server da dong");
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
