package tagliaferro.adriano.projetoposto.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tagliaferro.adriano.projetoposto.R;
import tagliaferro.adriano.projetoposto.controller.OnDataSelected;
import tagliaferro.adriano.projetoposto.controller.Updates;
import tagliaferro.adriano.projetoposto.controller.Veiculo;
import tagliaferro.adriano.projetoposto.controller.VeiculoController;
import tagliaferro.adriano.projetoposto.controller.VeiculosAdapter;

/**
 * Created by Adriano2 on 13/07/2017.
 */

public class FragmentListVeiculos extends Fragment implements OnDataSelected {

    //Criação dos atributos para lidar com a RecyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private VeiculosAdapter adapter;
    private VeiculoController controller;

    private List<Veiculo> mVeiculos;
    private Updates mUpdates;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_veiculos, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_list_veiculos);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        controller = new VeiculoController(getActivity());
        mVeiculos = controller.query();
        if (mVeiculos.size() > 0) {
            adapter = new VeiculosAdapter(getActivity(), mVeiculos, this);
            mRecyclerView.setAdapter(adapter);
        }

        mUpdates = (Updates) getActivity();
    }


    @Override
    public void onDataSelected(View view, int position) {
        mUpdates.updateListAbastecimentos(mVeiculos.get(position).getVeiculo_id());
    }

    @Override
    public void onLongDataSelected(View view, int position) {
    }
}
