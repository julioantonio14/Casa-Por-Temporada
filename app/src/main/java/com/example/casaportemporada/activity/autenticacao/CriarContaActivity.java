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
import com.example.casaportemporada.activity.MainActivity;
import com.example.casaportemporada.helper.FirebaseHelper;
import com.example.casaportemporada.model.Usuario;

public class CriarContaActivity extends AppCompatActivity {

    private EditText edit_nome;
    private EditText edit_email;
    private EditText edit_telefone;
    private EditText edit_senha;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_criar_conta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        configCliques();
        iniciaComponentes();
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());
    }

    private void iniciaComponentes(){
        edit_nome = findViewById(R.id.edit_nome);
        edit_email = findViewById(R.id.edit_email);
        edit_telefone = findViewById(R.id.edit_telefone);
        edit_senha = findViewById(R.id.edit_senha);
        progressBar = findViewById(R.id.progressbar);
        TextView text_titulo = findViewById(R.id.text_titulo);
        text_titulo.setText("Crie Sua Conta");
    }

    public void validaDados(View view){
        String nome = edit_nome.getText().toString();
        String email = edit_email.getText().toString();
        String telefone = edit_telefone.getText().toString();
        String senha = edit_senha.getText().toString();

        if (!nome.isEmpty()){
            if (!email.isEmpty()){
                if (!telefone.isEmpty()){
                    if (!senha.isEmpty()){
                        progressBar.setVisibility(View.VISIBLE);
                        Usuario usuario = new Usuario();
                        usuario.setNome(nome);
                        usuario.setEmail(email);
                        usuario.setTelefone(telefone);
                        usuario.setSenha(senha);
                        cadastrarUsuario(usuario);
                    }else {
                        edit_senha.requestFocus();
                        edit_senha.setError("Informe Sua Senha");
                    }
                }else {
                    edit_telefone.requestFocus();
                    edit_telefone.setError("Informe Seu telefone");
                }
            }else {
                edit_email.requestFocus();
                edit_email.setError("Informe Seu email");
            }
        }else {
            edit_nome.requestFocus();
            edit_nome.setError("Informe Seu nome");
        }
    }

    private void cadastrarUsuario(Usuario usuario){
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String idUser = task.getResult().getUser().getUid();
                usuario.setId(idUser);
                usuario.salvar();

                finish();
                startActivity(new Intent(this, MainActivity.class));
            }else{
                String error = task.getException().getMessage();
                Toast.makeText(this,error,Toast.LENGTH_SHORT).show();
            }
        });
    }
}