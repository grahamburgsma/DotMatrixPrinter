import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Project: DotPrinter
 * Name: Graham Burgsma
 * Created on 30 March, 2016
 */
public class DotPrinterPC {

    private int[][] printMatrix;

    public DotPrinterPC() {
        ImageProcessor imageProcessor = new ImageProcessor("parrot.jpg");
        imageProcessor.cannyEdgeDetector();
        printMatrix = imageProcessor.imageToMatrix();

        sendDataExample();
    }

    public static void main(String[] args) {
        new DotPrinterPC();
    }


    private void sendPrintMatrix() throws IOException {
        NXTConnector conn = new NXTConnector();

        if (!conn.connectTo("usb://")) {
            System.err.println("No NXT found using USB");
            System.exit(1);
        }

        DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());

        //write dimensions
        outputStream.writeInt(printMatrix.length);
        outputStream.writeInt(printMatrix[0].length);

        for (int [] y : printMatrix) {
            for (int x : y) {
                outputStream.writeInt(x);
            }
        }

        outputStream.close();
        conn.close();
    }

    private void sendDataExample() {
        NXTConnector conn = new NXTConnector();

        conn.addLogListener(new NXTCommLogListener() {
                                public void logEvent(String message) {
                                    System.out.println("USBSend Log.listener: " + message);
                                }

                                public void logEvent(Throwable throwable) {
                                    System.out.println("USBSend Log.listener - stack trace: ");
                                    throwable.printStackTrace();
                                }
                            }
        );

        if (!conn.connectTo("usb://")) {
            System.err.println("No NXT found using USB");
            System.exit(1);
        }

        DataInputStream inDat = new DataInputStream(conn.getInputStream());
        DataOutputStream outDat = new DataOutputStream(conn.getOutputStream());

        int x = 0;
        for (int i = 0; i < 100; i++) {
            try {
                outDat.writeInt(i);
                outDat.flush();

            } catch (IOException ioe) {
                System.err.println("IO Exception writing bytes");
            }

            try {
                x = inDat.readInt();
            } catch (IOException ioe) {
                System.err.println("IO Exception reading reply");
            }
            System.out.println("Sent " + i + " Received " + x);
        }

        try {
            inDat.close();
            outDat.close();
            System.out.println("Closed data streams");
        } catch (IOException ioe) {
            System.err.println("IO Exception Closing connection");
        }

        try {
            conn.close();
            System.out.println("Closed connection");
        } catch (IOException ioe) {
            System.err.println("IO Exception Closing connection");
        }
    }
}
