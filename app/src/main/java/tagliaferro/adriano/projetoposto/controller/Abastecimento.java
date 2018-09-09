package tagliaferro.adriano.projetoposto.controller;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adriano2 on 20/07/2017.
 */

public class Abastecimento implements Parcelable{

    private int abastecimento_id;
    private int abastecimento_veiculo_id;
    private int abastecimento_posto_id;
    private String abastecimento_comb;
    private String abastecimento_valor;
    private String abastecimento_valor_litro;
    private String abastecimento_data;
    private String abastecimento_km_atual;

    public Abastecimento(){}


    protected Abastecimento(Parcel in) {
        abastecimento_id = in.readInt();
        abastecimento_veiculo_id = in.readInt();
        abastecimento_posto_id = in.readInt();
        abastecimento_comb = in.readString();
        setAbastecimento_valor(in.readString());
        abastecimento_valor_litro = in.readString();
        abastecimento_data = in.readString();
        abastecimento_km_atual = in.readString();
    }

    public static final Creator<Abastecimento> CREATOR = new Creator<Abastecimento>() {
        @Override
        public Abastecimento createFromParcel(Parcel in) {
            return new Abastecimento(in);
        }

        @Override
        public Abastecimento[] newArray(int size) {
            return new Abastecimento[size];
        }
    };

    public int getAbastecimento_id() {
        return abastecimento_id;
    }

    public void setAbastecimento_id(int abastecimento_id) {
        this.abastecimento_id = abastecimento_id;
    }

    public int getAbastecimento_veiculo_id() {
        return abastecimento_veiculo_id;
    }

    public void setAbastecimento_veiculo_id(int abastecimento_veiculo_id) {
        this.abastecimento_veiculo_id = abastecimento_veiculo_id;
    }

    public int getAbastecimento_posto_id() {
        return abastecimento_posto_id;
    }

    public void setAbastecimento_posto_id(int abastecimento_posto_id) {
        this.abastecimento_posto_id = abastecimento_posto_id;
    }

    public String getAbastecimento_comb() {
        return abastecimento_comb;
    }

    public void setAbastecimento_comb(String abastecimento_comb) {
        this.abastecimento_comb = abastecimento_comb;
    }

    public String getAbastecimento_valor_litro() {
        return abastecimento_valor_litro;
    }

    public void setAbastecimento_valor_litro(String abastecimento_valor_litro) {
        this.abastecimento_valor_litro = abastecimento_valor_litro;
    }

    public String getAbastecimento_data() {
        return abastecimento_data;
    }

    public void setAbastecimento_data(String abastecimento_data) {
        this.abastecimento_data = abastecimento_data;
    }

    public String getAbastecimento_km_atual() {
        return abastecimento_km_atual;
    }

    public void setAbastecimento_km_atual(String abastecimento_km_atual) {
        this.abastecimento_km_atual = abastecimento_km_atual;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(abastecimento_id);
        dest.writeInt(abastecimento_veiculo_id);
        dest.writeInt(abastecimento_posto_id);
        dest.writeString(abastecimento_comb);
        dest.writeString(abastecimento_valor);
        dest.writeString(abastecimento_valor_litro);
        dest.writeString(abastecimento_data);
        dest.writeString(abastecimento_km_atual);
    }

    public String getAbastecimento_valor() {
        return abastecimento_valor;
    }

    public void setAbastecimento_valor(String abastecimento_valor) {
        this.abastecimento_valor = abastecimento_valor;
    }
}
