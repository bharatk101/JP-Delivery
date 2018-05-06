package bharat.example.com.jpdelivery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import bharat.example.com.jpdelivery.models.Token;

public class SignedIn extends AppCompatActivity {

    Button orders,signout;

    private void updateToken(String token) {
        String uemail;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token,false,true);
        uemail = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tokens.child(uemail).setValue(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);
        updateToken(FirebaseInstanceId.getInstance().getToken());

        orders = (Button)findViewById(R.id.order);
        signout = (Button)findViewById(R.id.signout);

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignedIn.this,OrderStatus.class);
                startActivity(i);
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignedIn.this, "Signed out", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                redirectLoginScreen();
            }
        });
    }

    private void redirectLoginScreen() {
        Intent intent = new Intent(SignedIn.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
