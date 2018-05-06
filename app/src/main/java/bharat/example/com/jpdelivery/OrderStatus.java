package bharat.example.com.jpdelivery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import bharat.example.com.jpdelivery.Common.Common;
import bharat.example.com.jpdelivery.Interface.ItemClickListener;
import bharat.example.com.jpdelivery.Remote.APIService;
import bharat.example.com.jpdelivery.ViewHolder.OrderViewHolder;
import bharat.example.com.jpdelivery.models.MyResponse;
import bharat.example.com.jpdelivery.models.Notification;
import bharat.example.com.jpdelivery.models.Request;
import bharat.example.com.jpdelivery.models.Sender;
import bharat.example.com.jpdelivery.models.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderStatus extends AppCompatActivity {
    private static final String TAG = "OrderStatus";

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    FirebaseDatabase database;
    APIService mService;
    DatabaseReference requests;
    String pos;
    Request currentR;
    String rson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        final String email;
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView) findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mService = Common.getFCMService();



        loadOrders(email);


        Log.d(TAG, "onif " + email);

    }

    private void loadOrders(String email) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("status").equalTo("2")
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, final int position) {
                Log.d(TAG, "populateViewHolder: started "+ adapter.getRef(position).getKey());
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToString(model.getStatus()));
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderdate.setText(model.getDatetime());
                viewHolder.orderName.setText(model.getName());
                pos = adapter.getRef(position).getKey();
                currentR = model;


                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent details = new Intent(OrderStatus.this,OrderDetails.class);
                        details.putExtra("OrderId",adapter.getRef(position).getKey());
                        Common.currentRequest=model;
                        startActivity(details);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals("Delivered")) {
            showUpdatedialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
            showUpdateDialogAdmin(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));

        }
        else if (item.getTitle().equals("Deliverey attempted")){
            atemptedOrder(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
            atemptedOrderAdmin(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals("Delivery Failed")){
            failedOrder(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
            failedorderAdmin(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }


        return super.onContextItemSelected(item);
    }

    private void failedorderAdmin(final String key, Request item) {
        requests.child(key).child("status").setValue("5");
        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByChild("isServerToken").equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Token token = postSnapShot.getValue(Token.class);

                            Notification notification = new Notification("Joe's Pizzeria","The delivery of order "+key+" was Failed!");
                            Sender content = new Sender(token.getToken(),notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1){
                                                Toast.makeText(OrderStatus.this, " order has been Updated", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(OrderStatus.this, "Order Updated but failed to Notify", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("onFailure: ",t.getMessage() );

                                        }
                                    });


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    private void failedOrder(final String key, Request item) {

        requests.child(key).child("status").setValue("5");
        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Token token = postSnapShot.getValue(Token.class);

                            Notification notification = new Notification("Joe's Pizzeria","The delivery of order "+key+" was Failed!");
                            Sender content = new Sender(token.getToken(),notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1){
                                                Toast.makeText(OrderStatus.this, " Order has been Updated!", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(OrderStatus.this, "Order Updated but failed to Notify", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("onFailure: ",t.getMessage() );

                                        }
                                    });


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void atemptedOrderAdmin(final String key, Request item) {

        requests.child(key).child("status").setValue("4");

        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByChild("isServerToken").equalTo(true)
            .addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
            {
                Token token = postSnapShot.getValue(Token.class);

                Notification notification = new Notification("Joe's Pizzeria","The delivery of order "+key+" was Attempted!!");
                Sender content = new Sender(token.getToken(),notification);

                mService.sendNotification(content)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.body().success == 1){
                                    Toast.makeText(OrderStatus.this, " Order has been Updated!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(OrderStatus.this, "Order Updated but failed to Notify", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Log.e("onFailure: ",t.getMessage() );

                            }
                        });


            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
}

    private void showUpdateDialogAdmin(final String key, Request item) {
        requests.child(key).child("status").setValue("3");
        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByChild("isServerToken").equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Token token = postSnapShot.getValue(Token.class);

                            Notification notification = new Notification("Joe's Pizzeria","The delivery of order "+key+" was Successful!");
                            Sender content = new Sender(token.getToken(),notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1){
                                                Toast.makeText(OrderStatus.this, " order has been Updated", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(OrderStatus.this, "Order Updated but failed to Notify", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("onFailure: ",t.getMessage() );

                                        }
                                    });


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    private void showUpdatedialog(final String key, Request item) {

        requests.child(key).child("status").setValue("3");
        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Token token = postSnapShot.getValue(Token.class);

                            Notification notification = new Notification("Joe's Pizzeria","The delivery of order "+key+" was successful!");
                            Sender content = new Sender(token.getToken(),notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1){
                                                Toast.makeText(OrderStatus.this, " Order has been Updated!", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(OrderStatus.this, "Order Updated but failed to Notify", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("onFailure: ",t.getMessage() );

                                        }
                                    });


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void atemptedOrder(final String key, Request item) {

        requests.child(key).child("status").setValue("4");
        DatabaseReference tokens = database.getReference("Tokens");


        tokens.orderByKey().equalTo(item.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Token token = postSnapShot.getValue(Token.class);

                            Notification notification = new Notification("Joe's Pizzeria","The delivery of order "+key+" was attempted!");
                            Sender content = new Sender(token.getToken(),notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1){
                                                Toast.makeText(OrderStatus.this, " order has been Updated", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(OrderStatus.this, "Order Updated but failed to Notify", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("onFailure: ",t.getMessage() );

                                        }
                                    });


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });






    }
}

