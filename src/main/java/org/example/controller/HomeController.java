package org.example.controller;

import org.example.core.Template;
import org.example.fractal.FractalManager;
import org.example.fractal.Layout;
import org.example.fractal.MandelbrotTask;
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
        int layoutHeight = Integer.parseInt(req.queryParams("layoutHeight"));
        int layoutWidth = Integer.parseInt(req.queryParams("layoutWidth"));

        MandelbrotTask.Vector newVector = new MandelbrotTask.Vector(moveX, moveY);
        FractalManager fractalManager = new FractalManager();
        Layout layout = new Layout(layoutHeight,layoutWidth);

        String b64Image = fractalManager.generateFractal(height, width, zoom, newVector,layout);
        res.type("text/plain");
        res.status(200);
        return b64Image;
    }
}
