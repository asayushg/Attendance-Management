package saini.ayush.markmyattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IndAttActivity extends AppCompatActivity {

    String email;
    ListView listView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref ;
    ProgressBar progressBar;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String[] TotalSubjects= {"","EM","AI","SE","OS","DAA","DS","TOC"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ind_att);
        Intent intent = getIntent();
        email = auth.getCurrentUser().getEmail();
        email = email.replace(".","dot");
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        listView = (ListView) findViewById(R.id.attListInd);
        int iend = email.indexOf("@");
        String roll= email.substring(0 , iend);
        TotalSubjects[0] = "Roll No.: " + roll;
        listView.setAdapter(new ArrayAdapter<String>(IndAttActivity.this, android.R.layout.simple_list_item_1,TotalSubjects) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                progressBar.setVisibility(View.INVISIBLE);
                return view;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
                if(position!=0){
                    Intent intent = new Intent(IndAttActivity.this,SubjectAttActivity.class);
                    intent.putExtra("subject",TotalSubjects[position]);
                    intent.putExtra("email",email);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.showatt){
            //
        }
        else if(id==R.id.signOut){
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
}
