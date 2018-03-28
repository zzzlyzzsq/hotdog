import java.io.*;
import java.net.*;
import java.io.File;

import java.lang.*;
/*
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        String host = "127.0.0.1";

        socket = new Socket(host, 4444);

        File file = new File("/Users/Junjie/Desktop/hotdog.jpg");
        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();

        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.close();
        in.close();
        socket.close();
    }
}
*/
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        String host = "128.46.115.20";
        String filePath = "/Users/Junjie/Desktop/Hotdog-Classification-master/server/";
        String[] fileName = listFileNames(filePath);
        String[] paths = new String[fileName.length]; 
        for (int i = 0;i < fileName.length;i++){
            paths[i] = filePath + fileName[i];
            System.out.println(paths[i]);
        }
        
        long start_time = System.currentTimeMillis();
        File file;
        for (int j = 0; j < paths.length; j++){
            socket = new Socket(host, 4444);
            file = new File(paths[j]);
            // Get the size of the file
            long length = file.length();
            System.out.println(length);

            byte[] bytes = new byte[16 * 1024];
            InputStream in = new FileInputStream(file);
            OutputStream out = socket.getOutputStream();

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }

            out.close();
            in.close();
            socket.close();
        }
        long end_time = System.currentTimeMillis();
        System.out.println("Start time is " );
        System.out.println(start_time);
        System.out.println("End time is " );
        System.out.println(end_time); 
        
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
