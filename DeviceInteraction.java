import java.awt.Toolkit;
import java.awt.Robot;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class DeviceInteraction
{
    static Robot robot = null;

    public static BufferedImage getScreenshot(){
        try{
            if(robot == null)
                robot = new Robot();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage img = robot.createScreenCapture(new Rectangle(screenSize));
            return img;
            //File f = new File("test.png");
            //ImageIO.write(img, "png", f);

        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
}