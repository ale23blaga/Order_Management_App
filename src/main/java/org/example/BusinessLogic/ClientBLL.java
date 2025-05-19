package org.example.BusinessLogic;

import org.example.DataAccess.ClientDAO;
import org.example.Model.Client;
import org.example.Model.Status;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business Logic Layer for Client operations.
 * Delegates data acess to {@link ClientDAO} and performs validation or filtering as needed.
 */
public class ClientBLL {
    private final ClientDAO clientDAO = new ClientDAO();

    /**
     * Retrieves all clients, including soft-deleted ones.
     * @return list of all clients
     */
    public List<Client> getAllClients(){
        List<Client> allClients = clientDAO.findAll();
        return allClients;
    }

    /**
     * Retrieves only clients marked as active.
     * @return list of active clients
     */
    public List<Client> getAllActiveClients(){
        List<Client> allClients = clientDAO.findAll();
        return allClients.stream()
                .filter(c -> c.getStatus() == Status.ACTIVE)
                .collect(Collectors.toList());
    }

    /**
     * Inserts a new client into the database.
     * @param client the client to add
     */
    public void addClient(Client client) {
        clientDAO.insert(client);
    }

    /**
     * Updates an existing client's information.
     * @param client the client with updated fields
     */
    public void updateClient(Client client) {
        clientDAO.update(client);
    }

    /**
     * Soft-deletes a client by marking them as {@code Status.DELETED}.
     * @param client the client to mark as deleted
     */
    public void softDeleteClient(Client client) {
        clientDAO.softDelete(client);  // soft delete
    }

    /**
     * Retries a client based on its id.
     * @param id the id of the wanted client.
     * @return client returned
     */
    public Client findById(int id) {
        return clientDAO.findById(id);
    }

    /**
     * Completely removes a client and all their orders from the database.
     * @param id the ID of the client to delete
     */
    public void hardDeleteById(int id){
        clientDAO.hardDeleteById(id);
    }
}
