package tagliaferro.adriano.projetoposto.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tagliaferro.adriano.projetoposto.R;
import tagliaferro.adriano.projetoposto.controller.Abastecimento;
import tagliaferro.adriano.projetoposto.controller.AbastecimentoController;
import tagliaferro.adriano.projetoposto.controller.AbastecimentosAdapter;
import tagliaferro.adriano.projetoposto.controller.OnDataSelected;
import tagliaferro.adriano.projetoposto.controller.Updates;

/**
 * Created by Adriano2 on 13/07/2017.
 */

public class FragmentListAbastecimentos extends Fragment implements OnDataSelected {

    //Criação dos atributos para lidar com a RecyclerView
    private RecyclerView mRecyclerView;

    private List<Abastecimento> mAbastecimentosList;
    private AbastecimentoController abastController;
    private AbastecimentosAdapter abastAdapter;
    private int idVeiculo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_abastecimentos, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_list_abastecimentos);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        idVeiculo = getArguments() != null ? getArguments().getInt(getString(R.string.update_abast_key)) : -1;
        abastController = new AbastecimentoController(getActivity());

        if (idVeiculo != -1) {
            mAbastecimentosList = abastController.query(idVeiculo);
            if (!mAbastecimentosList.isEmpty()) {
                //Aqui é feita a ordenação da lista de abastecimento de acordo com a data.
                Collections.sort(mAbastecimentosList, new Comparator<Abastecimento>() {
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
                abastAdapter = new AbastecimentosAdapter(getActivity(), mAbastecimentosList, this);
                mRecyclerView.setAdapter(abastAdapter);
                //Para exibir o último registro de abastecimento
                mRecyclerView.scrollToPosition(mAbastecimentosList.size() - 1);
            }
        }
    }

    @Override
    public void onDataSelected(View view, int position) {
        //Abre a tela de abastecimento para realizar alterações no abastecimento.
        AbastecimentoActivity.mAbastecimento = mAbastecimentosList.get(position);
        Intent abastActivity = new Intent(getActivity(), AbastecimentoActivity.class);
        startActivity(abastActivity);
    }

    @Override
    public void onLongDataSelected(View view, int position) {
        //Exclui um abastecimento.
        buildAlerts(getString(R.string.warning), getString(R.string.del_abastecimento),
                mAbastecimentosList.get(position).getAbastecimento_id());

    }

    public void buildAlerts(String title, String msg, final int id_abast) {
        AlertDialog.Builder ask = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(msg);
        ask.setNegativeButton(R.string.nao, null);
        ask.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    int ret = abastController.delete(id_abast);
                    if (ret != 0) {
                        Toast.makeText(getActivity(), R.string.del_sucesso, Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new MainActivity(idVeiculo));
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        ask.show();
    }

}