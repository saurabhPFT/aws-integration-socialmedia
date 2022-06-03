package com.saurabh.awsimageupload.bucket;

public enum BucketName {

    PROFILE_IMAGE("amazon-image-upload-123");

    public String getBucketName() {
        return bucketName;
    }

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
