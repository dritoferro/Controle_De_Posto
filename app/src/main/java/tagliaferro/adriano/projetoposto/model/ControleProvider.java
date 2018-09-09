package tagliaferro.adriano.projetoposto.model;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tagliaferro.adriano.projetoposto.model.Contract.AbastecimentoContract;
import tagliaferro.adriano.projetoposto.model.Contract.PostoContract;
import tagliaferro.adriano.projetoposto.model.Contract.UriContract;
import tagliaferro.adriano.projetoposto.model.Contract.VeiculoContract;

/**
 * Created by Adriano2 on 25/07/2017.
 */

public class ControleProvider extends ContentProvider {

    //ID's for URI MATCHER
    public static final int CODE_VEICULO = 100;
    public static final int CODE_VEICULO_WITH_ID = 110;
    public static final int CODE_POSTO = 200;
    public static final int CODE_POSTO_WITH_ID = 210;
    public static final int CODE_ABASTECIMENTO = 300;
    public static final int CODE_ABASTECIMENTO_WITH_ID = 310;
    public static final int CODE_ABASTECIMENTO_WITH_VEICID = 320;
    public static final int CODE_ABASTECIMENTO_WITH_POSTOID = 330;

    public static final UriMatcher sUriMatcher = buildUriMatcher();
    private MyDBOpenHelper dbOpenHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = UriContract.CONTENT_AUTHORITY;

        //Add matcher for veiculos
        matcher.addURI(authority, UriContract.PATH_VEICULO, CODE_VEICULO);
        matcher.addURI(authority, UriContract.PATH_VEICULO + "/#", CODE_VEICULO_WITH_ID);
        //Add matcher for postos
        matcher.addURI(authority, UriContract.PATH_POSTO, CODE_POSTO);
        matcher.addURI(authority, UriContract.PATH_POSTO + "/#", CODE_POSTO_WITH_ID);
        //Add matcher for abastecimentos
        matcher.addURI(authority, UriContract.PATH_ABASTECIMENTO, CODE_ABASTECIMENTO);
        matcher.addURI(authority, UriContract.PATH_ABASTECIMENTO + "/#", CODE_ABASTECIMENTO_WITH_ID);
        matcher.addURI(authority, UriContract.PATH_ABASTECIMENTO_WITH_VEICULO + "/#", CODE_ABASTECIMENTO_WITH_VEICID);
        matcher.addURI(authority, UriContract.PATH_ABASTECIMENTO_WITH_POSTO + "/#", CODE_ABASTECIMENTO_WITH_POSTOID);

        return matcher;
    }

    @Override
    public boolean onCreate() {

        dbOpenHelper = MyDBOpenHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case CODE_VEICULO:
                cursor = dbOpenHelper.getReadableDatabase().query(
                        VeiculoContract.tbVeiculo,
                        null, null, null, null, null, null
                );
                break;

            case CODE_VEICULO_WITH_ID:
                cursor = dbOpenHelper.getReadableDatabase().query(
                        VeiculoContract.tbVeiculo,
                        null,
                        VeiculoContract.Columns.veiculo_id + " = ?",
                        selectionArgs, null, null, null
                );
                break;

            case CODE_POSTO:
                cursor = dbOpenHelper.getReadableDatabase().query(
                        PostoContract.tbPosto,
                        null, null, null, null, null, null
                );
                break;

            case CODE_POSTO_WITH_ID:
                cursor = dbOpenHelper.getReadableDatabase().query(
                        PostoContract.tbPosto,
                        null,
                        PostoContract.Columns.posto_id + " = ?",
                        selectionArgs, null, null, null
                );
                break;

            case CODE_ABASTECIMENTO:
                cursor = dbOpenHelper.getReadableDatabase().query(
                        AbastecimentoContract.tbAbastecimento,
                        null, null, null, null, null, null
                );
                break;

            case CODE_ABASTECIMENTO_WITH_ID:
                cursor = dbOpenHelper.getReadableDatabase().query(
                        AbastecimentoContract.tbAbastecimento,
                        null, AbastecimentoContract.Columns.abastecimento_id + " = ?",
                        selectionArgs, null, null, null
                );
                break;

            case CODE_ABASTECIMENTO_WITH_VEICID:
                cursor = dbOpenHelper.getReadableDatabase().query(
                        AbastecimentoContract.tbAbastecimento,
                        null, selection, selectionArgs, null, null, null
                );
                break;

            case CODE_ABASTECIMENTO_WITH_POSTOID:
                cursor = dbOpenHelper.getReadableDatabase().query(
                        AbastecimentoContract.tbAbastecimento,
                        null, AbastecimentoContract.Columns.abastecimento_id_posto + " = ?",
                        selectionArgs, null, null, null
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = null;
        int idInserted;
        Uri mUri = null;
        try {
            db = dbOpenHelper.getWritableDatabase();
            db.beginTransaction();

            switch (sUriMatcher.match(uri)) {
                case CODE_VEICULO:
                    idInserted = (int) db.insert(VeiculoContract.tbVeiculo, null, values);
                    db.setTransactionSuccessful();
                    mUri = VeiculoContract.Columns.getUriWithVeiculoID(idInserted);
                    break;

                case CODE_POSTO:
                    idInserted = (int) db.insert(PostoContract.tbPosto, null, values);
                    db.setTransactionSuccessful();
                    mUri = PostoContract.Columns.getUriWithPostoID(idInserted);
                    break;

                case CODE_ABASTECIMENTO:
                    idInserted = (int) db.insert(AbastecimentoContract.tbAbastecimento, null, values);
                    db.setTransactionSuccessful();
                    mUri = AbastecimentoContract.Columns.getUriWithAbastID(idInserted);
                    break;

                default:
                    mUri = null;
            }
        } catch (Exception e) {
            String t = e.getMessage();
        } finally {
            db.endTransaction();
        }
        return mUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowDeleted = 0;
        SQLiteDatabase db = null;
        try {
            db = dbOpenHelper.getWritableDatabase();
            db.beginTransaction();
            switch (sUriMatcher.match(uri)) {
                case CODE_VEICULO_WITH_ID:
                    rowDeleted = db.delete(VeiculoContract.tbVeiculo, VeiculoContract.Columns.veiculo_id + " = ?", selectionArgs);
                    db.setTransactionSuccessful();
                    break;

                case CODE_POSTO_WITH_ID:
                    rowDeleted = db.delete(PostoContract.tbPosto, PostoContract.Columns.posto_id + " = ?", selectionArgs);
                    db.setTransactionSuccessful();
                    break;

                case CODE_ABASTECIMENTO_WITH_ID:
                    rowDeleted = db.delete(AbastecimentoContract.tbAbastecimento,
                            AbastecimentoContract.Columns.abastecimento_id + " = ?", selectionArgs);
                    db.setTransactionSuccessful();
                    break;

                default:
                    rowDeleted = -1;
            }
        } finally {
            db.endTransaction();
        }

        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowUpdated = 0;
        SQLiteDatabase db = null;
        try {
            db = dbOpenHelper.getWritableDatabase();
            db.beginTransaction();
            switch (sUriMatcher.match(uri)) {
                case CODE_VEICULO_WITH_ID:
                    rowUpdated = db.update(VeiculoContract.tbVeiculo, values, selection, selectionArgs);
                    db.setTransactionSuccessful();
                    break;

                case CODE_POSTO_WITH_ID:
                    rowUpdated = db.update(PostoContract.tbPosto, values, selection, selectionArgs);
                    db.setTransactionSuccessful();
                    break;

                case CODE_ABASTECIMENTO_WITH_ID:
                    rowUpdated = db.update(AbastecimentoContract.tbAbastecimento, values,
                           selection, selectionArgs);
                    db.setTransactionSuccessful();
                    break;

                default:
                    rowUpdated = -1;
            }
        } finally {
            db.endTransaction();
        }

        return rowUpdated;
    }
}
