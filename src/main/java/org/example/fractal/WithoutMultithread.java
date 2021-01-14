package org.example.fractal;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class WithoutMultithread {
    public static void main(String[] args) {
        Vector newVector = new Vector(0, 0);
        Layout layout = new Layout(1000,1000);
        MandelbrotTask mandelbrotTask = new MandelbrotTask(layout.getWidth(), layout.getHeight(), 0,0,0,newVector,0,layout, 5000);
        FractalResult fractalResult = mandelbrotTask.call();
        String src = "";
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(fractalResult.image, "jpg", out);
            byte[] bytes = out.toByteArray();
            String base64bytes = Base64.getEncoder().encodeToString(bytes);
            src = "data:image/jpeg;base64," + base64bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
