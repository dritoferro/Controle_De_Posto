package tagliaferro.adriano.projetoposto.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import tagliaferro.adriano.projetoposto.R;
import tagliaferro.adriano.projetoposto.model.Contract.PostoContract;

/**
 * Created by Adriano2 on 26/07/2017.
 */

public class PostoController implements MainController<Posto> {

    private Context context;

    public PostoController(Context context) {
        this.context = context;
    }

    @Override
    public void insert(Posto obj) {
        if (checkInputs(obj)) {
            ContentValues values = mntPosto(obj);
            try {
                Uri uriInsert = PostoContract.Columns.CONTENT_URI;
                context.getContentResolver().insert(uriInsert, values);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new RuntimeException(context.getString(R.string.error_posto));
        }
    }

    private boolean checkInputs(Posto obj) {
        if (obj.getPosto_nome().isEmpty()) {
            return false;
        }
        if (obj.getPosto_comb1().isEmpty()) {
            return false;
        }
        if (obj.getPosto_valor_comb1().isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public int update(Posto obj) {
        int ret = 0;

        try {
            if (checkInputs(obj)) {
                ContentValues values = mntPosto(obj);
                Uri uriUpdate = PostoContract.Columns.getUriWithPostoID(obj.getPosto_id());
                ret = context.getContentResolver().update(uriUpdate, values, PostoContract.Columns.posto_id + " = ?", new String[]{String.valueOf(obj.getPosto_id())});

                return ret;
            } else {
                throw new RuntimeException(context.getString(R.string.error_posto));
            }

        } catch (Exception e) {

        }

        return 0;
    }

    @Override
    public int delete(int obj) {
        int ret = 0;
        try {
            Uri uriDelete = PostoContract.Columns.getUriWithPostoID(obj);
            ret = context.getContentResolver().delete(uriDelete, null, new String[]{String.valueOf(obj)});

            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Posto> query(Posto obj) {
        return null;
    }

    @Override
    public List<Posto> query() {
        List<Posto> postos = new ArrayList<>();
        Posto posto;

        int idPosto;
        String nome;
        String comb1;
        String comb2;
        String precoComb1;
        String precoComb2;
        String location;

        Cursor cursor = null;

        try {
            Uri uriQuery = PostoContract.Columns.CONTENT_URI;
            cursor = context.getContentResolver().query(uriQuery, null, null, null, null);

            while (cursor.moveToNext()) {
                posto = new Posto();
                idPosto = cursor.getInt(cursor.getColumnIndex(PostoContract.Columns.posto_id));
                nome = cursor.getString(cursor.getColumnIndex(PostoContract.Columns.posto_nome));
                comb1 = cursor.getString(cursor.getColumnIndex(PostoContract.Columns.posto_comb1));
                comb2 = cursor.getString(cursor.getColumnIndex(PostoContract.Columns.posto_comb2));
                precoComb1 = cursor.getString(cursor.getColumnIndex(PostoContract.Columns.posto_valor_comb1));
                precoComb2 = cursor.getString(cursor.getColumnIndex(PostoContract.Columns.posto_valor_comb2));
                location = cursor.getString(cursor.getColumnIndex(PostoContract.Columns.posto_localizacao));

                posto.setPosto_id(idPosto);
                posto.setPosto_nome(nome);
                posto.setPosto_comb1(comb1);
                posto.setPosto_comb2(comb2);
                posto.setPosto_valor_comb1(precoComb1);
                posto.setPosto_valor_comb2(precoComb2);
                posto.setPosto_localizacao(location);

                postos.add(posto);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            cursor.close();
        }

        return postos;
    }

    public Posto getPostoByID(int id){
        Cursor cursor = null;
        try {
            Posto posto = new Posto();
            Uri postoUri = PostoContract.Columns.getUriWithPostoID(id);
            cursor = context.getContentResolver().query(postoUri, null, null, new String[]{String.valueOf(id)}, null);
            if(cursor.moveToFirst()){
                posto.setPosto_id(id);
                posto.setPosto_nome(cursor.getString(cursor.getColumnIndex(PostoContract.Columns.posto_nome)));
                posto.setPosto_localizacao(cursor.getString(cursor.getColumnIndex(PostoContract.Columns.posto_localizacao)));
                posto.setPosto_comb1(cursor.getString(cursor.getColumnIndex(PostoContract.Columns.posto_valor_comb1)));
                posto.setPosto_comb2(cursor.getString(cursor.getColumnIndex(PostoContract.Columns.posto_comb2)));
                posto.setPosto_valor_comb1(cursor.getString(cursor.getColumnIndex(PostoContract.Columns.posto_valor_comb1)));
                posto.setPosto_valor_comb2(cursor.getString(cursor.getColumnIndex(PostoContract.Columns.posto_valor_comb2)));
            }

            return posto;
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        } finally {
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public ContentValues mntPosto(Posto obj) {
        ContentValues values = new ContentValues();
        values.put(PostoContract.Columns.posto_nome, obj.getPosto_nome());
        values.put(PostoContract.Columns.posto_comb1, obj.getPosto_comb1());
        values.put(PostoContract.Columns.posto_comb2, obj.getPosto_comb2());
        values.put(PostoContract.Columns.posto_valor_comb1, obj.getPosto_valor_comb1());
        values.put(PostoContract.Columns.posto_valor_comb2, obj.getPosto_valor_comb2());
        values.put(PostoContract.Columns.posto_localizacao, obj.getPosto_localizacao());
        if (obj.getPosto_id() != -1) {
            values.put(PostoContract.Columns.posto_id, obj.getPosto_id());
        }
        return values;
    }
}
