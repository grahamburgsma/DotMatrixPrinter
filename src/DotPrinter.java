import lejos.nxt.*;
import lejos.util.Delay;

/**
 * Project: DotPrinter
 * Name: Graham Burgsma
 * Created on 30 March, 2016
 */
public class DotPrinter {

    public DotPrinter() {
//        ImageProcessor imageProcessor = new ImageProcessor("parrot.jpg");
//        imageProcessor.cannyEdgeDetector();
//        imageProcessor.imageToMatrix();

        LCD.drawString(" - Dot Matrix Printer - ", 0, 0);
        LCD.drawString("Press enter to start", 0, 0);
        Button.waitForAnyPress();

        feedPaperIn();

        while(true){
            drawDot();
            incrementPaperForward();
        }
    }

    public void drawDot(){
        Motor.C.setSpeed(300);

        Motor.C.backward();
        Delay.msDelay(180);
        Motor.C.stop();
        Delay.msDelay(100);
        Motor.C.forward();
        Delay.msDelay(180);
        Motor.C.stop();
    }

    public void incrementPaperForward(){
        Motor.A.setSpeed(50);
        Motor.A.rotate(45,false);
    }

    private void feedPaperIn(){
        Motor.A.setSpeed(300);
        while (SensorPort.S3.readValue() <= 1){
            Motor.A.forward();
        }
        Motor.A.stop();
    }

    public static void main(String[] args) {
        new DotPrinter();
    }
}
