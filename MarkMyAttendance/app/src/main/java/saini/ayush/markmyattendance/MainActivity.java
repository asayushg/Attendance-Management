package saini.ayush.markmyattendance;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    public EditText emailText,pass1Text,pass2Text;
    String email,pass1,pass2;
    RadioButton radioButton;
    Button LoginButton;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailText = (EditText) findViewById(R.id.email);
        pass1Text = (EditText) findViewById(R.id.pass);
        pass2Text = (EditText) findViewById(R.id.pass2);
        radioButton = (RadioButton) findViewById(R.id.radioButton);
        LoginButton = (Button) findViewById(R.id.loginbtn);
        progressBar = (ProgressBar) findViewById(R.id.registerBar);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void loginAct(View v){
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    String keyEmail;
    public  void setKeyEmail(){
        keyEmail = email.replace(".","dot");
    }




    public void register(){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email,pass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(email,pass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isComplete()){
                                Intent intent = new Intent(MainActivity.this, SubjectActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }
                            else {
                                Toast.makeText(MainActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    public void Onclick(View v) {

        email = emailText.getText().toString();
        pass1 = pass1Text.getText().toString();
        pass2 = pass2Text.getText().toString();
        if(email.isEmpty()) Toast.makeText(this,"Email can't be empty",Toast.LENGTH_SHORT).show();
        else if(pass1.isEmpty()) Toast.makeText(this,"Password can't be empty",Toast.LENGTH_SHORT).show();
        else if(pass2.isEmpty()) Toast.makeText(this,"Password can't be empty",Toast.LENGTH_SHORT).show();
        else if(!pass1.equals(pass2)) Toast.makeText(this,"Password didn't match",Toast.LENGTH_SHORT).show();
        else if(!radioButton.isChecked()) Toast.makeText(this,"Accept terms and condition",Toast.LENGTH_SHORT).show();
        else {
           register();
        }
    }
}
