package org.example.PresentatoinLayer;

import org.example.BusinessLogic.BillBLL;
import org.example.Model.Bill;
import org.example.Utilities.TableGenerator;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.util.List;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

/**
 * GUI panel for viewing all the generated bills.
 * <p>
 *     Provied functinality to export a selected bill to a PDF file.
 * </p>
 */
public class BillPanel extends JPanel {
    //sa faci pannel ca sa vezi toate bills
    //sa faci sql data dump
    private JTable billTable;
    private BillBLL billBLL = new BillBLL();

    public BillPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton pdfButton = new JButton("Make Recipt");
        topPanel.add(pdfButton);

        List<Bill> bills = billBLL.getAllBills();
        billTable = new TableGenerator<Bill>().generateTable(bills);
        billTable.setAutoResizeMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(billTable);

        pdfButton.addActionListener(e -> makeRecipt());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

    }

    /**
     * Exports the selected bill from a table into a formatted PDF file.
     * Shows a message dialog on success or failure.
     */
    private void makeRecipt() {
        int row = billTable.getSelectedRow();
        if (row == -1){
            JOptionPane.showMessageDialog(null, "Select a Bill to make PDF");
            return;
        }

        int id = (int)billTable.getValueAt(row, 0);
        String client = billTable.getValueAt(row, 2).toString();
        String product = billTable.getValueAt(row, 3).toString();
        int quantity = Integer.parseInt(billTable.getValueAt(row, 4).toString());
        double total = Double.parseDouble(billTable.getValueAt(row, 5).toString());

        String fileName = "Bill_" + id + ".pdf";

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            document.add(new Paragraph("--- RECEIPT ---"));
            document.add(new Paragraph("Client: " + client));
            document.add(new Paragraph("Product: " + product));
            document.add(new Paragraph("Quantity: " + quantity));
            document.add(new Paragraph(String.format("Total: %.2f", total)));

            document.close();
            JOptionPane.showMessageDialog(this, "Bill exported to PDF: " + fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
