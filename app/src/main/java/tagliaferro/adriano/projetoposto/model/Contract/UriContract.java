package tagliaferro.adriano.projetoposto.model.Contract;

import android.net.Uri;

/**
 * Created by Adriano2 on 25/07/2017.
 */

public class UriContract {

    public static final String PATH_VEICULO = "veiculo";
    public static final String PATH_POSTO = "posto";
    public static final String PATH_ABASTECIMENTO = "abastecimento";
    public static final String PATH_ABASTECIMENTO_WITH_VEICULO = "abastecimento_veiculo_id";
    public static final String PATH_ABASTECIMENTO_WITH_POSTO = "abastecimento_posto_id";

    public static final String CONTENT_AUTHORITY = "tagliaferro.adriano.projetoposto";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


}
