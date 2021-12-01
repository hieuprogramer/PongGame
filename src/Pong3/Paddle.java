/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pong3;
import static Pong3.Score.GAME_WIDTH;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
/**
 *
 * @author daoxuanhieu
 */
public class Paddle extends Rectangle {
    int id;
    int yVelocity;
    int speed = 10;
    String result="";
    Paddle(int x, int y, int PADDLE_WIDTH, int PADDLE_HEIGHT, int id) {
        super(x,y, PADDLE_WIDTH, PADDLE_HEIGHT);
        this.id = id;
    }
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP) {
            setYDirection(-speed);
            move();
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            setYDirection(speed);
            move();
        }
    }
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP) {
            setYDirection(0);
            move();
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            setYDirection(0);
            move();
        }
    }
    public void setYDirection(int yDirection) {
        yVelocity = yDirection;
    }
    public void move() {
        y = y + yVelocity;
    }
    public void draw(Graphics g) {
        g.setFont(new Font("Consolas", Font.PLAIN, 60));
        if(id==1) {
            g.setColor(Color.blue);
            g.drawString(result, (GAME_WIDTH/4)-100, 500);
        } else {
            g.setColor(Color.red);
            g.drawString(result, (3*GAME_WIDTH/4)-100, 500);
        }
        g.fillRect(x, y, width, height);
        
    }
}
