package org.example.fractal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

public class JuliaTaks implements Callable<FractalResult> {
    private int width;
    private int height;
    private int max;
    private float zoom;
    private Vector pan;
    private double startX;
    private double startY;
    private int id;
    private Layout layout;

    public JuliaTaks(int width, int height, double startX, double startY,int id, Vector pan, float zoom, Layout layout, int max) {
        this.width = width;
        this.height = height;
        this.max = max;
        this.zoom = zoom;
        this.pan = pan;
        this.startX = startX;
        this.startY = startY;
        this.id = id;
        this.layout = layout;
    }


    @Override
    public FractalResult call() throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int iterations = calculatePixels(x+(int)startX, y+(int)startY);
                int c = Color.HSBtoRGB((max / iterations) % 1, 1, iterations > 0 ? 1 : 0);
                image.setRGB(x, y, c);
            }
        }

        FractalResult result = new FractalResult(id,image);
        return result;
    }

    private int calculatePixels(int col, int row){
        double p_re = 1.5 * (row - layout.getWidth() / 2) / (0.5 * zoom * layout.getWidth()) + pan.x;
        double p_im = 1.5 * (col - layout.getHeight() / 2) / (0.5 * zoom * layout.getHeight()) + pan.y;
        int i = max;
        while (p_re * p_re + p_im * p_im < 4 && i > 0) {
            double tmp = p_re * p_re - p_im * p_im + -0.7;
            p_im = 2.0 * p_re * p_im + 0.27015;
            p_re = tmp;
            i--;
        }
        return i;
    }
}
