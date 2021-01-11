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

    private int wSquare = WIDTH / 3 ;
    private int hSquare = HEIGHT / 2 ;

    private final BufferedImage image = new BufferedImage( WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR );
    private final Graphics2D g = image.createGraphics();



    public String generateFractal(int width, int height, int zoom, MandelbrotTask.Position newPosition) {

        List<Future<BufferedImage>> futures = new ArrayList<>();

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
        System.out.println(elapsed);
        return image;
    }

    private BufferedImage drawMandelbrots(List<BufferedImage> allMandelbrots , int width , int height ) {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = image.createGraphics();
        try {
            combine(g, allMandelbrots);
        } catch (Exception e) {
            e.printStackTrace();
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

    private void combine(Graphics2D g , List<BufferedImage> images )  throws Exception {
        int offsetX = 0;
        int offsetY = 0;

       for (BufferedImage image : images){
           offsetX = offsetX + WIDTH/3;

           g.drawImage(image , null, offsetX , offsetY );

           if( offsetX == (WIDTH/3) * 2 ){
               offsetY = offsetY + HEIGHT/2;
           }

       }


    }


}
