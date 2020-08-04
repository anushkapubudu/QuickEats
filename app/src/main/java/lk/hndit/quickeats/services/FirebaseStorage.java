package lk.hndit.quickeats.services;

import android.net.Uri;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseStorage {

    private static FirebaseStorage firebaseStorage;
    private static com.google.firebase.storage.FirebaseStorage storage = com.google.firebase.storage.FirebaseStorage.getInstance();
    private static StorageReference reference = storage.getReference();
    private static String downloadUri;

    private FirebaseStorage() {
    }

    public static FirebaseStorage getInstance(){
        return firebaseStorage == null ? new FirebaseStorage() : firebaseStorage;
    }

    public static StorageReference getstorageReference(){
        return reference;
    }


    public UploadTask addNewPicture(final String child, final String filename, Uri uri){

        final UploadTask uploadTask = reference.child(child).child(filename).putFile(uri);

        return uploadTask;
    }
}
