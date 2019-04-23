package com.example.duyda.onlinesaleshop;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.duyda.onlinesaleshop.Common.Common;
import com.example.duyda.onlinesaleshop.Database.Database;
import com.example.duyda.onlinesaleshop.Interface.ItemClickListener;
import com.example.duyda.onlinesaleshop.Models.Product;
import com.example.duyda.onlinesaleshop.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference productList;


    String categoryId = "";

    DatabaseReference categories;

    FirebaseRecyclerAdapter searchAdapter;

    List<String> suggestList = new ArrayList<>();

    MaterialSearchBar searchBar;

    FirebaseRecyclerAdapter adapter;

    Database localDB;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/my_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_product_list);


        localDB = new Database(this);

        database = FirebaseDatabase.getInstance();
        productList = database.getReference("Products");

        categories = database.getReference("Products");

        recyclerView = findViewById(R.id.recycler_product);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty() && categoryId != null) {
            if (Common.isConnectedToInternet(getBaseContext()))
                loadProductList();
            else {
                Toast.makeText(ProductList.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
//        loadProductList();

        searchBar = findViewById(R.id.searchBar);
        searchBar.setHint("Enter your search...");

        loadSuggest();
        searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<String>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase())) {
                        suggest.add(search);
                        Log.d("DMMLower:", suggest.toString());
                    }
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    private void loadProductList() {

        Query query = FirebaseDatabase
                .getInstance()
                .getReference("Product")
                .orderByChild("MenuId")
                .equalTo(categoryId);


        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(query, Product.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_item, parent, false);

                return new ProductViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ProductViewHolder holder, final int position, @NonNull final Product model) {
                holder.product_name.setText(model.getName());
                holder.product_price.setText(String.format("%s vnđ", model.getPrice().toString()));
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.product_image);

                if (localDB.isFavorite(adapter.getRef(position).getKey()))
                    holder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                holder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDB.isFavorite(adapter.getRef(position).getKey())) {
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            holder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(ProductList.this, "" + model.getName() + "was added to favorites!", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeToFavorites(adapter.getRef(position).getKey());
                            holder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(ProductList.this, "" + model.getName() + "was removed to favorites!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Product clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent productDetail = new Intent(ProductList.this, ProductDetail.class);
                        productDetail.putExtra("ProductId", adapter.getRef(position).getKey());
                        startActivity(productDetail);
                    }
                });
            }

        };

        recyclerView.setAdapter(adapter);
    }

    private void startSearch(CharSequence text) {
        String str = text.toString().toLowerCase();
        String search_text = str.substring(0, 1).toUpperCase() + str.substring(1);
        Query query = FirebaseDatabase
                .getInstance()
                .getReference("Product")
                .orderByChild("Name")
                .startAt(search_text);

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(query, Product.class)
                        .build();
//        adapter.stopListening();

        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_item, parent, false);

                return new ProductViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ProductViewHolder holder, final int position, @NonNull final Product model) {
                holder.product_name.setText(model.getName());
                holder.product_price.setText(String.format("%s vnđ", model.getPrice().toString()));
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.product_image);

                if (localDB.isFavorite(adapter.getRef(position).getKey()))
                    holder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                holder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDB.isFavorite(adapter.getRef(position).getKey())) {
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            holder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(ProductList.this, "" + model.getName() + "was added to favorites!", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeToFavorites(adapter.getRef(position).getKey());
                            holder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(ProductList.this, "" + model.getName() + "was removed to favorites!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Product clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent productDetail = new Intent(ProductList.this, ProductDetail.class);
                        productDetail.putExtra("ProductId", adapter.getRef(position).getKey());
                        startActivity(productDetail);
                    }
                });
            }

        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void loadSuggest() {
        categories.child(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Product item = postSnapshot.getValue(Product.class);
                    suggestList.add(item.getName());
                    Log.d("DMMLoadsearch:", item.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
