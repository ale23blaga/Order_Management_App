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

    /**
     * Generates the select query.
     * @param field the field the selection is based on
     * @return the generated query
     */
    private String createSelectQuery(String field){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(" * ");
        sb.append(" FROM ");
        sb.append(type.getSimpleName());
        sb.append(" WHERE " + field + " =?");
        return sb.toString();
    }

    /**
     * Generates the inset query.
     * @param fields provided fields name
     * @param values provided placeholders vor values ("?,")
     * @return the generated query
     */
    private String createInsertQuery(StringBuilder fields, StringBuilder values){
        fields.setLength(fields.length()-1);
        values.setLength(values.length()-1);
        return "INSERT INTO " + type.getSimpleName() + " (" + fields + ") VALUES (" + values + ")";
    }

    /**
     * Generates the update query.
     * @return the generated query
     */
    private String createUpdateQuery(){
        Field[] fields = type.getDeclaredFields();
        StringBuilder sb = new StringBuilder("UPDATE " + type.getSimpleName() + " SET ");
        for(Field f : fields){
            if(!f.getName().equalsIgnoreCase("id")){
                sb.append(f.getName()).append(" = ?,");
            }
        }
        sb.setLength(sb.length()-1);
        sb.append(" WHERE id = ?");
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
     * @param resultSet the result set to read from
     * @return a list of populated objects
     */
    private List<T> createObjects(ResultSet resultSet){
        List<T> list = new ArrayList<>();
        Constructor<?> constructor = getNoArgsConstructor();

        try{
            constructor.setAccessible(true);
            while(resultSet.next()){
                T instance = (T) constructor.newInstance();
                populateFieldsFromResultSet(instance, resultSet);
                list.add(instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrives the no-argument constructor of the current type.
     * @return the no-argument constructor
     */
    private Constructor<?> getNoArgsConstructor(){
        for(Constructor<?> constructor : type.getDeclaredConstructors()){
            if(constructor.getParameterCount() == 0){
                return constructor;
            }
        }
        throw new RuntimeException("No noArgsConstructor found" + type.getName());
    }

    /**
     * Populates fileds of the given object instance with values from the current Result set.
     * Supports enum conversion when target field is an enum and the value is a String
     * @param instance the object instance to populate
     * @param resultSet the current ResultSet row
     * @throws Exception if reflection or property access fails
     */
    private void populateFieldsFromResultSet(T instance, ResultSet resultSet) throws Exception{
        for(Field field : type.getDeclaredFields()){
            String fieldName = field.getName();
            Object value = resultSet.getObject(fieldName);
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
            Method setter = propertyDescriptor.getWriteMethod();

            if(value != null && field.getType().isEnum() && value instanceof String){
                Object enumValue = Enum.valueOf((Class<Enum>)field.getType(), value.toString());
                setter.invoke(instance, enumValue);
            }
            else {
                setter.invoke(instance, value);
            }
        }
    }

    /**
     * Inserts a new object into the database.
     * Automatically retrieves and sets the generated primary key.
     * @param object the object to insert
     */
    public void insert(T object){
        Field[] allFields = type.getDeclaredFields();
        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for(Field field : allFields){
            if(!field.getName().equalsIgnoreCase("id")){
                fields.append(field.getName()).append(",");
                values.append("?,");
            }
        }

        String query = createInsertQuery(fields, values);

        Connection connection = null;
        PreparedStatement statement = null;

        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            setPreparedStatementValues(statement, allFields, object);
            statement.executeUpdate();
            setGeneratedId(statement, object);

        }catch(SQLException | IllegalAccessException | NoSuchFieldException e){
            LOGGER.log(Level.WARNING, type.getName() + "DAO: insert " + e.getMessage());
        }finally{
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    /**
     * Sets the values of a PreparedStatement based on the fields of the given object.
     * @param statement the PreparedStatement to populate
     * @param fields the array of fields to retrive values from
     * @param object the object containing values to insert
     * @throws IllegalAccessException if field access is denied
     * @throws SQLException if setting a parameter fails
     */
    private void setPreparedStatementValues(PreparedStatement statement, Field[] fields, T object) throws IllegalAccessException, SQLException {
        int i = 1;
        for(Field field : fields){
            if(!field.getName().equalsIgnoreCase("id")){
                field.setAccessible(true);
                Object value = field.get(object);
                statement.setObject(i++, (value instanceof Enum<?>) ? value.toString() : value );
            }
        }
    }

    /**
     * Retrives the auto-generated id after insert.
     * @param statement the statement used for the insert
     * @param object the object whose ID field will be updated
     * @throws SQLException if result set operation fail
     * @throws NoSuchFieldException if the "Id" field is missing
     * @throws IllegalAccessException if setting the field fails
     */
    private void setGeneratedId(PreparedStatement statement, T object) throws SQLException, NoSuchFieldException, IllegalAccessException{
        ResultSet rs = statement.getGeneratedKeys();
        if(rs.next()){
            int id = rs.getInt(1);
            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(object, id);
        }
    }

    /**
     * Updates an existing object in the database based on its ID.
     * @param object the object to update
     */
    public void update(T object){
        String query = createUpdateQuery();
        try(Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)){
            setUpdateStatementValues(statement, object);
            statement.executeUpdate();
        }catch(SQLException | IllegalAccessException | NoSuchFieldException e){
            LOGGER.log(Level.WARNING, type.getName() + "DAO: update " + e.getMessage());
        }
    }

    /**
     * Sets the parameters of a PreparedStatement for an update operation. Does not update id.
     * @param statement the statement to complete
     * @param object the object containing updated values
     * @throws IllegalAccessException if field access is denied
     * @throws SQLException if setting a parameter fails
     * @throws NoSuchFieldException if "id" field is missing
     */
    private void setUpdateStatementValues(PreparedStatement statement, T object) throws IllegalAccessException, SQLException, NoSuchFieldException {
        Field[] fields = type.getDeclaredFields();
        int i = 1;
        for(Field field : fields){
            if(!field.getName().equalsIgnoreCase("id")){
                field.setAccessible(true);
                Object value = field.get(object);
                statement.setObject(i++, (value instanceof Enum<?>) ? value.toString() : value );
            }
        }
        Field idField = type.getDeclaredField("id");
        idField.setAccessible(true);
        statement.setObject(i, idField.get(object));
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

