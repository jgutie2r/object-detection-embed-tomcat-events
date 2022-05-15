package serverless.model;

import java.util.Objects;

public class Job {
   // Source url of the image
   private String sourceUrl;
   // Host and port to upload results
   private String destUrl;
   // Name of the bucket parameter used in the upload service
   private String bucketParamName;
   // Value of the bucker parameter (the directory name to uload)
   private String bucketParamValue;
   // Auth token parameter used by the upload service
   private String authTokenDestName;
   // Auth token value in the upload service
   private String authTokenDestValue;
   // coco labels to detect in the image
   private String[] cocoLabels;
   // Confidence threshold applied to detect objects
   private double confThreshold;


   public Job() {
   }

   public Job(String sourceUrl, String destUrl, String bucketParamName, String bucketParamValue, String authTokenDestName, String authTokenDestValue, String[] cocoLabels, double confThreshold) {
      this.sourceUrl = sourceUrl;
      this.destUrl = destUrl;
      this.bucketParamName = bucketParamName;
      this.bucketParamValue = bucketParamValue;
      this.authTokenDestName = authTokenDestName;
      this.authTokenDestValue = authTokenDestValue;
      this.cocoLabels = cocoLabels;
      this.confThreshold = confThreshold;
   }

   public String getSourceUrl() {
      return this.sourceUrl;
   }

   public void setSourceUrl(String sourceUrl) {
      this.sourceUrl = sourceUrl;
   }

   public String getDestUrl() {
      return this.destUrl;
   }

   public void setDestUrl(String destUrl) {
      this.destUrl = destUrl;
   }

   public String getBucketParamName() {
      return this.bucketParamName;
   }

   public void setBucketParamName(String bucketParamName) {
      this.bucketParamName = bucketParamName;
   }

   public String getBucketParamValue() {
      return this.bucketParamValue;
   }

   public void setBucketParamValue(String bucketParamValue) {
      this.bucketParamValue = bucketParamValue;
   }

   public String getAuthTokenDestName() {
      return this.authTokenDestName;
   }

   public void setAuthTokenDestName(String authTokenDestName) {
      this.authTokenDestName = authTokenDestName;
   }

   public String getAuthTokenDestValue() {
      return this.authTokenDestValue;
   }

   public void setAuthTokenDestValue(String authTokenDestValue) {
      this.authTokenDestValue = authTokenDestValue;
   }

   public String[] getCocoLabels() {
      return this.cocoLabels;
   }

   public void setCocoLabels(String[] cocoLabels) {
      this.cocoLabels = cocoLabels;
   }

   public double getConfThreshold() {
      return this.confThreshold;
   }

   public void setConfThreshold(double confThreshold) {
      this.confThreshold = confThreshold;
   }

   public Job sourceUrl(String sourceUrl) {
      setSourceUrl(sourceUrl);
      return this;
   }

   public Job destUrl(String destUrl) {
      setDestUrl(destUrl);
      return this;
   }

   public Job bucketParamName(String bucketParamName) {
      setBucketParamName(bucketParamName);
      return this;
   }

   public Job bucketParamValue(String bucketParamValue) {
      setBucketParamValue(bucketParamValue);
      return this;
   }

   public Job authTokenDestName(String authTokenDestName) {
      setAuthTokenDestName(authTokenDestName);
      return this;
   }

   public Job authTokenDestValue(String authTokenDestValue) {
      setAuthTokenDestValue(authTokenDestValue);
      return this;
   }

   public Job cocoLabels(String[] cocoLabels) {
      setCocoLabels(cocoLabels);
      return this;
   }

   public Job confThreshold(double confThreshold) {
      setConfThreshold(confThreshold);
      return this;
   }

   @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Job)) {
            return false;
        }
        Job job = (Job) o;
        return Objects.equals(sourceUrl, job.sourceUrl) && Objects.equals(destUrl, job.destUrl) && Objects.equals(bucketParamName, job.bucketParamName) && Objects.equals(bucketParamValue, job.bucketParamValue) && Objects.equals(authTokenDestName, job.authTokenDestName) && Objects.equals(authTokenDestValue, job.authTokenDestValue) && Objects.equals(cocoLabels, job.cocoLabels) && confThreshold == job.confThreshold;
   }

   @Override
   public int hashCode() {
      return Objects.hash(sourceUrl, destUrl, bucketParamName, bucketParamValue, authTokenDestName, authTokenDestValue, cocoLabels, confThreshold);
   }

   @Override
   public String toString() {
      return "{" +
         " sourceUrl='" + getSourceUrl() + "'" +
         ", destUrl='" + getDestUrl() + "'" +
         ", bucketParamName='" + getBucketParamName() + "'" +
         ", bucketParamValue='" + getBucketParamValue() + "'" +
         ", authTokenDestName='" + getAuthTokenDestName() + "'" +
         ", authTokenDestValue='" + getAuthTokenDestValue() + "'" +
         ", cocoLabels='" + String.join(",",getCocoLabels()) + "'" +
         ", confThreshold='" + getConfThreshold() + "'" +
         "}";
   }
}