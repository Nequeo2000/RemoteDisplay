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
        // check for given input
        String ipAddress = "127.0.0.1";
        int port = 8080;
        if(args.length > 0)
            ipAddress = args[0];
        if(args.length == 2)
            port = Integer.parseInt(args[1]);

        // initialize webserver
        HttpServer server = HttpServer.create(new InetSocketAddress(ipAddress,port), 0);
        System.out.println("Server running at : "+ipAddress+":"+port);
        server.createContext("/", new FrontPageHandler());
        server.createContext("/stream", new StreamHandler());

        server.setExecutor(null);
        server.start();
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

    static class FrontPageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = Webserver.getFrontPage();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class StreamHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            BufferedImage img = DeviceInteraction.getScreenshot();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", baos);
            byte[] bytes = baos.toByteArray();
            baos.close();

            // configure http respnse header
            String userAgent = t.getRequestHeaders().get("user-agent").get(0).toLowerCase();
            t.getResponseHeaders().set("cache-control", "no-cache, must-revalidate, no-store");
            t.getResponseHeaders().set("Accept-Encoding", "identity");
            t.getResponseHeaders().set("Content-Type", "image/jpeg");
            if( userAgent.contains("firefox") ){
                t.sendResponseHeaders(200, bytes.length*2);
            } else {
                t.sendResponseHeaders(200, bytes.length);
            }

            // write response body and send response
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
}
