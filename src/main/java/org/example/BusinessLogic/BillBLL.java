package org.example.BusinessLogic;

import org.example.DataAccess.BillDAO;
import org.example.Model.Bill;

import java.util.List;

/**
 * Handles reading of all bill records from the log.
 * Delegates database access to {@link BillDAO}.
 */
public class BillBLL {
    private final BillDAO billDAO = new BillDAO();

    /**
     * Retrieves all bills from the log table.
     *
     * @return list of {@link Bill} records
     */
    public List<Bill> getAllBills(){
        List<Bill> allBills = billDAO.findAll();
        return allBills;
    }
}
