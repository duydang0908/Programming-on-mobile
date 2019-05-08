package com.example.duyda.onlinesaleshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.duyda.onlinesaleshop.Common.Common;
import com.example.duyda.onlinesaleshop.ViewHolder.OrderDetailAdapter;

public class OrderDetail extends AppCompatActivity {
    TextView order_id, order_phone, order_address, order_total;
    String order_id_value = "";
    RecyclerView lstProducts;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        order_id = findViewById(R.id.order_id);
        order_phone = findViewById(R.id.order_phone);
        order_address = findViewById(R.id.order_ship_to);
        order_total = findViewById(R.id.order_total);

        lstProducts = findViewById(R.id.lstProducts);
        lstProducts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstProducts.setLayoutManager(layoutManager);

        if(getIntent() != null)
        {
            order_id_value = getIntent().getStringExtra("OrderId");
        }

        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTotal());
        order_address.setText(Common.currentRequest.getAddress());

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getProducts());
        adapter.notifyDataSetChanged();
        lstProducts.setAdapter(adapter);


    }
}
