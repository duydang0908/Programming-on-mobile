package com.example.duyda.onlinesaleshop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.duyda.onlinesaleshop.Common.Common;
import com.example.duyda.onlinesaleshop.Database.Database;
import com.example.duyda.onlinesaleshop.Models.Order;
import com.example.duyda.onlinesaleshop.Models.Product;
import com.example.duyda.onlinesaleshop.Models.Rating;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductDetail extends AppCompatActivity implements RatingDialogListener {

    TextView product_name, product_price, product_description;
    ImageView product_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart, btnRating;
    ElegantNumberButton btnNumber;

    RatingBar ratingBar;

    FirebaseUser currentUser;

    String productId = "";

    FirebaseDatabase database;

    DatabaseReference product;

    FirebaseRecyclerAdapter adapter;

    DatabaseReference ratingTbl;

    Product currentProduct;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_product_detail);

        database = FirebaseDatabase.getInstance();
        product = database.getReference("Product");
        ratingTbl = database.getReference("Rating");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btnNumber = findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btnCart);

        ratingBar = findViewById(R.id.ratingBar);
        btnRating = findViewById(R.id.btnRating);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingBarDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        productId,
                        currentProduct.getName(),
                        btnNumber.getNumber(),
                        currentProduct.getPrice(),
                        currentProduct.getDiscount()
                ));
                Toast.makeText(ProductDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        product_description = findViewById(R.id.product_description);
        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);

        product_image = findViewById(R.id.img_product);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapedAppbar);

        if (getIntent() != null)
            productId = getIntent().getStringExtra("ProductId");
        if (!productId.isEmpty()) {
            if (Common.isConnectedToInternet(getBaseContext())) {
                loadProductDetail(productId);
                loadRatingProduct(productId);
            } else
                Toast.makeText(ProductDetail.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();

        }

    }

    private void loadRatingProduct(String productId) {
        com.google.firebase.database.Query productRating = ratingTbl.orderByChild("productId").equalTo(productId);

        productRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValure());
                    count++;
                }
                if (count != 0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void showRatingBarDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Summit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "OK", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this product")
                .setDescription("Please select some star and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogStyle) //??
                .create(ProductDetail.this)
                .show();
    }


    private void loadProductDetail(final String productId) {
        product.child(productId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentProduct = dataSnapshot.getValue(Product.class);
                Picasso.with(getBaseContext()).load(currentProduct.getImage()).into(product_image);
                collapsingToolbarLayout.setTitle(currentProduct.getName());
                product_price.setText(currentProduct.getPrice());
                product_name.setText(currentProduct.getName());
                product_description.setText(currentProduct.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        final Rating rating = new Rating(currentUser.getUid(), productId, String.valueOf(i), s);
        ratingTbl.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUser.getUid()).exists()) {
                    ratingTbl.child(currentUser.getUid()).removeValue();
                    ratingTbl.child(currentUser.getUid()).setValue(rating);
                } else {
                    ratingTbl.child(currentUser.getUid()).setValue(rating);
                }
                Toast.makeText(ProductDetail.this, "Thank for summit ratting!!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
