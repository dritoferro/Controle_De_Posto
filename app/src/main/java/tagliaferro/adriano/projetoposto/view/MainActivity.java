package tagliaferro.adriano.projetoposto.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import tagliaferro.adriano.projetoposto.R;
import tagliaferro.adriano.projetoposto.controller.Abastecimento;
import tagliaferro.adriano.projetoposto.controller.AbastecimentoController;
import tagliaferro.adriano.projetoposto.controller.MyFirebaseMessaging;
import tagliaferro.adriano.projetoposto.controller.Updates;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Updates {

    //Criação dos atributos de Fragment e outros necessários para exibição da Activity
    private FragmentListVeiculos mFragmentListVeiculos;
    private static FragmentListAbastecimentos mFragmentListAbastecimentos;
    private static FragmentManager mFragmentManager;

    private AbastecimentoController abastController;
    private Toolbar mToolbar;
    private FloatingActionButton fabAddAbast;

    private int idVeiculo;
    private List<Abastecimento> mAbastecimentoList;

    private TextView txtKmMes;
    private TextView txtValorMes;

    private AdView mAdView;


    public MainActivity() {
    }

    //Criação deste construtor para poder utilizar o EventBUS
    public MainActivity(int idVeiculo) {
        this.idVeiculo = idVeiculo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentListVeiculos = new FragmentListVeiculos();
        mFragmentListAbastecimentos = new FragmentListAbastecimentos();
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .add(R.id.frameListVeiculos, mFragmentListVeiculos, getString(R.string.tagFrameVeiculos))
                .add(R.id.frameListAbastecimentos, mFragmentListAbastecimentos, getString(R.string.tagFrameAbastecimentos))
                .commit();

        mToolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(mToolbar);

        fabAddAbast = (FloatingActionButton) findViewById(R.id.fab_add_abastecimento);
        fabAddAbast.setOnClickListener(this);

        MobileAds.initialize(this, "ca-app-pub-9932349623638451~5582145071");

        mAdView = (AdView) findViewById(R.id.ad_main_activity);

        abastController = new AbastecimentoController(this);

        txtKmMes = (TextView) findViewById(R.id.text_main_km_mes);
        txtValorMes = (TextView) findViewById(R.id.text_main_valor_mes);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_posto) {
            Intent postoActivity = new Intent(this, PostoActivity.class);
            startActivity(postoActivity);
        } else if (item.getItemId() == R.id.menu_item_veiculo) {
            Intent veiculoActivity = new Intent(this, VeiculoActivity.class);
            startActivity(veiculoActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent newAbast = new Intent(this, AbastecimentoActivity.class);
        startActivity(newAbast);
    }


    @Override
    public void updateListAbastecimentos(int id_veiculo) {
        Bundle args = new Bundle();
        args.putInt(getString(R.string.update_abast_key), id_veiculo);
        mFragmentListAbastecimentos = new FragmentListAbastecimentos();
        mFragmentListAbastecimentos.setArguments(args);
        mFragmentManager.beginTransaction()
                .replace(R.id.frameListAbastecimentos, mFragmentListAbastecimentos, getString(R.string.tagFrameAbastecimentos))
                .commit();

        //Calcular a km rodada no mês e o valor gasto
        mAbastecimentoList = abastController.query(id_veiculo);
        if (!mAbastecimentoList.isEmpty()) {
            //Aqui é feita a ordenação da lista de abastecimento de acordo com a data.
            Collections.sort(mAbastecimentoList, new Comparator<Abastecimento>() {
                @Override
                public int compare(Abastecimento o1, Abastecimento o2) {
                    DateFormat format = DateFormat.getDateInstance(DateFormat.DATE_FIELD, Locale.getDefault());
                    try {
                        Date data1 = format.parse(o1.getAbastecimento_data());
                        Date data2 = format.parse(o2.getAbastecimento_data());
                        if (data1.before(data2)) {
                            return -1;
                        } else if (data1.after(data2)) {
                            return 1;
                        } else if (data1.equals(data2)) {
                            if (o1.getAbastecimento_id() < o2.getAbastecimento_id()) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return 0;
                }
            });
            try {
                Double kmInicial = 0.0, kmFinal = 0.0, kmMes, valorMes = 0.0;
                int mesAtual, anoAtual, mesAbast, anoAbast;
                DateFormat format = DateFormat.getDateInstance(DateFormat.DATE_FIELD, Locale.getDefault());
                Date dataAtual = new Date();
                Date dataAbast;
                GregorianCalendar calendar = new GregorianCalendar(Locale.getDefault());
                calendar.setTime(dataAtual);
                mesAtual = calendar.get(Calendar.MONTH);
                anoAtual = calendar.get(Calendar.YEAR);
                for (int i = 0; i < mAbastecimentoList.size(); i++) {
                    dataAbast = format.parse(mAbastecimentoList.get(i).getAbastecimento_data());
                    calendar.setTime(dataAbast);
                    mesAbast = calendar.get(Calendar.MONTH);
                    anoAbast = calendar.get(Calendar.YEAR);
                    if ((anoAtual == anoAbast && mesAtual == mesAbast) && kmInicial == 0.0) {
                        kmInicial = Double.parseDouble(mAbastecimentoList.get(i).getAbastecimento_km_atual());
                    }
                    if (anoAtual == anoAbast && mesAtual == mesAbast) {
                        valorMes += Double.parseDouble(mAbastecimentoList.get(i).getAbastecimento_valor());
                    }
                    if (i == (mAbastecimentoList.size() - 1)) {
                        dataAbast = format.parse(mAbastecimentoList.get(i).getAbastecimento_data());
                        calendar.setTime(dataAbast);
                        mesAbast = calendar.get(Calendar.MONTH);
                        anoAbast = calendar.get(Calendar.YEAR);
                        if ((anoAtual == anoAbast && mesAtual == mesAbast) && kmFinal == 0.0) {
                            kmFinal = Double.parseDouble(mAbastecimentoList.get(i).getAbastecimento_km_atual());
                        }
                    }
                }
                kmMes = kmFinal - kmInicial;

                txtKmMes.setText(String.valueOf(kmMes));
                txtValorMes.setText(String.valueOf(valorMes));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            //Entra aqui caso não tenha nenhum abastecimento na lista
            txtValorMes.setText("0.0");
            txtKmMes.setText("0.0");
        }
    }

    @Subscribe
    public void updateAbast(MainActivity updates) {
        updateListAbastecimentos(updates.idVeiculo);
    }

    //O EventBUS deve ser registrado e desregistrado em onStart e onStop.

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        startService(new Intent(this, MyFirebaseMessaging.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
