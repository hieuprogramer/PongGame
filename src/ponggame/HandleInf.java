/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponggame;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author daoxuanhieu
 */
public interface HandleInf extends Remote {
    public void suspend() throws RemoteException;
    public void resume() throws RemoteException;
}
