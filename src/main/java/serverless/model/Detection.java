package serverless.model;

import java.util.Objects;

public class Detection {
   private String label;
   private int class_id;
   private int x;
   private int y;
   private int width;
   private int height;
   private double confidence;

   public Detection() {
   }

   public Detection(String label, int class_id, int x, int y, int width, int height, double confidence) {
      this.label = label;
      this.class_id = class_id;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.confidence = confidence;
   }

   public String getLabel() {
      return this.label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public int getClass_id() {
      return this.class_id;
   }

   public void setClass_id(int class_id) {
      this.class_id = class_id;
   }

   public int getX() {
      return this.x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getY() {
      return this.y;
   }

   public void setY(int y) {
      this.y = y;
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public double getConfidence() {
      return this.confidence;
   }

   public void setConfidence(double confidence) {
      this.confidence = confidence;
   }

   public Detection label(String label) {
      setLabel(label);
      return this;
   }

   public Detection class_id(int class_id) {
      setClass_id(class_id);
      return this;
   }

   public Detection x(int x) {
      setX(x);
      return this;
   }

   public Detection y(int y) {
      setY(y);
      return this;
   }

   public Detection width(int width) {
      setWidth(width);
      return this;
   }

   public Detection height(int height) {
      setHeight(height);
      return this;
   }

   public Detection confidence(double confidence) {
      setConfidence(confidence);
      return this;
   }

   @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Detection)) {
            return false;
        }
        Detection detection = (Detection) o;
        return Objects.equals(label, detection.label) && class_id == detection.class_id && x == detection.x && y == detection.y && width == detection.width && height == detection.height && confidence == detection.confidence;
   }

   @Override
   public int hashCode() {
      return Objects.hash(label, class_id, x, y, width, height, confidence);
   }

   @Override
   public String toString() {
      return "{" +
         " label='" + getLabel() + "'" +
         ", class_id='" + getClass_id() + "'" +
         ", x='" + getX() + "'" +
         ", y='" + getY() + "'" +
         ", width='" + getWidth() + "'" +
         ", height='" + getHeight() + "'" +
         ", confidence='" + getConfidence() + "'" +
         "}";
   }

}
