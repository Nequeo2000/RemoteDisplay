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
        HttpServer server = HttpServer.create(new InetSocketAddress("192.168.178.43",8080), 0);
        server.createContext("/", new frontPage());
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

            String userAgent = t.getRequestHeaders().get("user-agent").get(0).toLowerCase();
            //System.out.println(userAgent);
            t.getResponseHeaders().set("cache-control", "no-cache, must-revalidate, no-store");
            t.getResponseHeaders().set("Accept-Encoding", "identity");
            t.getResponseHeaders().set("Content-Type", "image/png");
            if( userAgent.contains("firefox") ){
                t.sendResponseHeaders(200, bytes.length*2);
            } else {
                t.sendResponseHeaders(200, bytes.length);
            }

            System.out.println(bytes.length);
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
                str += scanner.nextLine() +"\n";
            return str;
        }catch(Exception e){
            System.out.println(e);
            return "";
        }
    }

    static class frontPage implements HttpHandler {
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