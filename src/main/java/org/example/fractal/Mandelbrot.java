package org.example.fractal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Mandelbrot {
    int width;
    int height;
    int max;
    int zoom = 1;
    int move = 1;

    static String saveBasePath = "src/main/resources/static/img/";

    public Mandelbrot(int width, int height, int max, int zoom, int move) {
        this.width = width;
        this.height = height;
        this.max = max;
        this.zoom = zoom;
        this.move = move;
    }

    public String draw(){
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int black = 0;
        int[] colors = new int[max];
        for (int i = 0; i<max; i++) {
            colors[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        }

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // Valeur réel = X
                // c_re = (col - move/2)*4.0/ ZoomVal;
                double c_re = ((col - (width+move)/2)*4.0/ width)/ zoom;
                // Valeur Imaginaire = Y
                // c_im = (row - move/2)*4.0/ ZoomVal;
                double c_im = ((row - (height+move)/2)*4.0/ width)/zoom;
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
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", out);
            byte[] bytes = out.toByteArray();
            String base64bytes = Base64.getEncoder().encodeToString(bytes);
            String src = "data:image/jpeg;base64," + base64bytes;
            return src;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
