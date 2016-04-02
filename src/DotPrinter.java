/**
 * Project: DotPrinter
 * Name: Graham Burgsma
 * Created on 30 March, 2016
 */
public class DotPrinter {

    public DotPrinter() {
        ImageProcessor imageProcessor = new ImageProcessor("test2.jpg");
        imageProcessor.cannyEdgeDetector();
        imageProcessor.imageToMatrix();
    }

    public static void main(String[] args) {
        new DotPrinter();
    }
}
