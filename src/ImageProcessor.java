import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Project: DotPrinter
 * Name: Graham Burgsma
 * Created on 30 March, 2016
 */

public class ImageProcessor {

    private static final int MAX_PRINT_WIDTH = 50;
    private static final int PRINT_THESHOLD = 100;
    private BufferedImage originalImage, edgeImage;
    private String imageName;

    public ImageProcessor(String imageName) {
        this.imageName = imageName;

        try {
            originalImage = ImageIO.read(new File("images/" + imageName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cannyEdgeDetector() {
        CannyEdgeDetector cannyEdgeDetector = new CannyEdgeDetector();
        cannyEdgeDetector.setSourceImage(originalImage);
        cannyEdgeDetector.process();

        edgeImage = cannyEdgeDetector.getEdgesImage();
    }

    public int[][] imageToMatrix() {
        BufferedImage resizedImage = Scalr.resize(edgeImage, MAX_PRINT_WIDTH);

        int[][] imageMatrix = new int[resizedImage.getHeight()][resizedImage.getWidth()];

        for (int y = 0; y < resizedImage.getHeight(); y++) {
            for (int x = 0; x < resizedImage.getWidth(); x++) {
                Color color = new Color(resizedImage.getRGB(x, y));
                imageMatrix[y][x] = color.getRed() > PRINT_THESHOLD ? 1 : 0;
            }
        }

        displayImage(resizedImage);

        return imageMatrix;
    }

    public void displayImage(BufferedImage image) {
        File outputfile = new File("images/" + imageName.substring(0, imageName.length() - 4) + "Print.jpg");
        try {
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}