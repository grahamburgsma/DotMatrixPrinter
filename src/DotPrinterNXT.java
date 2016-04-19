import lejos.nxt.*;
import lejos.nxt.comm.USB;
import lejos.nxt.comm.USBConnection;
import lejos.util.Delay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Project: DotPrinter
 * Name: Graham Burgsma
 * Created on 30 March, 2016
 */

public class DotPrinterNXT {

    private static final int PEN_DOWN_ROTATION = -48;
    private static final int PEN_UP_ROTATION = -38;
    private static final int PAPER_INCREMENT = 18;
    private int colourIteration = 1;
    private int height;
    private int width;
    private USBConnection conn;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public DotPrinterNXT() {
        setup();
        changePen();

        conn = USB.waitForConnection();
        inputStream = conn.openDataInputStream();
        outputStream = conn.openDataOutputStream();

        for (int i = 1; i < Globals.paletteSize; i++) {
            Motor.B.rotateTo(0, false);
            Motor.C.rotateTo(PEN_UP_ROTATION, false);
            Delay.msDelay(300);

            try {
                printMatrix();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (colourIteration == Globals.paletteSize - 1) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                conn.close();
                drawBorder();
                ejectPaper();
            } else {
                reversePaperFeed();
                colourIteration++;
                LCD.clear(0);

                changePen();
            }
        }
    }

    public static void main(String[] args) {
        new DotPrinterNXT();
    }

    private void changePen() {
        if (colourIteration == 1) {
            Sound.playSample(new File("robotred.wav"));
            LCD.drawString("Insert RED", 0, 0);
        } else if (colourIteration == 2) {
            Sound.playSample(new File("robotgreen.wav"));
            LCD.drawString("Insert GREEN", 0, 0);
        } else if (colourIteration == 3) {
            Sound.playSample(new File("robotblue.wav"));
            LCD.drawString("Insert BLUE", 0, 0);
        } else if (colourIteration == 4) {
            Sound.playSample(new File("robotblack.wav"));
            LCD.drawString("Insert BLACK", 0, 0);
        }
        LCD.drawString("Press Button", 0, 1);
        Motor.C.rotateTo(0, true);
        Motor.B.rotateTo(Globals.MAX_SLIDER_DISTANCE / 2, true);
        Button.waitForAnyPress();
        LCD.clear();
    }

    private void setup() {
        Motor.C.setSpeed(500);
        resetSlider();
        resetPen();
        Motor.B.rotateTo(1300, false);
        feedPaperIn();

        Motor.A.resetTachoCount();
    }

    private void drawBorder() {
        Motor.A.setSpeed(150);
        Motor.A.rotateTo(0, false);

        Motor.B.rotateTo(0, false);
        Motor.C.rotateTo(PEN_DOWN_ROTATION, false);
        Motor.B.rotateTo((width * Globals.PRINT_X_SPACING) + Globals.SLIDER_START_DISTANCE + Globals.PRINT_X_SPACING, false);

        Motor.A.rotateTo((height * PAPER_INCREMENT) + PAPER_INCREMENT, false);
        Motor.B.rotateTo(0, false);
        Motor.A.rotateTo(0, false);

        Motor.C.rotateTo(0, false);
    }

    private void resetSlider() {
        TouchSensor touch = new TouchSensor(SensorPort.S1);
        while (!touch.isPressed()) {
            Motor.B.backward();
            Delay.msDelay(50);
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
        Motor.C.resetTachoCount();
        Motor.C.stop();
    }

    private void printMatrix() throws IOException {
        outputStream.writeBoolean(true);
        outputStream.flush();
        height = inputStream.readInt();
        width = inputStream.readInt();
        outputStream.writeBoolean(true);
        outputStream.flush();

        LCD.drawString(colourIteration == 1 ? "RED" : colourIteration == 2 ? "GREEN" : colourIteration == 3 ? "BLUE" : "BLACK", 0, 3);

        for (int y = 0; y < height; y++) {
            LCD.drawString("Line: " + (y + 1) + "/" + height, 0, 4);
            for (int x = 0; x < width; x++) {
                if (inputStream.readInt() == colourIteration) {
                    Motor.B.rotateTo((x * Globals.PRINT_X_SPACING) + Globals.SLIDER_START_DISTANCE, false);
                    drawDot();
                }
            }
            outputStream.writeInt(0); //Give ready symbol
            outputStream.flush();

            resetSlider();
            incrementPaperFeed();
        }
    }

    private void drawDot() {
        Motor.C.setSpeed(230); //was 200

        Motor.C.rotateTo(PEN_DOWN_ROTATION);
        Motor.C.rotateTo(PEN_UP_ROTATION);
    }

    private void incrementPaperFeed() {
        Motor.A.setSpeed(50);
        Motor.A.rotate(PAPER_INCREMENT, false);
    }

    private void reversePaperFeed() {
        Motor.A.rotateTo(0, false);
    }

    private void feedPaperIn() {
        new ColorSensor(SensorPort.S3);
        while (SensorPort.S3.readValue() <= 1) {
            Motor.A.forward();
        }
        Motor.A.stop();
    }

    private void ejectPaper() {
        Motor.A.setSpeed(400);
        LCD.clear();
        LCD.drawString("Press to STOP", 0, 0);

        while (!Button.ENTER.isDown()) {
            Motor.A.forward();
            Delay.msDelay(100);
        }
        Motor.A.stop();
    }
}
