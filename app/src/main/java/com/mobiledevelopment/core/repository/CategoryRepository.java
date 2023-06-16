package com.mobiledevelopment.core.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobiledevelopment.BuildConfig;
import com.mobiledevelopment.core.models.Category;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoryRepository {
    public static String TAG = "CategoryRepository";
    private List<Category> categories = new ArrayList<>();
    private final CollectionReference collectionRef = FirebaseFirestore.getInstance()
            .collection("Category");
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final NotificationApi notificationApi;

    public CategoryRepository() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        notificationApi = new Retrofit.Builder()
                .baseUrl(BuildConfig.SEND_NOTIFICATION_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(NotificationApi.class);
    }

    public ListenerRegistration getCategories(
            CategoryRepository.OnGetCategoriesCompleteListener onCompleteListener) {
        return collectionRef
                .addSnapshotListener((querySnapshot, exception) -> {
                    categories = new ArrayList<>();

                    if (exception != null) {
                        Log.w(
                                TAG,
                                "getAllCategories() - Failed to add listener. Exception: ",
                                exception);
                        onCompleteListener.onComplete(categories);
                        return;
                    }

                    assert querySnapshot != null;

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Category category = doc.toObject(Category.class);
                        category.setId(doc.getId());
                        categories.add(category);
                    }
                    Log.d(TAG, "getAllCategories() - result: " + categories);
                    onCompleteListener.onComplete(categories);
                });
    }

    private void checkExistedCategory(OnCheckCategoryExistedListener onCompleteListener, String name) {
        collectionRef
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Document with field does not exist
                            onCompleteListener.onComplete(false);
                        } else {
                            // Document with field exists
                            onCompleteListener.onComplete(true);
                        }
                    } else {
                        // Handle errors
                    }
                });

    }

    public void getCategory(String idCategory, OnGetCategoryListener onGetListener) {
        collectionRef.document(idCategory).get().addOnCompleteListener(task -> {
            Category category = new Category();

            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "getCategoryById() - id = " + idCategory + "; Data:" + document.getData());
                    category = document.toObject(Category.class);
                } else {
                    onGetListener.onError("Category is not existed");

                    Log.d(TAG, "getCategoryById() - id = " + idCategory + "; No such document");
                }
            } else {
                onGetListener.onError(task.getException().getMessage());
                Log.d(
                        TAG,
                        "getCategoryById() - id = " + idCategory + "; Failed with exception:",
                        task.getException());
            }

            onGetListener.onComplete(category);
        });
    }

    public void addCategory(
            OnAddCategoryFailureListener onFailureListener,
            OnAddCategoryCompleteListener onCompleteListener, String name, byte[] imageData) {
        checkExistedCategory(new OnCheckCategoryExistedListener() {
            @Override
            public void onComplete(boolean result) {
                if (result != false) {
                    onFailureListener.onError("Category is existed");
                    return;
                } else {
                    Category category = new Category();
                    category.setName(name);
                    uploadCategoryImage(new OnUploadCategoryImageCompleteListener() {
                        @Override
                        public void onComplete(String uri) {
                            category.setImage(uri);

                            collectionRef.add(category).addOnSuccessListener(documentReference -> {
                                        Log.d(
                                                TAG,
                                                "addCategory() - DocumentSnapshot written with ID: " + documentReference.getId());
                                        onCompleteListener.onComplete(documentReference.getId());
                                    })
                                    .addOnFailureListener(e -> Log.w(
                                            TAG,
                                            "addCategory() - Error adding document; Exception:",
                                            e));
                        }
                    }, imageData, name);
                }
            }
        }, name);
    }

    public void updateCategory(OnUpdateCategoryListener onUpdateListener, Category category) {
        checkExistedCategory(new OnCheckCategoryExistedListener() {
            @Override
            public void onComplete(boolean result) {
                if (result != false) {
                    onUpdateListener.onError("Name is existed");
                    return;
                } else {
                    collectionRef
                            .document(category.getId())
                            .set(category)
                            .addOnSuccessListener(Void -> {
                                Log.d(
                                        TAG,
                                        "updateCategory() - categoryId: " + category.getId() + " DocumentSnapshot successfully written!")
                                ;
                                onUpdateListener.onComplete();
                            })
                            .addOnFailureListener(e -> {
                                onUpdateListener.onError(e.getMessage());
                                Log.w(
                                        TAG,
                                        "updateCategory() - Error updating document; Exception:",
                                        e);
                            });
                }
            }
        }, category.getName());


    }

    public void updateCategoryWithNewImage(OnUpdateCategoryListener onUpdateListener, Category category, byte[] dataImage) {
        checkExistedCategory(new OnCheckCategoryExistedListener() {
            @Override
            public void onComplete(boolean result) {
                if (result != false) {
                    onUpdateListener.onError("Name is existed");
                    return;
                } else {
                    uploadCategoryImage(new OnUploadCategoryImageCompleteListener() {
                        @Override
                        public void onComplete(String uri) {
                            category.setImage(uri);

                            collectionRef
                                    .document(category.getId())
                                    .set(category)
                                    .addOnSuccessListener(Void -> {
                                        Log.d(
                                                TAG,
                                                "updateCategory() - categoryId: " + category.getId() + " DocumentSnapshot successfully written!")
                                        ;
                                        onUpdateListener.onComplete();
                                    })
                                    .addOnFailureListener(e -> {
                                        onUpdateListener.onError(e.getMessage());
                                        Log.w(
                                                TAG,
                                                "updateCategory() - Error updating document; Exception:",
                                                e);
                                    });
                        }
                    }, dataImage, category.getName());
                }
            }
        }, category.getName());
    }

    public void deleteCategory(OnDeleteCategoryListener onDeleteListener, String idCategory) {
        collectionRef
                .document(idCategory)
                .delete()
                .addOnSuccessListener(Void -> {
                    Log.d(
                            TAG,
                            "deleteCategory() - categoryId: " + idCategory + " DocumentSnapshot successfully deleted!")
                    ;
                    onDeleteListener.onComplete();
                })
                .addOnFailureListener(e -> {
                    onDeleteListener.onError(e.getMessage());
                    Log.w(
                            TAG,
                            "delete() - Error deleting document; Exception:",
                            e);
                });
    }

    public void uploadCategoryImage(OnUploadCategoryImageCompleteListener onCompleteListener, byte[] data, String nameCategory) {
        // fix cho nay nha !!! them child
        final StorageReference ref = storageRef
                .child("categoryPics")
                .child(nameCategory);


        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.d(
                            TAG,
                            "uploadImage() - Error upload image; Exception:",
                            task.getException());
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    onCompleteListener.onComplete(downloadUri.toString());
                } else {
                    Log.d(
                            TAG,
                            "uploadImage() - Error upload image; Exception:",
                            task.getException());
                }
            }
        });
    }

    public interface OnCheckCategoryExistedListener {
        void onComplete(boolean result);
    }

    public interface OnDeleteCategoryListener {
        void onComplete();

        void onError(String errorMessage);
    }

    public interface OnGetCategoriesCompleteListener {
        void onComplete(List<Category> categories);
    }

    public interface OnGetCategoryListener {
        void onComplete(Category category);

        void onError(String errorMessage);
    }

    public interface OnUploadCategoryImageCompleteListener {
        void onComplete(String uri);
    }

    public interface OnAddCategoryFailureListener {
        void onError(String error);
    }

    public interface OnAddCategoryCompleteListener {
        void onComplete(String idCategory);
    }

    public interface OnUpdateCategoryListener {
        void onComplete();

        void onError(String errorMessage);
    }
}
