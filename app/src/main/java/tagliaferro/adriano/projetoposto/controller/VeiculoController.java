package tagliaferro.adriano.projetoposto.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import tagliaferro.adriano.projetoposto.R;
import tagliaferro.adriano.projetoposto.model.Contract.VeiculoContract;

/**
 * Created by Adriano2 on 26/07/2017.
 */

public class VeiculoController implements MainController<Veiculo> {

    private Context context;

    public VeiculoController(Context context) {
        this.context = context;
    }

    @Override
    public void insert(Veiculo obj) {
        if (checkInputs(obj)) {
            ContentValues values = mntVeiculo(obj);

            try {
                Uri uriInsert = VeiculoContract.Columns.CONTENT_URI;
                Uri uriInserted = context.getContentResolver().insert(uriInsert, values);
                int newId = Integer.parseInt(uriInserted.getLastPathSegment());
                if (newId != -1) {
                    throw new RuntimeException(context.getString(R.string.add_sucesso));
                } else {
                    throw new RuntimeException(context.getString(R.string.erro_insert));
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new RuntimeException(context.getString(R.string.error_veiculo));
        }

    }

    private boolean checkInputs(Veiculo obj) {
        if (obj.getVeiculo_nome().isEmpty()) {
            return false;
        }
        if (obj.getVeiculo_comb1().equals(context.getString(R.string.select))) {
            return false;
        }
        return true;
    }

    @Override
    public int update(Veiculo obj) {
        if (checkInputs(obj)) {
            int ret = 0;
            ContentValues values = mntVeiculo(obj);
            try {
                Uri uriUpdate = VeiculoContract.Columns.getUriWithVeiculoID(obj.getVeiculo_id());
                ret = context.getContentResolver().update(uriUpdate, values, VeiculoContract.Columns.veiculo_id + " = ? ", new String[]{String.valueOf(obj.getVeiculo_id())});

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return ret;
        } else {
            throw new RuntimeException("");
        }
    }

    @Override
    public int delete(int obj) {
        int ret = 0;
        try {
            Uri uriDelete = VeiculoContract.Columns.getUriWithVeiculoID(obj);
            ret = context.getContentResolver().delete(uriDelete, null, new String[]{String.valueOf(obj)});
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return ret;
    }

    @Override
    public List<Veiculo> query() {
        List<Veiculo> veiculos = new ArrayList<>();
        Veiculo veiculo;
        int id;
        String nome;
        String comb1;
        String comb2;
        String img;
        Cursor cursor = null;

        try {
            Uri query = VeiculoContract.Columns.CONTENT_URI;
            cursor = context.getContentResolver().query(query, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    id = cursor.getInt(cursor.getColumnIndex(VeiculoContract.Columns.veiculo_id));
                    nome = cursor.getString(cursor.getColumnIndex(VeiculoContract.Columns.veiculo_nome));
                    comb1 = cursor.getString(cursor.getColumnIndex(VeiculoContract.Columns.veiculo_comb1));
                    comb2 = cursor.getString(cursor.getColumnIndex(VeiculoContract.Columns.veiculo_comb2));
                    img = cursor.getString(cursor.getColumnIndex(VeiculoContract.Columns.veiculo_img));

                    veiculo = new Veiculo();
                    veiculo.setVeiculo_id(id);
                    veiculo.setVeiculo_nome(nome);
                    veiculo.setVeiculo_comb1(comb1);
                    veiculo.setVeiculo_comb2(comb2);
                    veiculo.setVeiculo_imagem(img);
                    veiculos.add(veiculo);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            cursor.close();
        }
        return veiculos;
    }

    @Override
    public List<Veiculo> query(Veiculo obj) {
        return null;
    }

    public ContentValues mntVeiculo(Veiculo obj) {
        ContentValues values = new ContentValues();
        if (obj.getVeiculo_id() != -1) {
            values.put(VeiculoContract.Columns.veiculo_id, obj.getVeiculo_id());
        }
        values.put(VeiculoContract.Columns.veiculo_nome, obj.getVeiculo_nome());
        values.put(VeiculoContract.Columns.veiculo_comb1, obj.getVeiculo_comb1());
        values.put(VeiculoContract.Columns.veiculo_comb2, obj.getVeiculo_comb2());
        values.put(VeiculoContract.Columns.veiculo_img, obj.getVeiculo_imagem());

        return values;
    }
}
