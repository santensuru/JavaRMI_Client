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
import java.io.PrintStream;
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
    
    static volatile String path = "C:\\cygwin64\\home\\user\\coba\\SISTER\\";
    static volatile PrintStream out;
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO code application logic here
        
        long joiner = 0;
        Date tot = new Date();
        String files, format;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        int length = listOfFiles.length;
        out = new PrintStream("filename_"+tot.toString().replaceAll(":", ".").replaceAll(" ", "-")+".txt");
        int i;
        long tottime = 0;
        for (i=0; i<length; i++) {
            if (listOfFiles[i].isFile()) {
                files = listOfFiles[i].getName();
                format = "";
                if (files.endsWith(".png") || files.endsWith(".PNG")) {
                    format = "PNG";
                } else if (files.endsWith(".jpg") || files.endsWith(".JPG")) {
                    format = "JPEG";
                }
                if (!format.equals("")) {
                    
                    Thread task = new doExecution(files, format, i);
                    task.start();
                    if ((i+1)%8 == 0)
                        task.join(joiner);
                }
            }
        }
        tottime = new Date().getTime() - tot.getTime();
        out.println("Done with total time execution: +/- "+etaConvert(tottime)+".");
        
    }
    
    private static class doExecution extends Thread {
        String files, format;
        long time;
        int index;
        
        doExecution(String files, String format, int index) {
            this.files = files;
            this.format = format;
            this.index = index;
        }
        
        @Override
        public void run() {
            try {
                        
                out.println(index+". "+files);

                Date date = new Date();
                //System.out.println(date);

                out.println(index+". "+"Read file ...");
                File file = new File(path+files);
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

                out.println(index+". "+"File readed.");

                out.println(index+". "+"Try connect ...");
                Registry registry;
                if (index%2 == 0)
                    registry = LocateRegistry.getRegistry("10.151.12.201");
                else
                    registry = LocateRegistry.getRegistry("10.151.12.202");
                ImageInterface stub = (ImageInterface) registry.lookup("toBW");
                out.println(index+". "+"Connected.");

                out.println(index+". "+"Do RMI ...");

                int[] output = stub.toBW(0, 0, w, h, input, 0, w);
                BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

                for (y=0; y<h; y++) {
                    for (x=0; x<w; x++) {
                        dest.setRGB(x, y, output[x + w*y]);
                    }
                }

                File fbw = new File(path+"BW\\BW-"+files);
                if (!ImageIO.write(dest, format, fbw)) {
                    throw new RuntimeException("Unexpected error writing image");
                }
                time = new Date().getTime() - date.getTime();
                //System.out.println(date);
                out.println(index+". "+"Done with time execution: "+etaConvert(time)+".");

            } catch (NotBoundException | MalformedURLException | RemoteException ex) {
                out.println(ex);
                Logger.getLogger(JavaRMI_Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                out.println(ex);
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
