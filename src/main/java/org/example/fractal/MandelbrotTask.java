package org.example.fractal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

public class MandelbrotTask implements Callable<FractalResult> {
    private int width;
    private int height;
    private int max = 5000 ;
    private float zoom;
    private Vector pan;
    private double endX;
    private double endY;
    private double startX;
    private double startY;
    private int id;
    private Layout layout;

    static String saveBasePath = "src/main/resources/static/img/";



    public MandelbrotTask(int widthChuck, int heightChuck, double startX, double startY,int id, Vector vector, float zoom, Layout layout) {
        this.width = widthChuck;
        this.height = heightChuck;
        this.startX = startX;
        this.endX = startX + widthChuck;
        this.startY = startY;
        this.endY = startY + heightChuck;
        this.id = id;
        this.layout = layout;
        this.pan = vector;
        this.zoom = zoom;

    }



    @Override
    public FractalResult call() {
        System.out.println("Zoom call : "+zoom);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int black = 0;
        int[] colors = new int[max];
        for (int i = 0; i<max; i++) {
            colors[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        }
        for (int row = 0 ; row < height; row ++) {
            for (int col = 0; col < width; col++) {
                int iterations = calculatePixels(col+startX, row+startY, pan, layout);
                if (iterations < max) image.setRGB(col, row, colors[iterations]);
                else image.setRGB(col, row, black);
            }
        }
        FractalResult result = new FractalResult(id,image);
        return result;
    }


    //Génére les pixels du Mandelbrots
    public int calculatePixels(double pixelX, double pixelY, Vector vector, Layout layout) {
//        double c_re = ((((pixelX + startX + vector.x) - 500)) * 4.0  /500)/ zoom;
        double c_re = 2 * (pixelX - layout.getWidth()/2) / ( 0.5 * zoom * layout.getWidth()) + pan.x;
//        double c_im = ((((pixelY+ startY + vector.y) - 500)) * 4.0 / 500)/ zoom;
        double c_im = 2 * (pixelY - layout.getHeight()/2) / ( 0.5 * zoom * layout.getHeight()) + pan.y;
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
