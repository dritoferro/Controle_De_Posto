package tagliaferro.adriano.projetoposto.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import tagliaferro.adriano.projetoposto.R;
import tagliaferro.adriano.projetoposto.controller.Abastecimento;
import tagliaferro.adriano.projetoposto.model.Contract.AbastecimentoContract;
import tagliaferro.adriano.projetoposto.model.Contract.VeiculoContract;

/**
 * Created by Adriano2 on 06/01/2018.
 */

public class PostoRemoteViewService extends RemoteViewsService {


    private Cursor data = null;

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {

        final RemoteViewsFactory factory = new RemoteViewsFactory() {
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        VeiculoContract.Columns.CONTENT_URI, null, null, null, null
                );
                Log.d("WIDGET", data.toString());
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int i) {
                if (i == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(i)) {
                    return null;
                }
                RemoteViews remviews = new RemoteViews(getPackageName(), R.layout.widget_list_items);

                String srcImg = data.getString(data.getColumnIndex(VeiculoContract.Columns.veiculo_img));
                String nome = data.getString(data.getColumnIndex(VeiculoContract.Columns.veiculo_nome));

                remviews.setTextViewText(R.id.widget_item_nome, nome);

                Log.d("WIDGET_POSTO", srcImg);
                if (srcImg.equals(getApplicationContext().getString(R.string.teste))) {
                    Log.d("WIDGET_POSTO", "entrou no if");
                    remviews.setImageViewResource(R.id.widget_item_img, R.drawable.sample);
                } else {
                    Log.d("WIDGET_POSTO", "entrou no else");
                    Uri imageUri = Uri.parse(srcImg);
                    remviews.setImageViewUri(R.id.widget_item_img, imageUri);
                }
                int id_veiculo = data.getInt(data.getColumnIndex(VeiculoContract.Columns.veiculo_id));
                String[] dados = calcMensal(id_veiculo);
                remviews.setTextViewText(R.id.widget_item_km, dados[0].concat(" KM"));
                remviews.setTextViewText(R.id.widget_item_valor, "$ ".concat(dados[1]));

                return remviews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_items);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                if (data.moveToPosition(i)) {
                    return data.getInt(data.getColumnIndex(VeiculoContract.Columns.veiculo_id));
                }
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }


        };

        return factory;
    }

    public String[] calcMensal(int id_veiculo) {
        String[] dados = new String[2];
        try {
            List<Abastecimento> mAbastecimentoList = query(id_veiculo);
            if (!mAbastecimentoList.isEmpty()) {
                //Aqui é feita a ordenação da lista de abastecimento de acordo com a data.
                Collections.sort(mAbastecimentoList, new Comparator<Abastecimento>() {
                    @Override
                    public int compare(Abastecimento o1, Abastecimento o2) {
                        DateFormat format = DateFormat.getDateInstance(DateFormat.DATE_FIELD, Locale.getDefault());
                        try {
                            Date data1 = format.parse(o1.getAbastecimento_data());
                            Date data2 = format.parse(o2.getAbastecimento_data());
                            if (data1.before(data2)) {
                                return -1;
                            } else if (data1.after(data2)) {
                                return 1;
                            } else if (data1.equals(data2)) {
                                if (o1.getAbastecimento_id() < o2.getAbastecimento_id()) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        return 0;
                    }
                });
                try {
                    Double kmInicial = 0.0, kmFinal = 0.0, kmMes, valorMes = 0.0;
                    int mesAtual, anoAtual, mesAbast, anoAbast;
                    DateFormat format = DateFormat.getDateInstance(DateFormat.DATE_FIELD, Locale.getDefault());
                    Date dataAtual = new Date();
                    Date dataAbast;
                    GregorianCalendar calendar = new GregorianCalendar(Locale.getDefault());
                    calendar.setTime(dataAtual);
                    mesAtual = calendar.get(Calendar.MONTH);
                    anoAtual = calendar.get(Calendar.YEAR);
                    for (int i = 0; i < mAbastecimentoList.size(); i++) {
                        dataAbast = format.parse(mAbastecimentoList.get(i).getAbastecimento_data());
                        calendar.setTime(dataAbast);
                        mesAbast = calendar.get(Calendar.MONTH);
                        anoAbast = calendar.get(Calendar.YEAR);
                        if ((anoAtual == anoAbast && mesAtual == mesAbast) && kmInicial == 0.0) {
                            kmInicial = Double.parseDouble(mAbastecimentoList.get(i).getAbastecimento_km_atual());
                        }
                        if (anoAtual == anoAbast && mesAtual == mesAbast) {
                            valorMes += Double.parseDouble(mAbastecimentoList.get(i).getAbastecimento_valor());
                        }
                        if (i == (mAbastecimentoList.size() - 1)) {
                            dataAbast = format.parse(mAbastecimentoList.get(i).getAbastecimento_data());
                            calendar.setTime(dataAbast);
                            mesAbast = calendar.get(Calendar.MONTH);
                            anoAbast = calendar.get(Calendar.YEAR);
                            if ((anoAtual == anoAbast && mesAtual == mesAbast) && kmFinal == 0.0) {
                                kmFinal = Double.parseDouble(mAbastecimentoList.get(i).getAbastecimento_km_atual());
                            }
                        }
                    }
                    kmMes = kmFinal - kmInicial;

                    dados[0] = String.valueOf(kmMes);
                    dados[1] = String.valueOf(valorMes);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                //Entra aqui caso não tenha nenhum abastecimento na lista
                dados[0] = String.valueOf("0.0");
                dados[1] = String.valueOf("0.0");
            }
        } catch (Exception e) {
            Log.d("WIDGET_POSTO", e.getMessage());
        }
        return dados;
    }

    public List<Abastecimento> query(int VeiculoID) {
        List<Abastecimento> abastecimentos = new ArrayList<>();

        Cursor cursor = null;
        try {
            Uri uriQuery = AbastecimentoContract.Columns.getUriWithVeicID(VeiculoID);
            cursor = getContentResolver().query(uriQuery, null, AbastecimentoContract.Columns.abastecimento_id_veiculo + " = ?", new String[]{String.valueOf(VeiculoID)}, null);
            while (cursor.moveToNext()) {
                abastecimentos.add(abastFromCursor(cursor));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return abastecimentos;
    }

    public Abastecimento abastFromCursor(Cursor cursor) {
        Abastecimento abast;
        String data;
        String kmAtual;
        String valorLitro;
        String valor;
        String comb;
        int idPosto;
        int idVeiculo;
        int idAbast;

        idAbast = cursor.getInt(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_id));
        idVeiculo = cursor.getInt(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_id_veiculo));
        idPosto = cursor.getInt(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_id_posto));
        data = cursor.getString(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_data));
        kmAtual = cursor.getString(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_km_atual));
        valorLitro = cursor.getString(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_valor_litro));
        valor = cursor.getString(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_valor));
        comb = cursor.getString(cursor.getColumnIndex(AbastecimentoContract.Columns.abastecimento_comb));

        abast = new Abastecimento();
        abast.setAbastecimento_id(idAbast);
        abast.setAbastecimento_veiculo_id(idVeiculo);
        abast.setAbastecimento_posto_id(idPosto);
        abast.setAbastecimento_data(data);
        abast.setAbastecimento_km_atual(kmAtual);
        abast.setAbastecimento_valor_litro(valorLitro);
        abast.setAbastecimento_valor(valor);
        abast.setAbastecimento_comb(comb);

        return abast;
    }
}
