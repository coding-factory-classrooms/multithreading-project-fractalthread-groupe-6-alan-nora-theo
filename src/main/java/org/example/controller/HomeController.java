package org.example.controller;

import org.example.core.Template;
import org.example.fractal.Mandelbrot;
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
        int width = Integer.parseInt(req.queryParams("width"));
        int height = Integer.parseInt(req.queryParams("height"));
        int zoom = Integer.parseInt(req.queryParams("zoom"));
        float moveX = Float.parseFloat(req.queryParams("moveX"));
        float moveY = Float.parseFloat(req.queryParams("moveY"));
        Mandelbrot.Position newPosition = new Mandelbrot.Position(moveX, moveY);

        Mandelbrot mandelbrot = new Mandelbrot(width,height, 1000, zoom, newPosition);
        String b64Image = mandelbrot.draw();
        res.type("text/plain");
        res.status(200);
        return b64Image;
    }
}
