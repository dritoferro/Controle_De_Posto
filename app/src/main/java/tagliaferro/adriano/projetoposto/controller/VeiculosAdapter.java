package tagliaferro.adriano.projetoposto.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import tagliaferro.adriano.projetoposto.R;

/**
 * Created by Adriano2 on 17/08/2017.
 */

public class VeiculosAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Veiculo> veiculos;
    private OnDataSelected mOnDataSelected;

    public VeiculosAdapter(Context context, List<Veiculo> veiculos, OnDataSelected onDataSelected) {
        this.mContext = context;
        this.veiculos = veiculos;
        this.mOnDataSelected = onDataSelected;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_veiculo, parent, false);
        VeiculoHolder vh = new VeiculoHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VeiculoHolder vh = (VeiculoHolder) holder;
        Veiculo veiculo = veiculos.get(position);

        if (veiculo.getVeiculo_imagem().equals(mContext.getString(R.string.teste))) {
            Glide.with(mContext).load(R.drawable.sample)
                    .into(vh.imgVeiculo);
        } else {
            Glide.with(mContext).load(veiculo.getVeiculo_imagem())
                    .into(vh.imgVeiculo);
        }
    }

    @Override
    public int getItemCount() {
        return veiculos.size();
    }

    public class VeiculoHolder extends RecyclerView.ViewHolder {

        private final ImageView imgVeiculo;

        public VeiculoHolder(View itemView) {
            super(itemView);

            imgVeiculo = (ImageView) itemView.findViewById(R.id.img_list_veiculo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnDataSelected.onDataSelected(v, getAdapterPosition());
                }
            });
        }
    }

}


