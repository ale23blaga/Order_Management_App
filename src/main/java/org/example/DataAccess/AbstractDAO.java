package org.example.DataAccess;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.example.Connection.ConnectionFactory;
import org.example.Model.Status;

/**
 * A generic abstract class that provides basic CRUD operations for any type T.
 * Uses reflection to dynamically build SQL queries and populate objects.
 * @param <T> tehe model type this DAO will oeprate on
 */
public class AbstractDAO <T>{
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    private final Class<T> type;

    /**
     * Construct an AbstractDAO and determines the actual class type fo T at runtime.
     */
    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private String createSelectQuery(String field){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(" * ");
        sb.append(" FROM ");
        sb.append(type.getSimpleName());
        sb.append(" WHERE " + field + " =?");
        return sb.toString();
    }

    private String createInsertQuery(String field){
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(type.getSimpleName());
        sb.append(" (");
        sb.append(field);
        sb.append(") ");
        return sb.toString();
    }

    /**
     * Retrieves all records from the table corresponding to type T.
     * @return a list of all objects of type T from the database
     */
    public List<T> findAll(){
        List<T> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM " + type.getSimpleName();

        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            list = createObjects(resultSet);
        }catch(SQLException ex){
            LOGGER.log(Level.WARNING, type.getName() + "DAO: findALL" + ex.getMessage());
        }finally{
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return list;
    }

    /**
     * Retrieves a single object by its ID from the database.
     * @param id the ID of the object
     * @return the foudn object, or null if not found
     */
    public T findById(int id){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");
        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            return createObjects(resultSet).get(0);
        } catch(SQLException e){
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById" + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return null;
    }

    /**
     * Uses reflection to instantiate and populate objects of type T from a ResultSet
     * @param resultSet the restult set to read from
     * @return a list of populated objects
     */
    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<>();
        Constructor<?>[] ctors = type.getDeclaredConstructors();
        Constructor<?> ctor = null;

        for (Constructor<?> c : ctors) {
            if (c.getParameterCount() == 0) {
                ctor = c;
                break;
            }
        }

        if (ctor == null) {
            throw new RuntimeException("No no-arg constructor found for " + type.getName());
        }

        try {
            ctor.setAccessible(true);

            while (resultSet.next()) {
                T instance = (T) ctor.newInstance();

                for (Field field : type.getDeclaredFields()) {
                    String fieldName = field.getName();
                    Object value = resultSet.getObject(fieldName);
                    PropertyDescriptor pd = new PropertyDescriptor(fieldName, type);
                    Method setter = pd.getWriteMethod();

                    if (value != null && field.getType().isEnum() && value instanceof String) {
                        Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), value.toString());
                        setter.invoke(instance, enumValue);
                    } else {
                        setter.invoke(instance, value);
                    }
                }

                list.add(instance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Inserts a new object into the database.
     * Automatically retrieves and sets the generated primary key.
     * @param object the object to insert
     */
    public void insert(T object){
        Connection connection = null;
        PreparedStatement statement = null;
        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();

        Field[] allFields = type.getDeclaredFields();
        for(Field field: allFields){
            if(!field.getName().equalsIgnoreCase("id")) {
                fields.append(field.getName()).append(",");
                values.append("?,");
            }
        }

        fields.setLength(fields.length() - 1); //remove ultima virgula
        values.setLength(values.length() - 1);

        String query = "INSERT INTO " + type.getSimpleName() + " (" + fields + ") VALUES (" + values + ")";

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            int paramIndex = 1;
            for(Field field: allFields){
                if(!field.getName().equalsIgnoreCase("id")) {
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if (value instanceof Enum<?>) {
                        statement.setObject(paramIndex++, value.toString());
                    } else {
                        statement.setObject(paramIndex++, value);
                    }
                }
            }
            statement.executeUpdate();

            //Retrive and set generated Id
            ResultSet generatedKyes = statement.getGeneratedKeys();
            if(generatedKyes.next()){
                int generatedId = generatedKyes.getInt(1);
                Field idField = type.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(object, generatedId);
            }
        }catch(SQLException | IllegalAccessException | NoSuchFieldException e){
            LOGGER.log(Level.WARNING, type.getName() + "DAO:insert" + e.getMessage());
        }finally{
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    /**
     * Updates an existing object in the database based on its ID.
     * @param object the object to update
     */
    public void update(T object){
        Connection connection = null;
        PreparedStatement statement = null;
        StringBuilder sb = new StringBuilder();
        Field[] fields = type.getDeclaredFields();

        sb.append("UPDATE ").append(type.getSimpleName()).append(" SET ");
        for(Field field: fields){
            if(!field.getName().equalsIgnoreCase("id")) {
                sb.append(field.getName()).append("=?,");
            }
        }
        sb.setLength(sb.length() - 1);
        sb.append("WHERE id = ?");

        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(sb.toString());

            int i = 1;
            for(Field field: fields){
                if(!field.getName().equalsIgnoreCase("id")) {
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if(value instanceof Enum<?>) {
                        statement.setObject(i++, value.toString());
                    }
                    else
                        statement.setObject(i++, value);
                }
            }

            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            statement.setObject(i, idField.get(object));

            statement.executeUpdate();
        }catch (SQLException | IllegalAccessException | NoSuchFieldException e){
            LOGGER.log(Level.WARNING, type.getName() + "DAO:update" + e.getMessage());
        }finally{
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    /**
     * Performs a soft delete by setting the status to DELETED.
     * This preserves referential integrity in related tables (order).
     * @param object the object to soft deleted
     */
    public void softDelete(T object){ //soft delete - due to FK and the requirement for for the order to still exist we can't actually delete things
        try{
            Field statusField = type.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(object, Status.DELETED);
            update(object);
        }catch (NoSuchFieldException | IllegalAccessException e){
            LOGGER.log(Level.WARNING, type.getName() + "DAO:delete (soft)" + e.getMessage());
        }
    }

    /**
     * Delete the object from the databases.
     * This does not preserve referential integrity in related tables (order).
     * @param id the id for the object to be deleted
     */
    public void hardDeleteById(int id){ //hard delete actually deletes the entry from the table (also deletes the tied orders)
        String sql = "DELETE FROM " + type.getSimpleName() + " WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection(); PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e){
            LOGGER.log(Level.WARNING, type.getName() + "DAO:delete (hard)" + e.getMessage());
        }
    }
}

