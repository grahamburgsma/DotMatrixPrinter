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
        ImageProcessor imageProcessor = new ImageProcessor("superman.jpg");

//        imageProcessor.cannyEdgeDetector();
        imageProcessor.sobelEdgeDetector();

        printMatrix = imageProcessor.imageToMatrix();
        imageProcessor.saveMatrixToFile();

        try {
            sendPrintMatrix();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        DataInputStream inputStream = new DataInputStream(conn.getInputStream());

        for (int i = 1; i < Globals.paletteSize; i++) {

            inputStream.readBoolean();
            //write dimensions
            outputStream.writeInt(printMatrix.length);
            outputStream.writeInt(printMatrix[0].length);
            outputStream.flush();
            inputStream.readBoolean();

            for (int[] y : printMatrix) {
                for (int x : y) {
                    System.out.print(x);

                    outputStream.writeInt(x);
                }
                System.out.println("");
                outputStream.flush();
                inputStream.readBoolean();
            }
        }
        outputStream.close();
        conn.close();
    }
}
