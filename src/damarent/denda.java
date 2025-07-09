/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author sauki
 */
public class denda extends javax.swing.JFrame {
    koneksi dbsetting;
    String driver,database,user,pass;
    Object tabel;
    /**
     * Creates new form 
     */
    public denda() {
        initComponents();
        
        dbsetting = new koneksi();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");
        
        penggunaMap = new java.util.LinkedHashMap<>(); 
        loadPenggunaToComboBox();
        tabel_denda.setModel(tableMode1);
        
        settableload();
            
    }
    
    private javax.swing.table.DefaultTableModel tableMode1=getDefaultTabelModel();
    private javax.swing.table.DefaultTableModel getDefaultTabelModel()
    {
        return new javax.swing.table.DefaultTableModel
        (
            new Object[][] {},
            new String [] 
            {
                "ID Denda",
                "ID Sewa",
                "Nama Penyewa", 
                "Jenis Denda",
                "Detail Denda",
                "Jumlah Denda",
                "Keterangan Denda"
            }
        )
        
        {
            boolean[] canEdit = new boolean[]
            {
                false, false, false, false, false, false, false, false
            };
            
            public boolean isCellEditable(int rowIndex, int columnIndex) 
            {
                return canEdit[columnIndex];
            }
        };
    }
    
    private void settableload()
    {
        tableMode1.setRowCount(0); // Kosongkan tabel
        
        Connection kon = null;
        Statement stt = null;
        ResultSet res = null;
        
        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            stt = kon.createStatement();
            
            // === Kueri SQL yang Direvisi (menambahkan d.detail_denda) ===
            String SQL = "SELECT " +
                         "    d.id_denda, " +
                         "    d.id_sewa, " +
                         "    p.nama_pelanggan, " + // Dari tabel pengguna
                         "    d.jenis_denda, " +
                         "    d.detail_denda, " +   // Kolom DETAIL DENDA
                         "    d.jumlah_denda, " +
                         "    d.keterangan_denda " + // Keterangan Denda
                         "FROM " +
                         "    denda d " + // Alias tabel denda sebagai 'd'
                         "JOIN " +
                         "    sewa s ON d.id_sewa = s.id_sewa " + // Alias tabel sewa sebagai 's'
                         "JOIN " +
                         "    pelanggan p ON s.id_pelanggan = p.id_pelanggan " + // Alias tabel pengguna sebagai 'p'
                         "ORDER BY d.id_denda DESC";
            
            res = stt.executeQuery(SQL);
            
            while (res.next()) {
                // === SESUAIKAN UKURAN ARRAY DENGAN JUMLAH KOLOM DI SELECT (SEKARANG 7 KOLOM) ===
                Object[] rowData = new Object[7]; 
                rowData[0] = res.getInt("id_denda");
                rowData[1] = res.getInt("id_sewa");
                rowData[2] = res.getString("nama_pelanggan"); // Nama pelanggan
                rowData[3] = res.getString("jenis_denda");
                rowData[4] = res.getString("detail_denda");  // Ambil detail_denda
                rowData[5] = res.getDouble("jumlah_denda");
                rowData[6] = res.getString("keterangan_denda"); // Keterangan denda
                
                tableMode1.addRow(rowData);
            }
            
        }
        catch(Exception ex){
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null,
                        ex.getMessage(),"error",
                        JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    
    private void loadPenggunaToComboBox() {
        combo_penyewa.removeAllItems();
        penggunaMap.clear();

        javax.swing.DefaultComboBoxModel<String> model = new javax.swing.DefaultComboBoxModel<>();
        Connection kon = null;
        Statement stt = null;
        ResultSet res = null;

        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            stt = kon.createStatement();

            String SQL = "SELECT s.id_sewa, p.nama_pelanggan " + 
                         "FROM sewa s " + 
                         "JOIN pelanggan p ON s.id_pelanggan = p.id_pelanggan " +
                         "ORDER BY p.nama_pelanggan ASC";
            
            res = stt.executeQuery(SQL);

            String defaultItemText = "Pilih Penyewa"; 
            model.addElement(defaultItemText);
            penggunaMap.put(defaultItemText, 0); 

            while (res.next()) {
                int idSewa = res.getInt("id_sewa"); 
                String namaPelanggan = res.getString("nama_pelanggan");
                String displayString = idSewa + " - " + namaPelanggan; 
                model.addElement(displayString);
                penggunaMap.put(displayString, idSewa); 
            }

            combo_penyewa.setModel(model);

        } catch(Exception ex){
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null,
                        ex.getMessage(),"error",
                        JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    
     public void membersihkan_teks() {
        txt_keterangan.setText("");
        txt_detail_denda.setText(""); // Bersihkan field detail denda
        txt_jumlah_denda.setText(""); // Bersihkan txt_jumlah_denda
        
        if (combo_penyewa.getItemCount() > 0) combo_penyewa.setSelectedIndex(0);
        if (combo_jenis.getItemCount() > 0) combo_jenis.setSelectedIndex(0);
    }
    
    int row = -1; 
    public void tampil_field()
    {
        row = tabel_denda.getSelectedRow();
        if (row >= 0) {
            String idDenda = tableMode1.getValueAt(row, 0).toString();
            String idSewaDisplay = tableMode1.getValueAt(row, 1).toString();
            String namaPenyewa = tableMode1.getValueAt(row, 2).toString();
            String jenisDenda = tableMode1.getValueAt(row, 3).toString();
            String detailDenda = tableMode1.getValueAt(row, 4) != null ? tableMode1.getValueAt(row, 4).toString() : ""; // Ambil Detail Denda
            Double jumlahDenda = (Double) tableMode1.getValueAt(row, 5); // Ambil Jumlah Denda
            String keteranganDenda = tableMode1.getValueAt(row, 6) != null ? tableMode1.getValueAt(row, 6).toString() : ""; // Ambil Keterangan Denda

            // Pilih item yang cocok di combo_penyewa
            for (int i = 0; i < combo_penyewa.getItemCount(); i++) {
                String itemText = (String) combo_penyewa.getItemAt(i);
                // Kita mencari item yang dimulai dengan ID Sewa yang cocok
                if (itemText.startsWith(idSewaDisplay + " - ")) { 
                    combo_penyewa.setSelectedIndex(i);
                    break;
                }
            }

            combo_jenis.setSelectedItem(jenisDenda);
            txt_detail_denda.setText(detailDenda); // Isi field detail denda
            txt_jumlah_denda.setText(String.valueOf(jumlahDenda)); // Isi field jumlah denda
            txt_keterangan.setText(keteranganDenda); // Isi field keterangan denda

            // Atur status tombol
            btn_ubah.setEnabled(true);
            btn_hapus.setEnabled(true);
            btn_simpan.setEnabled(false);
            btn_tambah.setEnabled(true);
        } else {
            // Jika tidak ada baris terpilih, reset status tombol
            btn_ubah.setEnabled(false);
            btn_hapus.setEnabled(false);
            btn_simpan.setEnabled(true);
            btn_tambah.setEnabled(true);
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

        jSlider1 = new javax.swing.JSlider();
        combo_sort = new javax.swing.JComboBox();
        txt_detail_denda = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        btn_tampil_semua = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        combo_jenis = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_denda = new javax.swing.JTable();
        combo_penyewa = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btn_sort = new javax.swing.JButton();
        txt_cari = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        combo_kategori = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        btn_cari = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btn_tambah = new javax.swing.JButton();
        btn_ubah = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        btn_batal = new javax.swing.JButton();
        btn_simpan = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txt_keterangan = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        txt_jumlah_denda = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        combo_sort.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Termahal", "Termurah", " " }));
        combo_sort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_sortActionPerformed(evt);
            }
        });

        btn_tampil_semua.setText("Tampilkan Semua Data");
        btn_tampil_semua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tampil_semuaActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Jenis Denda");

        combo_jenis.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jenis Denda", "Terlambat", "Kerusakan" }));
        combo_jenis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_jenisActionPerformed(evt);
            }
        });

        tabel_denda.setModel(new javax.swing.table.DefaultTableModel(
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
        tabel_denda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_dendaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabel_denda);

        combo_penyewa.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Sort");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Cari");

        btn_sort.setText("Sort");
        btn_sort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sortActionPerformed(evt);
            }
        });

        txt_cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_cariActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Penyewa");

        combo_kategori.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nama", "Plat Nomor", "Status" }));
        combo_kategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_kategoriActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setText("Denda");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(691, 691, 691)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        btn_cari.setText("Cari");
        btn_cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cariActionPerformed(evt);
            }
        });

        btn_tambah.setText("Tambah");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

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

        btn_batal.setText("Batal");
        btn_batal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batalActionPerformed(evt);
            }
        });

        btn_simpan.setText("Simpan");
        btn_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpanActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Detail Denda");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Keterangan");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Jumlah Denda");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_ubah, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(475, 475, 475))
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addComponent(jSeparator4)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(combo_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_tampil_semua, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(combo_sort, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_sort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(11, 11, 11)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(44, 44, 44)
                        .addComponent(combo_penyewa, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(combo_jenis, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt_detail_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txt_jumlah_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(combo_penyewa, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(combo_jenis, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_detail_denda)
                        .addComponent(jLabel7)
                        .addComponent(txt_jumlah_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txt_keterangan)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(combo_sort, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(btn_sort, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_cari)
                        .addComponent(combo_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_tampil_semua, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_ubah, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {combo_jenis, combo_penyewa});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txt_detail_denda, txt_jumlah_denda});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void combo_sortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_sortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_sortActionPerformed

    private void btn_tampil_semuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampil_semuaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_tampil_semuaActionPerformed

    private void btn_sortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_sortActionPerformed

    }//GEN-LAST:event_btn_sortActionPerformed

    private void txt_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cariActionPerformed

    private void combo_kategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_kategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_kategoriActionPerformed

    private void btn_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cariActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        menu_utama utama = new menu_utama();
        utama.setVisible(true);
    }//GEN-LAST:event_formWindowClosed

    private void combo_jenisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_jenisActionPerformed
      
    }//GEN-LAST:event_combo_jenisActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_ubahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ubahActionPerformed
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data denda yang akan diubah dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idDendaToUpdate = tableMode1.getValueAt(row, 0).toString();

        String selectedPenyewaDisplay = (String) combo_penyewa.getSelectedItem();
        String jenisDenda = (String) combo_jenis.getSelectedItem();
        String detailDenda = txt_detail_denda.getText(); // Ambil dari field detail denda
        String keteranganDenda = txt_keterangan.getText(); // Ambil dari field keterangan denda

        if (selectedPenyewaDisplay == null || selectedPenyewaDisplay.equals("--- Pilih Sewa (ID - Nama Pelanggan - Plat) ---") ||
            jenisDenda == null || jenisDenda.equals("Jenis Denda") ||
            detailDenda.isEmpty() || // Validasi detail denda
            keteranganDenda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double jumlahDendaFinal = 0.0;
        try {
            jumlahDendaFinal = Double.parseDouble(txt_jumlah_denda.getText()); // Mengambil dari txt_jumlah_denda
            if (jumlahDendaFinal <= 0) {
                 JOptionPane.showMessageDialog(this, "Jumlah denda harus lebih dari 0.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
                 return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah denda tidak valid (bukan angka).", "Error Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer idSewa = penggunaMap.get(selectedPenyewaDisplay);
        if (idSewa == null || idSewa == 0) {
            JOptionPane.showMessageDialog(this, "Pilih Sewa yang valid dari daftar.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection kon = null;
        PreparedStatement pst = null; // Gunakan PreparedStatement untuk keamanan
        
        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            
            String SQL = "UPDATE denda SET "
                       + "id_sewa = ?, "
                       + "jenis_denda = ?, "
                       + "detail_denda = ?, " // Update kolom detail_denda
                       + "jumlah_denda = ?, " 
                       + "keterangan_denda = ? " 
                       + "WHERE id_denda = ?";

            pst = kon.prepareStatement(SQL);
            pst.setInt(1, idSewa);
            pst.setString(2, jenisDenda);
            pst.setString(3, detailDenda); // Set detail_denda
            pst.setDouble(4, jumlahDendaFinal);
            pst.setString(5, keteranganDenda);
            pst.setString(6, idDendaToUpdate); // Kondisi WHERE

            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data denda berhasil diubah!");

                // Perbarui data di tableMode1 secara manual
                tableMode1.setValueAt(idSewa, row, 1);
                tableMode1.setValueAt(selectedPenyewaDisplay.substring(selectedPenyewaDisplay.indexOf(" - ") + 3), row, 2); 
                tableMode1.setValueAt(jenisDenda, row, 3);
                tableMode1.setValueAt(detailDenda, row, 4); // Update kolom detail_denda di UI
                tableMode1.setValueAt(jumlahDendaFinal, row, 5);
                tableMode1.setValueAt(keteranganDenda, row, 6);

                membersihkan_teks(); 
                btn_ubah.setEnabled(false);
                btn_hapus.setEnabled(false);
                btn_simpan.setEnabled(true);
                btn_tambah.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah data denda. Data tidak ditemukan atau tidak ada perubahan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        }catch (Exception ex){
            System.err.println(ex.getMessage());
        }
        
    }//GEN-LAST:event_btn_ubahActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Pilih data denda yang akan dihapus dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        String idDendaToDelete = tableMode1.getValueAt(row, 0).toString(); 

        Connection kon = null;
        Statement stt = null;

        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database,user,pass);
            stt = kon.createStatement();
 
            String SQL = "DELETE FROM denda " + "WHERE id_denda = '" + idDendaToDelete + "'";

            stt.executeUpdate(SQL);
            tableMode1.removeRow(row);

            JOptionPane.showMessageDialog(null, "Data denda berhasil dihapus!");
            membersihkan_teks(); // Clear fields after successful deletion
  
            btn_ubah.setEnabled(false);
            btn_hapus.setEnabled(false);
            btn_simpan.setEnabled(true);
            btn_tambah.setEnabled(true);

        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_batalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_batalActionPerformed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        String selectedPenyewaDisplay = (String) combo_penyewa.getSelectedItem();
        String jenisDenda = (String) combo_jenis.getSelectedItem();
        String detailDenda = txt_detail_denda.getText(); // Ambil dari field detail denda
        String keteranganDenda = txt_keterangan.getText(); // Ambil dari field keterangan denda

        // Validasi input
        if (selectedPenyewaDisplay == null || selectedPenyewaDisplay.equals("--- Pilih Sewa (ID - Nama Pelanggan - Plat) ---") ||
            jenisDenda == null || jenisDenda.equals("Jenis Denda") ||
            detailDenda.isEmpty() || // Validasi detail denda
            keteranganDenda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double jumlahDendaFinal = 0.0;
        try {
            jumlahDendaFinal = Double.parseDouble(txt_jumlah_denda.getText()); // Mengambil dari txt_jumlah_denda
            if (jumlahDendaFinal <= 0) {
                 JOptionPane.showMessageDialog(this, "Jumlah denda harus lebih dari 0.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
                 return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah denda tidak valid (bukan angka).", "Error Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer idSewa = penggunaMap.get(selectedPenyewaDisplay);
        if (idSewa == null || idSewa == 0) {
            JOptionPane.showMessageDialog(this, "ID Sewa tidak valid. Silahkan pilih penyewa yang benar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection kon = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            
            // Perbaikan SQL: Insert ke kolom detail_denda
            String SQL = "INSERT INTO denda (id_sewa, jenis_denda, detail_denda, jumlah_denda, keterangan_denda) " +
                         "VALUES (?, ?, ?, ?, ?)";
            
            pst = kon.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, idSewa);
            pst.setString(2, jenisDenda);
            pst.setString(3, detailDenda); // Set detail_denda
            pst.setDouble(4, jumlahDendaFinal);
            pst.setString(5, keteranganDenda);

            pst.executeUpdate(); 
            rs = pst.getGeneratedKeys(); 

            String generatedIdDenda = null;
            if (rs.next()) {
                generatedIdDenda = rs.getString(1); 
            }

            JOptionPane.showMessageDialog(this, "Data denda berhasil disimpan!");

            // Perbarui tabel UI
            Object[] newRowData = new Object[7]; // Sesuaikan ukuran array
            newRowData[0] = generatedIdDenda;
            newRowData[1] = idSewa;
            newRowData[2] = selectedPenyewaDisplay.substring(selectedPenyewaDisplay.indexOf(" - ") + 3); // Ambil nama saja
            newRowData[3] = jenisDenda;
            newRowData[4] = detailDenda; // Tambahkan detail_denda ke UI table
            newRowData[5] = jumlahDendaFinal;
            newRowData[6] = keteranganDenda;
            
            tableMode1.addRow(newRowData); // Tambahkan baris baru ke JTable

            int lastRowIndex = tableMode1.getRowCount() - 1;
            if (lastRowIndex >= 0) {
                tabel_denda.setRowSelectionInterval(lastRowIndex, lastRowIndex);
                tabel_denda.scrollRectToVisible(tabel_denda.getCellRect(lastRowIndex, 0, true));
            }
            
            membersihkan_teks();
            btn_simpan.setEnabled(true);
            btn_ubah.setEnabled(false);
            btn_hapus.setEnabled(false);
            btn_batal.setEnabled(false);
            
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null,
                ex.getMessage(),"error",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void tabel_dendaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_dendaMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount()== 1)
        {
            tampil_field();
        }
    }//GEN-LAST:event_tabel_dendaMouseClicked

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
            java.util.logging.Logger.getLogger(denda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(denda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(denda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(denda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new denda().setVisible(true);
            }
        });
    }

    private java.util.Map<String, Integer> penggunaMap;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_batal;
    private javax.swing.JButton btn_cari;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton btn_sort;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_tampil_semua;
    private javax.swing.JButton btn_ubah;
    private javax.swing.JComboBox combo_jenis;
    private javax.swing.JComboBox combo_kategori;
    private javax.swing.JComboBox combo_penyewa;
    private javax.swing.JComboBox combo_sort;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTable tabel_denda;
    private javax.swing.JTextField txt_cari;
    private javax.swing.JTextField txt_detail_denda;
    private javax.swing.JTextField txt_jumlah_denda;
    private javax.swing.JTextField txt_keterangan;
    // End of variables declaration//GEN-END:variables
}
