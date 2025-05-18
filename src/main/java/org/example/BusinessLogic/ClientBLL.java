package org.example.BusinessLogic;

import org.example.DataAccess.ClientDAO;
import org.example.Model.Client;
import org.example.Model.Status;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business Logic Layer for Client operations.
 */
public class ClientBLL {
    private final ClientDAO clientDAO = new ClientDAO();

    public List<Client> getAllClients(){
        List<Client> allClients = clientDAO.findAll();
        return allClients;
    }

    public List<Client> getAllActiveClients(){
        List<Client> allClients = clientDAO.findAll();
        return allClients.stream()
                .filter(c -> c.getStatus() == Status.ACTIVE)
                .collect(Collectors.toList());
    }

    public void addClient(Client client) {
        clientDAO.insert(client);
    }

    public void updateClient(Client client) {
        clientDAO.update(client);
    }

    public void deleteClient(Client client) {
        clientDAO.delete(client);  // soft delete
    }

    public Client findById(int id) {
        return clientDAO.findById(id);
    }
}
