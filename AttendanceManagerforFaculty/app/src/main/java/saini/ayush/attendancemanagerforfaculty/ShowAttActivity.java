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

public class ShowAttActivity extends AppCompatActivity {
    ListView mListView;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref ;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_att);
        getRollno();
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        mListView=(ListView) findViewById(R.id.rollNo);


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

    public  void getRollno(){
        ref = database.getReference("students");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                value = (HashMap) dataSnapshot.getValue();
                int i=0;
                int size = (int) dataSnapshot.getChildrenCount();
                String[] rollNo = new String[size];
                final String[] emails = new String[size];
                Set rollSet= value.keySet();
                for(Object object : rollSet) {
                    String roll = (String) object;
                    emails[i]=roll;
                    int iend = roll.indexOf("@");
                    roll= roll.substring(0 , iend);
                    rollNo[i]=roll;
                    i++;
                }
                mListView.setAdapter(new ArrayAdapter<String>(ShowAttActivity.this, android.R.layout.simple_list_item_1,rollNo) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView view = (TextView) super.getView(position, convertView, parent);
                        progressBar.setVisibility(View.INVISIBLE);
                        return view;
                    }
                });

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id) {

                        Intent intent = new Intent(ShowAttActivity.this,IndAttActivity.class);
                        intent.putExtra("Email",emails[position]);
                        startActivity(intent);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ShowAttActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }
}
