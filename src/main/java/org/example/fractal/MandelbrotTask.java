package org.example.fractal;

import javax.imageio.ImageIO;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Callable;

public class MandelbrotTask implements Callable<List<List<Double>>> {
    private int width;
    private int height;
    private int max;
    private int zoom;
    private Position pan;
    private double xSkip;
    private double ySkip;
    private double x0;
    private double y0;

    static String saveBasePath = "src/main/resources/static/img/";

    public MandelbrotTask(int width, int height, int max, int zoom, Position pan) {
        this.width = width;
        this.height = height;
        this.max = max;
        this.zoom = zoom;
        this.pan = pan;
    }


    public MandelbrotTask(int pixelWidth, int pixelHeight, double startX, double endX, double startY, double endY) {
        this.width = pixelWidth;
        this.height = pixelHeight;
        this.x0 = startX;
        this.xSkip = (endX - startX) / pixelWidth;
        this.y0 = startY;
        this.ySkip = (endY - startY) / pixelHeight;
    }



    @Override
    public List<List<Double>> call() {

        List<List<Double>> pixels = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            List<Double> row = new ArrayList<>();
            for (int x = 0; x < width; x++) {

                row.add(calculatePixels(x,y));

               // if (iterations < max) image.setRGB(col, row, colors[iterations]);
              //  else image.setRGB(col, row, black);

            }
            pixels.add(row);

        }

        return pixels;
    }

    public double calculatePixels(double pixelX, double pixelY) {
        // Valeur réel = X
        // c_re = (col - move/2)*4.0/ ZoomVal;
//                double c_re = ((col - (width + position.y)/2)*4.0/ width)/ zoom;

        double c_re = x0 + pixelX * xSkip;
        // Valeur Imaginaire = Y
        // c_im = (row - move/2)*4.0/ ZoomVal;
//                double c_im = ((row - (height + position.x)/2)*4.0/ width)/zoom;
        double c_im = y0 + pixelY * ySkip;
        double x = 0, y = 0;
        int iterations = 0;
        while (x * x + y * y < 4 && iterations < max) {
            double x_new = x * x - y * y + c_re;
            y = 2 * x * y + c_im;
            x = x_new;
            iterations++;
        }
        return Math.log(iterations) / Math.log(max);
    }

    public static class Position{
        float x;
        float y;

        public Position(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
