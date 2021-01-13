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


    //private static final int WIDTH = (int)layout.getWidth();
    //private static final int HEIGHT = (int)layout.getHeight();
    //private static final int THREADS = 8;

    private final int max = 5000;

    private FractalConfig fractalConfig;
        this.fractalConfig = fractalConfig;
    public FractalManager(FractalConfig fractalConfig) {
    }

    public String generateFractal(int width, int height, float zoom, MandelbrotTask.Vector newVector, Layout layout) {
        List<MandelbrotTask> mandelbrotTasks = new ArrayList<>();

        int widthChunk = WIDTH / 3 ;
        int heightChunk = HEIGHT / 3 ;
        int startX = 0;
        int startY = 0;

        // On lance le threadpool
        int nbChunks = fractalConfig.nbChunkWidth*fractalConfig.nbChunkHeight;
        for (int chunk = 0; chunk < nbChunks; chunk++) {
            // On identifie chaque tuile
            mandelbrotTasks.add(new MandelbrotTask(widthChunk, heightChunk, startX, startY, chunk, zoom, newVector, layout));
            // On calcule les limites de chaque tuile
            startX = startX+widthChunk;

            if(startX == (layout.getWidth()/fractalConfig.nbChunkWidth) * fractalConfig.nbChunkWidth){
                startX = 0;
            }
        }

        List<Future<FractalResult>> futures = threadFractal(mandelbrotTasks);

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

        BufferedImage bufferedImage = drawMandelbrots(allMandelbrots, WIDTH, HEIGHT);
        String image = generate(bufferedImage);

        return image;
    }

    private List<Future<FractalResult>> threadFractal(List<MandelbrotTask> mandelbrotTasks){
        List<Future<FractalResult>> futures = new ArrayList<>();

        //On crée le threadpool
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService threadPool = Executors.newFixedThreadPool(cores);
        // On lance le threadpool
        for (MandelbrotTask mandelbrot : mandelbrotTasks) {
            futures.add(threadPool.submit(mandelbrot));
        }

        threadPool.shutdown();

        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return futures;
    }

    private BufferedImage drawMandelbrots(List<FractalResult> allMandelbrots , int width , int height ) {

        BufferedImage image = new BufferedImage(layout.getWidth(), layout.getHeight(), BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = image.createGraphics();
        try {
            combine(g, allMandelbrots, layout);
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

        for (FractalResult result : results){

            g.drawImage(result.image , null, offsetX , offsetY );
            offsetX = offsetX + layout.WIDTH/fractalConfig.nbChunkWidth;

            if( offsetX == (layout.WIDT/fractalConfig.nbChunkWidth) * fractalConfig.nbChunkWidth ){
                offsetY = offsetY + layout.HEIGHT/fractalConfig.nbChunkHeight;
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


    public class FractalConfig{
        int width;
        int height;
        int startX;
        int endX;
        int startY;
        int endY;
        float zoom;
        MandelbrotTask.Vector Vector;

        public FractalConfig(int width, int height, int startX, int endX, int startY, int endY, float zoom, MandelbrotTask.Vector vector ) {
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

