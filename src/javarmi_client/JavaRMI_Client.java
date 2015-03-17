/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarmi_client;
import imagermi.ImageInterface;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
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
        long time = 0;
        for (j=1; j<=i; j++) {
        
            try {
                
                Date date = new Date();
                System.out.println(date);
                
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
                Registry registry = LocateRegistry.getRegistry("10.151.12.201");
                ImageInterface stub = (ImageInterface) registry.lookup("toBW");
                System.out.println("Connected.");

                System.out.println("Do RMI ...");
                
                int[] output = stub.toBW(0, 0, w, h, input, 0, w);
                BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

                for (y=0; y<h; y++) {
                    for (x=0; x<w; x++) {
                        dest.setRGB(x, y, output[x + w*y]);
                    }
                }

                File fbw = new File("BW-"+String.valueOf(j)+".jpg");
                if (!ImageIO.write(dest, "JPEG", fbw)) {
                    throw new RuntimeException("Unexpected error writing image");
                }
                time = new Date().getTime() - date.getTime();
                System.out.println(date);
                System.out.println("Done with time execution: "+etaConvert(time)+".");

            } catch (NotBoundException | MalformedURLException | RemoteException ex) {
                Logger.getLogger(JavaRMI_Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    private static String etaConvert(long l_d) {
        String eta = "";
        l_d /= 1000;
        if (l_d >= 86400) {
            eta += String.valueOf(l_d/86400) + " d ";
            l_d %= 86400;
        }
        if (l_d >= 3600) {
            eta += String.valueOf(l_d/3600) + " h ";
            l_d %= 3600;
        }
        if (l_d >= 60) {
            eta += String.valueOf(l_d/60) + " m ";
            l_d %= 60;
        }
        eta += String.valueOf(l_d) + " s";
        return eta;
    }
}
