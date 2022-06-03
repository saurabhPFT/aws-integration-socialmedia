package com.saurabh.awsimageupload.profile;

import com.saurabh.awsimageupload.bucket.BucketName;
import com.saurabh.awsimageupload.filestore.FileStore;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


@Service
public class UserProfileService {

    private final UserProfileDataAccessService userProfileDataAccessService;
    private final FileStore fileStore;

    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService,
                              FileStore fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

    List<UserProfile> getUserProfiles() {
        return userProfileDataAccessService.getUserProfiles();

    }

    public void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
        // 1. Check if image is not empty
        isEmptyFile(file);

        // 2. If file is an Image
        isImage(file);

        // 3. The User Exists in our database
        UserProfile user = getUserProfileOrThrow(userProfileId);

        // 4. Grab Some metadata from file if any
        Map<String, String> metadata = extractMetaData(file);

        //5. Store the image in s3 and update database with s3 image link.
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getUserProfileId());
        String fileName = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());

        try {
            fileStore.save(path, fileName, Optional.of(metadata), file.getInputStream());
            user.setUserProfileImageLink(fileName);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }


    }

    private Map<String, String> extractMetaData(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    private UserProfile getUserProfileOrThrow(UUID userProfileId) {
        return userProfileDataAccessService.getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User Profile %s not found", userProfileId)));
    }

    private void isImage(MultipartFile file) {
        if (!Arrays.asList(ContentType.IMAGE_JPEG.getMimeType(), ContentType.IMAGE_GIF.getMimeType(), ContentType.IMAGE_PNG.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException("File must be an Image [" + file.getContentType() +"]");
        }
    }

    private void isEmptyFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("cannot upload empty file [ " + file.getSize() + "]");
        }
    }

    public byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile user = getUserProfileOrThrow(userProfileId);

       String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(),
               user.getUserProfileId());

      if(user.getUserProfileImageLink()==null || user.getUserProfileImageLink().isEmpty()){
          return new byte[0];
      }
      return fileStore.download(path,user.getUserProfileImageLink());
    }
}
