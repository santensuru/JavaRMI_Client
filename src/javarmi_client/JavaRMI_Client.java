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
import java.util.ArrayList;
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
    static volatile ArrayList<Pair<String, String>> list = new ArrayList<>();
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO code application logic here
        
        String[] server = {"10.151.12.201", "10.151.12.202"};
        long joiner = 0;
        String files, format;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        int length = listOfFiles.length;
        Date now = new Date();
        out = new PrintStream("filename_"+now.toString().replaceAll(":", ".").replaceAll(" ", "-")+".txt");
        int i;
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
                    
                    list.add(new Pair(files, format));
                }
            }
        }
        
        for (i=0; i<4; i++) {
            Thread task = new doExecution(server[i%2], i+1);
            task.start();
        }
        
    }
    
    private static class doExecution extends Thread {
        long tottime = 0, time;
        String dest;
        int index;
        
        doExecution(String dest, int index) {
            this.dest = dest;
            this.index = index;
        }
        
        @Override
        public void run() {
            try {
                Registry registry;
                out.println(index+". "+"Try connect ...");
                registry = LocateRegistry.getRegistry(dest);
                ImageInterface stub = (ImageInterface) registry.lookup("toBW");
                out.println(index+". "+"Connected.");
                
                int i;
                for (i=index; i<list.size(); i+=4) {
                    out.println(index+"."+i+". "+list.get(i).getLeft());

                    Date date = new Date();
                    //System.out.println(date);

                    out.println(index+"."+i+". "+"Read file ...");
                    File file = new File(path+list.get(i).getLeft());
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

                    out.println(index+"."+i+". "+"File readed.");

                    out.println(index+"."+i+". "+"Do RMI ...");

                    int[] output = stub.toBW(0, 0, w, h, input, 0, w);
                    BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

                    for (y=0; y<h; y++) {
                        for (x=0; x<w; x++) {
                            dest.setRGB(x, y, output[x + w*y]);
                        }
                    }

                    File fbw = new File(path+"BW\\BW-"+list.get(i).getLeft());
                    if (!ImageIO.write(dest, list.get(i).getRight(), fbw)) {
                        throw new RuntimeException("Unexpected error writing image");
                    }
                    time = new Date().getTime() - date.getTime();
                    tottime += time;
                    //System.out.println(date);
                    out.println(index+"."+i+". "+"Done with time execution: "+etaConvert(time)+".");
                }
                
                out.println(index+". "+"Done with total time execution: +/- "+etaConvert(tottime)+".");
                
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
