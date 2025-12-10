
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Bookings extends javax.swing.JFrame {

    Connection conn;
    private int key = 0;
    
    
    public Bookings() {
        initComponents();
        customizeComponents();
        try {
            conn = DBConnection.connect();
            loadRooms();
            loadCustomers();
            fetchRoomData();
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            headerDate.setText(today.format(formatter));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Map<Integer, String> roomsMap;
    private Map<Integer,String> customersMap;
    
    public static Map<Integer, String> getRoomsMap(Connection conn) {
        Map<Integer, String> rooms = new HashMap<>();
        String query = "SELECT id, name FROM room";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                rooms.put(id, name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }
    
    public static Map<Integer,String> getCustomerMap(Connection conn){
        Map<Integer,String> customers = new HashMap<>();
        String query = "SELECT id, name FROM customer";
        try(PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                customers.put(id, name);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return customers;
    }
    
    
    
    private void loadRooms() {
        bRoom.removeAllItems();
        roomsMap = getRoomsMap(conn);
        for (String roomName : roomsMap.values()) {
            bRoom.addItem(roomName);
        }
    }
    
    private void loadCustomers(){
        bCustomer.removeAllItems();
        customersMap = getCustomerMap(conn);
        for (String customerName : customersMap.values()) {
            bCustomer.addItem(customerName);
        }
        
    }
    
    
    private void fetchRoomData() {
    DefaultTableModel model = (DefaultTableModel) bTable.getModel();
    model.setRowCount(0);

        try {
            String query = "SELECT b.id, r.name AS room_name, c.name AS customer_name, b.booking_date, b.duration, b.cost " +
                       "FROM booking b " +
                       "JOIN room r ON b.room = r.id " +
                       "JOIN customer c ON b.customer = c.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String roomName = rs.getString("room_name");
                String customerName = rs.getString("customer_name");
                String date = rs.getString("booking_date");
                int duration = rs.getInt("duration");
                int id       = rs.getInt("id");
                int cost    = rs.getInt("cost");

                model.addRow(new Object[]{roomName, customerName, date, duration,cost,id});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        bTable.getColumnModel().getColumn(5).setMinWidth(0);
        bTable.getColumnModel().getColumn(5).setMaxWidth(0);
        bTable.getColumnModel().getColumn(5).setWidth(0);
    }

    
    private void saveRoomData(boolean isEdit) {  
        
            String selectedRoomName = (String) bRoom.getSelectedItem();
            String selectedCustomerName = (String) bCustomer.getSelectedItem();
            java.util.Date utilDate = bDate.getDate();

            // Validate inputs
            if (selectedRoomName == null || selectedCustomerName == null || utilDate == null) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Missing Data", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int durationDays;
            int cost;
            
        PreparedStatement preparedStatement = null;
        try {
            String query;
            Integer selectedRoomId = null;
            Integer selectedCustomerId = null;
            
            durationDays = Integer.parseInt(bDurationDays.getText());
            cost = Integer.parseInt(bCost.getText());
            
            if (durationDays <= 0 || cost <= 0) {
                JOptionPane.showMessageDialog(this, "Duration and cost must be positive numbers!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            
            for (Map.Entry<Integer, String> entry : roomsMap.entrySet()) {
                if (entry.getValue().equals(selectedRoomName)) {
                    selectedRoomId = entry.getKey();
                    break;
                }
            }
        
            for (Map.Entry<Integer, String> entry : customersMap.entrySet()) {
                if (entry.getValue().equals(selectedCustomerName)) {
                    selectedCustomerId = entry.getKey();
                    break;
                }
            }
            
            if (selectedRoomId == null || selectedCustomerId == null) {
                JOptionPane.showMessageDialog(this, "Invalid room or customer selection!", "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            
            if (isEdit) {
                // Update existing booking
                query = "UPDATE booking SET room = ?, customer = ?, booking_date = ?, duration = ?, cost = ? WHERE id = ?";
            } else {
                // Insert new booking
                query = "INSERT INTO booking (room, customer, booking_date, duration, cost) VALUES (?, ?, ?, ?, ?)";
            }

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                
                pstmt.setInt(1, selectedRoomId);
                pstmt.setInt(2, selectedCustomerId);
                pstmt.setDate(3, sqlDate);
                pstmt.setInt(4, durationDays);
                pstmt.setDouble(5, cost);
                
                if (isEdit) {
                    
                    if (key == 0) {
                        JOptionPane.showMessageDialog(this, "No Customer selected for editing!");
                        return;
                    }
                    pstmt.setInt(6, key);
                }

                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Rows affected: " + rowsAffected);

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, isEdit ? "Customer updated successfully!" : "Customer added successfully!");
                    clearData();
                    
                    
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save booking!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
                fetchRoomData();
            }
            

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
    bAdd.setBackground(new Color(45, 103, 102));
    bAdd.setForeground(Color.WHITE);
    bEdit.setBackground(new Color(45, 103, 102));
    bEdit.setForeground(Color.WHITE);
    bDelete.setBackground(new Color(45, 103, 102));
    bDelete.setForeground(Color.WHITE);
    clearData.setBackground(new Color(45, 103, 102));
    clearData.setForeground(Color.WHITE);

    // Customize table
    customizeTableHeader();
    customizeTableRows();
}
    
   private void customizeTableHeader() {
    JTableHeader header = bTable.getTableHeader();
    header.setFont(new Font("Arial", Font.BOLD, 14));
    header.setBackground(new Color(45, 103, 102)); // Dark teal color
    header.setForeground(Color.WHITE); // White text color
    header.setDefaultRenderer(new DefaultTableCellHeaderRenderer());
    header.setPreferredSize(new Dimension(header.getWidth(), 40)); // Increase header height
}
    
  private void customizeTableRows() {
    bTable.setRowHeight(60); // Set row height
    bTable.setFont(new Font("Arial", Font.PLAIN, 14)); // Set default font for table rows
    bTable.setGridColor(new Color(200, 200, 200)); // Set grid color
    bTable.setShowGrid(true); // Show grid lines
    bTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        bAdd = new javax.swing.JButton();
        bEdit = new javax.swing.JButton();
        bDelete = new javax.swing.JButton();
        clearData = new javax.swing.JButton();
        bCustomer = new javax.swing.JComboBox<>();
        bCost = new javax.swing.JTextField();
        bDate = new com.toedter.calendar.JDateChooser();
        jLabel17 = new javax.swing.JLabel();
        bRoom = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        bDurationDays = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        bTable = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        headerDate = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
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

        customersMenu.setBackground(new java.awt.Color(45, 103, 102));
        customersMenu.setPreferredSize(new java.awt.Dimension(255, 44));
        customersMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                customersMenuMouseClicked(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setIcon(new javax.swing.ImageIcon("/home/shakib/Downloads/customers.png")); // NOI18N
        jLabel11.setText("Customers");

        javax.swing.GroupLayout customersMenuLayout = new javax.swing.GroupLayout(customersMenu);
        customersMenu.setLayout(customersMenuLayout);
        customersMenuLayout.setHorizontalGroup(
            customersMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customersMenuLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel11)
                .addContainerGap(78, Short.MAX_VALUE))
        );
        customersMenuLayout.setVerticalGroup(
            customersMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customersMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        bookingsMenu.setBackground(new java.awt.Color(36, 85, 86));
        bookingsMenu.setPreferredSize(new java.awt.Dimension(255, 44));
        bookingsMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bookingsMenuMouseClicked(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setIcon(new javax.swing.ImageIcon("/home/shakib/Downloads/booking_new.png")); // NOI18N
        jLabel12.setText("Bookings");

        javax.swing.GroupLayout bookingsMenuLayout = new javax.swing.GroupLayout(bookingsMenu);
        bookingsMenu.setLayout(bookingsMenuLayout);
        bookingsMenuLayout.setHorizontalGroup(
            bookingsMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bookingsMenuLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel12)
                .addContainerGap(96, Short.MAX_VALUE))
        );
        bookingsMenuLayout.setVerticalGroup(
            bookingsMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bookingsMenuLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
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

        bAdd.setText("Add");
        bAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddActionPerformed(evt);
            }
        });

        bEdit.setText("Edit");
        bEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bEditActionPerformed(evt);
            }
        });

        bDelete.setText("Delete");
        bDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDeleteActionPerformed(evt);
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
                        .addComponent(bAdd)
                        .addGap(18, 18, 18)
                        .addComponent(bEdit)
                        .addGap(18, 18, 18)
                        .addComponent(bDelete)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(clearData, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        bCustomer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        bCost.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
        bCost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCostActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 102, 102));
        jLabel17.setText("Duration In Days");

        bRoom.setFont(new java.awt.Font("Liberation Sans", 0, 15)); // NOI18N
        bRoom.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        bRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRoomActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 102, 102));
        jLabel6.setText("Cost");

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 102, 102));
        jLabel3.setText("Room");

        jLabel5.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("Booking Date");

        bDurationDays.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
        bDurationDays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDurationDaysActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 102, 102));
        jLabel16.setText("Customer");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5)
                                .addComponent(bDate, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(bCost, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bDurationDays, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel3)
                .addGap(12, 12, 12)
                .addComponent(bRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bDate, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addComponent(bDurationDays, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bCost, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
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
                .addGap(0, 13, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setPreferredSize(new java.awt.Dimension(681, 561));

        bTable.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        bTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"5", "55", "55", "55", null, null},
                {"555", "55", "55", "666", null, null},
                {"54656", "55", "55", "55", null, null},
                {null, "55", "55", "55", null, null}
            },
            new String [] {
                "Room", "Customer", "Date", "Duration", "Cost", "ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        bTable.setMinimumSize(new java.awt.Dimension(100, 240));
        bTable.setRowHeight(60);
        bTable.setSelectionBackground(new java.awt.Color(255, 255, 255));
        bTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        bTable.setShowGrid(false);
        bTable.setShowHorizontalLines(true);
        bTable.setSurrendersFocusOnKeystroke(true);
        bTable.setUpdateSelectionOnSort(false);
        bTable.setVerifyInputWhenFocusTarget(false);
        bTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(bTable);

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

        jLabel18.setFont(new java.awt.Font("Liberation Sans", 0, 22)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(153, 158, 168));
        jLabel18.setIcon(new javax.swing.ImageIcon("/home/shakib/Downloads/header_icon.png")); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 741, Short.MAX_VALUE)
                .addComponent(headerDate, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addGap(43, 43, 43))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(headerDate, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18))
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
        jLabel2.setText("Manage Booking");

        jLabel7.setFont(new java.awt.Font("Liberation Sans", 0, 22)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(153, 158, 168));
        jLabel7.setText("Create and manage room bookings.");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel2))
                .addContainerGap(948, Short.MAX_VALUE))
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

    private void bAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddActionPerformed
        try {

            saveRoomData(false);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to add booking: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_bAddActionPerformed

    private void bEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bEditActionPerformed
        saveRoomData(true);
    }//GEN-LAST:event_bEditActionPerformed

    private void bDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeleteActionPerformed
        if(key == 0 ){
            JOptionPane.showMessageDialog(this, "Please Select a Table row");
        }else{
            String deleteQuery = "DELETE FROM booking WHERE id = ?";
            try {
                PreparedStatement pstmt = conn.prepareStatement(deleteQuery);
                pstmt.setInt(1, key);

                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Room deleted successfully!");
                    fetchRoomData();
                    clearData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete room!");
                }

            } catch (SQLException ex) {
//                Logger.getLogger(Rooms.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_bDeleteActionPerformed

    private void clearDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearDataMouseClicked
        //        JOptionPane.showMessageDialog(this, "Data was clear from filed");
    }//GEN-LAST:event_clearDataMouseClicked

    private void clearDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearDataActionPerformed
        JOptionPane.showMessageDialog(this, "Data was clear from filed");
        clearData();
    }//GEN-LAST:event_clearDataActionPerformed

    private void bCostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCostActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bCostActionPerformed

    private void bRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRoomActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bRoomActionPerformed

    private void bDurationDaysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDurationDaysActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bDurationDaysActionPerformed

    private void bTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bTableMouseClicked
        DefaultTableModel model = (DefaultTableModel)bTable.getModel();
        int myIndex = bTable.getSelectedRow();

        key = (int) model.getValueAt(myIndex, 5);

        String roomName = (String) model.getValueAt(myIndex, 0);
        String customerName = (String) model.getValueAt(myIndex, 1);
        String bookingDate = model.getValueAt(myIndex, 2).toString();
        int durationDays = (int) model.getValueAt(myIndex, 3);
        int cost = (int) model.getValueAt(myIndex, 4);

        bRoom.setSelectedItem(roomName);
        bCustomer.setSelectedItem(customerName);
        bDate.setDate(java.sql.Date.valueOf(bookingDate));
        bDurationDays.setText(String.valueOf(durationDays));
        bCost.setText(String.valueOf(cost));
        bEdit.setText("Update");
    }//GEN-LAST:event_bTableMouseClicked

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
         bRoom.setSelectedIndex(-1);
        bCustomer.setSelectedIndex(-1);
        bDate.setDate(null);
        bDurationDays.setText("");
        bCost.setText("");
        key = 0;
        bEdit.setText("Edit");
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
            java.util.logging.Logger.getLogger(Bookings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Bookings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Bookings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Bookings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Bookings().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAdd;
    private javax.swing.JTextField bCost;
    private javax.swing.JComboBox<String> bCustomer;
    private com.toedter.calendar.JDateChooser bDate;
    private javax.swing.JButton bDelete;
    private javax.swing.JTextField bDurationDays;
    private javax.swing.JButton bEdit;
    private javax.swing.JComboBox<String> bRoom;
    private javax.swing.JTable bTable;
    private javax.swing.JPanel bookingsMenu;
    private javax.swing.JButton clearData;
    private javax.swing.JPanel customersMenu;
    private javax.swing.JPanel dashboardMenu;
    private javax.swing.JLabel headerDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
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
    private javax.swing.JLabel loginBtn;
    private javax.swing.JPanel roomsMenu;
    // End of variables declaration//GEN-END:variables
}
