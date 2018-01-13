package com.tomakic.web;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;

@RequestScoped
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConfigResource {

    private static HashMap<String, Html> cache = new HashMap<>();

    @Inject
    private ConfigProperties properties;


    @GET
    @Path("/get/{hash}")
    public Response getPage(@PathParam("hash") String hash) {
        if (cache.containsKey(hash)) {
            return Response.ok(cache.get(hash).getBody()).build();
        }
        return Response.serverError().build();
    }

    @GET
    @Path("/cache/{url}")
    public Response cache(@PathParam("url") String urlText) {

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(urlText.getBytes());
            String hash = Base64.getEncoder().encodeToString(messageDigest.digest());


            if (cache.containsKey(hash)) {
                String key = Base64.getEncoder().encodeToString(hash.getBytes());
                Html xs = new Html();
                xs.setId(key);
                return Response.ok(xs).build();
            }


            BufferedReader in = null;
            try {
                URL url = new URL(urlText);
                in = new BufferedReader(new InputStreamReader(url.openStream()));

                String inputLine;
                String out = "";
                while ((inputLine = in.readLine()) != null) {
                    // System.out.println(inputLine);
                    out += inputLine;
                }
                Html x = new Html();

                String key = Base64.getEncoder().encodeToString(hash.getBytes());
                x.setId(key);
                x.setBody(out);
                cache.put(key, x);

                Html xs = new Html();
                xs.setId(key);
                return Response.ok(xs).build();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // e.printStackTrace();
                        return Response.serverError().build();
                    }
                }
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok(new Html()).build();
    }


    /*
    @GET
    @Path("/config")
    public Response test() {
        String response =
                "{" +
                        "\"stringProperty\": \"%s\"," +
                        "\"booleanProperty\": %b," +
                        "\"integerProperty\": %d" +
                        "}";

        response = String.format(
                response,
                properties.getStringProperty(),
                properties.getBooleanProperty(),
                properties.getIntegerProperty());

        return Response.ok(response).build();
    }

    @GET
    @Path("/get")
    public Response get() {
        return Response.ok(ConfigurationUtil.getInstance().get("rest-config.string-property").orElse("nope")).build();
    }*/
}
