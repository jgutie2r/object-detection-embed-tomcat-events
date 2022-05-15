package serverless.function;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.v1.CloudEventBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.events.CloudEventListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import serverless.model.Detection;
import serverless.model.DetectionResponse;
import serverless.model.Job;
import serverless.opencv.ObjectDetect;

/**
 * The code of this class is not fully functional
 * You must add the code to download the image in the method downloadResource
 * and the code to upload the files in uploadResources
 */
public class ObjectDetectCloudEventListener extends CloudEventListener {
    private Gson gson = new Gson();
    private ObjectDetect objDetector;

    private static Logger log = LoggerFactory.getLogger(ObjectDetectCloudEventListener.class);

    public ObjectDetectCloudEventListener() throws FileNotFoundException {
        objDetector = new ObjectDetect();
    }

    @Override
    public void consumeEvent(CloudEvent ev, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Different for each request request
        String uuid = UUID.randomUUID().toString();
        String imagesPath = "/tmp/" + uuid;

        new File(imagesPath).mkdirs();

        log.info("Call to function to process image");
       

        String body = new String(ev.getData().toBytes());
        log.debug("Body of the cloud event: {}", body);

        Job job = gson.fromJson(body, Job.class);
        log.debug("Job: {}", job);

        // Take the name of the video from the URL"
        String url = job.getSourceUrl();
        String[] parts = url.split("/");
        String fileName = parts[parts.length - 1];

        String extension = fileName.substring(fileName.indexOf('.'));
        String name = fileName.substring(0, fileName.indexOf('.'));
        String dest = name + "_detected_" + String.join("_", job.getCocoLabels()) + extension;
        String destJson = name + "_detected_" + String.join("_", job.getCocoLabels()) + ".json";

        try {
            
            if (downloadResource(job, imagesPath)) {
                List<Detection> detections = processResource(job, imagesPath, fileName, dest, destJson);
                if (detections.size() > 0)
                    uploadResult(job, dest, destJson, imagesPath);
               
                log.info("Found {} objects of type {}", detections.size(), String.join(" ", job.getCocoLabels()));
               
                DetectionResponse dResp = new DetectionResponse().detections(detections);

                CloudEvent event = new CloudEventBuilder().withId(UUID.randomUUID().toString())
                        .withData("application/json", gson.toJson(dResp).getBytes()).withType("detections")
                        .withSource(new URI("/object-detect")).withSubject("objects detected in " + job.getSourceUrl())
                        .build();

                sendCloudEvent(event, resp);
            } else {
                resp.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
                log.debug("Image to download not found");
            }

        } catch (Exception ex) {
            log.warn("Exception processing request", ex);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            clean(imagesPath, fileName, dest, destJson);
        }
    }

    private boolean downloadResource(Job job, String path) throws Exception {
        log.info("Downloading ...");
        // Download file
       
        // Take the name of the video from the URL
        String url = job.getSourceUrl();
        String[] parts = url.split("/");
        String fileName = parts[parts.length - 1];
        // Save the video from the URL in tmp folder with its name
        String image = path + "/" + fileName;

        // Put here the code to download the file

        File fd = new File(image);
        boolean downloaded = false;
        if (fd.exists()) {
            downloaded = true;
            log.debug("Saved: {}", image);
        }
        return downloaded;
    }

    private List<Detection> processResource(Job job, String path, String fileName, String dest, String destJson)
            throws Exception {
        // Process image
        log.info("Processing ...");

        List<Detection> detections = objDetector.detectObjectsOnImage(path, fileName, dest, destJson,
                job.getCocoLabels(), job.getConfThreshold());

        return detections;
    }

    private void uploadResult(Job job, String detected, String detectedJson, String path) throws Exception {
        log.info("Uploading results...");
        // Put here the code to upload the files
    }


    private void clean(String path, String fileName, String dest, String destJson) {
        try {
            Files.delete(Paths.get(path + "/" + fileName));
        } catch (IOException ex) {
            log.debug("Problem deleting file {}", ex.toString());
        }
        try {
            Files.delete(Paths.get(path + "/" + dest));
        } catch (IOException ex) {
            log.debug("Problem deleting file {}", ex.toString());
        }
        try {
            Files.delete(Paths.get(path + "/" + destJson));
        } catch (IOException ex) {
            log.debug("Problem deleting file {}", ex.toString());
        }
        try {
            Files.delete(Paths.get(path));
            log.debug("Deleted folder", path);
        } catch (IOException ex) {
            log.debug("Problem deleting folder", ex.toString());
        }
    }

}
