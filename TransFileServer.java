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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.*;



public class TransFileServer {  
    public static void main(String[] args) {  
        uploadThread t = new uploadThread();  
        t.start();
    }  
} 

class uploadThread extends Thread {  
    private static final int HOST_PORT = 2222;
    private static final int NEW_HOST_PORT = 2223;

    DataInputStream inputStream;  
    FileOutputStream fos;
    boolean flag = false;
    int cnt = 0;
    long start_time1 = System.currentTimeMillis();

    @Override  
    public void run() {  
        Socket skt = null;  
        Socket new_skt = null;
        long start_time = System.currentTimeMillis();
        try {  
            ServerSocket server = new ServerSocket(HOST_PORT);
            server.setSoTimeout(3000);
            System.out.println("Host port is " + HOST_PORT);
            while (true) {
                skt = server.accept();
                System.out.println("接收到Socket请求");
                //接收客户端文件  
                inputStream = new DataInputStream(skt.getInputStream());  
                PrintWriter writer = new PrintWriter(skt.getOutputStream());  
                String trueName = inputStream.readUTF();  
                fos = new FileOutputStream("/Users/Junjie/Desktop/Hotdog-Classification-master/edge/" + trueName);  
                byte[] inputByte = new byte[1024 * 8];  
                int length;  
                while ((length = inputStream.read(inputByte,0,inputByte.length)) > 0) {  
                    System.out.println("正在接收数据..." + length);  
                    flag = true;  
                    fos.write(inputByte, 0, length);  
                    fos.flush();  
                }  
                inputStream.close();
                fos.close(); 
              
                System.out.println("图片接收完成");  
                //Execute label_image and send back result  
                /*      
                skt = server.accept();  
                Process p = Runtime.getRuntime().exec("python3 label_image.py");
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                
                DataOutputStream dos = new DataOutputStream(skt.getOutputStream());
                while ((line = input.readLine()) != null) {
                    System.out.println(line);
                    dos.writeUTF(line);
                    dos.flush();
                }
                dos.close();
                */
                // 服务器发送消息  
                writer.println(flag);// 返回是否接收到图片  
                writer.flush();  
                writer.close();  
                skt.close();
            }  
        }catch (IOException e) {  
            e.printStackTrace();  
        }

        long end_time1 = System.currentTimeMillis();
        System.out.println("Start time1 is " );
        System.out.println(start_time1);
        System.out.println("End time1 is " );
        System.out.println(end_time1); 
        System.out.println("Time interval is");
        System.out.println((end_time1-start_time1)/200);

        String filePath = "/Users/Junjie/Desktop/Hotdog-Classification-master/edge/";
        String server_filePath = "/Users/Junjie/Desktop/Hotdog-Classification-master/server/";
        String[] fileName = listFileNames(filePath);
        String[] paths = new String[fileName.length]; 
        for (int i = 0;i < fileName.length;i++){
            paths[i] = filePath + fileName[i];
            //System.out.println(paths[i]);
        }
        
        //Offload some work to cloud based on calculation;Currently we assume all the workload of other servers are zero so the calculate prportion is 76%
        for (int i = 0;i<paths.length * 0.76;i++){
            try{
                Path temp = Files.move(Paths.get(paths[i]), Paths.get(server_filePath+fileName[i]));
                System.out.println("File number " + i );
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        //run client and upload
        try{
            ProcessBuilder pb = new ProcessBuilder("java", "Client");
            pb.redirectOutput(Redirect.INHERIT);
            pb.redirectError(Redirect.INHERIT);
            Process p = pb.start();
            p.waitFor();
        }catch (IOException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        } 
        

        try{
            ProcessBuilder pb = new ProcessBuilder("python3", "label_image_edge.py");
            pb.redirectOutput(Redirect.INHERIT);
            pb.redirectError(Redirect.INHERIT);
            Process p = pb.start();
            p.waitFor();
        }catch (IOException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        
        try{ 
            
            ProcessBuilder pb = new ProcessBuilder("python3", "label_image.py");
            pb.redirectOutput(Redirect.INHERIT);
            pb.redirectError(Redirect.INHERIT);
            ServerSocket new_server = new ServerSocket(NEW_HOST_PORT);
            System.out.println("before server");
            new_skt = new_server.accept();  
            System.out.println("after server");
            Process p = pb.start();
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            DataOutputStream dos = new DataOutputStream(new_skt.getOutputStream());
            while ((line = input.readLine()) != null) {
                System.out.println("I am here" + line);
                dos.writeUTF(line);
                dos.flush();
            }
            dos.close();
            new_skt.close();
            p.waitFor();
            

            ServerSocket new_server = new ServerSocket(NEW_HOST_PORT);
            //System.out.println("hello");
            new_skt = new_server.accept();
            System.out.println("hello");  
            Process p = Runtime.getRuntime().exec("python3 label_image.py");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            DataOutputStream dos = new DataOutputStream(new_skt.getOutputStream());
            while ((line = input.readLine()) != null) {
                //System.out.println("hello");
                dos.writeUTF(line);
                dos.flush();
            }
            dos.close();
            
        
        }catch (IOException e) {  
            e.printStackTrace();  
        }
        */

        long end_time = System.currentTimeMillis();
        System.out.println("End time is " );
        System.out.println(end_time - start_time);
        //System.out.println("Average time is ");
        //System.out.println((end_time-start_time)/100);

    }  
    public static String[] listFileNames(String dir) {
        File file = new File(dir);
        if (file.isDirectory()) {
            String names[] = file.list();
            return names;
        } else {
        // If it's not a directory
        return null;
        }   
    }
}

