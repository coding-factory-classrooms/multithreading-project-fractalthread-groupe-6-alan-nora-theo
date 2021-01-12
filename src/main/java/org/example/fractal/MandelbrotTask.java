package org.example.fractal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

public class MandelbrotTask implements Callable<BufferedImage> {
    private int width;
    private int height;
    private int max = 5000 ;
    private int zoom;
    private Vector pan;
    private double endX;
    private double endY;
    private double startX;
    private double startY;

    static String saveBasePath = "src/main/resources/static/img/";

    public MandelbrotTask(int width, int height, int max, int zoom, Vector pan) {
        this.width = width;
        this.height = height;
        this.max = max;
        this.zoom = zoom;
        this.pan = pan;
    }


    public MandelbrotTask(int pixelWidth, int pixelHeight, double startX, double endX, double startY, double endY) {
        this.width = pixelWidth;
        this.height = pixelHeight;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
    }



    @Override
    public BufferedImage call() {
//        System.out.println(startX);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int black = 0;
        int[] colors = new int[max];
        for (int i = 0; i<max; i++) {
            colors[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        }
        for (int row = 0 ; row < height; row ++) {
            for (int col = 0; col < width; col++) {
                int iterations = calculatePixels(col , row);
                if (iterations < max) image.setRGB(col, row, colors[iterations]);
                else image.setRGB(col, row, black);
            }
        }
//        System.out.println("Finished mandelbrot at y = " + y0);
        return image;
    }


    //Génére les pixels du Mandelbrots
    public int calculatePixels(double pixelX, double pixelY) {

        double c_re = ((pixelY+ startY) - 500) * 4.0 / 500;
        double c_im = ((pixelX + startX) - 500) * 4.0  /500;
        double x = 0, y = 0;
        int iterations = 0;
        while (x * x + y * y < 4 && iterations < max) {
            double x_new = x * x - y * y + c_re;
            y = 2 * x * y + c_im;
            x = x_new;
            iterations++;
        }
       // double result = Math.log(iterations) / Math.log(max);
        return iterations;
    }

    public static class Vector { // TODO: Vector
        float x;
        float y;

        public Vector(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
