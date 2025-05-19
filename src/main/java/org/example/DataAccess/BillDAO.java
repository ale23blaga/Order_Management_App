package org.example.DataAccess;

import org.example.Connection.ConnectionFactory;
import org.example.Model.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for bill records.
 * Bills are immutable and stored only via insert. Updates and deletes are not supported.
 */
public class BillDAO{
    /**
     * Inserts a new bill into the log table.
     * @param bill the bill to insert
     */
    public void insert(Bill bill) {
        String sql = "INSERT INTO bill (orderId, clientName, productName, quantity, totalPrice) VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bill.orderId());
            ps.setString(2, bill.clientName());
            ps.setString(3, bill.productName());
            ps.setInt(4, bill.quantity());
            ps.setDouble(5, bill.totalPrice());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all bills from the log table.
     * @return list of all bills
     */
    public List<Bill> findAll(){
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bill";

        try(Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
            while(rs.next()){
                bills.add(new Bill(
                        rs.getInt("id"),
                        rs.getInt("orderId"),
                        rs.getString("clientName"),
                        rs.getString("productName"),
                        rs.getInt("quantity"),
                        rs.getDouble("totalPrice")
                ));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return bills;
    }
}
