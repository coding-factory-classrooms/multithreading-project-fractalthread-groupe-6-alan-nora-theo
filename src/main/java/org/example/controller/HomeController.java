package org.example.controller;

import org.example.core.Template;
import org.example.fractal.FractalConfig;
import org.example.fractal.FractalManager;
import org.example.fractal.Layout;
import org.example.fractal.Vector;
import org.example.fractal.TypeFractal;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class HomeController {
    public String home(Request req, Response res){
        Map<String, Object> model = new HashMap<>();
        return Template.render("home.html", model);
    }

    public String getPicture(Request req, Response res){
        float zoom = Float.parseFloat(req.queryParams("zoom"));
        float moveX = Float.parseFloat(req.queryParams("moveX"));
        float moveY = Float.parseFloat(req.queryParams("moveY"));
        int layoutHeight = Integer.parseInt(req.queryParams("layoutHeight"));
        int layoutWidth = Integer.parseInt(req.queryParams("layoutWidth"));

        Vector newVector = new Vector(moveX, moveY);
        FractalConfig fractalConfig = new FractalConfig(3,3,5000, TypeFractal.MANDELBROT);
        FractalManager fractalManager = new FractalManager(fractalConfig);
        Layout layout = new Layout(layoutHeight,layoutWidth);
        String b64Image = fractalManager.generateFractal(zoom, newVector, layout, 5000);
        res.type("text/plain");
        res.status(200);
        return b64Image;
    }
}
