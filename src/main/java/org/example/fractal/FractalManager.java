package org.example.fractal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.*;

public class FractalManager {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;
    //private static final int THREADS = 8;

    private final int max = 5000;

    public String generateFractal(int width, int height, int zoom, MandelbrotTask.Vector newVector) {

        int wSquare = WIDTH / 3 ;
        int hSquare = HEIGHT / 2 ;

        int startX = 0;
        int endX = 0;
        int startY = 0;
        int endY = 0;

        List<Future<BufferedImage>> futures = new ArrayList<>();

        //On crée le threadpool
       // int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService threadPool = Executors.newFixedThreadPool(6);
        // On lance le threadpool
        endY = endY + hSquare;

        for (int thread = 0; thread < 6; thread++) {
            // On calcule les limites de chaque tuile
            startX = endX;
            endX = endX + wSquare;

            System.out.println(startX +" : "+endX);
            System.out.println(startY +" : "+endY);
            System.out.println("================");

            futures.add(threadPool.submit(new MandelbrotTask(wSquare, hSquare, startX, endX, startY, endY)));

            if( endX == (WIDTH/3) * 3 ){
                startY = endY;
                endY = endY + hSquare;
                startX = 0;
                endX = 0;
            }
        }

        long start = System.currentTimeMillis();
        threadPool.shutdown();

        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<BufferedImage> allMandelbrots = new ArrayList<>();
        for ( Future<BufferedImage> future : futures) {
            try {
                allMandelbrots.add(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        BufferedImage bufferedImage = drawMandelbrots(allMandelbrots, width, height);
        String image = generate(bufferedImage);

        long elapsed = System.currentTimeMillis() - start;
//        System.out.println(elapsed);
        return image;
    }

    private BufferedImage drawMandelbrots(List<BufferedImage> allMandelbrots , int width , int height ) {

        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = image.createGraphics();
        try {
            combine(g, allMandelbrots);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }


    private void combine(Graphics2D g , List<BufferedImage> images )  throws Exception {
        int offsetX = 0;
        int offsetY = 0;
       for (BufferedImage image : images){
           g.drawImage(image , null, offsetX , offsetY );
           offsetX = offsetX + WIDTH/3;
           if( offsetX == (WIDTH/3) * 3 ){
               offsetY = offsetY + HEIGHT/2;
               offsetX = 0;
           }
       }
    }

    public String generate(BufferedImage image){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", new File("mandelbrot.png"));
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


    public class FractalConfig{
        int width;
        int height;
        int startX;
        int endX;
        int startY;
        int endY;
        float zoom;
        MandelbrotTask.Vector Vector;

        public FractalConfig(int width, int height, int startX, int endX, int startY, int endY, float zoom, MandelbrotTask.Vector vector) {
            this.width = width;
            this.height = height;
            this.startX = startX;
            this.endX = endX;
            this.startY = startY;
            this.endY = endY;
            this.zoom = zoom;
            Vector = vector;
        }
    }

}
