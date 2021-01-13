package org.example.fractal;

public class FractalConfig {
    int nbChunkWidth;
    int nbChunkHeight;
    int maxIteration;
    TypeFractal typeFractal;

    public FractalConfig(int nbChunkWidth, int nbChunkHeight, int maxIteration, TypeFractal typeFractal) {
        this.nbChunkWidth = nbChunkWidth;
        this.nbChunkHeight = nbChunkHeight;
        this.maxIteration = maxIteration;
        this.typeFractal = typeFractal;
    }
}
