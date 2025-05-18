package org.example.Utilities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Utility class to create a JTable dynamically using reflection on objects.
 * @param <T>
 */

public class TableGenerator<T> {
    /**
     * Generates a JTable from a list of objects using reflection.
     *
     * @param objectList the list of objects to display
     * @return JTable representing the data
     */
    public JTable generateTable(List<T> objectList){
        if(objectList == null || objectList.isEmpty()){
            return new JTable();
        }

        T sample = objectList.get(0);
        Class<?> clazz = sample.getClass();
        DefaultTableModel model = new DefaultTableModel();

        try{
            //Set column names
            for(var field : clazz.getDeclaredFields()){
                model.addColumn(field.getName());
            }

            //Add rows
            for (T obj: objectList){
                Object[] rowData = new Object[clazz.getDeclaredFields().length];
                int i = 0;

                for(var field : clazz.getDeclaredFields()){
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                    Method getter = pd.getReadMethod();
                    rowData[i++] = getter.invoke(obj);
                }
                model.addRow(rowData);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return new JTable(model);
    }
}
