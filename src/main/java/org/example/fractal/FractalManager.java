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
    LRUCache<String> cache = new LRUCache<>(6);
    String key;

    public String generateFractal(int width, int height, float zoom, MandelbrotTask.Vector newVector) {
        List<Future<FractalResult>> futures = new ArrayList<>();


        int wSquare = WIDTH / 3 ;
        int hSquare = HEIGHT / 2 ;
        int startX = 0;
        int startY = 0;
        int id = 0;

        //On crée le threadpool
        // int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService threadPool = Executors.newFixedThreadPool(6);
        // On lance le threadpool
        for (int thread = 0; thread < 6; thread++) {
            // On identifie chaque tuile
            futures.add(threadPool.submit(new MandelbrotTask(wSquare, hSquare, startX, startY, id, zoom, newVector)));

            // On calcule les limites de chaque tuile
            startX = startX+wSquare;

            if(startX == (WIDTH/3) * 3){
                startY = startY+ hSquare;
                startX = 0;
            }
        }

        long start = System.currentTimeMillis();
        threadPool.shutdown();

        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<FractalResult> allMandelbrots = new ArrayList<>();
        for ( Future<FractalResult> future : futures) {
            try {
                allMandelbrots.add(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


        BufferedImage bufferedImage = drawMandelbrots(allMandelbrots, width, height);
        //        if (key de allMandelbrots existe dans le cache et si il existe l'appeler){
//           BufferedImage bufferedImage = value du cache correspondant a la key
//        }else{
//            generer l'image et lui attribuer une key
//          BufferedImage bufferedImage = drawMandelbrots(allMandelbrots, width, height);
//        }
            String image;
            key = "1";
            System.out.println("Entrée:");
            System.out.println(cache.get(key));
            if (cache.get(key) != null){
                System.out.println("from cache");
                 image = cache.get(key).toString();
            }else{
                System.out.println("from generate");
                 image = generate(bufferedImage);
                cache.put("1",image);
                System.out.println("Put:");
                System.out.println(cache.get(key));
            }





        long elapsed = System.currentTimeMillis() - start;
//        System.out.println(elapsed);
        return image;
    }

    private BufferedImage drawMandelbrots(List<FractalResult> allMandelbrots , int width , int height ) {


        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = image.createGraphics();
        try {
            combine(g, allMandelbrots);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }


    private void combine(Graphics2D g , List<FractalResult> results )  throws Exception {
        int offsetX = 0;
        int offsetY = 0;
        results.sort((a,b)->{
            return a.id-b.id;
        });
        System.out.println(results.toString());
        for (FractalResult result : results){

            g.drawImage(result.image , null, offsetX , offsetY );
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


class FractalResult {
    int id;
    BufferedImage image;

    public FractalResult(int id, BufferedImage image) {
        this.id = id;
        this.image = image;
    }

    @Override
    public String toString() {
        return "FractalResult{" +
                "id=" + id +
                ", image=" + image +
                '}';
    }
}

