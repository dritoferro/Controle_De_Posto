package tagliaferro.adriano.projetoposto.controller;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adriano2 on 22/12/2017.
 */

public class Token implements Parcelable{

    private String token_app;

    public Token(String token){
        this.token_app = token;
    }


    protected Token(Parcel in) {
        token_app = in.readString();
    }

    public static final Creator<Token> CREATOR = new Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel in) {
            return new Token(in);
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };

    public String getToken_app() {
        return token_app;
    }

    public void setToken_app(String token_app) {
        this.token_app = token_app;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(token_app);
    }
}
