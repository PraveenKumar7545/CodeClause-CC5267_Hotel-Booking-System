/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package forum01.hotelmanagement;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Praveen
 */
public class Customers extends javax.swing.JFrame {

    Connection conn;
    private int key = 0;
    
    public Customers() {
        initComponents();
        customizeComponents();
        conn = DBConnection.connect();
        fetchCustomerData();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        headerDate.setText(today.format(formatter));
    }
    
    private void fetchCustomerData() {
        DefaultTableModel model = (DefaultTableModel) cTable.getModel();
        model.setRowCount(0);

        try {
            String query = "SELECT * FROM customer";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String gender = rs.getString("gender");
                String address = rs.getString("address");
                int id       = rs.getInt("id");

                model.addRow(new Object[]{name, phone, gender, address,id});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        cTable.getColumnModel().getColumn(4).setMinWidth(0);
        cTable.getColumnModel().getColumn(4).setMaxWidth(0);
        cTable.getColumnModel().getColumn(4).setWidth(0);
    }
    
    private void saveRoomData(boolean isEdit) {  
        if (cName.getText().isEmpty() || cGender.getSelectedIndex() == -1 || cPhone.getText().isEmpty() || cAddress.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Missing Data!!!");
            return;
        }
        PreparedStatement preparedStatement = null;
        try {
            String query;
            
            if (isEdit) {
                
                if (key == 0) {
                    JOptionPane.showMessageDialog(this, "No Customer selected for editing!");
                    return;
                }
                query = "UPDATE customer SET name = ?, phone = ?, gender = ?, address = ? WHERE id = ?";
                preparedStatement = conn.prepareStatement(query);
                
                preparedStatement.setInt(5, key);
            } else {
                query = "INSERT INTO customer (name, phone, gender, address) VALUES (?, ?, ?, ?)";
                preparedStatement = conn.prepareStatement(query);
            }

            preparedStatement.setString(1, cName.getText());
            preparedStatement.setString(2, cPhone.getText());
            preparedStatement.setString(3, cGender.getSelectedItem().toString());
            preparedStatement.setString(4, cAddress.getText());

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, isEdit ? "Customer updated successfully!" : "Customer added successfully!");
               clearData();
            
            } else {
                JOptionPane.showMessageDialog(this, "Operation failed! Please try again.");
            }
            
            fetchCustomerData(); 
            

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
}
    
    private void customizeComponents() {
    // Customize the main panel
    jPanel1.setBackground(new Color(240, 240, 240));

    // Customize the header panel
    jPanel2.setBackground(new Color(45, 103, 102));
    jLabel1.setFont(new Font("Arial", Font.BOLD, 24));
    jLabel1.setForeground(Color.WHITE);

    // Customize the form panel
    jPanel6.setBackground(Color.WHITE);
    jPanel6.setLayout(new BoxLayout(jPanel6, BoxLayout.Y_AXIS));
    jPanel6.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, Color.GRAY), // Use gray border color
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));
    jPanel7.setBackground(Color.WHITE);
    jPanel7.setLayout(new BoxLayout(jPanel7, BoxLayout.Y_AXIS));
    jPanel7.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, Color.GRAY), // Use gray border color
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));
    

    // Customize buttons
    cAdd.setBackground(new Color(45, 103, 102));
    cAdd.setForeground(Color.WHITE);
    cEdit.setBackground(new Color(45, 103, 102));
    cEdit.setForeground(Color.WHITE);
    cDelete.setBackground(new Color(45, 103, 102));
    cDelete.setForeground(Color.WHITE);
    clearData.setBackground(new Color(45, 103, 102));
    clearData.setForeground(Color.WHITE);

    // Customize table
    customizeTableHeader();
    customizeTableRows();
}
    
   private void customizeTableHeader() {
    JTableHeader header = cTable.getTableHeader();
    header.setFont(new Font("Arial", Font.BOLD, 14));
    header.setBackground(new Color(45, 103, 102)); // Dark teal color
    header.setForeground(Color.WHITE); // White text color
    header.setDefaultRenderer(new DefaultTableCellHeaderRenderer());
    header.setPreferredSize(new Dimension(header.getWidth(), 40)); // Increase header height
}
    
  private void customizeTableRows() {
    cTable.setRowHeight(60); // Set row height
    cTable.setFont(new Font("Arial", Font.PLAIN, 14)); // Set default font for table rows
    cTable.setGridColor(new Color(200, 200, 200)); // Set grid color
    cTable.setShowGrid(true); // Show grid lines
    cTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Set foreground color to #4F4F51 (dark gray)
            c.setForeground(new Color(79, 79, 81)); // RGB for #4F4F51

            // Set font to bold and increase size
            setFont(new Font("Arial", Font.BOLD, 14)); // Bold and size 14

            // Alternate row colors for background
            c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240)); // Alternate row colors

            // Add padding
            ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding

            // Center-align text
            setHorizontalAlignment(SwingConstants.CENTER); // Center-align text

            return c;
        }
    });
}
    static class DefaultTableCellHeaderRenderer extends DefaultTableCellRenderer {
        public DefaultTableCellHeaderRenderer() {
            setHorizontalAlignment(JLabel.CENTER); // Center-align header text
        }
    }
    
   public class RoundedBorder extends AbstractBorder {
    private int radius;
    private Color borderColor; // Add a field for border color

    public RoundedBorder(int radius, Color borderColor) {
        this.radius = radius;
        this.borderColor = borderColor; // Initialize the border color
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borderColor); // Use the specified border color
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = this.radius;
        insets.top = insets.bottom = this.radius;
        return insets;
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dashboardMenu = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        roomsMenu = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        customersMenu = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        bookingsMenu = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        loginBtn = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        cAdd = new javax.swing.JButton();
        cEdit = new javax.swing.JButton();
        cDelete = new javax.swing.JButton();
        clearData = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cName = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        cPhone = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cGender = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        cAddress = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        cTable = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        headerDate = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(1593, 840));

        jPanel2.setBackground(new java.awt.Color(45, 103, 102));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon("/home/shakib/Downloads/Pasted image.png")); // NOI18N
        jLabel1.setText("HotelPro");

        dashboardMenu.setBackground(new java.awt.Color(45, 103, 102));
        dashboardMenu.setPreferredSize(new java.awt.Dimension(255, 44));
        dashboardMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashboardMenuMouseClicked(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setIcon(new javax.swing.ImageIcon("/home/shakib/Downloads/Screenshot from 2025-03-15 19-42-15.png")); // NOI18N
        jLabel9.setText("Dashboard");

        javax.swing.GroupLayout dashboardMenuLayout = new javax.swing.GroupLayout(dashboardMenu);
        dashboardMenu.setLayout(dashboardMenuLayout);
        dashboardMenuLayout.setHorizontalGroup(
            dashboardMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardMenuLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel9)
                .addContainerGap(83, Short.MAX_VALUE))
        );
        dashboardMenuLayout.setVerticalGroup(
            dashboardMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        roomsMenu.setBackground(new java.awt.Color(45, 103, 102));
        roomsMenu.setPreferredSize(new java.awt.Dimension(255, 44));
        roomsMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                roomsMenuMouseClicked(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setIcon(new javax.swing.ImageIcon("/home/shakib/Downloads/room_new.png")); // NOI18N
        jLabel10.setText("Rooms");

        javax.swing.GroupLayout roomsMenuLayout = new javax.swing.GroupLayout(roomsMenu);
        roomsMenu.setLayout(roomsMenuLayout);
        roomsMenuLayout.setHorizontalGroup(
            roomsMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roomsMenuLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel10)
                .addContainerGap(115, Short.MAX_VALUE))
        );
        roomsMenuLayout.setVerticalGroup(
            roomsMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roomsMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        customersMenu.setBackground(new java.awt.Color(36, 85, 86));
        customersMenu.setPreferredSize(new java.awt.Dimension(255, 44));
        customersMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                customersMenuMouseClicked(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setIcon(new javax.swing.ImageIcon("/home/shakib/Downloads/customer_new.png")); // NOI18N
        jLabel11.setText("Customers");

        javax.swing.GroupLayout customersMenuLayout = new javax.swing.GroupLayout(customersMenu);
        customersMenu.setLayout(customersMenuLayout);
        customersMenuLayout.setHorizontalGroup(
            customersMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customersMenuLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel11)
                .addContainerGap(82, Short.MAX_VALUE))
        );
        customersMenuLayout.setVerticalGroup(
            customersMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customersMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bookingsMenu.setBackground(new java.awt.Color(45, 103, 102));
        bookingsMenu.setPreferredSize(new java.awt.Dimension(255, 44));
        bookingsMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bookingsMenuMouseClicked(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setIcon(new javax.swing.ImageIcon("/home/shakib/Downloads/booking.png")); // NOI18N
        jLabel12.setText("Bookings");

        javax.swing.GroupLayout bookingsMenuLayout = new javax.swing.GroupLayout(bookingsMenu);
        bookingsMenu.setLayout(bookingsMenuLayout);
        bookingsMenuLayout.setHorizontalGroup(
            bookingsMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bookingsMenuLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel12)
                .addContainerGap(97, Short.MAX_VALUE))
        );
        bookingsMenuLayout.setVerticalGroup(
            bookingsMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bookingsMenuLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12)
                .addContainerGap())
        );

        loginBtn.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        loginBtn.setForeground(new java.awt.Color(255, 255, 255));
        loginBtn.setIcon(new javax.swing.ImageIcon("/home/shakib/Downloads/logout.png")); // NOI18N
        loginBtn.setText("Logout");
        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginBtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(customersMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dashboardMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roomsMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(loginBtn)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(bookingsMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(71, 71, 71)
                .addComponent(dashboardMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(roomsMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(customersMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bookingsMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(loginBtn)
                .addGap(19, 19, 19))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(1338, 869));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Liberation Sans", 0, 15), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel6.setPreferredSize(new java.awt.Dimension(329, 562));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        cAdd.setText("Add");
        cAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cAddActionPerformed(evt);
            }
        });

        cEdit.setText("Edit");
        cEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cEditActionPerformed(evt);
            }
        });

        cDelete.setText("Delete");
        cDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cDeleteActionPerformed(evt);
            }
        });

        clearData.setText("Clear Value");
        clearData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearDataMouseClicked(evt);
            }
        });
        clearData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(clearData, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(cAdd)
                        .addGap(18, 18, 18)
                        .addComponent(cEdit)
                        .addGap(18, 18, 18)
                        .addComponent(cDelete)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(clearData, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 102, 102));
        jLabel3.setText("Name");

        cName.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
        cName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cNameActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 102, 102));
        jLabel14.setText("Phone");

        cPhone.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
        cPhone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cPhoneActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("Gender");

        cGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female", " " }));

        jLabel6.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 102, 102));
        jLabel6.setText("Address");

        cAddress.setColumns(20);
        cAddress.setRows(5);
        jScrollPane2.setViewportView(cAddress);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cName)
                    .addComponent(cGender, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cPhone, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cName, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cGender, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setPreferredSize(new java.awt.Dimension(681, 561));

        cTable.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        cTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"5", "55", "55", "55", null},
                {"555", "55", "55", "666", null},
                {"54656", "55", "55", "55", null},
                {null, "55", "55", "55", null}
            },
            new String [] {
                "Name", "Phone", "Gender", "Address", "ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        cTable.setMinimumSize(new java.awt.Dimension(100, 240));
        cTable.setRowHeight(60);
        cTable.setSelectionBackground(new java.awt.Color(255, 255, 255));
        cTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        cTable.setShowGrid(false);
        cTable.setShowHorizontalLines(true);
        cTable.setSurrendersFocusOnKeystroke(true);
        cTable.setUpdateSelectionOnSort(false);
        cTable.setVerifyInputWhenFocusTarget(false);
        cTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(cTable);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 698, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setOpaque(false);

        jLabel8.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(58, 67, 84));
        jLabel8.setText("Hotel Management System");

        headerDate.setFont(new java.awt.Font("Liberation Sans", 0, 22)); // NOI18N
        headerDate.setForeground(new java.awt.Color(153, 158, 168));
        headerDate.setText("March 13,2025");

        jLabel16.setFont(new java.awt.Font("Liberation Sans", 0, 22)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(153, 158, 168));
        jLabel16.setIcon(new javax.swing.ImageIcon("/home/shakib/Downloads/header_icon.png")); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 745, Short.MAX_VALUE)
                .addComponent(headerDate, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addGap(39, 39, 39))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(headerDate, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16))
                    .addComponent(jLabel8))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setOpaque(false);
        jPanel10.setPreferredSize(new java.awt.Dimension(981, 107));

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 36)); // NOI18N
        jLabel2.setText("Manage Customers");

        jLabel7.setFont(new java.awt.Font("Liberation Sans", 0, 22)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(153, 158, 168));
        jLabel7.setText("Add, edit, or remove customer information.");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel2))
                .addContainerGap(881, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 707, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 1332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(354, 354, 354))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(86, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 1337, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 836, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1592, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cAddActionPerformed
        saveRoomData(false);
    }//GEN-LAST:event_cAddActionPerformed

    private void cEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cEditActionPerformed
        saveRoomData(true);
    }//GEN-LAST:event_cEditActionPerformed

    private void cDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cDeleteActionPerformed
        if (key == 0) {
        JOptionPane.showMessageDialog(this, "Please Select a Table row");
    } else {
        try {
            // First, delete associated bookings
            String deleteBookingsQuery = "DELETE FROM booking WHERE customer = ?";
            PreparedStatement pstmtBookings = conn.prepareStatement(deleteBookingsQuery);
            pstmtBookings.setInt(1, key);
            pstmtBookings.executeUpdate();

            // Then, delete the customer
            String deleteCustomerQuery = "DELETE FROM customer WHERE id = ?";
            PreparedStatement pstmtCustomer = conn.prepareStatement(deleteCustomerQuery);
            pstmtCustomer.setInt(1, key);

            int rowsDeleted = pstmtCustomer.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Customer and associated bookings deleted successfully!");
                fetchCustomerData();
                clearData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete customer!");
            }

        } catch (SQLException ex) {
            Logger.getLogger(Customers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    }//GEN-LAST:event_cDeleteActionPerformed

    private void clearDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearDataMouseClicked
        //        JOptionPane.showMessageDialog(this, "Data was clear from filed");
    }//GEN-LAST:event_clearDataMouseClicked

    private void clearDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearDataActionPerformed
        JOptionPane.showMessageDialog(this, "Data was clear from filed");
        clearData();
    }//GEN-LAST:event_clearDataActionPerformed

    private void cNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cNameActionPerformed

    private void cPhoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cPhoneActionPerformed

    }//GEN-LAST:event_cPhoneActionPerformed

    private void cTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cTableMouseClicked
        DefaultTableModel model = (DefaultTableModel)cTable.getModel();
        int myIndex = cTable.getSelectedRow();

        key = (int) model.getValueAt(myIndex, 4);

        cName.setText(model.getValueAt(myIndex, 0).toString());
        cPhone.setText(model.getValueAt(myIndex, 1).toString());
        cGender.setSelectedItem(model.getValueAt(myIndex, 2).toString());
        cAddress.setText(model.getValueAt(myIndex, 3).toString());
        cEdit.setText("Update");
    }//GEN-LAST:event_cTableMouseClicked

    private void dashboardMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardMenuMouseClicked
       this.dispose();
       new Dashboard().setVisible(true);
    }//GEN-LAST:event_dashboardMenuMouseClicked

    private void roomsMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roomsMenuMouseClicked
       this.dispose();
       new Rooms().setVisible(true);
    }//GEN-LAST:event_roomsMenuMouseClicked

    private void customersMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customersMenuMouseClicked
        this.dispose();
       new Customers().setVisible(true);
    }//GEN-LAST:event_customersMenuMouseClicked

    private void bookingsMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bookingsMenuMouseClicked
        this.dispose();
       new Bookings().setVisible(true);
    }//GEN-LAST:event_bookingsMenuMouseClicked

    private void loginBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnMouseClicked
        // Clear the session
        Session.setLoggedIn(false);
        Session.setUsername("");

        // Close the current dashboard window
        dispose();
        new Login().setVisible(true);
    }//GEN-LAST:event_loginBtnMouseClicked

    
    private void clearData(){
        cName.setText("");
        cPhone.setText("");
        cGender.setSelectedIndex(-1);
        cAddress.setText("");
        key = 0;
        cEdit.setText("Edit");
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Customers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Customers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Customers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Customers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Customers().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bookingsMenu;
    private javax.swing.JButton cAdd;
    private javax.swing.JTextArea cAddress;
    private javax.swing.JButton cDelete;
    private javax.swing.JButton cEdit;
    private javax.swing.JComboBox<String> cGender;
    private javax.swing.JTextField cName;
    private javax.swing.JTextField cPhone;
    private javax.swing.JTable cTable;
    private javax.swing.JButton clearData;
    private javax.swing.JPanel customersMenu;
    private javax.swing.JPanel dashboardMenu;
    private javax.swing.JLabel headerDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel loginBtn;
    private javax.swing.JPanel roomsMenu;
    // End of variables declaration//GEN-END:variables
}
