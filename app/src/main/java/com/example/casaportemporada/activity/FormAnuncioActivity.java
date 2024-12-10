package com.example.casaportemporada.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.casaportemporada.R;
import com.example.casaportemporada.helper.FirebaseHelper;
import com.example.casaportemporada.model.Anuncio;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.IOException;
import java.util.List;

public class FormAnuncioActivity extends AppCompatActivity {

    private static final int REQUEST_GALERIA = 100;
    private EditText edit_titulo;
    private EditText edit_descricao;
    private EditText edit_quarto;
    private EditText edit_banheiro;
    private EditText edit_garagem;
    private CheckBox cb_status;

    private ImageView img_anuncio;
    private String caminhoImagem;
    private Bitmap imagem;
    private Anuncio anuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_anuncio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        iniciaComponentes();
        configCliques();
    }

    private void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,REQUEST_GALERIA);
    }

    public void verificaPermissaoGaleria(View view){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(FormAnuncioActivity.this, "Permissão Negada", Toast.LENGTH_SHORT).show();
            }
        };

        showDialogPermissaoGaleria(permissionListener, new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO});
    }

    private void showDialogPermissaoGaleria(PermissionListener listener, String[] permissoes){
        TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedTitle("Permissões Negadas")
                .setDeniedMessage("Você negou a permissão para acessar a galeria do dispositivo, deseja permitir ?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissoes)
                .check();
    }

    private void configCliques(){
        findViewById(R.id.ib_salvar).setOnClickListener(view -> validaDados());
    }

    private void validaDados(){
        String titulo = edit_titulo.getText().toString();
        String descricao = edit_descricao.getText().toString();
        String quarto = edit_quarto.getText().toString();
        String banheiro = edit_banheiro.getText().toString();
        String garagem = edit_garagem.getText().toString();

        if (!titulo.isEmpty()){
            if (!descricao.isEmpty()){
                if (!quarto.isEmpty()){
                    if (!banheiro.isEmpty()){
                        if (!garagem.isEmpty()){
                            if (anuncio == null) anuncio = new Anuncio();
                            anuncio.setTitulo(titulo);
                            anuncio.setDescricao(descricao);
                            anuncio.setQuarto(quarto);
                            anuncio.setBanheiro(banheiro);
                            anuncio.setGaragem(garagem);
                            anuncio.setStatus(cb_status.isChecked());

                            if (caminhoImagem != null){
                                salvarImagemAnuncio();
                            }else{
                                Toast.makeText(this, "Selecione Uma Imagem para o anúncio", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            edit_garagem.requestFocus();
                            edit_garagem.setError("Informação obrigatória");
                        }
                    }else{
                        edit_banheiro.requestFocus();
                        edit_banheiro.setError("Informação obrigatória");
                    }
                }else{
                    edit_quarto.requestFocus();
                    edit_quarto.setError("Informação obrigatória");
                }
            }else{
                edit_descricao.requestFocus();
                edit_descricao.setError("Informe a descrição");
            }
        }else{
            edit_titulo.requestFocus();
            edit_titulo.setError("Informe o título");
        }
    }

    private void salvarImagemAnuncio(){
        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("anuncios")
                .child(anuncio.getId() + ".jpeg");

        UploadTask uploadTask =  storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task ->{
            String urlImagem = task.getResult().toString();
            anuncio.setUrlImagem(urlImagem);
            anuncio.salvar();
            //finish();
        })).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void iniciaComponentes(){
        TextView text_titulo = findViewById(R.id.text_titulo);
        text_titulo.setText("Novo Anúncio");
        edit_titulo = findViewById(R.id.edit_titulo);
        edit_descricao = findViewById(R.id.edit_descricao);
        edit_quarto = findViewById(R.id.edit_quarto);
        edit_banheiro = findViewById(R.id.edit_banheiro);
        edit_garagem = findViewById(R.id.edit_garagem);
        cb_status = findViewById(R.id.cb_status);
        img_anuncio = findViewById(R.id.img_anuncio);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_GALERIA){
                Uri localImagemSelecionada = data.getData();
                caminhoImagem = localImagemSelecionada.toString();

                if (Build.VERSION.SDK_INT < 28){
                    try {
                        imagem = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(),localImagemSelecionada);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    ImageDecoder.Source source = ImageDecoder.createSource(getBaseContext().getContentResolver(), localImagemSelecionada);
                    try {
                        imagem = ImageDecoder.decodeBitmap(source);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                img_anuncio.setImageBitmap(imagem);
            }
        }
    }
}