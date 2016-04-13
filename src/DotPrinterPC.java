import lejos.pc.comm.NXTConnector;

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
}
