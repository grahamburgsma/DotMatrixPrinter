import lejos.nxt.*;
import lejos.nxt.comm.USB;
import lejos.nxt.comm.USBConnection;
import lejos.util.Delay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * Project: DotPrinter
 * Name: Graham Burgsma
 * Created on 30 March, 2016
 */
public class DotPrinterNXT {

    private int[][] printMatrix;

    public DotPrinterNXT() {
        LCD.drawString("Dot Matrix Printer", 0, 0);

        try {
            connectToPCExample();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setup();

        printMatrix();
    }

    public static void main(String[] args) {
        new DotPrinterNXT();
    }

    private void readPrintMatrix() throws IOException {
        USBConnection conn = USB.waitForConnection();

        DataInputStream inputStream = conn.openDataInputStream();

        printMatrix = new int[inputStream.readInt()][inputStream.readInt()];

        for (int y = 0; y < printMatrix.length; y++) {
            for (int x = 0; x < printMatrix[0].length; x++) {
                printMatrix[y][x] = inputStream.readInt();
            }
        }

        inputStream.close();
        conn.close();
    }

    private void connectToPCExample() throws IOException {
        LCD.drawString("waiting", 0, 0);
        USBConnection conn = USB.waitForConnection();
        DataOutputStream dOut = conn.openDataOutputStream();
        DataInputStream dIn = conn.openDataInputStream();

        while (true) {
            int b;
            try {
                b = dIn.readInt();
            } catch (EOFException e) {
                break;
            }
            dOut.writeInt(-b);
            dOut.flush();
            LCD.drawInt(b, 8, 0, 1);
        }
        dOut.close();
        dIn.close();
        conn.close();
    }

    private void setup() {
        Motor.C.setSpeed(500);

        feedPaperIn();
        resetSlider();
        resetPen();
    }

    private void resetSlider() {
        TouchSensor touch = new TouchSensor(SensorPort.S1);
        while (!touch.isPressed()) {
            Motor.B.backward();
            Delay.msDelay(100);
        }
        Motor.B.stop();

        Motor.B.resetTachoCount();
    }

    private void resetPen() {
        Motor.C.setStallThreshold(50, 300);
        Motor.C.setSpeed(50);

        while (!Motor.C.isStalled()) {
            Motor.C.forward();
            Delay.msDelay(100);
        }
        Motor.C.stop();
    }

    private void printMatrix() {
        int widthFactor = 10;

        for (int[] y : printMatrix) {
            for (int i = 0; i < y.length; i++) {
                if (y[i] == 1) {
                    Motor.B.rotateTo(i * widthFactor, false);
                    drawDot();
                }
            }
            resetSlider();
            incrementPaperFeed();
        }
    }

    private void drawDot() {
        Motor.C.setSpeed(300);

        Motor.C.backward();
        Delay.msDelay(230);
        Motor.C.stop();
        Delay.msDelay(100);
        Motor.C.forward();
        Delay.msDelay(230);
        Motor.C.stop();
    }

    private void incrementPaperFeed() {
        Motor.A.setSpeed(50);
        Motor.A.rotate(25, false);
    }

    private void feedPaperIn() {
        new ColorSensor(SensorPort.S3);
        while (SensorPort.S3.readValue() <= 1) {
            Motor.A.forward();
        }
        Motor.A.stop();
    }
}
