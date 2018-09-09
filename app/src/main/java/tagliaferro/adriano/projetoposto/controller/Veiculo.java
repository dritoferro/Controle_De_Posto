package tagliaferro.adriano.projetoposto.controller;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adriano2 on 20/07/2017.
 */

public class Veiculo implements Parcelable {

    private int veiculo_id;
    private String veiculo_nome;
    private String veiculo_comb1;
    private String veiculo_comb2;
    private String veiculo_imagem;

    public Veiculo() {
    }


    protected Veiculo(Parcel in) {
        veiculo_id = in.readInt();
        veiculo_nome = in.readString();
        veiculo_comb1 = in.readString();
        veiculo_comb2 = in.readString();
        veiculo_imagem = in.readString();
    }

    public static final Creator<Veiculo> CREATOR = new Creator<Veiculo>() {
        @Override
        public Veiculo createFromParcel(Parcel in) {
            return new Veiculo(in);
        }

        @Override
        public Veiculo[] newArray(int size) {
            return new Veiculo[size];
        }
    };


    public int getVeiculo_id() {
        return veiculo_id;
    }

    public void setVeiculo_id(int veiculo_id) {
        this.veiculo_id = veiculo_id;
    }

    public String getVeiculo_nome() {
        return veiculo_nome;
    }

    public void setVeiculo_nome(String veiculo_nome) {
        this.veiculo_nome = veiculo_nome;
    }

    public String getVeiculo_comb1() {
        return veiculo_comb1;
    }

    public void setVeiculo_comb1(String veiculo_comb1) {
        this.veiculo_comb1 = veiculo_comb1;
    }

    public String getVeiculo_comb2() {
        return veiculo_comb2;
    }

    public void setVeiculo_comb2(String veiculo_comb2) {
        this.veiculo_comb2 = veiculo_comb2;
    }

    public String getVeiculo_imagem() {
        return veiculo_imagem;
    }

    public void setVeiculo_imagem(String veiculo_imagem) {
        this.veiculo_imagem = veiculo_imagem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(veiculo_id);
        dest.writeString(veiculo_nome);
        dest.writeString(veiculo_comb1);
        dest.writeString(veiculo_comb2);
        dest.writeString(veiculo_imagem);
    }
}
