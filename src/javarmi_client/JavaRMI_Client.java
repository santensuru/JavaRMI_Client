/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarmi_client;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author user
 */
public class JavaRMI_Client {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        try {
            // TODO code application logic here
            File file = new File("1.jpg");
            BufferedImage image = ImageIO.read(file);
            ImageInterface stub = (ImageInterface)Naming.lookup("rmi://10.151.12.201:5000/image");
            BufferedImage dest = stub.toBW(image);
            File fbw = new File("BW.jpg");
            if (!ImageIO.write(dest, "JPEG", fbw)) {
                throw new RuntimeException("Unexpected error writing image");
            }
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            Logger.getLogger(JavaRMI_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
