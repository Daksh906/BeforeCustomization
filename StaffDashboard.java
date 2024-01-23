import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;


public class StaffDashboard extends JFrame {
    private InventoryDAO inventoryDAO;
    private JTable productTable;
    private JPanel mainPanel;
    private int currentStaffId;
    public StaffDashboard(int currentStaffId) {
        setTitle("Staff Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.currentStaffId = currentStaffId;
        inventoryDAO = new InventoryDAO();
        initializeProductTable();

        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, Staff!");
        welcomeLabel.setFont(new Font("Helvetica", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel staffFeaturesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20)); // Adjusted for spacing
        staffFeaturesPanel.add(createPlaceOrderButton());
        staffFeaturesPanel.add(createBillButton());
        staffFeaturesPanel.add(createAttendanceButton());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> dispose());

        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(staffFeaturesPanel, BorderLayout.CENTER);
        mainPanel.add(logoutButton, BorderLayout.SOUTH);
        add(mainPanel);
        setLocationRelativeTo(null);
    }

    private void initializeProductTable() {
        productTable = new JTable();
        refreshProductList();
    }

    private void refreshProductList() {
        try {
            List<Product> products = inventoryDAO.selectAllProducts();
            productTable.setModel(createTableModel(products));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching products from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void openAttendancePanel() {
        getContentPane().removeAll();
        getContentPane().add(new AttendancePanel(currentStaffId));
        getContentPane().revalidate();
        getContentPane().repaint();
    }
    private JButton createAttendanceButton() {
        JButton attendanceButton = new JButton("Attendance");
        attendanceButton.addActionListener(e -> openAttendancePanel());
        attendanceButton.setPreferredSize(new Dimension(200, 100)); // Match other buttons' size
        return attendanceButton;
    }
    private DefaultTableModel createTableModel(List<Product> products) {
        String[] columnNames = {"Product ID", "Name", "Price", "Quantity", "Image Path"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Product product : products) {
            Object[] row = new Object[]{
                    product.getProductID(),
                    product.getName(),
                    product.getPrice(),
                    product.getQuantity(),
                    product.getImagePath()
            };
            model.addRow(row);
        }

        return model;
    }

    private JButton createPlaceOrderButton() {
        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.addActionListener(e -> {
            ArrayList<Product> products = fetchProductsFromDatabase(); // Fetch the products from the database
            POSBillScreen posBillScreen = new POSBillScreen(); // Create a new instance of POSBillScreen
            ProductListScreen productListScreen = new ProductListScreen(products, posBillScreen); // Pass both products and posBillScreen
            productListScreen.setVisible(true);
        });
        placeOrderButton.setPreferredSize(new Dimension(200, 100)); // Set preferred size as an example
        return placeOrderButton;
    }

    private ArrayList<Product> fetchProductsFromDatabase() {
        ArrayList<Product> products = new ArrayList<>();
        try {
            products.addAll(inventoryDAO.selectAllProducts());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching products from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return products;
    }


    private JButton createBillButton() {
        JButton billButton = new JButton("Bill / Point of Sale");
        billButton.addActionListener(e -> {
            POSBillScreen posBillScreen = new POSBillScreen();
            posBillScreen.setVisible(true);
        });
        billButton.setPreferredSize(new Dimension(200, 100)); // Set preferred size as an example
        return billButton;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int staffId = 1;
            StaffDashboard staffDashboard = new StaffDashboard(staffId);
            staffDashboard.setVisible(true);
        });
    }

}