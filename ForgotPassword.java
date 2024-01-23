import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgotPassword extends JFrame {
    private JTextField firstNameTextField, lastNameTextField, phoneNumberTextField;
    private JPasswordField newPasswordField;
    private JButton resetPasswordButton;

    public ForgotPassword() {
        setTitle("Forgot Password");
        setSize(400, 300);
        setLayout(new GridLayout(5, 2));
        setLocationRelativeTo(null); // Center the window

        add(new JLabel("First Name:"));
        firstNameTextField = new JTextField();
        add(firstNameTextField);

        add(new JLabel("Last Name:"));
        lastNameTextField = new JTextField();
        add(lastNameTextField);

        add(new JLabel("Phone Number:"));
        phoneNumberTextField = new JTextField();
        add(phoneNumberTextField);

        add(new JLabel("New Password:"));
        newPasswordField = new JPasswordField();
        add(newPasswordField);

        resetPasswordButton = new JButton("Reset Password");
        resetPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetPassword();
            }
        });
        add(resetPasswordButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void resetPassword() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String phoneNumber = phoneNumberTextField.getText();
        String newPassword = new String(newPasswordField.getPassword());

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE reg SET password = ? WHERE first_name = ? AND last_name = ? AND phone_number = ?";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, hashPassword(newPassword));
            pst.setString(2, firstName);
            pst.setString(3, lastName);
            pst.setString(4, phoneNumber);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Password reset successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "No user found with provided details.");
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(this, "A database error occurred.");
            e.printStackTrace();
        }
    }


    private static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        return Base64.getEncoder().encodeToString(digest);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ForgotPassword();
            }
        });
    }
}
