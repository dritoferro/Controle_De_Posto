package tagliaferro.adriano.projetoposto.model.Contract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Adriano2 on 20/07/2017.
 */

public final class AbastecimentoContract {

    public static final String tbAbastecimento = "tbabastecimento";

    public static final class Columns implements BaseColumns{

        public static final String abastecimento_id = "_id";
        public static final String abastecimento_id_veiculo = "veiculo_id";
        public static final String abastecimento_id_posto = "posto_id";
        public static final String abastecimento_comb = "comb";
        public static final String abastecimento_valor = "valor";
        public static final String abastecimento_valor_litro = "valor_litro";
        public static final String abastecimento_data = "data";
        public static final String abastecimento_km_atual = "km_atual";

        public static final Uri CONTENT_URI = UriContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(UriContract.PATH_ABASTECIMENTO)
                .build();

        public static final Uri CONTENT_URI_WTH_VEIC = UriContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(UriContract.PATH_ABASTECIMENTO_WITH_VEICULO)
                .build();

        public static final Uri CONTENT_URI_WTH_POSTO = UriContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(UriContract.PATH_ABASTECIMENTO_WITH_POSTO)
                .build();

        public static Uri getUriWithAbastID(int idAbast){
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(idAbast))
                    .build();
        }

        public static Uri getUriWithVeicID(int idVeic){
            return CONTENT_URI_WTH_VEIC.buildUpon()
                    .appendPath(String.valueOf(idVeic))
                    .build();
        }

        public static Uri getUriWithPostoID(int idPosto){
            return CONTENT_URI_WTH_POSTO.buildUpon()
                    .appendPath(String.valueOf(idPosto))
                    .build();
        }
    }
}
