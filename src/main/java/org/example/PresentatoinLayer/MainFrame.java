package org.example.PresentatoinLayer;

import org.example.BusinessLogic.ClientBLL;
import org.example.BusinessLogic.OrderBLL;
import org.example.BusinessLogic.ProductBLL;

import javax.swing.*;
import java.awt.*;

public class MainFrame  extends JFrame {
    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private ClientPanel clientPanel;
    private ProductPanel productPanel;
    private OrderPanel orderPanel;

    public MainFrame() {
        setTitle("Orders Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clientButton = new JButton("Clients");
        JButton productButton = new JButton("Products");
        JButton orderButton = new JButton("Orders");

        topButtonPanel.add(clientButton);
        topButtonPanel.add(productButton);
        topButtonPanel.add(orderButton);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        clientPanel = new ClientPanel();
        productPanel = new ProductPanel();
        orderPanel = new OrderPanel();

        cardPanel.add(clientPanel, "Clients");
        cardPanel.add(productPanel, "Products");
        cardPanel.add(orderPanel, "Orders");

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

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topButtonPanel, BorderLayout.NORTH);
        getContentPane().add(cardPanel, BorderLayout.CENTER);
    }
}
