package org.example.PresentatoinLayer;

import org.example.BusinessLogic.ClientBLL;
import org.example.Model.Client;
import org.example.Utilities.TableGenerator;
import org.example.Utilities.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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

        topPanel.add(addButton);
        topPanel.add(updateButton);
        topPanel.add(deleteButton);
        topPanel.add(showAllCheckBox);

        clientsTable = new TableGenerator<Client>().generateTable(clientBLL.getAllActiveClients());
        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane = new JScrollPane(clientsTable);

        showAllCheckBox.addActionListener(e -> refreshTable());
        addButton.addActionListener(e -> addClient());
        updateButton.addActionListener(e -> updateClient());
        deleteButton.addActionListener(e -> deleteClient());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

    }

    private void refreshTable(){
        List<Client> data = showAllCheckBox.isSelected() ? clientBLL.getAllClients() : clientBLL.getAllActiveClients();
        clientsTable = new TableGenerator<Client>().generateTable(data);
        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(clientsTable);
    }

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

    private void deleteClient() {
        int row = clientsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "No client selected.");
            return;
        }
        int id = Integer.parseInt(clientsTable.getValueAt(row, 0).toString());
        Client c = clientBLL.findById(id);
        clientBLL.deleteClient(c);
        refreshTable();
    }
}
