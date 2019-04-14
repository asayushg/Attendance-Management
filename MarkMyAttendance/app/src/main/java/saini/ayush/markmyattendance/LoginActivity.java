package saini.ayush.markmyattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email,pass;
    SharedPreferences sp;
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.pass);
        bar = (ProgressBar) findViewById(R.id.progressBar);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sp = getSharedPreferences("login",MODE_PRIVATE);

        if(sp.getBoolean("logged",false)){
            Intent intent = new Intent(LoginActivity.this, SubjectActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }



    String emailId,password;
    public void LogIn(View v){

        emailId = email.getText().toString();
        password = pass.getText().toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if( !emailId.equals("") && !password.equals("")){          // firebase code
            bar.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(emailId,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        bar.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(LoginActivity.this, SubjectActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        finish();
                        startActivity(intent);
                        sp.edit().putBoolean("logged",true).apply();
                    }
                    else {
                        bar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }

                }

            });
        }
        else{                                  // email or pass is not null CHECK
            Toast.makeText(this,"Please Enter valid E-mail and password",Toast.LENGTH_SHORT).show();
        }

    }

    public void RegisterAct(View v){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
