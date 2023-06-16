package com.mobiledevelopment.core.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobiledevelopment.core.models.Category;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.models.Voucher;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductRepository {
    public static String TAG = "ProductRepository";
    private final CollectionReference collectionRef =
            FirebaseFirestore.getInstance().collection("Product");
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public ListenerRegistration getProducts(
            String idCategory,
            OnGetProductsCompleteListener onCompleteListener){
        return collectionRef.addSnapshotListener((querySnapshot, exception)->{
            List<Product> products = new ArrayList<>();

            if (exception != null) {
                Log.w(
                        TAG,
                        "getAllProducts() - Failed to add listener. Exception: ",
                        exception);
                onCompleteListener.onComplete(products);
                return;
            }

            assert querySnapshot != null;

            for (QueryDocumentSnapshot doc : querySnapshot) {
                Product product = doc.toObject(Product.class);

                if(product.getIdCategory().compareTo(idCategory) == 0)
                    products.add(product);
            }

            Log.d(TAG, "getAllProducts() of category: " + idCategory + " - result: " + products);
            onCompleteListener.onComplete(products);
        });
    }

    /**
     *
     * @param ids should be 30 max due to Firestore limitation
     */
    public void getProductsByIds(
            List<String> ids,
            OnGetProductsCompleteListener onCompleteListener){
        Log.d(
                TAG,
                "Calling getProductsByIds() - param ids = " + ids);
        collectionRef
                .whereIn(FieldPath.documentId(), ids)
                .get().addOnCompleteListener(task -> {
                    List<Product> result = new ArrayList<>();
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            result.add(doc.toObject(Product.class));
                        }
                    }
                    else {
                        Log.d(
                                TAG,
                                "getProductsByIds() - ids = " + ids + "; Failed with exception:",
                                task.getException());
                    }

                    Log.d(TAG, "getProductsByIds() - result: " + result);
                    onCompleteListener.onComplete(result);
                });
    }

    private void checkExistedProduct(OnCheckProductExistedListener onCompleteListener, String nameProduct, String idCategory) {
        collectionRef
                .whereEqualTo("name", nameProduct)
                .whereEqualTo("idCategory",idCategory)
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

    public void updateProduct(OnUpdateProductListener onUpdateListener, Product product) {
        checkExistedProduct(new OnCheckProductExistedListener() {
            @Override
            public void onComplete(boolean result) {
                if (result != false) {
                    onUpdateListener.onError("Name is existed");
                    return;
                } else {
                    collectionRef
                            .document(product.getId())
                            .set(product)
                            .addOnSuccessListener(Void -> {
                                Log.d(
                                        TAG,
                                        "updateProduct() - productId: " + product.getId() + " DocumentSnapshot successfully written!")
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
        }, product.getName(), product.getIdCategory());
    }

    public void updateProductWithNewImage(OnUpdateProductListener onUpdateListener, Product product, List<byte[]> dataImages) {
        checkExistedProduct(new OnCheckProductExistedListener() {
            @Override
            public void onComplete(boolean result) {
                if (result != false) {
                    onUpdateListener.onError("Name is existed");
                    return;
                } else {
                    uploadProductImage(new OnUploadProductImageCompleteListener() {
                        @Override
                        public void onComplete(List<String> uris) {
                            product.setImages(uris);

                            collectionRef
                                    .document(product.getId())
                                    .set(product)
                                    .addOnSuccessListener(Void -> {
                                        Log.d(
                                                TAG,
                                                "updateProduct() - productId: " + product.getId() + " DocumentSnapshot successfully written!")
                                        ;
                                        onUpdateListener.onComplete();
                                    })
                                    .addOnFailureListener(e -> {
                                        onUpdateListener.onError(e.getMessage());
                                        Log.w(
                                                TAG,
                                                "updateProduct() - Error updating document; Exception:",
                                                e);
                                    });
                        }
                    }, dataImages, product.getName());
                }
            }
        }, product.getName(), product.getIdCategory());
    }

    public void uploadProductImage(OnUploadProductImageCompleteListener onCompleteListener, List<byte[]> dataImages, String nameProduct) {
        final int[] count = {0};
        List<String> uriImages = new ArrayList<>();

        for (byte[] data:dataImages) {
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString();

            final StorageReference ref = storageRef
                    .child("productPics")
                    .child(nameProduct)
                    .child(uuidString);

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
                        uriImages.add(downloadUri.toString());
                        count[0]++;

                        if(count[0] == dataImages.size()){
                            onCompleteListener.onComplete(uriImages);
                        }
                    } else {
                        Log.d(
                                TAG,
                                "uploadImage() - Error upload image; Exception:",
                                task.getException());
                    }
                }
            });
        }
    }

    public void addProduct(OnAddProductListener onProductListener,
                           Product product, List<byte[]> dataImages) {
        try {
            uploadProductImage(new OnUploadProductImageCompleteListener() {
                @Override
                public void onComplete(List<String> urls) {
                    product.setImages(urls);

                    collectionRef.add(product)
                            .addOnSuccessListener(documentReference -> {
                                Log.d(
                                        TAG,
                                        "addProduct() - DocumentSnapshot written with ID: " + documentReference.getId());
                                onProductListener.onComplete(documentReference.getId());
                            })
                            .addOnFailureListener(e -> {
                                onProductListener.onError(e.getMessage());

                                Log.w(
                                        TAG,
                                        "addProduct() - Error adding document; Exception:",
                                        e);
                            });
                }
            },dataImages,product.getName());
        }catch (Exception e){
            onProductListener.onError(e.getMessage());
        }
    }

    public void deleteProduct(OnDeleteProductListener onDeleteListener, String idProduct) {
        collectionRef
                .document(idProduct)
                .delete()
                .addOnSuccessListener(Void -> {
                    Log.d(
                            TAG,
                            "deleteProduct() - productId: " + idProduct + " DocumentSnapshot successfully deleted!")
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

    public void getProduct(String idProduct, OnGetProductListener onGetListener) {
        collectionRef.document(idProduct).get().addOnCompleteListener(task -> {
            Product product = new Product();

            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "getProductById() - id = " + idProduct + "; Data:" + document.getData());
                    product = document.toObject(Product.class);
                } else {
                    onGetListener.onError("Product is not existed");

                    Log.d(TAG, "getProductById() - id = " + idProduct + "; No such document");
                }
            } else {
                onGetListener.onError(task.getException().getMessage());
                Log.d(
                        TAG,
                        "getProductById() - id = " + idProduct + "; Failed with exception:",
                        task.getException());
            }

            onGetListener.onComplete(product);
        });
    }

    public interface OnGetProductListener {
        void onComplete(Product product);

        void onError(String errorMessage);
    }

    public interface OnCheckProductExistedListener {
        void onComplete(boolean result);
    }

    public interface OnUploadProductImageCompleteListener{
        void onComplete(List<String> urls);
    }
    public interface OnGetProductsCompleteListener {
        void onComplete(List<Product> products);
    }

    public interface OnAddProductListener {
        void onComplete(String idProduct);

        void onError(String errorMessage);
    }

    public interface OnUpdateProductListener {
        void onComplete();

        void onError(String errorMessage);
    }

    public interface OnDeleteProductListener {
        void onComplete();

        void onError(String errorMessage);
    }
}
