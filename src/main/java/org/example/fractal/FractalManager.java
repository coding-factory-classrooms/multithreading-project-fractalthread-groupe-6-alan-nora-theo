package org.example.fractal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.*;

public class FractalManager {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;
    //private static final int THREADS = 8;

    private int STARTX = 0;
    private int ENDX = 0;
    private int STARTY = 0;
    private int ENDY = 0;
    private final int max = 5000;



    public String generateFractal(int width, int height, int zoom, MandelbrotTask.Position newPosition) {

        List<Future<List<List<Integer>>>> futures = new ArrayList<>();

        int wSquare = WIDTH / 3 ;
        int hSquare = HEIGHT / 2 ;

        //On crée le threadpool
       // int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService threadPool = Executors.newFixedThreadPool(6);
        // On lance le threadpool
        for (int thread = 0; thread < 6; thread++) {

            // On calcule les limites de chaque tuile
            STARTX = ENDX;
            ENDX = ENDX + wSquare;
            STARTY = ENDY;
            ENDY = ENDY + hSquare;


            //MandelbrotTask mandelbrotTask = new MandelbrotTask(width,height, 5000, zoom, newPosition);

            futures.add(threadPool.submit(new MandelbrotTask(wSquare, hSquare, STARTX, ENDX, STARTY, ENDY)));


        }

        long start = System.currentTimeMillis();
        threadPool.shutdown();

        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<List<List<Integer>>> allMandelbrots = new ArrayList<>();
        for (Future<List<List<Integer>>> future : futures) {
            try {
                allMandelbrots.add(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        BufferedImage bufferedImage = drawMandelbrots(allMandelbrots,max, width, height);
        String image = generate(bufferedImage);

        long elapsed = System.currentTimeMillis() - start;
        System.out.println(elapsed);
        return image;
    }

    private BufferedImage drawMandelbrots(List<List<List<Integer>>> allMandelbrots, int max , int width , int height ) {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int black = 0;
        int[] colors = new int[max];
        for (int i = 0; i<max; i++) {
            colors[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        }
        for (List<List<Integer>> pixels : allMandelbrots) {
            for (int y = 0; y < pixels.size(); y++) {
                //StringBuilder output = new StringBuilder();
                List<Integer> xs = pixels.get(y);
                for (int x = 0; x < xs.size(); x++) {

                    int iteration = xs.get(x);
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
