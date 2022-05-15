package serverless.opencv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

import com.google.gson.Gson;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import serverless.model.Detection;

public class ObjectDetect {
   private List<String> cocoLabels;
   private Net dnnNet;
   private ArrayList<Scalar> colors;
   private Gson gson;
   private static Logger log = LoggerFactory.getLogger(ObjectDetect.class);
   private static String YOLO_T_CFG = "yolov3-tiny.cfg";
   private static String YOLO_T_WEIGHTS = "yolov3-tiny.weights";


   public ObjectDetect() throws FileNotFoundException {
      // Load COCO labels
      Scanner scan = new Scanner(new FileReader("coco.names"));
      cocoLabels = new ArrayList<String>();
      while (scan.hasNextLine()) {
         cocoLabels.add(scan.nextLine());
      }

      // load our YOLO object detector trained on COCO dataset
      dnnNet = Dnn.readNetFromDarknet(YOLO_T_CFG, YOLO_T_WEIGHTS);

      // generate radnom color in order to draw bounding boxes
      Random random = new Random();
      colors = new ArrayList<Scalar>();
      for (int i = 0; i < cocoLabels.size(); i++) {
         colors.add(new Scalar(new double[] { random.nextInt(255), random.nextInt(255), random.nextInt(255) }));
      }
      gson = new Gson();

   }

   public List<Detection> detectObjectsOnImage(String path, String filename, String dest, String destJson,
         String[] cocoObjects,
         double confThreshold) {
      // load our input image
      log.debug("Loading {}", path + "/" + filename);

      Mat img = Imgcodecs.imread(path + "/" + filename);

      log.debug("Info about the image: {}", img.toString());

      List<String> layerNames = dnnNet.getLayerNames();

      List<String> outputLayers = new ArrayList<String>();
      for (Integer i : dnnNet.getUnconnectedOutLayers().toList()) {
         outputLayers.add(layerNames.get(i - 1));
      }

      // Prepare some data structures to
      // store data returned by the network
      ArrayList<Rect> boxes = new ArrayList<>();
      ArrayList<Float> confidences = new ArrayList<>();
      ArrayList<Integer> class_ids = new ArrayList<>();

      // DNN Prediction
      predict(img, dnnNet, outputLayers, cocoObjects, confThreshold, boxes, confidences, class_ids);

      List<Detection> detections = new ArrayList<>();

      if (class_ids.size() > 0) {
         // Get indices from non maximum supression
         MatOfInt indices = getBBoxIndicesFromNonMaximumSuppression(boxes,
               confidences, confThreshold);

         // Get detected objects from indices
         detections = getDetections(indices, boxes, class_ids, confidences);

         if (detections.size() > 0) {

            // Draw boxes and save results
            img = drawBoxesOnTheImage(img, indices, detections);

            // Save image
            Imgcodecs.imwrite(path + "/" + dest, img);

            try {
               PrintWriter pw = new PrintWriter(new FileWriter(path + "/" + destJson));
               pw.println(gson.toJson(detections));
               pw.flush();
               pw.close();
            } catch (IOException ex) {
               ex.printStackTrace();
            }
         }
      }
      if (img != null)
         img.release();
      return detections;
   }

   private void predict(Mat img,
         Net dnnNet,
         List<String> outputLayers,
         String[] cocoObjects,
         double confThreshold,
         ArrayList<Rect> boxes,
         ArrayList<Float> confidences,
         ArrayList<Integer> class_ids) {

      // The expected image input size to the network is 416
      Mat blob_from_image = Dnn.blobFromImage(img, 1 / 255.0,
            new Size(416, 416),
            new Scalar(0), true, false);

      // Set the input to the DNN
      dnnNet.setInput(blob_from_image);

      log.debug("Performing detection of {}", String.join(" ", cocoObjects));

      Mat output = dnnNet.forward();

      // loop over each of the detections. Each row is a candidate detection,
      log.debug("Output.rows(): {}, Output.cols(): {}", output.rows(), output.cols());

      for (int i = 0; i < output.rows(); i++) {
         Mat row = output.row(i);
         List<Float> detect = new MatOfFloat(row).toList();
         List<Float> scores = detect.subList(5, output.cols());
         int class_id = argmax(scores);
         float conf =  detect.get(4);
         if (conf >= confThreshold) {
            if (Stream.of(cocoObjects).anyMatch(t -> t.equals(cocoLabels.get(class_id)))) {
               log.debug("{} detected in image", cocoLabels.get(class_id));
               int center_x = (int) (detect.get(0) * img.cols());
               int center_y = (int) (detect.get(1) * img.rows());
               int width = (int) (detect.get(2) * img.cols());
               int height = (int) (detect.get(3) * img.rows());
               int x = (center_x - width / 2);
               int y = (center_y - height / 2);
               Rect box = new Rect(x, y, width, height);

               boxes.add(box);
               confidences.add(conf);
               class_ids.add(class_id);
            }
         }
      }

   }

   private static int argmax(List<Float> data) {
      var pos = 0;
      var max = data.get(0);
      for (var i = 1; i < data.size(); i++) {
         if (data.get(i) > max) {
            max = data.get(i);
            pos = i;
         }
      }
      return pos;
   }

   private static MatOfInt getBBoxIndicesFromNonMaximumSuppression(ArrayList<Rect> boxes,
         ArrayList<Float> confidences, double confThreshold) {

      MatOfRect mOfRect = new MatOfRect();
      mOfRect.fromList(boxes);
      MatOfFloat mfConfs = new MatOfFloat(Converters.vector_float_to_Mat(confidences));
      MatOfInt result = new MatOfInt();
      Dnn.NMSBoxes(mOfRect, mfConfs, (float) (confThreshold), (float) (0.4), result);
      return result;
   }

   private List<Detection> getDetections(MatOfInt indices,
         ArrayList<Rect> boxes,
         ArrayList<Integer> class_ids,
         ArrayList<Float> confidences) {

      List<Detection> detections = new ArrayList<>();
      var indices_list = indices.toList();
      for (int i = 0; i < boxes.size(); i++) {
         if (indices_list.contains(i)) {
            Rect box = boxes.get(i);
            String label = cocoLabels.get(class_ids.get(i));
            detections.add(new Detection().label(label).class_id(class_ids.get(i)).confidence(confidences.get(i))
                  .x(box.x).y(box.y).width(box.width).height(box.height));
         }
      }
      return detections;
   }

   private Mat drawBoxesOnTheImage(Mat img,
         MatOfInt indices,
         List<Detection> detections) {

      for (Detection d : detections) {
         Point text_point = new Point(d.getX(), d.getY() - 5);
         Point x_y = new Point(d.getX(), d.getY());
         Point w_h = new Point(d.getX() + d.getWidth(), d.getY() + d.getHeight());
         Imgproc.rectangle(img, w_h, x_y, colors.get(d.getClass_id()), 1);
         Imgproc.putText(img, d.getLabel(), text_point, Core.FONT_HERSHEY_COMPLEX, 1, colors.get(d.getClass_id()), 2);
      }
      return img;
   }
}
