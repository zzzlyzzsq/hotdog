import java.io.DataInputStream;  
import java.io.DataOutputStream;    
import java.io.FileInputStream;    
import java.io.FileOutputStream;
import java.io.IOException;    
import java.io.PrintWriter;
import java.net.InetSocketAddress;    
import java.net.ServerSocket;    
import java.net.Socket;  
import java.lang.Process;
import java.lang.Runtime;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.lang.ProcessBuilder.Redirect;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;


public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        int fileNum = 0;
        //String filePath = "/Users/Junjie/Desktop/Hotdog-Classification-master/new_server/";
        String filePath = "/root/ys/Hotdog-Classification-master/new_server";
        long start_time = System.currentTimeMillis();
        try {
            serverSocket = new ServerSocket(4444);
            serverSocket.setSoTimeout(3000);
            while(true){
                Socket socket = null;
                InputStream in = null;
                OutputStream out = null;
                socket = serverSocket.accept();
                in = socket.getInputStream(); 

                fileNum += 1;
                System.out.println("fileNum is " + fileNum);
                out = new FileOutputStream(filePath+Integer.toString(fileNum)+".jpg");
                //out = new FileOutputStream("/Users/Junjie/Desktop/shenhua/1.jpg");

                byte[] bytes = new byte[16*1024];

                int count;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }

                out.close();
                in.close();
                socket.close();
            }
        } catch (IOException e) {
            //System.out.println("Can't setup server on this port number. ");
            e.printStackTrace();
        } /*catch (FileNotFoundException ex) {
            System.out.println("File not found. ");
        }*/
        serverSocket.close();
        System.out.println("I am here ");
        long end_time = System.currentTimeMillis();
        System.out.println("Start time is " );
        System.out.println(start_time);
        System.out.println("End time is " );
        System.out.println(end_time);   
        System.out.println("Time interval is");
        System.out.println((end_time-start_time)/100);
        try{
            ProcessBuilder pb = new ProcessBuilder("python3", "label_image_server.py");
            pb.redirectOutput(Redirect.INHERIT);
            pb.redirectError(Redirect.INHERIT);
            Process p = pb.start();
            p.waitFor();
        }catch (IOException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

             
    }
}