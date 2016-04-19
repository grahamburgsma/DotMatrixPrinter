import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Project: DotPrinter
 * Name: Graham Burgsma
 * Created on 30 March, 2016
 */

public class ImageProcessor {

    public static final int[] palette = {Color.white.getRGB(), Color.red.getRGB(), Color.green.getRGB(), Color.blue.getRGB(), Color.black.getRGB()};
    private int MAX_PRINT_WIDTH = 5;
    private BufferedImage originalImage, edgeImage;
    private String imageName;
    private int[][] printMatrix;

    public ImageProcessor(String imageName) {
        this.imageName = imageName;

        MAX_PRINT_WIDTH = (Globals.MAX_SLIDER_DISTANCE / Globals.PRINT_X_SPACING) - (Globals.SLIDER_START_DISTANCE / Globals.PRINT_X_SPACING);

        try {
            originalImage = ImageIO.read(new File("images/" + imageName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cannyEdgeDetector() {
        CannyEdgeDetector cannyEdgeDetector = new CannyEdgeDetector();
        cannyEdgeDetector.setLowThreshold(0.5f);
        cannyEdgeDetector.setHighThreshold(1f);
        cannyEdgeDetector.setSourceImage(originalImage);
        cannyEdgeDetector.process();

        edgeImage = cannyEdgeDetector.getEdgesImage();
    }

    public int[][] imageToMatrix() {
        int height = edgeImage.getHeight() / (edgeImage.getWidth() / MAX_PRINT_WIDTH);

        Image imageEdge = edgeImage.getScaledInstance(MAX_PRINT_WIDTH, height, Image.SCALE_AREA_AVERAGING);
        Image imageOriginal = originalImage.getScaledInstance(MAX_PRINT_WIDTH, height, Image.SCALE_AREA_AVERAGING);

        BufferedImage resizedImage = toBufferedImage(imageEdge);
        BufferedImage resizedImageOriginal = toBufferedImage(imageOriginal);

        int[][] imageMatrix = new int[resizedImage.getHeight()][resizedImage.getWidth()];

        for (int y = 0; y < resizedImage.getHeight(); y++) {
            for (int x = 0; x < resizedImage.getWidth(); x++) {
//                if (getRed(resizedImage.getRGB(x, y)) + getGreen(resizedImage.getRGB(x, y)) + getBlue(resizedImage.getRGB(x, y)) > 0) {
                int minDistance = Integer.MAX_VALUE;
                int closestColour = 0;

                for (int i = 0; i < palette.length; i++) {
                    int distance = getDistance(resizedImageOriginal.getRGB(x, y), palette[i]);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestColour = i;
                    }
                    }
                if (closestColour == 1 || closestColour == 2 || closestColour == 3)
                    imageMatrix[y][x] = 0;
                else
                    imageMatrix[y][x] = closestColour;
//                } else {
//                    imageMatrix[y][x] = 0;
//                }
            }
        }

        displayImage(resizedImageOriginal);

        printMatrix = imageMatrix;
        return imageMatrix;
    }

    private int getDistance(int color1, int color2) {
        return ((int) (Math.pow(getRed(color2) - getRed(color1), 2) + Math.pow(getGreen(color2) - getGreen(color1), 2) + Math.pow(getBlue(color2) - getBlue(color1), 2)));
    }

    private int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    private int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    private int getBlue(int rgb) {
        return rgb & 0xFF;
    }

    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public void saveMatrixToFile() {
        System.out.println(printMatrix.length);
        System.out.println(printMatrix[0].length);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("matrix.txt")));

            for (int y = 0; y < printMatrix.length; y++) {
                for (int x = 0; x < printMatrix[0].length; x++) {
                    writer.write(String.valueOf(printMatrix[y][x]));
                    writer.write(',');
                }
                writer.write("\n");
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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