package com.example.casaportemporada.activity.autenticacao;

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
import com.example.casaportemporada.helper.FirebaseHelper;

public class RecuperarContaActivity extends AppCompatActivity {

    private EditText edit_email;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recuperar_conta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        configCliques();
        iniciaComponentes();
    }

    public void validaDados(View view){
        String email = edit_email.getText().toString();

        if (!email.isEmpty()){
            progressBar.setVisibility(View.VISIBLE);
            recuperarSenha(email);
        }else{
            edit_email.requestFocus();
            edit_email.setError("Informe seu e-mail");
        }
    }

    private void recuperarSenha(String email){
        FirebaseHelper.getAuth().sendPasswordResetEmail(email).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this,"E-mail enviado com sucesso",Toast.LENGTH_SHORT).show();
                    }else{
                        progressBar.setVisibility(View.GONE);
                        String error = task.getException().getMessage();
                        Toast.makeText(this,error,Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());
    }

    private void iniciaComponentes(){
        edit_email = findViewById(R.id.edit_email);
        progressBar = findViewById(R.id.progressbar);
    }
}