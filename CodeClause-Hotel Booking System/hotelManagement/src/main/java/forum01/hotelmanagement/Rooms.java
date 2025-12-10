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
public class Rooms extends javax.swing.JFrame {

    Connection conn;
    private int key = 0;
    
    
    public Rooms() {
        initComponents();
        customizeComponents();
        conn = DBConnection.connect();
        fetchRoomData();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        headerDate.setText(today.format(formatter));
    }
    
    
    private void fetchRoomData() {
        DefaultTableModel model = (DefaultTableModel) rTable.getModel();
        model.setRowCount(0);

        try {
            String query = "SELECT * FROM room";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String name = rs.getString("name");
                String cat = rs.getString("type");
                String status = rs.getString("status");
                String price = rs.getString("number");
                int id       = rs.getInt("id");

                model.addRow(new Object[]{name, cat, status, price,id});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        rTable.getColumnModel().getColumn(4).setMinWidth(0);
        rTable.getColumnModel().getColumn(4).setMaxWidth(0);
        rTable.getColumnModel().getColumn(4).setWidth(0);
    }
    
    private void saveRoomData(boolean isEdit) {  
    if (rName.getText().isEmpty() || rCat.getSelectedIndex() == -1 || rStatus.getSelectedIndex() == -1 || rPrice.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Missing Data!!!");
        return;
    }
        PreparedStatement preparedStatement = null;
        try {
            String query;
            
            if (isEdit) {
                
                if (key == 0) {
                    JOptionPane.showMessageDialog(this, "No room selected for editing!");
                    return;
                }
                query = "UPDATE room SET name = ?, number = ?, type = ?, status = ? WHERE id = ?";
                preparedStatement = conn.prepareStatement(query);
                
                preparedStatement.setInt(5, key);
            } else {
                query = "INSERT INTO room (name, number, type, status) VALUES (?, ?, ?, ?)";
                preparedStatement = conn.prepareStatement(query);
            }

            preparedStatement.setString(1, rName.getText());
            preparedStatement.setString(2, rPrice.getText());
            preparedStatement.setString(3, rCat.getSelectedItem().toString());
            preparedStatement.setString(4, rStatus.getSelectedItem().toString());

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, isEdit ? "Room updated successfully!" : "Room added successfully!");
                clearData();
            
            } else {
                JOptionPane.showMessageDialog(this, "Operation failed! Please try again.");
            }
            
            fetchRoomData(); 
            rEdit.setText("Edit");

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
    rAdd.setBackground(new Color(45, 103, 102));
    rAdd.setForeground(Color.WHITE);
    rEdit.setBackground(new Color(45, 103, 102));
    rEdit.setForeground(Color.WHITE);
    rDelete.setBackground(new Color(45, 103, 102));
    rDelete.setForeground(Color.WHITE);
    clearData.setBackground(new Color(45, 103, 102));
    clearData.setForeground(Color.WHITE);

    // Customize table
    customizeTableHeader();
    customizeTableRows();
}
    
   private void customizeTableHeader() {
    JTableHeader header = rTable.getTableHeader();
    header.setFont(new Font("Arial", Font.BOLD, 14));
    header.setBackground(new Color(45, 103, 102)); // Dark teal color
    header.setForeground(Color.WHITE); // White text color
    header.setDefaultRenderer(new DefaultTableCellHeaderRenderer());
    header.setPreferredSize(new Dimension(header.getWidth(), 40)); // Increase header height
}
    
  private void customizeTableRows() {
    rTable.setRowHeight(60); // Set row height
    rTable.setFont(new Font("Arial", Font.PLAIN, 14)); // Set default font for table rows
    rTable.setGridColor(new Color(200, 200, 200)); // Set grid color
    rTable.setShowGrid(true); // Show grid lines
    rTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        customers = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        bookingsMenu = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        loginBtn = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        rName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        rPrice = new javax.swing.JTextField();
        rStatus = new javax.swing.JComboBox<>();
        rCat = new javax.swing.JComboBox<>();
        jPanel11 = new javax.swing.JPanel();
        rAdd = new javax.swing.JButton();
        rEdit = new javax.swing.JButton();
        rDelete = new javax.swing.JButton();
        clearData = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        rTable = new javax.swing.JTable();
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

        roomsMenu.setBackground(new java.awt.Color(36, 85, 86));
        roomsMenu.setPreferredSize(new java.awt.Dimension(255, 44));
        roomsMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                roomsMenuMouseClicked(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setIcon(new javax.swing.ImageIcon("/home/shakib/Downloads/rooms.png")); // NOI18N
        jLabel10.setText("Rooms");

        javax.swing.GroupLayout roomsMenuLayout = new javax.swing.GroupLayout(roomsMenu);
        roomsMenu.setLayout(roomsMenuLayout);
        roomsMenuLayout.setHorizontalGroup(
            roomsMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roomsMenuLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel10)
                .addContainerGap(114, Short.MAX_VALUE))
        );
        roomsMenuLayout.setVerticalGroup(
            roomsMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roomsMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        customers.setBackground(new java.awt.Color(45, 103, 102));
        customers.setPreferredSize(new java.awt.Dimension(255, 44));
        customers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                customersMouseClicked(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setIcon(new javax.swing.ImageIcon("/home/shakib/Downloads/customers.png")); // NOI18N
        jLabel11.setText("Customers");

        javax.swing.GroupLayout customersLayout = new javax.swing.GroupLayout(customers);
        customers.setLayout(customersLayout);
        customersLayout.setHorizontalGroup(
            customersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customersLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel11)
                .addContainerGap(78, Short.MAX_VALUE))
        );
        customersLayout.setVerticalGroup(
            customersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(9, Short.MAX_VALUE))
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
                .addGap(46, 46, 46)
                .addComponent(jLabel12)
                .addContainerGap(93, Short.MAX_VALUE))
        );
        bookingsMenuLayout.setVerticalGroup(
            bookingsMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bookingsMenuLayout.createSequentialGroup()
                .addComponent(jLabel12)
                .addGap(0, 11, Short.MAX_VALUE))
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
                    .addComponent(bookingsMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dashboardMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roomsMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(loginBtn))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(customers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
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

        rName.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
        rName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rNameActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(82, 82, 84));
        jLabel3.setText("Name");

        jLabel4.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(82, 82, 84));
        jLabel4.setText("Categories");

        jLabel5.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(82, 82, 84));
        jLabel5.setText("Status");

        jLabel6.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(82, 82, 84));
        jLabel6.setText("Price");

        rPrice.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
        rPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rPriceActionPerformed(evt);
            }
        });

        rStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Free", "Unlocked", " " }));

        rCat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "VIP", "Double Bed", "Single Bed", "Family", " ", " " }));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        rAdd.setText("Add");
        rAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rAddActionPerformed(evt);
            }
        });

        rEdit.setText("Edit");
        rEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rEditActionPerformed(evt);
            }
        });

        rDelete.setText("Delete");
        rDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rDeleteActionPerformed(evt);
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
                        .addComponent(rAdd)
                        .addGap(18, 18, 18)
                        .addComponent(rEdit)
                        .addGap(18, 18, 18)
                        .addComponent(rDelete)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(clearData, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rPrice)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rName)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rCat, 0, 316, Short.MAX_VALUE)
                            .addComponent(rStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rName, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rCat, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 105, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 69, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setPreferredSize(new java.awt.Dimension(681, 561));

        rTable.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        rTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"5", "55", "55", "55", null},
                {"555", "55", "55", "666", null},
                {"54656", "55", "55", "55", null},
                {null, "55", "55", "55", null}
            },
            new String [] {
                "Name", "Category", "Status", "Price", "ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        rTable.setMinimumSize(new java.awt.Dimension(100, 240));
        rTable.setRowHeight(60);
        rTable.setSelectionBackground(new java.awt.Color(255, 255, 255));
        rTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        rTable.setShowGrid(false);
        rTable.setShowHorizontalLines(true);
        rTable.setSurrendersFocusOnKeystroke(true);
        rTable.setUpdateSelectionOnSort(false);
        rTable.setVerifyInputWhenFocusTarget(false);
        rTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(rTable);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 786, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                .addGap(0, 0, 0))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(headerDate, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addGap(30, 30, 30))
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
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setOpaque(false);
        jPanel10.setPreferredSize(new java.awt.Dimension(1083, 107));

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 36)); // NOI18N
        jLabel2.setText("Manage Rooms");

        jLabel7.setFont(new java.awt.Font("Liberation Sans", 0, 22)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(153, 158, 168));
        jLabel7.setText("Add, edit or remove from your inventory");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 786, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 1326, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(74, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rNameActionPerformed

    }//GEN-LAST:event_rNameActionPerformed

    private void rPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rPriceActionPerformed

    }//GEN-LAST:event_rPriceActionPerformed

    private void rAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rAddActionPerformed
        saveRoomData(false);
    }//GEN-LAST:event_rAddActionPerformed

    private void rEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rEditActionPerformed
        saveRoomData(true);
    }//GEN-LAST:event_rEditActionPerformed

    private void rDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rDeleteActionPerformed
        if(key == 0 ){
            JOptionPane.showMessageDialog(this, "Please Select a Table row");
        }else{
            String deleteQuery = "DELETE FROM room WHERE id = ?";
            try {
                // First, delete associated bookings
                String deleteBookingsQuery = "DELETE FROM booking WHERE room = ?";
                PreparedStatement pstmtBookings = conn.prepareStatement(deleteBookingsQuery);
                pstmtBookings.setInt(1, key);
                pstmtBookings.executeUpdate();

                // Then, delete the room
                String deleteRoomQuery = "DELETE FROM room WHERE id = ?";
                PreparedStatement pstmtRoom = conn.prepareStatement(deleteRoomQuery);
                pstmtRoom.setInt(1, key);

                int rowsDeleted = pstmtRoom.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Room and associated bookings deleted successfully!");
                    fetchRoomData();
                    clearData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete room!");
                }

            } catch (SQLException ex) {
                Logger.getLogger(Rooms.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_rDeleteActionPerformed

    private void clearDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearDataMouseClicked
        //        JOptionPane.showMessageDialog(this, "Data was clear from filed");
    }//GEN-LAST:event_clearDataMouseClicked

    private void clearDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearDataActionPerformed
        JOptionPane.showMessageDialog(this, "Data was clear from filed");
        clearData();
    }//GEN-LAST:event_clearDataActionPerformed

    private void rTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rTableMouseClicked
        DefaultTableModel model = (DefaultTableModel)rTable.getModel();
        int myIndex = rTable.getSelectedRow();

        key = (int) model.getValueAt(myIndex, 4);

        rName.setText(model.getValueAt(myIndex, 0).toString());
        rCat.setSelectedItem(model.getValueAt(myIndex, 1).toString());
        rStatus.setSelectedItem(model.getValueAt(myIndex, 2).toString());
        rPrice.setText(model.getValueAt(myIndex, 3).toString());
        rEdit.setText("Update");
    }//GEN-LAST:event_rTableMouseClicked

    private void dashboardMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardMenuMouseClicked
       this.dispose();
       new Dashboard().setVisible(true);
    }//GEN-LAST:event_dashboardMenuMouseClicked

    private void roomsMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roomsMenuMouseClicked
       this.dispose();
       new Rooms().setVisible(true);
    }//GEN-LAST:event_roomsMenuMouseClicked

    private void customersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customersMouseClicked
       this.dispose();
       new Customers().setVisible(true);
    }//GEN-LAST:event_customersMouseClicked

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
        rName.setText("");
        rPrice.setText("");
        rCat.setSelectedIndex(-1);
        rStatus.setSelectedIndex(-1);
        key = 0;
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
            java.util.logging.Logger.getLogger(Rooms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Rooms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Rooms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Rooms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Rooms().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bookingsMenu;
    private javax.swing.JButton clearData;
    private javax.swing.JPanel customers;
    private javax.swing.JPanel dashboardMenu;
    private javax.swing.JLabel headerDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
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
    private javax.swing.JLabel loginBtn;
    private javax.swing.JButton rAdd;
    private javax.swing.JComboBox<String> rCat;
    private javax.swing.JButton rDelete;
    private javax.swing.JButton rEdit;
    private javax.swing.JTextField rName;
    private javax.swing.JTextField rPrice;
    private javax.swing.JComboBox<String> rStatus;
    private javax.swing.JTable rTable;
    private javax.swing.JPanel roomsMenu;
    // End of variables declaration//GEN-END:variables
}
