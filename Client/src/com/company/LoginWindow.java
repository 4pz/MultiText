package com.company;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;


public class LoginWindow extends JFrame
{
  private final ChatClient client;

  private static JFrame thisWindow;
  private JTextField nameField;
  private JPasswordField passwordField;


  /**
   * Login window Constructor
   * @param title title for window
   */
  public LoginWindow(String title)
  {
    super(title);
    this.client = new ChatClient("localhost", 8818);
    client.connect();
    thisWindow = this;

    JLabel nameLabel = new JLabel("Login name:", JLabel.RIGHT);
    nameField = new JTextField(20);

    JLabel passwordLabel = new JLabel("Password:", JLabel.RIGHT);
    passwordField = new JPasswordField(20);

    JButton loginBtn = new JButton("Login");

    JPanel fieldsPanel = new JPanel(new GridLayout(3, 3, 10, 10));
    fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    fieldsPanel.add(nameLabel);
    fieldsPanel.add(nameField);
    fieldsPanel.add(new JPanel());  // filler

    fieldsPanel.add(new JLabel("Password:", JLabel.RIGHT));
    fieldsPanel.add(passwordField);
    fieldsPanel.add(new JPanel());  // filler

    fieldsPanel.add(new JPanel());  // filler
    fieldsPanel.add(loginBtn);

    Container c = getContentPane();
    c.add(fieldsPanel);

    loginBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        doLogin();
      }
    });
  }

  /**
   * Login command
   */
  private void doLogin()
  {
    String name = nameField.getText();
    String password = passwordField.getText();

    try {
      if (client.login(name, password)) {
        UserListPane userListPane = new UserListPane(client);
        JFrame frame = new JFrame("User List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);

        frame.getContentPane().add(userListPane, BorderLayout.CENTER);
        frame.setVisible(true);

        setVisible(false);
      } else {
        JOptionPane.showMessageDialog(this, "Invalid login/password");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Start the GUI
   * @param args args
   */
  public static void main(String[]args) {
    LoginWindow window = new LoginWindow("MultiText");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setResizable(false);
    window.setBounds(0, 0, 360, 140);
    window.setLocationRelativeTo(null);
    window.setVisible(true);
  }
}