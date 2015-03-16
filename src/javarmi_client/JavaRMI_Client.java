/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarmi_client;
import imagermi.ImageInterface;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
        // TODO code application logic here
        int i = 10, j;
        for (j=1; j<=i; j++) {
        
            try {

                System.out.println("Read file ...");
                File file = new File(String.valueOf(j)+".jpg");
                BufferedImage image = ImageIO.read(file);

                int x, y;
                int h = image.getHeight();
                int w = image.getWidth();
                int[] input = new int[h*w];

                for (y=0; y<h; y++) {
                    for (x=0; x<w; x++) {
                        input[x + w*y] = image.getRGB(x, y);
                    }
                }

                System.out.println("File readed.");

                System.out.println("Try connect ...");
                Registry registry = LocateRegistry.getRegistry();
                ImageInterface stub = (ImageInterface) registry.lookup("toBW");
                System.out.println("Connected.");

                System.out.println("Do RMI ...");
                int[] output = stub.toBW(0, 0, w, h, input, 0, w);
                BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

                for (y=0; y<h; y++) {
                    for (x=0; x<w; x++) {
                        dest.setRGB(x, y, output[x + w*y] << 16 | output[x + w*y] << 8 | output[x + w*y]);
                    }
                }

                File fbw = new File("BW-"+String.valueOf(j)+".jpg");
                if (!ImageIO.write(dest, "JPEG", fbw)) {
                    throw new RuntimeException("Unexpected error writing image");
                }

            } catch (NotBoundException | MalformedURLException | RemoteException ex) {
                Logger.getLogger(JavaRMI_Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
}
