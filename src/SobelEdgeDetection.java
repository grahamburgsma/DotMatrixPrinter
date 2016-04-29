import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

/**
 * Project: DotMatrixPrinter
 * Name: grahamburgsma
 * Created on 29 April, 2016
 */
public class SobelEdgeDetection {

    BufferedImage image;

    public SobelEdgeDetection(BufferedImage image) {
        this.image = image;
    }

    BufferedImage process() {
        BufferedImage greyImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage edgeImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        colorConvert.filter(image, greyImage);

        int count, averageVertical, averageHorizontal;
        int arrayVertical[] = {1, 2, 1, 0, 0, 0, -1, -2, -1};
        int arrayHorizontal[] = {1, 0, -1, 2, 0, -2, 1, 0, -1};

        for (int x = 0; x < greyImage.getWidth(); x++) {
            for (int y = 0; y < greyImage.getHeight(); y++) {
                count = 0;
                averageVertical = 0;
                averageHorizontal = 0;
                for (int i = x - 1; i <= x + 1; i++) {
                    for (int j = y - 1; j <= y + 1; j++) {
                        if (j > 0 && j < greyImage.getHeight() && i > 0 && i < greyImage.getWidth()) {
                            averageVertical += getRed(greyImage.getRGB(i, j)) * arrayVertical[count];
                            averageHorizontal += getRed(greyImage.getRGB(i, j)) * arrayHorizontal[count];
                        }
                        count++;
                    }
                }

                averageVertical = averageVertical < 0 ? 0 : averageVertical > 255 ? 255 : averageVertical;
                averageHorizontal = averageHorizontal < 0 ? 0 : averageHorizontal > 255 ? 255 : averageHorizontal;

                int newColor = (int) Math.sqrt(Math.pow(averageVertical, 2) + Math.pow(averageHorizontal, 2));

                newColor = newColor < 0 ? 0 : newColor > 255 ? 255 : newColor;

                edgeImage.setRGB(x, y, new Color(newColor, newColor, newColor).getRGB());
            }
        }

        return edgeImage;
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
}
