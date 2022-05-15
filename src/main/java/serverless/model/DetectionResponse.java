package serverless.model;

import java.util.List;
import java.util.Objects;

public class DetectionResponse {
   private List<Detection> detections;

   public DetectionResponse() {
   }

   public DetectionResponse(List<Detection> detections) {
      this.detections = detections;
   }

   public List<Detection> getDetections() {
      return this.detections;
   }

   public void setDetections(List<Detection> detections) {
      this.detections = detections;
   }

   public DetectionResponse detections(List<Detection> detections) {
      setDetections(detections);
      return this;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this)
         return true;
      if (!(o instanceof DetectionResponse)) {
         return false;
      }
      DetectionResponse detectionResponse = (DetectionResponse) o;
      return Objects.equals(detections, detectionResponse.detections);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(detections);
   }

   @Override
   public String toString() {
      return "{" +
            " detections='" + getDetections() + "'" +
            "}";
   }

}
