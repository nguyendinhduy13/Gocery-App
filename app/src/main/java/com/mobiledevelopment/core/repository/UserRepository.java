package com.mobiledevelopment.core.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobiledevelopment.core.models.Order;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;

public class UserRepository {
    public static String TAG = "UserRepository";

    private final CollectionReference collectionRef = FirebaseFirestore.getInstance()
            .collection("User");

    private final StorageReference storageRootRef = FirebaseStorage.getInstance().getReference();

    public ListenerRegistration getUserStreamById(
            String id,
            OnGetUserCompleteListener onCompleteListener) {

        return collectionRef.document(id).addSnapshotListener((querySnapshot, exception) -> {
            User result = User.undefinedUser;

            if (exception != null) {
                Log.w(
                        TAG,
                        "getUserStreamById() - Failed to add listener. Exception: ",
                        exception);
                onCompleteListener.onComplete(result);
                return;
            }

            assert querySnapshot != null;
            if (querySnapshot.exists()) {
                Log.d(
                        TAG,
                        "getUserStreamById() - id = " + id + "; Data:" + querySnapshot.getData());
                result = querySnapshot.toObject(User.class);
            }
            onCompleteListener.onComplete(result);
        });
    }

    public void getUserById(
            String id,
            OnGetUserCompleteListener onCompleteListener) {

        collectionRef.document(id).addSnapshotListener((querySnapshot, exception) -> {
            User result = User.undefinedUser;

            if (exception != null) {
                Log.w(
                        TAG,
                        "getUserById() - Failed to add listener. Exception: ",
                        exception);
                onCompleteListener.onComplete(result);
                return;
            }

            assert querySnapshot != null;
            if (querySnapshot.exists()) {
                Log.d(TAG, "getUserById() - id = " + id + "; Data:" + querySnapshot.getData());
                result = querySnapshot.toObject(User.class);
            }
            onCompleteListener.onComplete(result);
        });
    }

    public ListenerRegistration getAllUsers(UserRepository.OnGetAllUsersCompleteListener onCompleteListener) {

        return collectionRef
                .addSnapshotListener((querySnapshot, exception) -> {
                    List<User> result = new ArrayList<>();

                    if (exception != null) {
                        onCompleteListener.onComplete(result);
                        return;
                    }

                    assert querySnapshot != null;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        User user = doc.toObject(User.class);
                        result.add(user);
                    }
                    onCompleteListener.onComplete(result);
                });
    }

    public void uploadAvatarImage(
            String userId, Uri avatarUri,
            OnUploadAvatarCompleteListener onCompleteListener) {

        StorageReference avatarRef =
                storageRootRef.child("user/" + userId + "/" + Calendar.getInstance()
                        .getTime() + ".png");

        avatarRef
                .putFile(avatarUri)
                .addOnFailureListener(exception -> Log.d(
                        TAG,
                        "uploadAvatarImage() - Failed to upload image; Exception: " + exception.getMessage()))
                .addOnSuccessListener(taskSnapshot -> avatarRef.getDownloadUrl()
                        .addOnSuccessListener(
                                downloadPhotoUrl -> {
                                    onCompleteListener.onComplete(downloadPhotoUrl.toString());
                                    Log.d(
                                            TAG,
                                            "uploadAvatarImage() - Success. downloadPhotoUrl: " + downloadPhotoUrl);
                                }));

    }

    public void updateUser(User user, OnUpdateUserCompleteListener onCompleteListener) {
        collectionRef.document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(
                            TAG,
                            "updateUser() - userId: " + user.getId() + " DocumentSnapshot successfully written!");
                    onCompleteListener.onComplete();
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

    }

    public void updateUserToken(User user,String token){
        collectionRef.document(user.getId())
                .update("token",token)
                .addOnSuccessListener(aVoid->{

                })
                .addOnFailureListener(e -> {

                });
    }

    public void getUserToken(String idUser,OnGetUserTokenCompleteListener onCompleteListener){
        collectionRef.document(idUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            String token = document.getString("token");
                            onCompleteListener.onComplete(token);
                        }
                    }
                });
    }

    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public interface OnGetUserCompleteListener {
        void onComplete(User user);
    }

    public interface OnGetUserTokenCompleteListener{
        void onComplete(String token);
    }

    public interface OnGetAllUsersCompleteListener {
        void onComplete(List<User> users);
    }

    public interface OnUpdateUserCompleteListener {
        void onComplete();
    }

    public interface OnUploadAvatarCompleteListener {
        void onComplete(String avatarUrl);
    }
}
