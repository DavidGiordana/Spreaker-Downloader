package org.davidgiordana.SpreakerDownloader.GUI.CheckboxList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;

import java.util.ArrayList;

/**
 * Lista de items con checkbox
 *
 * @author davidgiordana
 */
public class CheckboxList<T> extends ListView<CheckboxListItem> {

    /** Elementos de la lista (items) */
    private ObservableList<CheckboxListItem> dataList;

    /**
     * Constructor
     */
    public CheckboxList() {
        this(new ArrayList<T>());
    }

    /**
     * Constructor
     * @param data Lista de elementos de la ListView
     */
    public CheckboxList(ArrayList<T> data) {
        super();
        // Prepara la lista para asociar los elementos
        this.dataList = FXCollections.observableArrayList();
        for (T element: data) {
            dataList.add(new CheckboxListItem(element));
        }
        this.setItems(dataList);

        // Agrega el campo de checkbox
        Callback<ListView<CheckboxListItem>, ListCell<CheckboxListItem>> forListView = CheckBoxListCell.forListView(item -> {return item.selectedProperty();});
        this.setCellFactory(forListView);
    }

    /**
     * Obtiene los elementos seleccionados hasta el momento y los desmarca
     * @return Lista de elementos seleccionados
     */
    public ArrayList<T> getSelected() {
        ArrayList<T> list = new ArrayList<T>();
        for (CheckboxListItem<T> i: this.dataList) {
            if (i.selectedProperty().get()) {
                list.add(i.getData());
                i.selectedProperty().set(false);
            }
        }
        return list;
    }

    /**
     * Limpia las selecciones
     */
    public void clearSelections() {
        for (CheckboxListItem<T> i: this.dataList) {
            i.selectedProperty().setValue(false);
        }
    }

}

