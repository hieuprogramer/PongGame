/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponggame;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author daoxuanhieu
 */
public class Handle extends UnicastRemoteObject implements HandleInf {
    GamePanelServer g;
    Handle(GamePanelServer g) throws RemoteException {
        super();
        this.g =g;
    }
    @Override
    public void suspend() throws RemoteException {
        g.gameThread.suspend();
    }

    @Override
    public void resume() throws RemoteException {
        g.gameThread.resume();
        g.lastTime = System.nanoTime();
    }
    
}
