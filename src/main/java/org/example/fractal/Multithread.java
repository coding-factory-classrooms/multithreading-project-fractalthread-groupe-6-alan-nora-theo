package org.example.fractal;

public class Multithread {
    public static void main(String[] args) {
        Vector newVector = new Vector(0, 0);
        FractalConfig fractalConfig = new FractalConfig(6,6,5000, TypeFractal.MANDELBROT);
        FractalManager fractalManager = new FractalManager(fractalConfig);
        Layout layout = new Layout(1000,1000);
        fractalManager.generateFractal(1, newVector, layout, 5000);
    }
}
