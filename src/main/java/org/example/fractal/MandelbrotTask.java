package org.example.fractal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MandelbrotTask implements Callable<List<List<Integer>>> {
    private int width;
    private int height;
    private int max = 5000 ;
    private int zoom;
    private Position pan;
    private double endX;
    private double endY;
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
        this.endX = endX;
        this.y0 = startY;
        this.endY = endY;
    }



    @Override
    public List<List<Integer>> call() {

        List<List<Integer>> pixels = new ArrayList<>();

        for (int y = (int) y0; y < endY; y++) {
            List<Integer> row = new ArrayList<>();
            for (int x = (int) x0; x < endX; x++) {
                row.add(calculatePixels(x,y));
            }
            pixels.add(row);
        }
        System.out.println("Finished mandelbrot at y = " + y0);
        return pixels;
    }


    //Génére les pixels du Mandelbrots
    public int calculatePixels(double pixelX, double pixelY) {

        double c_re = (pixelY - 500 ) * 4.0 / 500;
        double c_im = (pixelX - 500 ) * 4.0 / 500 ;
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

    public static class Position{
        float x;
        float y;

        public Position(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
