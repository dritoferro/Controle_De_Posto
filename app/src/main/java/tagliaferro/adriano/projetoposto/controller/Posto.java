package tagliaferro.adriano.projetoposto.controller;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adriano2 on 20/07/2017.
 */

public class Posto implements Parcelable{

    private int posto_id;
    private String posto_nome;
    private String posto_comb1;
    private String posto_comb2;
    private String posto_valor_comb1;
    private String posto_valor_comb2;
    private String posto_localizacao;

    public Posto(){}


    protected Posto(Parcel in) {
        posto_id = in.readInt();
        posto_nome = in.readString();
        posto_comb1 = in.readString();
        posto_comb2 = in.readString();
        posto_valor_comb1 = in.readString();
        posto_valor_comb2 = in.readString();
        posto_localizacao = in.readString();
    }

    public static final Creator<Posto> CREATOR = new Creator<Posto>() {
        @Override
        public Posto createFromParcel(Parcel in) {
            return new Posto(in);
        }

        @Override
        public Posto[] newArray(int size) {
            return new Posto[size];
        }
    };

    public int getPosto_id() {
        return posto_id;
    }

    public void setPosto_id(int posto_id) {
        this.posto_id = posto_id;
    }

    public String getPosto_nome() {
        return posto_nome;
    }

    public void setPosto_nome(String posto_nome) {
        this.posto_nome = posto_nome;
    }

    public String getPosto_comb1() {
        return posto_comb1;
    }

    public void setPosto_comb1(String posto_comb1) {
        this.posto_comb1 = posto_comb1;
    }

    public String getPosto_comb2() {
        return posto_comb2;
    }

    public void setPosto_comb2(String posto_comb2) {
        this.posto_comb2 = posto_comb2;
    }

    public String getPosto_valor_comb1() {
        return posto_valor_comb1;
    }

    public void setPosto_valor_comb1(String posto_valor_comb1) {
        this.posto_valor_comb1 = posto_valor_comb1;
    }

    public String getPosto_valor_comb2() {
        return posto_valor_comb2;
    }

    public void setPosto_valor_comb2(String posto_valor_comb2) {
        this.posto_valor_comb2 = posto_valor_comb2;
    }

    public String getPosto_localizacao() {
        return posto_localizacao;
    }

    public void setPosto_localizacao(String posto_localizacao) {
        this.posto_localizacao = posto_localizacao;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(posto_id);
        dest.writeString(posto_nome);
        dest.writeString(posto_comb1);
        dest.writeString(posto_comb2);
        dest.writeString(posto_valor_comb1);
        dest.writeString(posto_valor_comb2);
        dest.writeString(posto_localizacao);
    }
}
