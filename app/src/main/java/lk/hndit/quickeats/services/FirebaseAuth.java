package lk.hndit.quickeats.services;

import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuth {

    private com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
    private static FirebaseAuth mauth;

    private FirebaseAuth() {
    }

    public static FirebaseAuth getInstance(){
        return mauth == null ? new FirebaseAuth() : mauth;
    }

    public FirebaseUser getCurrentUser(){
        return auth.getCurrentUser();
    }

    public void signOutCurrentUser(){
        auth.signOut();
    }


}
