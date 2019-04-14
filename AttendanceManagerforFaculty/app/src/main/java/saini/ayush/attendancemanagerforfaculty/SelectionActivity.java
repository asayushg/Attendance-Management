package saini.ayush.attendancemanagerforfaculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.dpizarro.uipicker.library.picker.PickerUI;
import com.dpizarro.uipicker.library.picker.PickerUISettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class SelectionActivity extends AppCompatActivity {
    TextView code,timer, sbjView;
    private PickerUI mPickerUI;
    private Button btSlide;
    private int currentPosition = -1;
    Button button;
    private List<String> options;
    String Subject="", Code;
    FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        findViews();
        setListeners();

        auth = FirebaseAuth.getInstance();

        //Populate list
        options = Arrays.asList("EM","AI","SE","OS","DAA","DS","TOC");

        //Populate list
        mPickerUI.setItems(this, options);
        mPickerUI.setColorTextCenter(R.color.background_picker);
        mPickerUI.setColorTextNoCenter(R.color.background_picker);
        mPickerUI.setBackgroundColorPanel(R.color.background_picker);
        mPickerUI.setLinesColor(R.color.background_picker);
        mPickerUI.setItemsClickables(false);
        mPickerUI.setAutoDismiss(false);

        mPickerUI.setOnClickItemPickerUIListener(
                new PickerUI.PickerUIItemClickListener() {

                    @Override
                    public void onItemClickPickerUI(int which, int position, String valueResult) {
                        currentPosition = position;
                        Subject = valueResult;
                        Toast.makeText(SelectionActivity.this, Subject, Toast.LENGTH_SHORT).show();
                    }
                });


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
            Intent intent = new Intent(SelectionActivity.this,ShowAttActivity.class);
            startActivity(intent);
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

    private void findViews() {
        btSlide = (Button) findViewById(R.id.bt_slide);
        mPickerUI = (PickerUI) findViewById(R.id.picker_ui_view);
        code=(TextView) findViewById(R.id.code);
        timer=(TextView) findViewById(R.id.timer);
        sbjView = (TextView) findViewById(R.id.sbjView);
        button = (Button) findViewById(R.id.button);
    }

    private void setListeners() {
        btSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickerUISettings pickerUISettings =
                        new PickerUISettings.Builder().withItems(options)
                                .withBackgroundColor(R.color.white)
                                .withAutoDismiss(true)
                                .withItemsClickables(true)
                                .withUseBlur(true)
                                .build();

                mPickerUI.setSettings(pickerUISettings);

                if(currentPosition==-1) {
                    mPickerUI.slide();
                }
                else{
                    mPickerUI.slide(currentPosition);
                }
            }
        });
    }

    String NewCodes;
    public void updateCodeonFirebase(){
        ref.child("subjects").child(Subject).setValue(Code, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                btSlide.setVisibility(View.INVISIBLE);
                sbjView.setText(Subject);
                sbjView.setVisibility(View.VISIBLE);
                button.setVisibility(View.INVISIBLE);
                code.setText(NewCodes);
                Timer();

            }
        });
    }

    public void updateCodeonFirebaseNA(){
        ref.child("subjects").child(Subject).setValue(Code, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                timer.setVisibility(View.INVISIBLE);
                code.setText("New code!");
                sbjView.setVisibility(View.INVISIBLE);
                btSlide.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
            }
        });


    }



    public void GenerateCode(View view){
       if(Subject.equals("")){
        Toast.makeText(SelectionActivity.this,"Please Select Subject :)",Toast.LENGTH_SHORT).show();
       } else {
           NewCodes = getAlphaNumericString();
           Code = NewCodes;
           //send code and subject to database
            updateCodeonFirebase();
           // 15 sec timer

       }

    }
    public void Timer(){

        timer.setVisibility(View.VISIBLE);
        new CountDownTimer(26000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("Seconds Remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {

                Code="NA";
                updateCodeonFirebaseNA();
               // proSwipeBtn.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    // function to generate a random string of length n
    public static String getAlphaNumericString()
    {
        int n=10;
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

}
