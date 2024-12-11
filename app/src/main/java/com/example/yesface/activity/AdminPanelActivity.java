package com.example.yesface.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yesface.R;
import com.example.yesface.model.Cosmetic;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class AdminPanelActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;  // For gallery
    private static final int PICK_CAMERA_REQUEST = 2; // For camera

    private EditText etProductName, etProductDescription, etProductPrice, etProductRating, etProductImageUrl;
    private ImageView ivSelectedImage;
    private Button btnSelectImage, btnSaveProduct;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        // Initialize Firestore and Firebase Storage
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Initialize UI components
        etProductName = findViewById(R.id.et_product_name);
        etProductDescription = findViewById(R.id.et_product_desc);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductRating = findViewById(R.id.et_product_rating);
        etProductImageUrl = findViewById(R.id.et_product_image_url);
        ivSelectedImage = findViewById(R.id.iv_selected_image);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnSaveProduct = findViewById(R.id.btn_save_product);

        // Set button click listeners
        btnSelectImage.setOnClickListener(v -> showImageSourceOptions());
        btnSaveProduct.setOnClickListener(v -> saveProductToFirestore());
    }

    private void showImageSourceOptions() {
        // Open gallery to pick an image
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedImageUri = data.getData();
                ivSelectedImage.setImageURI(selectedImageUri);
                etProductImageUrl.setText(selectedImageUri.toString()); // Optionally set the image URI
            } else if (requestCode == PICK_CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ivSelectedImage.setImageBitmap(photo);
                selectedImageUri = getImageUri(photo); // Convert Bitmap to URI
                etProductImageUrl.setText(selectedImageUri.toString());
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        // Convert Bitmap to URI
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void saveProductToFirestore() {
        // Get data from input fields
        String name = etProductName.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();
        String priceString = etProductPrice.getText().toString().trim();
        String rating = etProductRating.getText().toString().trim();

        // Validate input fields
        if (name.isEmpty() || description.isEmpty() || priceString.isEmpty() || rating.isEmpty() || selectedImageUri == null) {
            Toast.makeText(AdminPanelActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert price to double
        double price = Double.parseDouble(priceString);

        // Create a Cosmetic object
        Cosmetic newCosmetic = new Cosmetic(name, description, price, "", rating);

        // Save the new cosmetic product to Firestore
        db.collection("cosmetic")
                .add(newCosmetic)
                .addOnSuccessListener(documentReference -> {
                    // Upload the image to Firebase Storage
                    uploadImageToStorage(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminPanelActivity.this, "Failed to save product", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImageToStorage(String productId) {
        // Get the image file reference
        StorageReference productImageRef = storageReference.child("products/" + productId + ".jpg");

        // Upload the image to Firebase Storage
        productImageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    productImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save the image URL in Firestore
                        db.collection("cosmetic")
                                .document(productId)
                                .update("imageUrl", uri.toString())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(AdminPanelActivity.this, "Product saved successfully", Toast.LENGTH_SHORT).show();
                                    clearFields();  // Clear input fields after successful save
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(AdminPanelActivity.this, "Failed to update image URL", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminPanelActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        // Clear input fields after saving the product
        etProductName.setText("");
        etProductDescription.setText("");
        etProductPrice.setText("");
        etProductRating.setText("");
        etProductImageUrl.setText("");
        ivSelectedImage.setImageResource(R.drawable.ic_launcher_foreground); // Reset image
    }
}
