package saini.ayush.markmyattendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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

import javax.security.auth.Subject;

public class SubjectActivity extends AppCompatActivity {
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref ;
    ListView mListView;
    ProgressBar progressBar;
    String email, keyEmail;
    String[] SubjectsInit={"Select Subject (Click to refresh)","","","","","","","",""};
    String[] subjects ;
    String[] subjectsT= {"AI","DAA","DS","EM","OS","SE","TOC"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        progressBar = (ProgressBar) findViewById(R.id.progressBars);
        mListView=(ListView) findViewById(R.id.subjectsList);
        email = auth.getCurrentUser().getEmail();
        setKeyEmail();

        check();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
                if(position!=0&&subjects[position].length()>0){Intent intent = new Intent(SubjectActivity.this,HomeActivity.class);
                intent.putExtra("Subject",subjects[position]);
                startActivity(intent);}
                else if(position==0)check();
            }
        });



    }

    public  void setKeyEmail(){
        keyEmail = email.replace(".","dot");
    }
    HashMap value;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
    final String[] Subject = {"EM","AI","SE","OS","DAA","DS","TOC"};
    int i=0;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.showatt){
          Intent intent = new Intent(SubjectActivity.this,IndAttActivity.class);
          startActivity(intent);
        }
        if(id==R.id.signOut){
            Intent intent = new Intent(SubjectActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            auth.signOut();
            SharedPreferences sp;
            sp = getSharedPreferences("login",MODE_PRIVATE);
            sp.edit().putBoolean("logged",false).apply();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    public  void check(){
        subjects=SubjectsInit.clone();
        ref = database.getReference("subjects");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                value = (HashMap) dataSnapshot.getValue();
                int j=1;
                for(int i=0;i<7;i++){
                String c = value.get(subjectsT[i]).toString();
                if(c.length()==10)
                {subjects[j]=subjectsT[i];
                        j=j+1;
                }
                }
                mListView.setAdapter(new ArrayAdapter<String>(SubjectActivity.this, android.R.layout.simple_list_item_1, subjects) {
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
                Toast.makeText(SubjectActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });


    }



}
