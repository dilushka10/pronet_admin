package com.suresh.pronetadmin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.suresh.pronetadmin.modal.Category;
import com.suresh.pronetadmin.modal.Product;

import java.util.ArrayList;
import java.util.UUID;

public class ProductActivity extends AppCompatActivity {
public static final String TAG = ProductActivity.class.getName();
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    ArrayList<String> categorylist;


    EditText txt_productName;
    EditText txt_categoryName;
    EditText txt_price;
    EditText txt_qty;
    EditText txt_desc;
    ImageButton imageButton;
    Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        categorylist = new ArrayList<>();

        firestore.collection("categories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        categorylist.clear();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Category category = snapshot.toObject(Category.class);
                            categorylist.add(category.getCatName());
                        }
//                        categoryAdapter.notifyDataSetChanged();
                    }
                });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProductActivity.this, android.R.layout.simple_dropdown_item_1line, categorylist);
        AutoCompleteTextView textView = findViewById(R.id.txt_category);
        textView.setThreshold(1);
        textView.setAdapter(adapter);

        txt_productName = findViewById(R.id.product_name);
        txt_categoryName = findViewById(R.id.txt_category);
        txt_price = findViewById(R.id.price);
        txt_qty = findViewById(R.id.quantity);
        txt_desc = findViewById(R.id.description);
        imageButton = findViewById(R.id.imageButton_product);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));

            }
        });

        findViewById(R.id.btn_product_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imagePath != null) {

                    String productName = txt_productName.getText().toString();
                    String categoryName = txt_categoryName.getText().toString();
                    String price = txt_price.getText().toString();
                    String qty = txt_qty.getText().toString();
                    String desc = txt_desc.getText().toString();

                    Log.w(TAG,categoryName);

                    if (productName.isEmpty()) {
                        Toast.makeText(ProductActivity.this, "Enter the Product Name", Toast.LENGTH_SHORT).show();
                    } else if (categoryName.isEmpty()) {
                        Toast.makeText(ProductActivity.this, "Select a Category", Toast.LENGTH_SHORT).show();
                    } else if (price.isEmpty()) {
                        Toast.makeText(ProductActivity.this, "Enter the Price", Toast.LENGTH_SHORT).show();
                    } else if (qty.isEmpty()) {
                        Toast.makeText(ProductActivity.this, "Enter the Quantity", Toast.LENGTH_SHORT).show();
                    } else if (desc.isEmpty()) {
                        Toast.makeText(ProductActivity.this, "Enter the Description about the Product", Toast.LENGTH_SHORT).show();
                    } else {

                        String imageId = UUID.randomUUID().toString();

                        Product product =
                                new Product(productName, categoryName, price,
                                        qty, desc, imageId);


                        ProgressDialog dialog = new ProgressDialog(ProductActivity.this);
                        dialog.setMessage("New Product Adding.....");
                        dialog.setCancelable(false);
                        dialog.show();


                        firestore.collection("products")
                                .add(product)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        String productID = documentReference.getId().toString();

                                        dialog.setMessage("Uploading Image....");
                                        StorageReference reference = storage.getReference("product_images")
                                                .child(productID);
                                        reference.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                dialog.dismiss();
                                                Toast.makeText(ProductActivity.this, "Product added Successfully", Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                                Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                dialog.setMessage("Uploading... " + (int) progress + "%");
                                            }
                                        });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });


                    }
                } else {
                    Toast.makeText(ProductActivity.this, "Please Select a Image", Toast.LENGTH_LONG).show();
                }

            }

        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagePath = result.getData().getData();

                        Picasso.get().load(imagePath)
                                .centerCrop()
                                .fit()
                                .into(imageButton);

                    }
                }
            }
    );
}