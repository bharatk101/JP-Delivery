package bharat.example.com.jpdelivery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import bharat.example.com.jpdelivery.Common.Common;
import bharat.example.com.jpdelivery.Remote.APIService;
import bharat.example.com.jpdelivery.ViewHolder.OrderDetailAdapter;
import bharat.example.com.jpdelivery.models.Order;

public class OrderDetails extends AppCompatActivity {

    TextView orderId, orderDate, orderPrice,orderAddress, orderLandmark, orderPhone, orderAltPhone;
    String order_id_vale = "";
    RecyclerView foods;
    RecyclerView.LayoutManager layoutManager;
    OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getFoods());
    String localKey = "";
    FirebaseDatabase db;
    DatabaseReference requests;
    Order order;
    List<Order> myOrders;
    APIService mService;
    private static final String TAG = "OrderDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);


    db = FirebaseDatabase.getInstance();
    requests = db.getReference("Requests");
    mService = Common.getFCMService();

    orderId = (TextView)findViewById(R.id.order_id);
    orderDate = (TextView)findViewById(R.id.order_date);
    orderPrice = (TextView)findViewById(R.id.order_total);
    orderAddress = (TextView)findViewById(R.id.order_address);
    orderPhone = (TextView)findViewById(R.id.order_phone);
    orderAltPhone = (TextView)findViewById(R.id.order_alt_phone);
    orderLandmark = (TextView)findViewById(R.id.order_landmark);

    foods = (RecyclerView)findViewById(R.id.lstFood);
        foods.setHasFixedSize(true);
    layoutManager = new LinearLayoutManager(this);
        foods.setLayoutManager(layoutManager);

        if (getIntent() != null){
        order_id_vale = getIntent().getStringExtra("OrderId");
    }


        orderId.setText("Order ID : #"+order_id_vale);
        orderPhone.setText("Phone : "+Common.currentRequest.getPhone());
        orderPrice.setText("Total : "+Common.currentRequest.getTotal());
        orderAddress.setText("Address : "+Common.currentRequest.getAddress());
        orderAltPhone.setText(" Alternate Phone : "+Common.currentRequest.getAlternateMobile());
        orderDate.setText("TimeStamp : "+Common.currentRequest.getDatetime());
        orderLandmark.setText("Landmark : "+Common.currentRequest.getLandMark());



    localKey= order_id_vale;
        adapter.notifyDataSetChanged();
        foods.setAdapter(adapter);


}


}
