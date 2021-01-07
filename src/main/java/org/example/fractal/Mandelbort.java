package org.example.fractal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class Mandelbrot {
    int width;
    int height;
    int max;

    static String saveBasePath = "src/main/resources/static/img/";

    public Mandelbrot(int width, int height, int max) {
        this.width = width;
        this.height = height;
        this.max = max;
    }

    public void draw(){
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int black = 0;
        int[] colors = new int[max];
        for (int i = 0; i<max; i++) {
            colors[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        }

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                double c_re = (col - width/2)*4.0/width;
                double c_im = (row - height/2)*4.0/width;
                double x = 0, y = 0;
                int iterations = 0;
                while (x*x+y*y < 4 && iterations < max) {
                    double x_new = x*x-y*y+c_re;
                    y = 2*x*y+c_im;
                    x = x_new;
                    iterations++;
                }
                if (iterations < max) image.setRGB(col, row, colors[iterations]);
                else image.setRGB(col, row, black);
            }
        }

        try {
            ImageIO.write(image, "jpg", new File(saveBasePath+"mandelbrot.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}