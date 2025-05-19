package org.example.Model;

/**
 * Represents a client in the system.
 * Contains identifying and contact information, as well as active/deleted status.
 */
public class Client {
    private int id;
    private String name;
    private String address;
    private String email;
    private String phone;
    private Status status;

    /**
     * Construct a new Client with default status set to ACTIVE.
     */
    public Client() {
        this.status = Status.ACTIVE;
    }

    /**
     * Construct a new Client with the specified details.
     * Status is ACTIVE by default
     * @param id the client's unique id
     * @param name the client's name
     * @param address the client's address
     * @param email the client's email
     * @param phone the client's phone number
     */
    public Client(int id, String name, String address, String email, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.status = Status.ACTIVE;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
}
