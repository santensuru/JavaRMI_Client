package javarmi_client;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.image.BufferedImage;
import java.rmi.*;

/**
 *
 * @author AdminMobility
 */
public interface ImageInterface extends Remote {
    public BufferedImage toBW(BufferedImage input) throws RemoteException;
}
