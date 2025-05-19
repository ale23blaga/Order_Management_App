package org.example.PresentatoinLayer;

import org.example.BusinessLogic.ClientBLL;
import org.example.Model.Client;
import org.example.Utilities.TableGenerator;
import org.example.Utilities.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * GUI panel for displaying and managing clients.
 *     Provides controls to add, update, delete, and hard delete clients.
 *     Also supports viewing all clients or only active clients (not soft deleted ones).
 */

public class ClientPanel extends JPanel {
    private final ClientBLL clientBLL = new ClientBLL();
    private JTable clientsTable;
    private final JCheckBox showAllCheckBox = new JCheckBox("Show Deleted");
    private JScrollPane scrollPane;

    public ClientPanel() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();

        JButton addButton = new JButton("Add Client");
        JButton updateButton = new JButton("Update Client");
        JButton deleteButton = new JButton("Delete Client");
        JButton hardDeleteButton = new JButton("Hard Delete Client");

        topPanel.add(addButton);
        topPanel.add(updateButton);
        topPanel.add(deleteButton);
        topPanel.add(showAllCheckBox);
        topPanel.add(hardDeleteButton);

        clientsTable = new TableGenerator<Client>().generateTable(clientBLL.getAllActiveClients());
        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane = new JScrollPane(clientsTable);

        showAllCheckBox.addActionListener(e -> refreshTable());
        addButton.addActionListener(e -> addClient());
        updateButton.addActionListener(e -> updateClient());
        deleteButton.addActionListener(e -> softDeleteClient());
        hardDeleteButton.addActionListener(e -> deleteClient());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

    }

    /**
     * Refreshes the JTable based on the 'show deleted' checkbox.
     */

    private void refreshTable(){
        List<Client> data = showAllCheckBox.isSelected() ? clientBLL.getAllClients() : clientBLL.getAllActiveClients();
        clientsTable = new TableGenerator<Client>().generateTable(data);
        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(clientsTable);
    }

    /**
     * Displays a form to enter a new client's data and validates the input before insertion.
     */
    private void addClient() {
        JTextField name = new JTextField(30);
        JTextField address = new JTextField(30);
        JTextField phone = new JTextField(30);
        JTextField email = new JTextField(30);

        Object[] inputs = {"Name:", name, "Address:", address, "Email:", email, "Phone:", phone};
        int result = JOptionPane.showConfirmDialog(null, inputs, "Add Client", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (!ValidationUtils.isNonEmpty(name.getText()) || !ValidationUtils.isValidEmail(email.getText()) || !ValidationUtils.isValidPhone(phone.getText())) {
                JOptionPane.showMessageDialog(null, "Invalid input data");
                return;
            }
            clientBLL.addClient(new Client(0, name.getText(), address.getText(), email.getText(), phone.getText()));
            refreshTable();
        }
    }

    /**
     * Displays a from to update a client's information and validates the input before the update.
     */
    private void updateClient() {
        int row = clientsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "No client selected.");
            return;
        }
        int id = Integer.parseInt(clientsTable.getValueAt(row, 0).toString());
        Client existingClient = clientBLL.findById(id);


        JTextField name = new JTextField(String.valueOf(clientsTable.getValueAt(row, 1)));
        JTextField address = new JTextField(String.valueOf(clientsTable.getValueAt(row, 2)));
        JTextField email = new JTextField(String.valueOf(clientsTable.getValueAt(row, 3)));
        JTextField phone = new JTextField(String.valueOf(clientsTable.getValueAt(row, 4)));


        Object[] inputs = {"Name:", name, "Address:", address, "Email:", email, "Phone:", phone};
        int result = JOptionPane.showConfirmDialog(null, inputs, "Update Client", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (!ValidationUtils.isNonEmpty(name.getText()) || !ValidationUtils.isValidEmail(email.getText()) || !ValidationUtils.isValidPhone(phone.getText())) {
                JOptionPane.showMessageDialog(null, "Invalid input data");
                return;
            }
            Client updated = new Client(id, name.getText(), address.getText(), email.getText(), phone.getText());
            updated.setStatus(existingClient.getStatus());
            clientBLL.updateClient(updated);
            refreshTable();
        }
    }

    /**
     * Set's a client's status to 'deleted' (soft version for delete so that we maintain the related orders).
     */
    private void softDeleteClient() {
        int row = clientsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "No client selected.");
            return;
        }
        int id = Integer.parseInt(clientsTable.getValueAt(row, 0).toString());
        Client c = clientBLL.findById(id);
        clientBLL.softDeleteClient(c);
        refreshTable();
    }

    /**
     * Displays a Dialog to confirm that the user wants to delete the clinet and consequently all the orders they have.
     */
    private void deleteClient() {
        int row = clientsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "No client selected.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "This will permanently delete the selected client and all their orders. Continue?",
                "Confirm Hard Delete", JOptionPane.OK_CANCEL_OPTION);
        if (confirm == JOptionPane.OK_OPTION) {
            int id = Integer.parseInt(clientsTable.getValueAt(row, 0).toString());
            clientBLL.hardDeleteById(id);
            refreshTable();
        }
    }
}
