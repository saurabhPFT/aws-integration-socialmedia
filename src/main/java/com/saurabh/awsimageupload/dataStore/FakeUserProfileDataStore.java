package com.saurabh.awsimageupload.dataStore;


import com.saurabh.awsimageupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("2bdc9310-cd60-4b00-acdd-ddf4bf05ddc7"),"jANETJones",null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("a2a1ffb8-6ff3-47a2-a700-faa9646f498b"),"antonioJunior",null));
    }

    public List<UserProfile> getUserProfiles(){
        return USER_PROFILES;

    }
}
