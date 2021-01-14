package org.example.fractal;

import java.awt.image.BufferedImage;

public class FractalResult {
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
