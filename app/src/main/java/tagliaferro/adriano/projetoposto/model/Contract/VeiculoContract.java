package tagliaferro.adriano.projetoposto.model.Contract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Adriano2 on 20/07/2017.
 */

public final class VeiculoContract {

    public static final String tbVeiculo = "tbveiculo";

    public static final class Columns implements BaseColumns{

        public static final String veiculo_id = "_id";
        public static final String veiculo_nome = "nome";
        public static final String veiculo_comb1 = "comb1";
        public static final String veiculo_comb2 = "comb2";
        public static final String veiculo_img = "imagem";

        public static final Uri CONTENT_URI = UriContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(UriContract.PATH_VEICULO)
                .build();

        public static Uri getUriWithVeiculoID(int veiculo){
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(veiculo))
                    .build();
        }
    }
}
