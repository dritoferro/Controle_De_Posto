package tagliaferro.adriano.projetoposto.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tagliaferro.adriano.projetoposto.R;

/**
 * Created by Adriano2 on 18/08/2017.
 */

public class AbastecimentosAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Abastecimento> abastecimentos;
    private OnDataSelected mOnDataSelected;
    private AbastecimentoController abastController;
    private PostoController postoController;

    public AbastecimentosAdapter(Context context, List<Abastecimento> abastecimentos, OnDataSelected onDataSelected) {
        this.mContext = context;
        this.abastecimentos = abastecimentos;
        this.mOnDataSelected = onDataSelected;

        abastController = new AbastecimentoController(mContext);
        postoController = new PostoController(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_abastecimento, parent, false);
        AbastecimentoHolder holder = new AbastecimentoHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AbastecimentoHolder vh = (AbastecimentoHolder) holder;
        Abastecimento abast = abastecimentos.get(position);

        Posto posto = postoController.getPostoByID(abast.getAbastecimento_posto_id());
        Double kmRodada = 0.0;
        //Lógica para calcular o valor da km rodada
        if (position < abastecimentos.size()) {
            if ((position + 1) < abastecimentos.size()) {
                Abastecimento abastTemp = abastecimentos.get(position + 1);
                if (abastTemp != null) {
                    Abastecimento abastNext = abastController.getAbastByID(abastTemp.getAbastecimento_id());
                    if (abastNext != null) {
                        Double kmAtual = Double.parseDouble(abast.getAbastecimento_km_atual());
                        Double kmNext = Double.parseDouble(abastNext.getAbastecimento_km_atual());
                        kmRodada = kmNext - kmAtual;
                    }
                }
            }
        }

        vh.txtData.setText(abast.getAbastecimento_data());
        vh.txtValor.setText(abast.getAbastecimento_valor());
        vh.txtKmAtual.setText(abast.getAbastecimento_km_atual());
        vh.txtPosto.setText(posto.getPosto_nome());
        vh.txtKmRodada.setText(String.valueOf(kmRodada));
    }

    @Override
    public int getItemCount() {
        return abastecimentos.size();
    }

    public class AbastecimentoHolder extends RecyclerView.ViewHolder {

        private TextView txtData;
        private TextView txtValor;
        private TextView txtPosto;
        private TextView txtKmAtual;
        private TextView txtKmRodada;

        public AbastecimentoHolder(View itemView) {
            super(itemView);

            txtData = (TextView) itemView.findViewById(R.id.text_list_abast_data);
            txtValor = (TextView) itemView.findViewById(R.id.text_list_abast_valor);
            txtPosto = (TextView) itemView.findViewById(R.id.text_list_abast_posto);
            txtKmAtual = (TextView) itemView.findViewById(R.id.text_list_abast_km_atual);
            txtKmRodada = (TextView) itemView.findViewById(R.id.text_list_abast_km_rodada);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnDataSelected.onDataSelected(v, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnDataSelected.onLongDataSelected(v, getAdapterPosition());

                    return true;
                }
            });
        }
    }
}
