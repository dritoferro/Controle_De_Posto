package tagliaferro.adriano.projetoposto.model.Contract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Adriano2 on 20/07/2017.
 */

public final class PostoContract {

    public static final String tbPosto = "tbposto";

    public static final class Columns implements BaseColumns{

        public static final String posto_id = "_id";
        public static final String posto_nome = "nome";
        public static final String posto_comb1 = "comb1";
        public static final String posto_comb2 = "comb2";
        public static final String posto_valor_comb1 = "valor_comb1";
        public static final String posto_valor_comb2 = "valor_comb2";
        public static final String posto_localizacao = "localizacao";

        public static final Uri CONTENT_URI = UriContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(UriContract.PATH_POSTO)
                .build();

        public static Uri getUriWithPostoID(int posto){
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(posto))
                    .build();
        }
    }
}
