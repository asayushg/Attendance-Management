package saini.ayush.markmyattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {
    EditText code;
    String Subject, Code,email;
    String keyEmail;
    String pAtt;
    FirebaseAuth auth;
    TextView subjectView;
    ProgressBar bar;
    Button button;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        Subject = intent.getStringExtra("Subject");
        subjectView = (TextView) findViewById(R.id.subjectView);
        subjectView.setText(Subject);
        bar = (ProgressBar) findViewById(R.id.progressBar2);
        bar.setVisibility(View.INVISIBLE);
        button = (Button) findViewById(R.id.markAtt);
        code = (EditText) findViewById(R.id.code);
        code.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //  Do Something or Don't
                return true;
            }
        });
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();
        setKeyEmail();
    }




    HashMap value;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.showatt){
            Intent intent = new Intent(HomeActivity.this,IndAttActivity.class);
            startActivity(intent);
        }
        if(id==R.id.signOut){
            Intent intent = new Intent(this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            auth.signOut();
            SharedPreferences sp;
            sp = getSharedPreferences("login",MODE_PRIVATE);
            sp.edit().putBoolean("logged",false).apply();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }




    public  void setKeyEmail(){
        keyEmail = email.replace(".","dot");
    }

    long presentAtt;

    public boolean checkCode(){
        String userCode = code.getText().toString();
        if(userCode.length()!=10) return false;
        if(Code.equals(userCode)) return true;
        return false;
    }

    public void markAtt(View v){
        if(code.getText().toString().length()<10){
            Toast.makeText(HomeActivity.this, "Entered Code is Incorrect",Toast.LENGTH_LONG).show();
            return;
        }
        button.setEnabled(false);
        //update db
        bar.setVisibility(View.VISIBLE);
        getCode();
        new CountDownTimer(5000, 1000) {
                     public void onTick(long millisUntilFinished) {

                    }
                    public void onFinish() {
                         if(checkCode()){

                            Date c = Calendar.getInstance().getTime();
                            System.out.println("Current time => " + c);

                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            String formattedDate = df.format(c);
                            ref = database.getReference("dates");
                            ref.child(Subject).child(keyEmail).child(formattedDate).setValue(1);
                            ref = database.getReference("dates");
                            ref = ref.child(Subject).child(keyEmail);
                             bar.setVisibility(View.INVISIBLE);
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    presentAtt = dataSnapshot.getChildrenCount();
                                    Toast.makeText(HomeActivity.this, "Attendance marked \nTotal: "+presentAtt,Toast.LENGTH_LONG).show();
                                    button.setEnabled(true);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }

                        else {
                            bar.setVisibility(View.INVISIBLE);
                            Toast.makeText(HomeActivity.this, "Entered Code is Incorrect",Toast.LENGTH_LONG).show();
                            button.setEnabled(true);
                        }

                    }
        }.start();



    }



    public String getCode(){
        DatabaseReference refe;
        refe = database.getReference("subjects/"+Subject);
        refe.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Code = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
        return Code;
    }
}
