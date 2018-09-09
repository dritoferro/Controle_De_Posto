package tagliaferro.adriano.projetoposto.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import tagliaferro.adriano.projetoposto.R;
import tagliaferro.adriano.projetoposto.controller.Posto;
import tagliaferro.adriano.projetoposto.controller.PostoController;

/**
 * Created by Adriano2 on 18/07/2017.
 */

public class PostoActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private EditText edtNome;
    private EditText edtPrecoComb1;
    private EditText edtPrecoComb2;
    private Spinner spinnerPostos;
    private Spinner spinnerComb1;
    private Spinner spinnerComb2;
    private Button btnSalvar;
    private Button btnGetLocation;
    private Button btnExcluir;

    private PostoController controller;

    private List<Posto> postos;
    private Posto posto;

    private ArrayAdapter<String> combAdapter;
    private final int ALERT_TYPE_EXCLUIR = 1;
    private final int ALERT_TYPE_ERROR = 2;
    private final int ALERT_TYPE_LOCATION_CHANGED = 3;
    private final int ALERT_TYPE_LOCATION_CONFIRM = 4;

    //Objetos para lidar com a Location.
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String location;
    private boolean isLocationChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_posto);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_activity_posto);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtNome = (EditText) findViewById(R.id.edt_posto_nome);
        edtPrecoComb1 = (EditText) findViewById(R.id.edt_posto_preco_comb1);
        edtPrecoComb2 = (EditText) findViewById(R.id.edt_posto_preco_comb2);
        spinnerPostos = (Spinner) findViewById(R.id.spinner_postos);
        spinnerComb1 = (Spinner) findViewById(R.id.spinner_posto_comb1);
        spinnerComb2 = (Spinner) findViewById(R.id.spinner_posto_comb2);
        btnSalvar = (Button) findViewById(R.id.btn_posto_salvar);
        btnGetLocation = (Button) findViewById(R.id.btn_posto_get_location);
        btnExcluir = (Button) findViewById(R.id.btn_posto_excluir);

        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadExtra();

    }

    private void loadExtra() {
        //Registro dos Listeners para os cliques e toques em botões e spinners
        btnSalvar.setOnClickListener(this);
        btnGetLocation.setOnClickListener(this);
        btnExcluir.setOnClickListener(this);
        spinnerPostos.setOnItemSelectedListener(this);
        spinnerComb1.setOnItemSelectedListener(this);
        spinnerComb2.setOnItemSelectedListener(this);

        combAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.combs));
        spinnerComb1.setAdapter(combAdapter);
        spinnerComb2.setAdapter(combAdapter);

        controller = new PostoController(this);
        postos = controller.query();

        List<String> postosName = new ArrayList<>();
        postosName.add(getString(R.string.select));
        if (postos.size() > 0) {
            for (Posto p : postos) {
                postosName.add(p.getPosto_nome());
            }
        }
        ArrayAdapter<String> postosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, postosName);
        spinnerPostos.setAdapter(postosAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnSalvar.getId()) {
            if (spinnerPostos.getSelectedItem().toString().equals(getString(R.string.select))) {
                posto = new Posto();
            }
            posto.setPosto_nome(edtNome.getText().toString());
            posto.setPosto_comb1(spinnerComb1.getSelectedItem().toString());
            posto.setPosto_comb2(spinnerComb2.getSelectedItem().toString());
            posto.setPosto_valor_comb1(edtPrecoComb1.getText().toString());
            posto.setPosto_valor_comb2(edtPrecoComb2.getText().toString());
            if (isLocationChanged) {
                posto.setPosto_localizacao(location);
            }
            if (spinnerPostos.getSelectedItem().toString().equals(getString(R.string.select))) {
                //Verifica se o posto não existe para ser cadastrado.
                try {
                    posto.setPosto_id(-1);
                    controller.insert(posto);
                    Toast.makeText(this, getString(R.string.add_sucesso), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } catch (Exception e) {
                    buildAlerts(getString(R.string.warning), e.getMessage(), ALERT_TYPE_ERROR);
                }
            } else {
                //O posto já existe então será realizado um update no mesmo.
                try {
                    int ret = controller.update(posto);
                    if (ret != 0) {
                        Toast.makeText(this, getString(R.string.up_sucesso), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        buildAlerts(getString(R.string.warning), getString(R.string.erro_update), ALERT_TYPE_ERROR);
                    }
                } catch (Exception e) {
                    buildAlerts(getString(R.string.warning), e.getMessage(), ALERT_TYPE_ERROR);
                }
            }

        } else if (v.getId() == btnGetLocation.getId()) {
            //TODO pegar a localização do posto
            buildAlerts(getString(R.string.warning), getString(R.string.msg_change_location), ALERT_TYPE_LOCATION_CHANGED);
        } else if (v.getId() == btnExcluir.getId()) {
            buildAlerts(getString(R.string.warning), getString(R.string.msg_exc_posto), ALERT_TYPE_EXCLUIR);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Foi necessário pegar a position -1 devido ao fato de que a primeira posição é a palavra "selecione" que não é um veículo.
        if (parent.getId() == spinnerPostos.getId()) {
            if (position != 0) {
                posto = postos.get(position - 1);
                edtNome.setText(posto.getPosto_nome());
                spinnerComb1.setSelection(combAdapter.getPosition(posto.getPosto_comb1()));
                spinnerComb2.setSelection(combAdapter.getPosition(posto.getPosto_comb2()));
                edtPrecoComb1.setText(posto.getPosto_valor_comb1());
                edtPrecoComb2.setText(posto.getPosto_valor_comb2());
                location = posto.getPosto_localizacao();
                btnExcluir.setVisibility(View.VISIBLE);
                if (location != null) {
                    Toast.makeText(this, getString(R.string.location_posto_atual).concat(location), Toast.LENGTH_LONG).show();
                }
            } else {
                edtNome.setText("");
                edtPrecoComb1.setText("");
                edtPrecoComb2.setText("");
                spinnerComb1.setSelection(0);
                spinnerComb2.setSelection(0);
                btnExcluir.setVisibility(View.INVISIBLE);
            }
            //Limpar o campo do valor do litro referente ao combustível 1
        } else if (parent.getId() == spinnerComb1.getId()) {
            if (spinnerComb1.getSelectedItem().equals(getString(R.string.select))) {
                edtPrecoComb1.setText("");
            }

            //Limpar o campo do valor do litro referente ao combustível 2
        } else if (parent.getId() == spinnerComb2.getId()) {
            if (spinnerComb2.getSelectedItem().equals(getString(R.string.select))) {
                edtPrecoComb2.setText("");
            }
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
                        int ret = controller.delete(posto.getPosto_id());
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
        } else if (alert_type == ALERT_TYPE_LOCATION_CHANGED) {
            ask.setNegativeButton(getString(R.string.nao), null);
            ask.setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getLocation();
                }
            });
        } else if (alert_type == ALERT_TYPE_LOCATION_CONFIRM) {
            ask.setNegativeButton(getString(R.string.nao), null);
            ask.setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isLocationChanged = true;
                    Toast.makeText(getApplicationContext(), getString(R.string.localizacao_obtida_sucesso), Toast.LENGTH_SHORT).show();
                }
            });
        }

        ask.show();
    }

    //Métodos para o funcionamento do Google API Client Location Service.
    public void getLocation() {
        try {
            LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSOn = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSOn && verificaConexao()) {
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(1000);
                mLocationRequest.setNumUpdates(1);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                buildAlerts(getString(R.string.warning), getString(R.string.gps_rede_desabilitado), ALERT_TYPE_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = findAddress(location.getLatitude(), location.getLongitude());
        buildAlerts(getString(R.string.warning), getString(R.string.msg_confirm_location).concat(this.location)
                .concat(getString(R.string.ask_signal)), ALERT_TYPE_LOCATION_CONFIRM);
    }

    public String findAddress(double latitude, double longitude) {
        try {
            String address = new String();
            Geocoder mGeocoder;
            Address mAddress;
            List<Address> mAddressList;

            mGeocoder = new Geocoder(this);
            mAddressList = mGeocoder.getFromLocation(latitude, longitude, 1);
            if (mAddressList.size() > 0) {
                mAddress = mAddressList.get(0);
                address = mAddress.getAddressLine(0);
                if (mAddress.getMaxAddressLineIndex() > 0) {
                    for (int i = 1; i < mAddress.getMaxAddressLineIndex(); i++) {
                        address.concat(mAddress.getAddressLine(i));
                    }
                }
            }


            return address;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

}
