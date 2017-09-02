package org.davidgiordana.SpreakerDownloader.GUI.SpreakerDownloader;

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

import java.util.List;

public class SearchableTable<T> {

    /**
     * Componentes visuales
     */
    private TableView<T> table;
    private TextField searchTF;
    private Button cancelB;
    private TableColumn<T, Boolean> checkColumn;
    private CheckBox selectAllCB;

    /**
     * Callbacks para selecciones y búsqueda
     */
    private Callback<T, String> searchCallback;
    private Callback<T, BooleanProperty> selectionCallback;

    /**
     * Listas modelo de la tabla
     */
    private ObservableList<T> baseData;
    private FilteredList<T> filteredList;
    private SortedList<T> sortedList;

    /**
     * Constructor de la clase
     * @param table tabla de contenidos
     * @param searchTF Campo de texto de búesqueda
     * @param cancelB Boton para cancelar la búsqueda
     */
    public SearchableTable(TableView<T> table, TextField searchTF, Button cancelB) {
        // Llena los campos de la clase
        this.table = table;
        this.searchTF = searchTF;
        this.cancelB = cancelB;

        // Checkbox para seleccionar todo
        selectAllCB = new CheckBox();
        selectAllCB.setOnAction( event -> selectAllAction());

        // Columna de selecciones
        checkColumn = new TableColumn();
        checkColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkColumn));
        checkColumn.setGraphic(selectAllCB);

        // Prepara los componentes visuales
        table.getColumns().add(checkColumn);
        searchTF.textProperty().addListener((obs, o, n) -> search(n));
        cancelB.setOnAction(event -> searchTF.setText(null));
    }


    // MARK: - Select All Table elements

    /**
     * Método llamado al cambiar el valor de seleccionar todo
     */
    public void selectAllAction() {
        if (selectAllCB.isSelected()) {
            applySelectAll();
        } else {
            clearSelection();
        }
    }

    /**
     * Indica si todos los elementos están seleccionados
     * @return True si están todos los elementos seleccionados
     */
    private boolean isAllSelected() {
        if (selectionCallback == null) return true;
        System.out.println(getSelected());
        return getSelected().filtered(e -> !selectionCallback.call(e).get()).isEmpty();
    }

    /** Limpia los elementos seleccionados */
    private void clearSelection() {
        if (selectionCallback == null) return;
        for (T i: getSelected()) {
            selectionCallback.call(i).set(false);
        }
    }

    /**
     * Interfaz externa para seleccionar todo
     * @param state true para seleccionar los elementos, false para deseleccionar
     */
    public void selectAll(boolean state) {
        if (state) {
            applySelectAll();
        } else {
            clearSelection();
        }
    }

    /** Selecciona todos los elementos de la tabla */
    public void applySelectAll() {
        if (selectionCallback == null) return;
        for (T i: sortedList) {
            selectionCallback.call(i).set(true);
        }
    }

    /**
     * Filtra la lista base en base a un String
     * @param searchData String base para realizar la búsqueda
     */
    public void search(final String searchData) {
        if (filteredList == null || searchCallback == null) {
            return;
        }
        filteredList.setPredicate(item -> {
            if (searchData == null || searchData.isEmpty()) {
                return true;
            }
            String lowerCaseMatch = searchData.toLowerCase();
            String lowerCaseData = searchCallback.call(item).toLowerCase();
            return lowerCaseData.contains(lowerCaseMatch);
        });
    }


    /**
     * Getters & Setters
     */

    public TableView<T> getTable() {
        return table;
    }

    public TextField getSearchTF() {
        return searchTF;
    }

    public Button getCancelB() {
        return cancelB;
    }

    /**
     * Actualiza la informacíon base
     * @param baseData Lista observable para configurar la tabla
     */
    public void setBaseData(ObservableList<T> baseData) {
        this.baseData = baseData;
        this.filteredList = new FilteredList<T>(baseData);
        this.sortedList = new SortedList<T>(filteredList);
        table.setItems(sortedList);
    }

    public void setBaseData(List baseData) {
        this.setBaseData(FXCollections.observableArrayList(baseData));
    }

    public void setSearchCallback(Callback<T, String> searchCallback) {
        this.searchCallback = searchCallback;
    }

    public ObservableList<T> getSelected() {
        if (selectionCallback == null) {
            return FXCollections.observableArrayList();
        }
        return sortedList.filtered(item -> selectionCallback.call(item).getValue());
    }

    public void setSelectionCallback(Callback<T, BooleanProperty> selectionCallback) {
        this.selectionCallback = selectionCallback;
        if (selectionCallback != null) {
            checkColumn.setCellValueFactory(element -> selectionCallback.call(element.getValue()));
        }
    }
}
