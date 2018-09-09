package tagliaferro.adriano.projetoposto.controller;

import java.util.List;

/**
 * Created by Adriano2 on 26/07/2017.
 */

public interface MainController<T> {

    void insert(T obj);

    int update(T obj);

    int delete(int obj);

    List<T> query();

    List<T> query(T obj);

}
