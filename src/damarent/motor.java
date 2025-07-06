/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package damarent;
import java.sql.Connection;
import java.sql.DriverManager;
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
        try 
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(
                             database,
                             user,
                             pass);
            Statement stt=kon.createStatement();
            String SQL = "select * from motor";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                data[0] = res.getString("id_motor");
                data[1] = res.getString("merk");
                data[2] = res.getString("model");
                data[3] = res.getString("plat_nomor");
                data[4] = res.getString("harga_sewa");
                data[5] = res.getString("status");
                tableMode1.addRow(data);
            }
            res.close();
            stt.close();
            kon.close();
            
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "error", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    
    public void urutkanTabel() {
        tableMode1.setRowCount(0);
        String orderBy = combo_urutkan.getSelectedItem().toString().equals("Termurah") ? "ASC" : "DESC";
        try (Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            ResultSet res = stt.executeQuery("SELECT * FROM motor ORDER BY harga_sewa " + orderBy)) 
        {
            while (res.next()) 
            {
                data[0] = res.getString("id_motor");
                data[1] = res.getString("merk");
                data[2] = res.getString("model");
                data[3] = res.getString("plat_nomor");
                data[4] = res.getString("harga_sewa");
                data[5] = res.getString("status");

                tableMode1.addRow(data);
            }
        }   
        catch (Exception ex) 
        {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
}

    
    public void membersihkan_teks()
    {
        txt_merk.setText("");
        txt_model.setText("");
        txt_plat.setText("");
        txt_harga.setText("");
        combo_status.setSelectedItem(null);
    }
    public void aktif_teks()
    {
        txt_merk.setEnabled(true);
        txt_model.setEnabled(true);
        txt_plat.setEnabled(true);
        txt_harga.setEnabled(true);
        combo_status.setEnabled(true);
    }
    
    int row = 0;
    public void tampil_field()
    {
        row = tabel_motor.getSelectedRow();
        txt_merk.setText(tableMode1.getValueAt(row, 1).toString());
        txt_model.setText(tableMode1.getValueAt(row, 2).toString());
        txt_plat.setText(tableMode1.getValueAt(row, 3).toString());
        txt_harga.setText(tableMode1.getValueAt(row, 4).toString());
        combo_status.setSelectedItem(tableMode1.getValueAt(row, 5).toString());
        
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
        combo_status.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        combo_status = new javax.swing.JComboBox();
        btn_tambah = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txt_cari = new javax.swing.JTextField();
        combo_kategori = new javax.swing.JComboBox();
        btn_cari = new javax.swing.JButton();
        btn_tampil = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        combo_urutkan = new javax.swing.JComboBox();
        btn_simpan = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btn_ubah = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        btn_batal = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txt_model = new javax.swing.JTextField();
        txt_plat = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_harga = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_motor = new javax.swing.JTable();
        txt_merk = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Merk");

        combo_status.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tersedia", "Tidak" }));

        btn_tambah.setText("Tambah");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Model");

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(jLabel8)
                .addGap(54, 54, 54)
                .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(combo_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_tampil)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(combo_urutkan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(combo_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cari)
                    .addComponent(btn_tampil)
                    .addComponent(jLabel9)
                    .addComponent(combo_urutkan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 12, Short.MAX_VALUE))
        );

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

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Status");

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setText("Daftar Motor");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(370, 370, 370))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel1)
                .addContainerGap(37, Short.MAX_VALUE))
        );

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

        txt_merk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_merkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_model, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_merk, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txt_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(342, 342, 342))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txt_plat, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(72, 72, 72)
                        .addComponent(jLabel7)
                        .addGap(30, 30, 30)
                        .addComponent(combo_status, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51))))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(246, 246, 246)
                .addComponent(btn_tambah)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_simpan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_ubah)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_hapus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_batal)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txt_plat, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txt_merk, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(combo_status, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_harga, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_model, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel2)
                        .addGap(27, 27, 27)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan)
                    .addComponent(btn_ubah)
                    .addComponent(btn_hapus)
                    .addComponent(btn_batal)
                    .addComponent(btn_tambah))
                .addGap(40, 40, 40))
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
        if ((txt_merk.getText().isEmpty()) || (txt_plat.getText().isEmpty()))
        {
            JOptionPane.showMessageDialog(null,
                            "data tidak boleh kosong, silahkan dilengkapi");
            return;
        }

            try(Connection kon = DriverManager.getConnection(database, user, pass);
                Statement stt = kon.createStatement())
            {
                String SQL = String.format("INSERT INTO motor (merk, model, plat_nomor, harga_sewa, status) VALUES ('%s','%s','%s','%s','%s')",
                                    txt_merk.getText(), 
                                    txt_model.getText(), 
                                    txt_plat.getText(), 
                                    txt_harga.getText(), 
                                    combo_status.getSelectedItem().toString());
                stt.executeUpdate(SQL);
                
                data[1] = txt_merk.getText();
                data[2] = txt_model.getText();
                data[3] = txt_plat.getText();
                data[4] = txt_harga.getText();
                data[5] = combo_status.getSelectedItem().toString();
                tableMode1.insertRow(0, data);
                
                membersihkan_teks();
                nonaktif_teks();
                btn_simpan.setEnabled(false);
                
                JOptionPane.showMessageDialog(null,
                        "Data Berhasil Disimpan!");
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(null, ex.getMessage(),"error", JOptionPane.ERROR_MESSAGE);
            }
                                                  
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
        try (Connection kon = DriverManager.getConnection(database,user,pass);
            Statement stt = kon.createStatement())
        {
            String id = tableMode1.getValueAt(row, 0).toString();
            stt.executeUpdate("DELETE FROM motor WHERE id_motor='"+ id +"'");
            tableMode1.removeRow(row);
            
            membersihkan_teks();
            nonaktif_teks();
            btn_ubah.setEnabled(false);
            btn_hapus.setEnabled(false);
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage(),"error", JOptionPane.ERROR_MESSAGE);
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
        try (Connection kon = DriverManager.getConnection(database,user,pass);
            Statement stt = kon.createStatement())
        {
            String id = tableMode1.getValueAt(row, 0).toString();
            stt.executeUpdate(
                "UPDATE  motor SET "+
                "merk='"+txt_merk.getText()+"',"+
                "model='"+txt_model.getText()+"',"+
                "plat_nomor='"+txt_plat.getText()+"',"+
                "harga_sewa='"+txt_harga.getText()+"',"+
                "status='"+combo_status.getSelectedItem().toString()+"' "+
                "WHERE id_motor='"+id+"'");
       
        data[0] = id;
        data[1] = txt_merk.getText();
        data[2] = txt_model.getText();
        data[3] = txt_plat.getText();
        data[4] = txt_harga.getText();
        data[5] = combo_status.getSelectedItem().toString();
        tableMode1.removeRow(row);
        tableMode1.insertRow(row,data);
        
        membersihkan_teks();
        nonaktif_teks();
        btn_ubah.setEnabled(false);
        btn_hapus.setEnabled(false);
       
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
        String kolom = combo_kategori.getSelectedItem().toString().replace(" ", "_").toLowerCase();

        try (Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement()) 
        {
            String sql = "SELECT * FROM motor WHERE " + kolom + " LIKE '%" + kata + "%'";
            ResultSet res = stt.executeQuery(sql);

            while (res.next()) 
            {
                for (int i = 0; i < 6; i++) 
                {
                    data[i] = res.getString(i + 1);
                }
                tableMode1.addRow(data);
            }

        } 
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
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
    private javax.swing.JComboBox combo_status;
    private javax.swing.JComboBox combo_urutkan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabel_motor;
    private javax.swing.JTextField txt_cari;
    private javax.swing.JTextField txt_harga;
    private javax.swing.JTextField txt_merk;
    private javax.swing.JTextField txt_model;
    private javax.swing.JTextField txt_plat;
    // End of variables declaration//GEN-END:variables
}
