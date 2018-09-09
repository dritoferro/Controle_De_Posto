package tagliaferro.adriano.projetoposto.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import tagliaferro.adriano.projetoposto.R;
import tagliaferro.adriano.projetoposto.model.Contract.AbastecimentoContract;

/**
 * Created by Adriano2 on 26/07/2017.
 */

public class AbastecimentoController implements MainController<Abastecimento> {

    private Context context;

    public AbastecimentoController(Context context) {
        this.context = context;
    }

    @Override
    public void insert(Abastecimento obj) {
        if (checkInputs(obj)) {
            try {
                ContentValues values = mntAbastecimento(obj);
                Uri uriInsert = AbastecimentoContract.Columns.CONTENT_URI;
                context.getContentResolver().insert(uriInsert, values);
                throw new RuntimeException(context.getString(R.string.add_sucesso));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new RuntimeException(context.getString(R.string.error_abastecimento));
        }
    }

    private ContentValues mntAbastecimento(Abastecimento obj) {
        ContentValues values = new ContentValues();
        values.put(AbastecimentoContract.Columns.abastecimento_data, obj.getAbastecimento_data());
        values.put(AbastecimentoContract.Columns.abastecimento_km_atual, obj.getAbastecimento_km_atual());
        values.put(AbastecimentoContract.Columns.abastecimento_id_veiculo, obj.getAbastecimento_veiculo_id());
        values.put(AbastecimentoContract.Columns.abastecimento_id_posto, obj.getAbastecimento_posto_id());
        values.put(AbastecimentoContract.Columns.abastecimento_valor_litro, obj.getAbastecimento_valor_litro());
        values.put(AbastecimentoContract.Columns.abastecimento_comb, obj.getAbastecimento_comb());
        values.put(AbastecimentoContract.Columns.abastecimento_valor, obj.getAbastecimento_valor());
        if (obj.getAbastecimento_id() != -1) {
            values.put(AbastecimentoContract.Columns.abastecimento_id, obj.getAbastecimento_id());
        }
        return values;
    }

    private boolean checkInputs(Abastecimento obj) {
        if(String.valueOf(obj.getAbastecimento_veiculo_id()).isEmpty()){
            return false;
        }
        if(String.valueOf(obj.getAbastecimento_posto_id()).isEmpty()){
            return false;
        }
        if(obj.getAbastecimento_data().isEmpty()){
            return false;
        }
        if(obj.getAbastecimento_comb().isEmpty() || obj.getAbastecimento_comb().equals(context.getString(R.string.select))){
            return false;
        }
        if(obj.getAbastecimento_km_atual().isEmpty()){
            return false;
        }
        if(obj.getAbastecimento_valor_litro().isEmpty()){
            return false;
        }
        if(obj.getAbastecimento_valor().isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    public int update(Abastecimento obj) {
        int ret = 0;
        if (checkInputs(obj)) {
            try {
                ContentValues values = mntAbastecimento(obj);
                Uri uriUpdate = AbastecimentoContract.Columns.getUriWithAbastID(obj.getAbastecimento_id());
                ret = context.getContentResolver().update(uriUpdate, values, AbastecimentoContract.Columns.abastecimento_id + " = ?", new String[]{String.valueOf(obj.getAbastecimento_id())});
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new RuntimeException(context.getString(R.string.error_abastecimento));
        }
        return ret;
    }

    @Override
    public int delete(int obj) {
        int ret = 0;
        try {
            Uri uriDelete = AbastecimentoContract.Columns.getUriWithAbastID(obj);
            ret = context.getContentResolver().delete(uriDelete, AbastecimentoContract.Columns.abastecimento_id + " = ?", new String[]{String.valueOf(obj)});

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return ret;
    }

    @Override
    public List<Abastecimento> query() {
        List<Abastecimento> abastecimentos = new ArrayList<>();
        Cursor cursor = null;

        try {
            Uri uriQuery = AbastecimentoContract.Columns.CONTENT_URI;
            cursor = context.getContentResolver().query(uriQuery, null, null, null, null);
            while (cursor.moveToNext()) {

                abastecimentos.add(abastFromCursor(cursor));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            cursor.close();
        }

        return abastecimentos;
    }

    @Override
    public List<Abastecimento> query(Abastecimento obj) {
        return null;
    }

    public List<Abastecimento> query(int VeiculoID) {
        List<Abastecimento> abastecimentos = new ArrayList<>();

        Cursor cursor = null;
        try {
            Uri uriQuery = AbastecimentoContract.Columns.getUriWithVeicID(VeiculoID);
            cursor = context.getContentResolver().query(uriQuery, null, AbastecimentoContract.Columns.abastecimento_id_veiculo + " = ?", new String[]{String.valueOf(VeiculoID)}, null);
            while (cursor.moveToNext()) {
                abastecimentos.add(abastFromCursor(cursor));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if(cursor != null){
                cursor.close();
            }
        }

        return abastecimentos;
    }

    public Abastecimento getAbastByID(int id){
        Cursor cursor = null;
        try {
            Abastecimento mAbastecimento = null;
            Uri uriAbast = AbastecimentoContract.Columns.getUriWithAbastID(id);
            cursor = context.getContentResolver().query(uriAbast, null, null, new String[]{String.valueOf(id)}, null);
            if(cursor.moveToFirst()){
                mAbastecimento = abastFromCursor(cursor);
            }

            return mAbastecimento;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        } finally {
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public Abastecimento abastFromCursor(Cursor cursor) {
        Abastecimento abast;
        String data;
        String kmAtual;
        String valorLitro;
        String valor;
        String comb;
        int idPosto;
        int idVeiculo;
        int idAbast;

        idAbast = cursor.getInt(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_id));
        idVeiculo = cursor.getInt(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_id_veiculo));
        idPosto = cursor.getInt(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_id_posto));
        data = cursor.getString(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_data));
        kmAtual = cursor.getString(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_km_atual));
        valorLitro = cursor.getString(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_valor_litro));
        valor = cursor.getString(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_valor));
        comb = cursor.getString(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_comb));

        abast = new Abastecimento();
        abast.setAbastecimento_id(idAbast);
        abast.setAbastecimento_veiculo_id(idVeiculo);
        abast.setAbastecimento_posto_id(idPosto);
        abast.setAbastecimento_data(data);
        abast.setAbastecimento_km_atual(kmAtual);
        abast.setAbastecimento_valor_litro(valorLitro);
        abast.setAbastecimento_valor(valor);
        abast.setAbastecimento_comb(comb);

        return abast;
    }
}
