package com.example.casaportemporada.activity.autenticacao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.casaportemporada.R;
import com.example.casaportemporada.activity.FormAnuncioActivity;
import com.example.casaportemporada.activity.MainActivity;
import com.example.casaportemporada.helper.FirebaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText edit_email;
    private EditText edit_senha;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        configCliques();
        iniciaComponentes();
    }

    private void configCliques(){
        findViewById(R.id.text_criar_conta).setOnClickListener(view ->startActivity(new Intent(this, FormAnuncioActivity.class)));
        findViewById(R.id.text_recuperar_conta).setOnClickListener(view -> startActivity(new Intent(this, RecuperarContaActivity.class)));

    }

    public void validaDados(View view){
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        if (!email.isEmpty()){
            if (!senha.isEmpty()){
                progressBar.setVisibility(View.VISIBLE);
                logar(email,senha);
            }else{
                edit_senha.requestFocus();
                edit_senha.setError("Informe seu e-mail");
            }
        }else{
            edit_email.requestFocus();
            edit_email.setError("Informe seu e-mail");
        }
    }

    private void logar(String email, String senha){
        FirebaseHelper.getAuth().signInWithEmailAndPassword(email,senha)
                .addOnCompleteListener(task ->{
                    if (task.isSuccessful()){
                        finish();
                        startActivity(new Intent(this, MainActivity.class));
                    }else{
                        progressBar.setVisibility(View.GONE);
                        String error = task.getException().getMessage();
                        Toast.makeText(this,error,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void iniciaComponentes(){
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        progressBar = findViewById(R.id.progressbar);
    }
}