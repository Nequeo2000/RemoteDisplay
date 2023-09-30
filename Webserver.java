import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
public class Webserver {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("192.168.178.43",8000), 0);
        server.createContext("/", new MyHandler());
        server.createContext("/stream", new StreamHandler());
        
        server.setExecutor(null);
        server.start();
    }
    
    static class StreamHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            BufferedImage img = DeviceInteraction.getScreenshot();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            byte[] bytes = baos.toByteArray();

            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    public static String getFrontPage(){
        try{
            Scanner scanner = new Scanner(new FileReader(new File("./index.html")));
            String str = "";
            while(scanner.hasNextLine())
                str += scanner.nextLine();
            return str;
        }catch(Exception e){
            System.out.println(e);
            return "";
        }
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = Webserver.getFrontPage();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}