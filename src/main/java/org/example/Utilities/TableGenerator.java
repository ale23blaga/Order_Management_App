package org.example.Utilities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to generate a {@link javax.swing.JTable} dynamically from a list of objects.
 * Supports both standard Java classes and Java records using reflection.
 * @param <T> the type of objects to display in the table
 */
public class TableGenerator<T> {

    /**
     * Generates a JTable based on the fields or record components of the given objects.
     * @param objectList the list of objects to convert into table rows
     * @return a populated JTable, or an empty table if the list is null or empty
     */
    public JTable generateTable(List<T> objectList) {
        if (objectList == null || objectList.isEmpty()) {
            return new JTable();
        }

        T sample = objectList.get(0);
        Class<?> clazz = sample.getClass();
        DefaultTableModel model = new DefaultTableModel();

        try {
            List<String> columnNames = new ArrayList<>();
            List<Method> getters = new ArrayList<>();

            if (clazz.isRecord()) {
                for (var rc : clazz.getRecordComponents()) {
                    columnNames.add(rc.getName());
                    getters.add(rc.getAccessor());
                }
            } else {
                for (var field : clazz.getDeclaredFields()) {
                    columnNames.add(field.getName());
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                    getters.add(pd.getReadMethod());
                }
            }

            for (String col : columnNames) {
                model.addColumn(col);
            }

            for (T obj : objectList) {
                Object[] rowData = new Object[getters.size()];
                for (int i = 0; i < getters.size(); i++) {
                    rowData[i] = getters.get(i).invoke(obj);
                }
                model.addRow(rowData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JTable(model);
    }

}
