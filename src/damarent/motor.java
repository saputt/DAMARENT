/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package damarent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author hm867
 */
public class motor extends javax.swing.JFrame {
    
    koneksi dbsetting;
    String driver,database,user,pass;
    Object tabel;

    /**
     * Creates new form motor
     */
    public motor() {
        initComponents();
        
        dbsetting = new koneksi();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");
        


        tabel_motor.setModel(tableMode1);
        
        settableload(); 
        
        combo_urutkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { 
                urutkanTabel();
            }
        });
    }
    private javax.swing.JComboBox<String> combo_urut;
    private javax.swing.table.DefaultTableModel tableMode1=getDefaultTabelModel();
    private javax.swing.table.DefaultTableModel getDefaultTabelModel()
    {
        return new javax.swing.table.DefaultTableModel
        (
            new Object[][] {},
            new String [] 
            {
                
                "ID motor",
                "Merk",
                "Model",
                "Plat Nomor",
                "Harga Sewa",
                "Status"
            }
        )
        
        {
            boolean[] canEdit = new boolean[]
            {
                false, false, false, false, false, false
            };
            
            public boolean isCellEditable(int rowIndex, int columnIndex) 
            {
                return canEdit[columnIndex];
            }
        };
    }
    
    String data[] = new String[6];
    
    private void settableload()
    {
       tableMode1.setRowCount(0);

        String SQL = "SELECT " +
                 "   m.id_motor, " +
                 "   m.merk, " +
                 "   m.model, " +
                 "   m.plat_nomor, " +
                 "   m.harga_sewa, " +
                 "   CASE " +
                 "       WHEN MAX(s.id_sewa) IS NOT NULL THEN 'Tidak Tersedia' " + // <-- PERUBAHAN
                 "       ELSE 'Tersedia' " +
                 "   END AS status_sekarang " +
                 "FROM " +
                 "   motor m " +
                 "LEFT JOIN " +
                 "   sewa s ON m.id_motor = s.id_motor " +
                 "           AND s.status_sewa = 'aktif' " +
                 "           AND CURDATE() BETWEEN DATE(s.tanggal_peminjaman) AND DATE(s.tanggal_kembali) " +
                 "GROUP BY m.id_motor, m.merk, m.model, m.plat_nomor, m.harga_sewa";

        try (Connection kon = DriverManager.getConnection(database, user, pass);
             Statement stt = kon.createStatement();
             ResultSet res = stt.executeQuery(SQL)) {

            while (res.next()) {
                data[0] = res.getString("id_motor");
                data[1] = res.getString("merk");
                data[2] = res.getString("model");
                data[3] = res.getString("plat_nomor");
                data[4] = res.getString("harga_sewa");
                data[5] = res.getString("status_sekarang");
                tableMode1.addRow(data);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    public void urutkanTabel() {
        tableMode1.setRowCount(0);
        String orderBy = combo_urutkan.getSelectedItem().toString().equals("Termurah") ? "ASC" : "DESC";

         String SQL = "SELECT " +
                 "   m.id_motor, m.merk, m.model, m.plat_nomor, m.harga_sewa, " +
                 "   CASE WHEN MAX(s.id_sewa) IS NOT NULL THEN 'Tidak Tersedia' ELSE 'Tersedia' END AS status_sekarang " + // <-- PERUBAHAN
                 "FROM motor m " +
                 "LEFT JOIN sewa s ON m.id_motor = s.id_motor AND s.status_sewa = 'aktif' AND CURDATE() BETWEEN DATE(s.tanggal_peminjaman) AND DATE(s.tanggal_kembali) " +
                 "GROUP BY m.id_motor, m.merk, m.model, m.plat_nomor, m.harga_sewa " +
                 "ORDER BY m.harga_sewa " + orderBy;

        try (Connection kon = DriverManager.getConnection(database, user, pass);
             Statement stt = kon.createStatement();
             ResultSet res = stt.executeQuery(SQL)) {

            while (res.next()) {
                data[0] = res.getString("id_motor");
                data[1] = res.getString("merk");
                data[2] = res.getString("model");
                data[3] = res.getString("plat_nomor");
                data[4] = res.getString("harga_sewa");
                data[5] = res.getString("status_sekarang");
                tableMode1.addRow(data);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
}

    
    public void membersihkan_teks()
    {
        txt_merk.setText("");
        txt_model.setText("");
        txt_plat.setText("");
        txt_harga.setText("");
    }
    public void aktif_teks()
    {
        txt_merk.setEnabled(true);
        txt_model.setEnabled(true);
        txt_plat.setEnabled(true);
        txt_harga.setEnabled(true);
    }
    
    int row = 0;
    public void tampil_field()
    {
        row = tabel_motor.getSelectedRow();
        txt_merk.setText(tableMode1.getValueAt(row, 1).toString());
        txt_model.setText(tableMode1.getValueAt(row, 2).toString());
        txt_plat.setText(tableMode1.getValueAt(row, 3).toString());
        txt_harga.setText(tableMode1.getValueAt(row, 4).toString());
        
        btn_simpan.setEnabled(false);
        btn_ubah.setEnabled(true);
        btn_hapus.setEnabled(true);
        btn_batal.setEnabled(true);
        aktif_teks();   
    }
    
    public void nonaktif_teks()
    {
        txt_merk.setEnabled(false);
        txt_model.setEnabled(false);
        txt_plat.setEnabled(false);
        txt_harga.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel2 = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btn_simpan = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btn_ubah = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        btn_batal = new javax.swing.JButton();
        txt_model = new javax.swing.JTextField();
        txt_plat = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_harga = new javax.swing.JTextField();
        txt_merk = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_cari = new javax.swing.JTextField();
        combo_kategori = new javax.swing.JComboBox();
        btn_cari = new javax.swing.JButton();
        btn_tampil = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        combo_urutkan = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_motor = new javax.swing.JTable();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Merk");

        btn_tambah.setText("Tambah");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Model");

        btn_simpan.setText("Simpan");
        btn_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpanActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Plat Nomor");

        btn_ubah.setText("Ubah");
        btn_ubah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ubahActionPerformed(evt);
            }
        });

        btn_hapus.setText("Hapus");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Harga Sewa");

        btn_batal.setText("Batal");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD, jLabel1.getFont().getSize()+24));
        jLabel1.setText("Tabel Motor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 368, 9, 356);
        jPanel1.add(jLabel1, gridBagConstraints);

        txt_merk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_merkActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Cari");

        txt_cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_cariActionPerformed(evt);
            }
        });

        combo_kategori.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Merk", "Model", "Plat Nomor", "Status" }));

        btn_cari.setText("Cari");
        btn_cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cariActionPerformed(evt);
            }
        });

        btn_tampil.setText("Tampilkan Semua Data");
        btn_tampil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tampilActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Urutkan");

        combo_urutkan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Termurah", "Termahal" }));
        combo_urutkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_urutkanActionPerformed(evt);
            }
        });

        tabel_motor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabel_motor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_motorMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabel_motor);

        jScrollPane2.setViewportView(jScrollPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(224, 224, 224)
                                .addComponent(btn_tambah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_simpan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_ubah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_hapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_batal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(149, 149, 149))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(75, 75, 75)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_merk)
                                    .addComponent(txt_model))
                                .addGap(117, 117, 117)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_plat)
                                    .addComponent(txt_harga))
                                .addGap(17, 17, 17))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(65, 65, 65)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(26, 26, 26)
                                .addComponent(txt_cari)
                                .addGap(18, 18, 18)
                                .addComponent(combo_kategori, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_cari, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_tampil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(combo_urutkan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(78, 78, 78)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_plat, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_merk, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_model, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combo_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_tampil, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combo_urutkan, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_ubah, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        // TODO add your handling code here:
        membersihkan_teks();
        txt_merk.requestFocus();
        btn_simpan.setEnabled(true);
        btn_ubah.setEnabled(false);
        btn_hapus.setEnabled(false);
        aktif_teks();
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void txt_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cariActionPerformed

    private void txt_merkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_merkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_merkActionPerformed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        // TODO add your handling code here:
        String harga = txt_harga.getText();
        String merk = txt_merk.getText();
        
        if (txt_merk.getText().isEmpty() || txt_plat.getText().isEmpty() || txt_harga.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Merk, Plat Nomor, dan Harga tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!harga.matches("[0-9]+")) {
            JOptionPane.showMessageDialog(this, "harga hanya boleh berisi angka!", "Error Input", JOptionPane.ERROR_MESSAGE);
            txt_harga.requestFocus(); 
            txt_harga.setText("");
            return; 
        }
        if (merk.matches("[0-9]+")) {
            JOptionPane.showMessageDialog(this, "harga hanya boleh berisi huruf!", "Error Input", JOptionPane.ERROR_MESSAGE);
            txt_merk.requestFocus(); 
            txt_merk.setText("");
            return; 
        }
        String sql = "INSERT INTO motor (merk, model, plat_nomor, harga_sewa, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection kon = DriverManager.getConnection(database, user, pass);
             PreparedStatement pst = kon.prepareStatement(sql)) {

            pst.setString(1, txt_merk.getText());
            pst.setString(2, txt_model.getText());
            pst.setString(3, txt_plat.getText());
            pst.setDouble(4, Double.parseDouble(txt_harga.getText()));
            pst.setString(5, "Tersedia"); // Status otomatis "Tersedia"

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data Motor Berhasil Disimpan!");

            settableload(); // Muat ulang tabel untuk menampilkan data baru
            membersihkan_teks();
            nonaktif_teks();
            btn_simpan.setEnabled(false);
            btn_tambah.setEnabled(true);

        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage(),"error", JOptionPane.ERROR_MESSAGE);
        }
                                                  
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data motor yang akan dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data motor ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String idMotor = tableMode1.getValueAt(row, 0).toString();

            try (Connection kon = DriverManager.getConnection(database, user, pass)) {

                String sqlCheck = "SELECT COUNT(*) FROM sewa WHERE id_motor = ?";
                int jumlahSewa = 0;

                try (PreparedStatement pstCheck = kon.prepareStatement(sqlCheck)) {
                    pstCheck.setString(1, idMotor);
                    ResultSet rs = pstCheck.executeQuery();
                    if (rs.next()) {
                        jumlahSewa = rs.getInt(1);
                    }
                }

                if (jumlahSewa > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Gagal menghapus! Motor ini memiliki " + jumlahSewa + " riwayat transaksi sewa.",
                            "Error Foreign Key",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String sqlDelete = "DELETE FROM motor WHERE id_motor = ?";
                    try (PreparedStatement pstDelete = kon.prepareStatement(sqlDelete)) {
                        pstDelete.setString(1, idMotor);
                        int rowsAffected = pstDelete.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Data motor berhasil dihapus!");
                            tableMode1.removeRow(row);
                            membersihkan_teks();
                            nonaktif_teks();
                            btn_tambah.setEnabled(true);
                            btn_simpan.setEnabled(false);
                            btn_ubah.setEnabled(false);
                            btn_hapus.setEnabled(false);
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void tabel_motorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_motorMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount()== 1)
        {
            tampil_field();
        }
    }//GEN-LAST:event_tabel_motorMouseClicked

    private void btn_ubahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ubahActionPerformed
        // TODO add your handling code here:
       if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan diubah.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = tableMode1.getValueAt(row, 0).toString();
        String sql = "UPDATE motor SET merk=?, model=?, plat_nomor=?, harga_sewa=? WHERE id_motor=?";

        try (Connection kon = DriverManager.getConnection(database, user, pass);
             PreparedStatement pst = kon.prepareStatement(sql)) {

            pst.setString(1, txt_merk.getText());
            pst.setString(2, txt_model.getText());
            pst.setString(3, txt_plat.getText());
            pst.setDouble(4, Double.parseDouble(txt_harga.getText()));
            pst.setInt(5, Integer.parseInt(id));

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data Motor Berhasil Diubah!");

            settableload();
            membersihkan_teks();
            nonaktif_teks();
            btn_ubah.setEnabled(false);
            btn_hapus.setEnabled(false);
            btn_tambah.setEnabled(true);

        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage(),"error", JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_btn_ubahActionPerformed

    private void btn_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cariActionPerformed
        // TODO add your handling code here:
        tableMode1.setRowCount(0);
        String kata = txt_cari.getText();
        String kategori = combo_kategori.getSelectedItem().toString();
        String kolom;

        switch (kategori) {
            case "Merk":
                kolom = "m.merk";
                break;
            case "Model":
                kolom = "m.model";
                break;
            case "Plat Nomor":
                kolom = "m.plat_nomor";
                break;
            case "Status":
                kolom = "status_sekarang"; 
                break;
            default:
                return;
        }

        String baseSQL = "SELECT " +
                     "   m.id_motor, m.merk, m.model, m.plat_nomor, m.harga_sewa, " +
                     "   CASE WHEN MAX(s.id_sewa) IS NOT NULL THEN 'Tidak Tersedia' ELSE 'Tersedia' END AS status_sekarang " + // <-- PERUBAHAN
                     "FROM motor m " +
                     "LEFT JOIN sewa s ON m.id_motor = s.id_motor AND s.status_sewa = 'aktif' AND CURDATE() BETWEEN DATE(s.tanggal_peminjaman) AND DATE(s.tanggal_kembali) ";

        String finalSQL;
        if (kolom.equals("status_sekarang")) {
            finalSQL = baseSQL + "GROUP BY m.id_motor, m.merk, m.model, m.plat_nomor, m.harga_sewa HAVING status_sekarang LIKE ?";
        } else {
            finalSQL = baseSQL + "WHERE " + kolom + " LIKE ? GROUP BY m.id_motor, m.merk, m.model, m.plat_nomor, m.harga_sewa";
        }

        try (Connection kon = DriverManager.getConnection(database, user, pass);
            PreparedStatement pst = kon.prepareStatement(finalSQL)) {

            pst.setString(1, "%" + kata + "%");
            ResultSet res = pst.executeQuery();
            
            if (!res.isBeforeFirst()) { 
                JOptionPane.showMessageDialog(this, "Data motor tidak ditemukan untuk pencarian ini.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                settableload(); 
                txt_cari.setText(""); 
            } else {
                while (res.next()) {
                    data[0] = res.getString("id_motor");
                    data[1] = res.getString("merk");
                    data[2] = res.getString("model");
                    data[3] = res.getString("plat_nomor");
                    data[4] = res.getString("harga_sewa");
                    data[5] = res.getString("status_sekarang");
                    tableMode1.addRow(data);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btn_cariActionPerformed

    private void btn_tampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampilActionPerformed
        // TODO add your handling code here:
        tableMode1.setRowCount(0);
        settableload();
    }//GEN-LAST:event_btn_tampilActionPerformed

    private void combo_urutkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_urutkanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_urutkanActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:

    }//GEN-LAST:event_formWindowClosed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowOpened

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
            java.util.logging.Logger.getLogger(motor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(motor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(motor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(motor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new motor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_batal;
    private javax.swing.JButton btn_cari;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_tampil;
    private javax.swing.JButton btn_ubah;
    private javax.swing.JComboBox combo_kategori;
    private javax.swing.JComboBox combo_urutkan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable tabel_motor;
    private javax.swing.JTextField txt_cari;
    private javax.swing.JTextField txt_harga;
    private javax.swing.JTextField txt_merk;
    private javax.swing.JTextField txt_model;
    private javax.swing.JTextField txt_plat;
    // End of variables declaration//GEN-END:variables
}
