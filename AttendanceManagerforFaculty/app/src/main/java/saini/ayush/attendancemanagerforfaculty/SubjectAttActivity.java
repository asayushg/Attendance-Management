package saini.ayush.attendancemanagerforfaculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Set;

public class SubjectAttActivity extends AppCompatActivity {
    String email,subject;
    ListView listView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref ;
    ProgressBar progressBar;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_att);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        subject = intent.getStringExtra("subject");
        listView = (ListView) findViewById(R.id.dateList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar5);
        setDate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.selection_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.showatt){
            super.onBackPressed();
        }
        else if(id==R.id.signout){
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

    HashMap value;
    public  void setDate(){
        ref = database.getReference("dates").child(subject).child(email);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value = (HashMap) dataSnapshot.getValue();
                int size = (int) dataSnapshot.getChildrenCount(),i=1;
                final String[] subAtt = new String[size+1];
                int iend = email.indexOf("@");
                String roll= email.substring(0 , iend);
                subAtt[0]="Subject: "+subject+"  Roll: "+roll + "  Total: "+size;
                if(size>0){ Set AttSet= value.keySet();
                for(Object object : AttSet) {
                      String date = (String) object;
                       subAtt[i]=date;
                       i++;
                    }}
                listView.setAdapter(new ArrayAdapter<String>(SubjectAttActivity.this, android.R.layout.simple_list_item_1,subAtt) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView view = (TextView) super.getView(position, convertView, parent);
                        progressBar.setVisibility(View.INVISIBLE);
                        return view;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
