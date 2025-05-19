package org.example.PresentatoinLayer;

import org.example.BusinessLogic.ClientBLL;
import org.example.BusinessLogic.OrderBLL;
import org.example.BusinessLogic.ProductBLL;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI frame for the Order Management app.
 *     Contains navigation buttons for switching between Clients, Products,
 *     Orders, and Bills panels using a CardLayout.
 */
public class MainFrame  extends JFrame {
    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private ClientPanel clientPanel;
    private ProductPanel productPanel;
    private OrderPanel orderPanel;
    private BillPanel billPanel;

    /**
     * Initializes the main application window, sets up layout and navigation tools, and loads initial panels for eahc functional area
     */
    public MainFrame() {
        setTitle("Orders Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clientButton = new JButton("Clients");
        JButton productButton = new JButton("Products");
        JButton orderButton = new JButton("Orders");
        JButton billButton = new JButton("Bills");

        topButtonPanel.add(clientButton);
        topButtonPanel.add(productButton);
        topButtonPanel.add(orderButton);
        topButtonPanel.add(billButton);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        clientPanel = new ClientPanel();
        productPanel = new ProductPanel();
        orderPanel = new OrderPanel();
        billPanel = new BillPanel();

        cardPanel.add(clientPanel, "Clients");
        cardPanel.add(productPanel, "Products");
        cardPanel.add(orderPanel, "Orders");
        cardPanel.add(billPanel, "Bills");

        clientButton.addActionListener(e -> {
            clientPanel = new ClientPanel();
            cardPanel.remove(0);
            cardPanel.add(clientPanel, "Clients");
            cardLayout.show(cardPanel, "Clients");
        });

        productButton.addActionListener(e -> {
            productPanel = new ProductPanel();
            cardPanel.remove(1);
            cardPanel.add(productPanel, "Products");
            cardLayout.show(cardPanel, "Products");
        });

        orderButton.addActionListener(e -> {
            orderPanel = new OrderPanel();
            cardPanel.remove(2);
            cardPanel.add(orderPanel, "Orders");
            cardLayout.show(cardPanel, "Orders");
        });

        billButton.addActionListener(e -> {
            billPanel = new BillPanel();
            cardPanel.remove(3);
            cardPanel.add(billPanel, "Bills");
            cardLayout.show(cardPanel, "Bills");
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topButtonPanel, BorderLayout.NORTH);
        getContentPane().add(cardPanel, BorderLayout.CENTER);
    }
}
