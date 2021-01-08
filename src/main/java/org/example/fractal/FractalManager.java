package org.example.fractal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FractalManager {

    private static final int WIDTH = 128;
    private static final int HEIGHT = 8;
    private static final int THREADS = 8;

    private  final double STARTX = -1.5;
    private  final double ENDX = 0.5;
    private  final double STARTY = -1;

    public void generateFractal(int width, int height, int zoom, MandelbrotTask.Position newPosition) {

        List<Future<List<List<Double>>>> futures = new ArrayList<>();



        //On crée le threadpool
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService threadPool = Executors.newFixedThreadPool(cores);
        // On lance le threadpool
        for (int thread = 0; thread < cores; thread++) {
            double threadStartY = STARTY + thread * height;
            double threadEndY = STARTY + (thread+1) * height;
            //MandelbrotTask mandelbrotTask = new MandelbrotTask(width,height, 5000, zoom, newPosition);

            futures.add(threadPool.submit(new MandelbrotTask(WIDTH, HEIGHT, STARTX, ENDX, threadStartY, threadEndY)));
        }

        long start = System.currentTimeMillis();
        threadPool.shutdown();

        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println(elapsed);
    }

    private BufferedImage drawMandelbrots(List<List<List<Double>>> allMandelbrots, int max , int width , int height ) {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int black = 0;
        int[] colors = new int[max];
        for (int i = 0; i<max; i++) {
            colors[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        }
        for (List<List<Double>> pixels : allMandelbrots) {
            for (int y = 0; y < pixels.size(); y++) {
                //StringBuilder output = new StringBuilder();
                List<Double> xs = pixels.get(y);
                for (int x = 0; x < xs.size(); x++) {
                    int iteration =  xs.get(x).intValue();
                     if (xs.get(x) < max) image.setRGB(x, y, colors[iteration]);
                    else image.setRGB(x, y, black);
                    //double intensity = (xs.get(x) - minIntensity) / (maxIntensity - minIntensity);
                    //int idx = Math.min(palette.length - 1, (int) (palette.length * intensity));
                    //output.append(palette[idx]);
                }
            }
        }
        return image;
    }

    public String generate(BufferedImage image){
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
