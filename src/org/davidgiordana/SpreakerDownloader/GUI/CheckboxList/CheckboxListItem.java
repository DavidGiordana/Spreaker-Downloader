package org.davidgiordana.SpreakerDownloader.GUI.CheckboxList;

import javafx.beans.property.SimpleBooleanProperty;

/**
 * Item de lista con checkbox
 *
 * @author davidgiordana
 */
public class CheckboxListItem<T> {

    /** Dato almacenado */
    private T data;

    /** Propiedad donde se indica si el elemento está seleccionado */
    private SimpleBooleanProperty selected;

    /**
     * Constructor
     *
     * @param data Elemento a almacenar
     */
    public CheckboxListItem(T data) {
        this.data = data;
        this.selected = new SimpleBooleanProperty(false);
    }

    /**
     * GETTERS & SETTERS
     */

    public SimpleBooleanProperty selectedProperty() {
        return selected;
    }

    public T getData() { return data; }

    @Override
    public String toString() {
        return this.data.toString();
    }

}
