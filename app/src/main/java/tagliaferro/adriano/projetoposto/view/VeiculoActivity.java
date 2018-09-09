package tagliaferro.adriano.projetoposto.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tagliaferro.adriano.projetoposto.R;
import tagliaferro.adriano.projetoposto.controller.Veiculo;
import tagliaferro.adriano.projetoposto.controller.VeiculoController;

import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * Created by Adriano2 on 13/07/2017.
 */

public class VeiculoActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    private Toolbar mToolbar;
    private Button btnSalvar;
    private Button btnExcluir;
    private EditText edtNome;
    private Spinner spinnerVeiculos;
    private Spinner spinnerComb1;
    private Spinner spinnerComb2;
    private ImageView imgVeiculo;

    private VeiculoController controller;
    private List<Veiculo> veiculos;
    private Veiculo veiculo;

    private ArrayAdapter<String> combs;
    private ArrayAdapter<String> veiculosAdapter;
    private List<String> veiculosName;

    private final int ALERT_TYPE_EXCLUIR = 1;
    private final int ALERT_TYPE_ERROR = 2;
    private final int ALERT_TYPE_PHOTO = 3;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 5;
    private static final int PICK_IMAGE = 6;

    private static Uri outputFileUri;
    private RequestOptions options;
    private String imageFilePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veiculo);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_activity_veiculo);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtNome = (EditText) findViewById(R.id.edt_veiculo_nome);
        spinnerVeiculos = (Spinner) findViewById(R.id.spinner_veiculos);
        spinnerComb1 = (Spinner) findViewById(R.id.spinner_veiculo_comb1);
        spinnerComb2 = (Spinner) findViewById(R.id.spinner_veiculo_comb2);
        btnSalvar = (Button) findViewById(R.id.btn_veiculo_salvar);
        imgVeiculo = (ImageView) findViewById(R.id.img_veiculo);
        btnSalvar.setOnClickListener(this);
        btnExcluir = (Button) findViewById(R.id.btn_veiculo_excluir);
        btnExcluir.setOnClickListener(this);
        spinnerVeiculos.setOnItemSelectedListener(this);
        imgVeiculo.setOnClickListener(this);

        controller = new VeiculoController(this);

        loadExtra();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        int width = imgVeiculo.getMeasuredWidth();
        int height = imgVeiculo.getMeasuredHeight();

        options = new RequestOptions();
        options.override(width, height);
    }

    @Override
    protected void onDestroy() {
        outputFileUri = null;
        super.onDestroy();
    }

    private void loadExtra() {
        try {
            combs = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Arrays.asList(getResources().getStringArray(R.array.combs)));
            spinnerComb1.setAdapter(combs);
            spinnerComb2.setAdapter(combs);

            veiculos = controller.query();
            veiculosName = new ArrayList<>();
            String veicName;
            veiculosName.add(getString(R.string.select));
            for (int i = 0; i < veiculos.size(); i++) {
                veicName = veiculos.get(i).getVeiculo_nome();
                veiculosName.add(veicName);
            }
            veiculosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, veiculosName);
            spinnerVeiculos.setAdapter(veiculosAdapter);

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == btnSalvar.getId()) {
            if (spinnerVeiculos.getSelectedItem().toString().equals(getString(R.string.select))) {
                veiculo = new Veiculo();
            }
            veiculo.setVeiculo_nome(edtNome.getText().toString());
            veiculo.setVeiculo_comb1(spinnerComb1.getSelectedItem().toString());
            veiculo.setVeiculo_comb2(spinnerComb2.getSelectedItem().toString());
            if (outputFileUri != null) {
                veiculo.setVeiculo_imagem(outputFileUri.toString());
            } else {
                veiculo.setVeiculo_imagem(getString(R.string.teste));
            }
            //Aqui é para salvar as info do veículo.
            if (spinnerVeiculos.getSelectedItem().toString().equals(getString(R.string.select))) {
                try {
                    veiculo.setVeiculo_id(-1);
                    controller.insert(veiculo);
                } catch (Exception e) {
                    if (e.getMessage().equals(getString(R.string.add_sucesso))) {
                        Toast.makeText(this, getString(R.string.add_sucesso), Toast.LENGTH_SHORT).show();
                        Intent principal = new Intent(this, MainActivity.class);
                        principal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(principal);
                    } else {
                        buildAlerts(getString(R.string.warning), e.getMessage(), ALERT_TYPE_ERROR);
                    }
                }
                //Aqui é para atualizar as info do veículo.
            } else {
                int ret = controller.update(veiculo);
                if (ret != 0) {
                    Toast.makeText(this, getString(R.string.up_sucesso), Toast.LENGTH_SHORT).show();
                    Intent principal = new Intent(this, MainActivity.class);
                    principal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(principal);
                } else {
                    buildAlerts(getString(R.string.warning), getString(R.string.erro_update), ALERT_TYPE_ERROR);
                }
            }
        } else if (v.getId() == btnExcluir.getId()) {
            if (!spinnerVeiculos.getSelectedItem().toString().equals(getString(R.string.select))) {
                buildAlerts(getString(R.string.warning), getString(R.string.msg_delete_veiculo), ALERT_TYPE_EXCLUIR);

            }
        }//Aqui o clique vem pelo imageView da foto do veículo.
        else if (v.getId() == imgVeiculo.getId()) {
            buildAlerts(getString(R.string.select), getString(R.string.select_img_veiculo), ALERT_TYPE_PHOTO);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Foi necessário pegar a position -1 devido ao fato de que a primeira posição é a palavra "selecione" que não é um veículo.
        if (position != 0) {
            veiculo = veiculos.get(position - 1);
            edtNome.setText(veiculo.getVeiculo_nome());
            spinnerComb1.setSelection(combs.getPosition(veiculo.getVeiculo_comb1()));
            spinnerComb2.setSelection(combs.getPosition(veiculo.getVeiculo_comb2()));
            if (!veiculo.getVeiculo_imagem().equals(getString(R.string.teste))) {
                Glide.with(this).load(veiculo.getVeiculo_imagem()).apply(options).into(imgVeiculo);
            } else {
                Glide.with(this).load(R.drawable.sample).apply(options).into(imgVeiculo);
            }
            btnExcluir.setVisibility(View.VISIBLE);
        } else {
            btnExcluir.setVisibility(View.INVISIBLE);
            edtNome.setText("");
            spinnerComb1.setSelection(0);
            spinnerComb2.setSelection(0);
            Glide.with(this).load(R.drawable.sample)
                    .apply(options)
                    .into(imgVeiculo);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void buildAlerts(String title, String msg, final int alert_type) {
        AlertDialog.Builder ask = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg);

        if (alert_type == ALERT_TYPE_EXCLUIR) {
            ask.setNegativeButton(R.string.nao, null);
            ask.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        int ret = controller.delete(veiculo.getVeiculo_id());
                        if (ret != 0) {
                            Toast.makeText(getApplicationContext(), R.string.del_sucesso, Toast.LENGTH_SHORT).show();
                            Intent principal = new Intent(getApplicationContext(), MainActivity.class);
                            principal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(principal);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (alert_type == ALERT_TYPE_ERROR) {
            ask.setNeutralButton(R.string.ok, null);
        } else if (alert_type == ALERT_TYPE_PHOTO) {
            ask.setPositiveButton(R.string.btn_camera, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            ActivityCompat.requestPermissions(VeiculoActivity.this, new String[]{Manifest.permission.CAMERA}, 110);
                        }
                        getImageFromCamera();
                    } catch (Exception e) {
                        buildAlerts(getString(R.string.warning), e.getMessage(), ALERT_TYPE_ERROR);
                    }
                }
            });

            ask.setNegativeButton(R.string.btn_galeria, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            ActivityCompat.requestPermissions(VeiculoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                        }
                        getImageFromGalery();
                    } catch (Exception e) {
                        buildAlerts(getString(R.string.warning), e.getMessage(), ALERT_TYPE_ERROR);
                    }
                }
            });
        }

        ask.show();
    }

    public void getImageFromGalery() {
        try {
            if (!edtNome.getText().toString().isEmpty()) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(i, "Selecione uma imagem"), PICK_IMAGE);
            } else {
                buildAlerts(getString(R.string.warning), getString(R.string.error_nome_veiculo), ALERT_TYPE_ERROR);
            }
        } catch (Exception e) {
            buildAlerts(getString(R.string.warning), e.getMessage(), ALERT_TYPE_ERROR);
        }
    }

    public void getImageFromCamera() {
        try {
            if (!edtNome.getText().toString().isEmpty()) {
                //File newFile = getOutputMediaFile(edtNome.getText().toString());
                File newFile = createImageFile();
                outputFileUri = FileProvider.getUriForFile(getApplicationContext(), "tagliaferro.adriano.projetoposto.fileprovider", newFile);
                //outputFileUri = Uri.fromFile(getOutputMediaFile(edtNome.getText().toString()));
                if (outputFileUri != null) {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            } else {
                buildAlerts(getString(R.string.warning), getString(R.string.error_nome_veiculo), ALERT_TYPE_ERROR);
            }
        } catch (Exception e) {
            buildAlerts(getString(R.string.warning), e.getMessage(), ALERT_TYPE_ERROR);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            getImageFromGalery();
        }

        if (requestCode == 110 && (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            getImageFromCamera();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Glide.with(this).load(outputFileUri)
                    .apply(options)
                    .into(imgVeiculo);
        }

        if (requestCode == PICK_IMAGE && resultCode != RESULT_CANCELED) {
            try {
                outputFileUri = data.getData();
                File src = new File(getRealPathFromURI(outputFileUri));
                //File dest = getOutputMediaFile(edtNome.getText().toString());
                File dest = createImageFile();
                copy(src, dest);
                Glide.with(this).load(dest)
                        .apply(options)
                        .into(imgVeiculo);
            } catch (IOException e) {
                buildAlerts(getString(R.string.warning), e.getMessage(), ALERT_TYPE_ERROR);
            }

        }
    }

    //Método para criar uma cópia da imagem da galeria para ser salva junta as outras fotos do app.
    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    //Extrair o real caminho da foto para realizar a cópia.
    private String getRealPathFromURI(Uri contentURI) {
        String result = null;
        try {
            Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) {
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
        } catch (Exception e) {
            buildAlerts(getString(R.string.warning), e.getMessage(), ALERT_TYPE_ERROR);
        }
        return result;
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private static File getOutputMediaFile(String veiculo) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "ControleDePosto");
                // This location works best if you want the created images to be shared
                // between applications and persist after your app has been uninstalled.
                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("ControleDePosto", "failed to create directory");
                        return null;
                    }
                }
                // Create a media file name
                File mediaFile;
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_" + veiculo + "_" + timeStamp + ".jpg");

                return mediaFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
