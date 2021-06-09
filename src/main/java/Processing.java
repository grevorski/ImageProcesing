import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

public class Processing {
    private static final Scanner sc = new Scanner(System.in);
    private final BufferedImage inputImage;
            static double[][] baseKernel = { { -3, -3,  5, },
        { -3,  0,  5, },
        { -3, -3,  5, }, };
    static double[][][] kernelito = {{{-3.0, -3.0, 5.0}, {-3.0, 0.0, 5.0}, {-3.0, -3.0, 5.0}}, {{-3.0, -3.0, -3.0}, {-3.0, 0.0, 5.0}, {-3.0, 5.0, 5.0}}, {{-3.0, -3.0, -3.0}, {-3.0, 0.0, -3.0}, {5.0, 5.0, 5.0}}, {{-3.0,-3.0,-3.0}, {5.0,0.0,-3.0}, {5.0,5.0,-3}}, {{5.0,-3.0,-3.0}, {5.0,0.0,-3.0}, {5.0,-3.0,-3.0}}, {{5.0,5.0,-3.0}, {5.0,0.0,-3.0}, {-3.0,-3.0,-3.0}}, {{5.0,5.0,5.0}, {-3.0,0.0,-3.0}, {-3.0,-3.0,-3.0}}, {{-3.0,5.0,5.0}, {-3.0,0.0,5.0}, {-3.0,-3.0,-3.0}}};
    int maskCorrectX, maskCorrectY;
    double MY_PI = 3.1415926535898;
    static int[][] marker;
    public Processing(String imagePatch) throws IOException {
        File f = new File(imagePatch);
        inputImage = ImageIO.read(f);
    }

    private static class Centroid {
        private double X;
        private double Y;
        private int Count;
    }

    private static class BoundingBox {
        private double LeftTopX = -1;
        private double LeftTopY = -1;
        private double RightBottomX = -1;
        private double RightBottomY = -1;
        public double Height = RightBottomY - LeftTopY + 1;
        public double Width = RightBottomX - LeftTopX + 1;

        public void setLeftTopX(double leftTopX) {
            LeftTopX = leftTopX;
        }

        public void setLeftTopY(double leftTopY) {
            LeftTopY = leftTopY;
        }

        public void setRightBottomX(double rightBottomX) {
            RightBottomX = rightBottomX;
        }

        public void setRightBottomY(double rightBottomY) {
            RightBottomY = rightBottomY;
        }

        public double getLeftTopX() {
            return LeftTopX;
        }

        public double getLeftTopY() {
            return LeftTopY;
        }

        public double getRightBottomX() {
            return RightBottomX;
        }

        public double getRightBottomY() {
            return RightBottomY;
        }
    }


    public void regionProps() {
        Centroid[] centroids = new Centroid[256];
        BoundingBox[] boundingBoxes = new BoundingBox[256];
        double[] equivDiameters = new double[256];


        for (int i = 0; i < 256; i++) {
            centroids[i] = new Centroid();
            boundingBoxes[i] = new BoundingBox();
        }

        for (int x = 0; x < inputImage.getWidth(); x++) {
            for (int y = 0; y < inputImage.getHeight(); y++) {
                Color c = new Color(inputImage.getRGB(x, y));
                int id = c.getGreen();
                centroids[id].X += x + 1;
                centroids[id].Y += y + 1;
                centroids[id].Count++;

                if (boundingBoxes[id].getLeftTopX() == -1 || x < boundingBoxes[id].getLeftTopX())
                    boundingBoxes[id].setLeftTopX(x + 1);
                if ((boundingBoxes[id].getLeftTopY() == -1 || y < boundingBoxes[id].getLeftTopY()))
                    boundingBoxes[id].setLeftTopY(y + 1);
                if (boundingBoxes[id].getRightBottomX() == -1 || x > boundingBoxes[id].getRightBottomX())
                    boundingBoxes[id].setRightBottomX(x + 1);
                if (boundingBoxes[id].getRightBottomY() == -1 || y > boundingBoxes[id].getRightBottomY())
                    boundingBoxes[id].setRightBottomY(y + 1);

            }
        }

        for (int i = 0; i < 256; i++) {
            if (centroids[i].Count == 0) {
                centroids[i].X = -1;
                centroids[i].Y = -1;
                equivDiameters[i] = 0;
            } else {
                centroids[i].X /= centroids[i].Count;
                centroids[i].Y /= centroids[i].Count;
                equivDiameters[i] = Math.sqrt(4 * centroids[i].Count / Math.PI);
            }
        }


        try {
            FileWriter myWriter = new FileWriter("output.txt");
            myWriter.write("ID\t\tCentroid\t\tBounding Box\t\tEquivalent diameter\n");
            for (int i = 0; i < 256; i++) {

                myWriter.write(i + "\t[" + centroids[i].X + ",\t" + centroids[i].Y + "]\t[" + boundingBoxes[i].getLeftTopX() + ",\t" + boundingBoxes[i].getLeftTopY() + ",\t" + boundingBoxes[i].Height + "]\t\t" + equivDiameters[i] + "\n");
            }

            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }


    public void kirschFiltration() {

        System.out.println("Is image RGB(1) or mono(2)?");

        int input = sc.nextInt();
        BufferedImage result = convolutionFilter(inputImage,kernelito);
        if (input == 2) {
            File f = new File("kirsch.png");
            try {
                ImageIO.write(result, "png", f);

            } catch (IOException e) {
                System.out.println("Exception occured :" + e.getMessage());
            }
            System.out.println("Images were written succesfully.");
        }
        if (input == 1) {
            BufferedImage redBitmap = getOneChannelImage(result, 0);
            BufferedImage greenBitmap = getOneChannelImage(result, 1);
            BufferedImage blueBitmap = getOneChannelImage(result, 2);
            File r = null;
            File g = null;
            File b = null;
            try {
                r = new File("R.png");
                g = new File("G.png");
                b = new File("B.png");
                ImageIO.write(redBitmap, "png", r);
                ImageIO.write(greenBitmap, "png", g);
                ImageIO.write(blueBitmap, "png", b);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    private BufferedImage getOneChannelImage(BufferedImage rgbImage, int channel) {
        ColorModel cm = rgbImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = rgbImage.copyData(null);
        BufferedImage resultImage = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        for (int y = 0; y < rgbImage.getHeight(); y++) {
            for (int x = 0; x < rgbImage.getWidth(); x++) {
                Color c = new Color(rgbImage.getRGB(x, y));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                switch (channel) {
                    case 0:
                        Color redColor = new Color(red, red, red);
                        int r = redColor.getRGB();
                        resultImage.setRGB(x, y, r);
                        break;
                    case 1:
                        Color greenColor = new Color(green, green, green);
                        int g = greenColor.getRGB();
                        resultImage.setRGB(x, y, g);
                        break;
                    case 2:
                        Color blueColor = new Color(blue, blue, blue);
                        int b = blueColor.getRGB();
                        resultImage.setRGB(x, y, b);
                        break;

                }
            }
        }
        return resultImage;
    }

    private BufferedImage convolutionFilter(BufferedImage sourceImage, double[][][] filterMatrix){
        ColorModel cm = sourceImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = sourceImage.copyData(null);
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        int offset = 3;
        int offSetCenter = (offset - 1)/2;

        for (int y = 0; y < sourceImage.getHeight(); y++)
        {
            for (int x = 0; x < sourceImage.getWidth(); x++)
            {
                double blue = 0;
                double green = 0;
                double red = 0;

                for (int compass = 0; compass < 8; compass++)
                {
                    double blueCompass = 0;
                    double greenCompass = 0;
                    double redCompass = 0;

                    for (int yFilter = -offSetCenter; yFilter <= offSetCenter; yFilter++)
                    {
                        for (int xFilter = -offSetCenter; xFilter <= offSetCenter; xFilter++)
                        {
                            int pixelX = x + xFilter,
                                    pixelY = y + yFilter;
                            if (pixelX < 0)
                            {
                                pixelX = sourceImage.getWidth() + pixelX;
                            }
                            else if (pixelX >= sourceImage.getWidth())
                            {
                                pixelX -= sourceImage.getWidth();
                            }

                            if (pixelY < 0)
                            {
                                pixelY = sourceImage.getHeight() + pixelY;
                            }
                            else if (pixelY >= sourceImage.getHeight())
                            {
                                pixelY -= sourceImage.getHeight();
                            }

                            Color c = new Color(sourceImage.getRGB(pixelX, pixelY));
                            var filterMultiplication = filterMatrix[compass][yFilter + offSetCenter][ xFilter + offSetCenter];

                            blueCompass += c.getBlue() * filterMultiplication;
                            greenCompass += c.getGreen() * filterMultiplication;
                            redCompass += c.getRed() * filterMultiplication;
                        }
                    }
                    blue = Math.max(blue, blueCompass);
                    green = Math.max(green, greenCompass);
                    red = Math.max(red, redCompass);
                }

                blue = Math.min(Math.max(0, blue), 255);
                green = Math.min(Math.max(0, green), 255);
                red = Math.min(Math.max(0, red), 255);
                Color newColor = new Color((int)red, (int)green, (int)blue);
                result.setRGB(x,y,newColor.getRGB());
            }
        }

        return result;
    }

    public void ClosingLinear() throws IOException {
        BufferedImage inputImg;
        File f = new File("D:\\DEV\\GitHub\\ImageProcesing\\src\\main\\resources\\cm.tif");
        inputImg = ImageIO.read(f);


        BufferedImage outputImage = new BufferedImage(inputImg.getWidth(), inputImg.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        BufferedImage tempImage = new BufferedImage(inputImg.getWidth(), inputImg.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        int SE_length, SE_angle;
        System.out.println("What is the length of the structuring element (SE)? ");
        SE_length = sc.nextInt();
        System.out.println("What is the angle of the SE? ");
        SE_angle = sc.nextInt();

        int[][] mask = createLinearSE(SE_length, SE_angle);

        dilation(inputImg, tempImage, mask);
        erosion(tempImage, outputImage, mask);

        File file = new File("Closing.png");
        try {
            ImageIO.write(outputImage, "png", file);

        } catch (IOException e) {
            System.out.println("Exception occured :" + e.getMessage());
        }
        System.out.println("Images were written succesfully.");
    }

    private void dilation(BufferedImage inputImage, BufferedImage outputImage, int[][] se) {

        for (int kz = se.length / 2 + 1; kz < inputImage.getHeight() - se.length / 2 - 1; kz++) {
            for (int kx = se.length / 2 + 1; kx < inputImage.getWidth() - se.length / 2 - 1; kx++) {
                int max = GetMaxNeighborhoodVal(inputImage, se, kx, kz);
                Color color = new Color(max, max, max);
                int rgb = color.getRGB();
                outputImage.setRGB(kx,kz,rgb);
            }
        }
    }

    private int GetMaxNeighborhoodVal(BufferedImage inputImage,int[][] se , int kx, int kz) {
        int max = 0, temp;
        int posX, posY;

        for (int i = 0; i < se.length; i++) {
            posX = kx + i - maskCorrectX;
            for (int j = 0; j < se[i].length; j++) {
                posY = kz + j - maskCorrectY;
                if (se[i][j] == 1 && isInImage(inputImage, posX, posY)) {
                    Color color = new Color(inputImage.getRGB(posX,posY));
                    temp = color.getRed();
                    if (temp > max)
                        max = temp;
                }
            }
        }
        return max;
    }

    private void erosion(BufferedImage inputImage, BufferedImage outputImage, int[][] se) {
        for (int kz = se.length / 2 + 1; kz < inputImage.getHeight() - se.length / 2 - 1; kz++) {
            for (int kx = se.length/ 2 + 1; kx < inputImage.getWidth() - se.length / 2 - 1; kx++) {

                int min = GetMinNeighborhoodVal(inputImage, se, kx, kz);
                Color color = new Color(min, min, min);
                int rgb = color.getRGB();
                outputImage.setRGB(kx,kz,rgb);

            }
        }
    }

    private int GetMinNeighborhoodVal(BufferedImage inputImage,int[][] se , int kx, int kz) {
        int min = 255, temp;
        int posX, posY;
        for (int i = 0; i < se.length; i++) {
            posX = kx + i - maskCorrectX;
            for (int j = 0; j < se[i].length; j++) {
                posY = kz + j - maskCorrectY;
                if (se[i][j] == 1 && isInImage(inputImage, posX, posY)) {
                    Color color = new Color(inputImage.getRGB(posX,posY));
                    temp = color.getRed();

                    if (temp < min)
                        min = temp;
                }
            }
        }
        return min;
    }


    private Boolean isInImage(BufferedImage inputImage, int x, int y) {
        return x >= 0 && y >= 0 && x < inputImage.getWidth() && y < inputImage.getHeight();
    }

    private static class Pair{
        int first;
        int second;

        public Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }
    }

    private int[][] createLinearSE(int length, int userAngle) {
        if (length <= 1) {
            System.out.println("Length must be greather than 1!");
            System.exit(1);
        }
        int[][] mask = new int[length-1][length-1];
        double angle = (userAngle % 180) * MY_PI / 180;
        int x = (int) Math.round((float)(length - 1) / 2 * Math.cos(angle));
        int y = (int) Math.round((float)(length - 1) / 2 * Math.sin(angle));

        // draw line algorithm
        ArrayList<Pair> points = bresenham(-x, -y, x, y);

        // apply points to mask
        for (Pair p : points) {
            mask[p.first + x][p.second + y] = 1;
        }

        maskCorrectX = x;
        maskCorrectY = y;
        return mask;
    }

// function with algorithm for line generation
    private ArrayList<Pair> bresenham(int x1, int y1, int x2, int y2)
    {
        ArrayList<Pair> pointList= new ArrayList<>();
        int m_new = 2 * (y2 - y1);
        int slope_error_new = m_new - (x2 - x1);
        for (int x = x1, y = y1; x <= x2; x++)
        {
            pointList.add(new Pair(x, y));
            slope_error_new += m_new;
            if (slope_error_new >= 0)
            {
                y++;
                slope_error_new -= 2 * (x2 - x1);
            }
        }
        return pointList;
    }


    void GeodeticDistanceMap(){

        int width = inputImage.getWidth(), height = inputImage.getHeight();
        int x1, y1;
        System.out.println("Based on dziury.bmp image\nEnter start coordinates (2 numbers) (260 82) (235 21)\n");
        x1=sc.nextInt();
        y1=sc.nextInt();

        if (x1 < 0 || y1 < 0 || x1 >= width || y1 >= width) {
            System.out.println("Bad numbers!");
            System.exit(1);
        }
        marker = new int[width][height];
        // create marker matrix
       // int[][] marker = new int[width][height];
        // Set marker initial point
        marker[x1][y1] = 1;

        // create SE
        int[][] SE = { {1,1,1},{1,1,1},{1,1,1} }; //ones3

        // start iteration
        int iter = 0, prevPixels = 1, currPixels = 0;

        while (currPixels - prevPixels != 0) {
            binaryDilation(SE, iter);
            prevPixels = currPixels;
            currPixels = ANDmask(inputImage);
            ++iter;
        }
        System.out.println("Iterations: " + iter);

        normalizeMarker();

        saveMarkerToFile();

    }

    void binaryDilation(int[][] se, int iter) {
        //int[][] marker = newMarker.clone();
        //std::copy(newMarker.begin(), newMarker.end(), std::back_inserter(marker));
        int[][] newMarker = marker.clone();

        int posX, posY;
        boolean flag;
        for (int kx = 3; kx < newMarker.length - 3; kx++) {
            for (int kz = 3; kz < newMarker[kx].length - 3; kz++) {
                if (newMarker[kx][kz] == 0) {
                    flag = false;
                    for (int i = 0; i < 3; i++) {
                        posX = kx + i - 1;
                        for (int j = 0; j < 3; j++) {
                            posY = kz + j - 1;
                            if (se[i][j] == 1 && newMarker[posX][posY] > 0) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag) break;
                    }
                    if (flag) {
                        marker[kx][kz] = iter;
                    }
                }
            }
        }
    }

    int ANDmask(BufferedImage image) {
        int pixelCount = 0;
        for (int kx = 3; kx < marker.length - 3; kx++) {
            for (int kz = 3; kz < marker[kx].length - 3; kz++) {
                Color color = new Color(image.getRGB(kx,kz));
                int red = color.getGreen();
                if (red == 255 && marker[kx][kz] > 0) {
                    pixelCount++;
                }
                else {
                    marker[kx][kz] = 0;
                }
            }
        }
        return pixelCount;
    }

    void normalizeMarker() {
        // find max and min
        int max = 0, min = marker[0][0];
        for (int kx = 0; kx < marker.length; kx++) {
            for (int kz = 0; kz < marker[kx].length; kz++) {
                if (marker[kx][kz] < min)
                    min = marker[kx][kz];
                if (marker[kx][kz] > max)
                    max = marker[kx][kz];
            }
        }

        // normalize
        double fraction = (-255) / (double)(min - max);
        for (int kx = 0; kx < marker.length; kx++) {
            for (int kz = 0; kz < marker[kx].length; kz++) {
                marker[kx][kz] = (int) (fraction * (marker[kx][kz] - min) + 0);	// normalization equation
            }
        }
    }


    void saveMarkerToFile() {
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < marker.length; i++)
        {
            for (int j = 0; j < marker[i].length; j++)
            {
                if (marker[i][j] > 0) {
                    Color color = new Color(marker[i][j], marker[i][j], marker[i][j]);
                    int c = color.getRGB();
                    outputImage.setRGB(i,j,c);
                   // outputImage->SetPixel(i, j, Px.FromArgb(marker[i][j], marker[i][j], marker[i][j]));
                }
                else {
                    Color color = new Color(0,0,0);
                    int c = color.getRGB();
                    outputImage.setRGB(i,j,c);
                    //outputImage->SetPixel(i, j, Px.FromArgb(0, 0, 0));
                }

            }
        }
        File f = new File("geomap.png");
        try {
            ImageIO.write(outputImage, "png", f);

        } catch (IOException e) {
            System.out.println("Exception occured :" + e.getMessage());
        }
        System.out.println("Images were written succesfully.");
    }

}


