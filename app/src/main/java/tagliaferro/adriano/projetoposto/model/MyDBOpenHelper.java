package tagliaferro.adriano.projetoposto.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tagliaferro.adriano.projetoposto.model.Contract.AbastecimentoContract;
import tagliaferro.adriano.projetoposto.model.Contract.PostoContract;
import tagliaferro.adriano.projetoposto.model.Contract.VeiculoContract;

/**
 * Created by Adriano2 on 19/07/2017.
 */

public class MyDBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "controleposto";
    private static final int DB_VERSION = 3;

    private static MyDBOpenHelper instance;

    public static MyDBOpenHelper getInstance(Context context){
        if(instance == null){
            instance = new MyDBOpenHelper(context);
        }
        return instance;
    }

    private MyDBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableVeiculo());
        db.execSQL(createTablePosto());
        db.execSQL(createTableAbastecimento());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropTbPosto());
        db.execSQL(dropTbVeiculo());
        db.execSQL(dropTbAbastecimento());
        onCreate(db);
    }

    private String dropTbVeiculo(){
        String drop = "DROP TABLE " + VeiculoContract.tbVeiculo;
        return drop;
    }

    private String dropTbPosto(){
        String drop = "DROP TABLE " + PostoContract.tbPosto;
        return drop;
    }

    private String dropTbAbastecimento(){
        String drop = "DROP TABLE " + AbastecimentoContract.tbAbastecimento;
        return drop;
    }

    private String createTableVeiculo(){
        String tbVeiculo = new StringBuilder()
                .append("CREATE TABLE ")
                .append(VeiculoContract.tbVeiculo)
                .append("(")
                .append(VeiculoContract.Columns.veiculo_id)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(VeiculoContract.Columns.veiculo_nome)
                .append(" TEXT NOT NULL, ")
                .append(VeiculoContract.Columns.veiculo_comb1)
                .append(" TEXT NOT NULL, ")
                .append(VeiculoContract.Columns.veiculo_comb2)
                .append(" TEXT, ")
                .append(VeiculoContract.Columns.veiculo_img)
                .append(" TEXT)")
                .toString();

        return tbVeiculo;
    }

    private String createTablePosto(){
        String tbPosto = new StringBuilder()
                .append("CREATE TABLE ")
                .append(PostoContract.tbPosto)
                .append("(")
                .append(PostoContract.Columns.posto_id)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(PostoContract.Columns.posto_nome)
                .append(" TEXT NOT NULL, ")
                .append(PostoContract.Columns.posto_comb1)
                .append(" TEXT NOT NULL, ")
                .append(PostoContract.Columns.posto_comb2)
                .append(" TEXT, ")
                .append(PostoContract.Columns.posto_valor_comb1)
                .append(" TEXT NOT NULL, ")
                .append(PostoContract.Columns.posto_valor_comb2)
                .append(" TEXT, ")
                .append(PostoContract.Columns.posto_localizacao)
                .append(" TEXT)")
                .toString();

        return tbPosto;
    }

    private String createTableAbastecimento(){
        String tbAbastecimento = new StringBuilder()
                .append("CREATE TABLE ")
                .append(AbastecimentoContract.tbAbastecimento)
                .append("(")
                .append(AbastecimentoContract.Columns.abastecimento_id)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(AbastecimentoContract.Columns.abastecimento_id_veiculo)
                .append(" INTEGER NOT NULL, ")
                .append(AbastecimentoContract.Columns.abastecimento_id_posto)
                .append(" INTEGER NOT NULL, ")
                .append(AbastecimentoContract.Columns.abastecimento_comb)
                .append(" TEXT NOT NULL, ")
                .append(AbastecimentoContract.Columns.abastecimento_valor)
                .append(" TEXT NOT NULL, ")
                .append(AbastecimentoContract.Columns.abastecimento_valor_litro)
                .append(" TEXT NOT NULL, ")
                .append(AbastecimentoContract.Columns.abastecimento_data)
                .append(" TEXT NOT NULL, ")
                .append(AbastecimentoContract.Columns.abastecimento_km_atual)
                .append(" TEXT NOT NULL)")
                .toString();

        return tbAbastecimento;
    }
}
