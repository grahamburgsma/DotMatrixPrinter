import lejos.nxt.*;
import lejos.util.Delay;

/**
 * Project: DotPrinter
 * Name: Graham Burgsma
 * Created on 30 March, 2016
 */
public class DotPrinter {

    private int[][] printMatrix;

    public DotPrinter() {
//        ImageProcessor imageProcessor = new ImageProcessor("parrot.jpg");
//        imageProcessor.cannyEdgeDetector();
//        printMatrix = imageProcessor.imageToMatrix();

        LCD.drawString(" - Dot Matrix Printer - ", 0, 0);
        LCD.drawString("Press enter to start", 0, 0);
        Button.waitForAnyPress();

        setup();

        while(true){
            drawDot();
            incrementPaperFeed();
        }
    }

    private void setup() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                feedPaperIn();
            }
        }).run();
        new Thread(new Runnable() {
            @Override
            public void run() {
                resetSlider();
            }
        }).run();
        new Thread(new Runnable() {
            @Override
            public void run() {
                resetPen();
            }
        }).run();
    }

    private void resetSlider() {
        TouchSensor touch = new TouchSensor(SensorPort.S2);
        while (!touch.isPressed()) {
            Motor.B.backward();
        }
        Motor.B.stop();
        Motor.B.resetTachoCount();
    }

    private void resetPen() {
        Motor.C.setStallThreshold(50, 300);

        while (!Motor.C.isStalled())
            Motor.C.forward();
        Motor.C.stop();
    }

    private void drawDot(){
        Motor.C.setSpeed(300);

        Motor.C.backward();
        Delay.msDelay(180);
        Motor.C.stop();
        Delay.msDelay(100);
        Motor.C.forward();
        Delay.msDelay(180);
        Motor.C.stop();
    }

    private void incrementPaperFeed(){
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
