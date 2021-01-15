package org.example.controller;

import org.example.core.Template;
import org.example.fractal.*;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class HomeController {
    LRUCache<String> cache = new LRUCache<>(6);
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


        String key = String.valueOf(zoom +" - "+ moveX +" - "+ moveY +" - "+layoutHeight +" - "+layoutWidth );
        String b64Image = "";

        if(cache.get(key) != null){
            b64Image = cache.get(key).toString();
            System.out.println("Image existant dans le cache avec la key : " +  cache.get(key));
        }else{
            b64Image = fractalManager.generateFractal(zoom, newVector, layout, 5000);
            cache.put(key,b64Image);
            System.out.println("Image mise en cache key :" + cache.get(key));

            res.type("text/plain");
            res.status(200);
        }
        return b64Image;
    }
}





