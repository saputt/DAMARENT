/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package damarent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;

import javax.swing.*;
import com.toedter.calendar.JDateChooser; // Import JDateChooser
import java.awt.*; // Untuk Layout Manager, Font, Insets
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.Calendar;
import java.sql.Timestamp; // Penting untuk menyimpan DATETIME ke MySQL
import java.text.SimpleDateFormat; // Untuk memformat tampilan tanggal/waktu

/**
 *
 * @author sauki
 */
public class sewa extends javax.swing.JFrame {

    koneksi dbsetting;
    String driver,database,user,pass;
    Object tabel;
    
    /**
     * Creates new form frm_mhs
     */
    public sewa() {
        initComponents();
        
        dbsetting = new koneksi();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");

        tabel_sewa.setModel(tableMode1); // Set model untuk JTable sewa
        settableload(); // Memuat data ke tabel saat panel/frame dibuat
    }
    
    private javax.swing.table.DefaultTableModel tableMode1=getDefaultTabelModel();
    private javax.swing.table.DefaultTableModel getDefaultTabelModel()
    {
        return new javax.swing.table.DefaultTableModel
        (
            new Object[][] {},
            new String [] 
            {
                "ID Sewa",
                "Nama Pelanggan", 
                "Merk Motor", 
                "Model Motor",   
                "Plat Nomor",   
                "Tgl Peminjaman",
                "Tgl Kembali",  
                "Durasi Sewa (Jam)",
                "Status Sewa",
                "Total Denda",  
                "Total Keseluruhan"
            }
        )
        
        {
            boolean[] canEdit = new boolean[]
            {
                 false, false, false, false, false, false, false, false, false, false, false
            };
            
            public boolean isCellEditable(int rowIndex, int columnIndex) 
            {
                return canEdit[columnIndex];
            }
        };
    }
    
    String data[] = new String[7];
    private void settableload()
    {
        tableMode1.setRowCount(0); // Bersihkan baris yang ada di tabel

        Connection kon = null; // Deklarasi di luar try agar bisa diakses di finally
        Statement stt = null;
        ResultSet res = null;

        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            stt = kon.createStatement();

            // *** PERHATIAN: QUERY SQL INI ADALAH KUNCI ***
            // Ini adalah query JOIN yang sama seperti yang kita bahas sebelumnya
            String SQL = "SELECT " +
                         "    sewa.id_sewa, " +
                         "    pelanggan.nama_pelanggan, " +
                         "    motor.merk, " +
                         "    motor.model, " +
                         "    motor.plat_nomor, " +
                         "    sewa.tanggal_peminjaman, " +
                         "    sewa.tanggal_kembali_rencana, " + // Pastikan kolom ini ada di DB Anda
                         "    sewa.tanggal_kembali_aktual, " +  // Pastikan kolom ini ada di DB Anda
                         "    sewa.durasi_sewa_jam, " +
                         "    sewa.status_sewa, " +
                         "    COALESCE(SUM(ds.jumlah_denda), 0) AS total_denda_dihitung, " +
                         "    (motor.harga_sewa * s.durasi_sewa_jam / 24) + COALESCE(SUM(ds.jumlah_denda), 0) AS total_tagihan_akhir_dihitung " +
                         "FROM " +
                         "    sewa " +
                         "JOIN " +
                         "    pengguna ON sewa.id_pengguna = pengguna.id_pengguna " +
                         "JOIN " +
                         "    motor ON sewa.id_motor = motor.id_motor " +
                         "LEFT JOIN " +
                         "    denda_sewa ON s.id_sewa = denda_s.id_sewa " +
                         "GROUP BY " +
                         "    s.id_sewa, p.nama_pelanggan, m.merk, m.model, m.plat_nomor, " +
                         "    s.tanggal_peminjaman, s.tanggal_kembali_rencana, s.tanggal_kembali_aktual, " +
                         "    s.durasi_sewa_jam, s.status_sewa, m.harga_sewa " + // Tambahkan m.harga_sewa ke GROUP BY
                         "ORDER BY " +
                         "    s.tanggal_peminjaman DESC";

            res = stt.executeQuery(SQL);

            while (res.next()) {
                // Buat Object array untuk setiap baris
                Object[] rowData = new Object[12]; // Sesuaikan ukuran array dengan jumlah kolom yang Anda SELECT

                rowData[0] = res.getInt("id_sewa"); // Ambil sebagai INT, masukkan ke Object
                rowData[1] = res.getString("nama_pelanggan");
                rowData[2] = res.getString("merk");
                rowData[3] = res.getString("model");
                rowData[4] = res.getString("plat_nomor");
                rowData[5] = res.getTimestamp("tanggal_peminjaman"); // Gunakan Timestamp untuk DATETIME
                rowData[6] = res.getTimestamp("tanggal_kembali_rencana"); // Pastikan ini tidak null, atau handle
                rowData[7] = res.getTimestamp("tanggal_kembali_aktual"); // Pastikan ini tidak null, atau handle
                rowData[8] = res.getInt("durasi_sewa_jam");
                rowData[9] = res.getString("status_sewa");
                rowData[10] = res.getDouble("total_denda_dihitung");
                rowData[11] = res.getDouble("total_tagihan_akhir_dihitung");

                tableMode1.addRow(rowData); // Tambahkan baris data ke model tabel
            }

        } catch (SQLException ex) { // Tangkap SQLException secara spesifik
            System.err.println("SQL Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Terjadi kesalahan database: " + ex.getMessage(), "Error Database",
                    JOptionPane.ERROR_MESSAGE); // Gunakan ERROR_MESSAGE untuk kesalahan
            // System.exit(0); // Hindari exit program secara paksa, lebih baik tangani error
        } catch (ClassNotFoundException ex) { // Tangkap ClassNotFoundException jika driver tidak ditemukan
            System.err.println("Driver Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Driver database tidak ditemukan: " + ex.getMessage(), "Error Driver",
                    JOptionPane.ERROR_MESSAGE);
            // System.exit(0);
        } catch (Exception ex) { // Tangkap Exception umum lainnya
            System.err.println("General Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Terjadi kesalahan tak terduga: " + ex.getMessage(), "Error Umum",
                    JOptionPane.ERROR_MESSAGE);
            // System.exit(0);
        } finally {
            // Pastikan semua resource JDBC ditutup di blok finally
            try {
                if (res != null) res.close();
                if (stt != null) stt.close();
                if (kon != null) kon.close();
            } catch (SQLException ex) {
                System.err.println("Error closing resources: " + ex.getMessage());
            }
        }
    }
   
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_sewa = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        combo_status = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        btn_tambah1 = new javax.swing.JButton();
        btn_tambah2 = new javax.swing.JButton();
        btn_tambah3 = new javax.swing.JButton();
        btn_tambah4 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txt_cari = new javax.swing.JTextField();
        combo_kategori = new javax.swing.JComboBox();
        btn_cari = new javax.swing.JButton();
        btn_tampil_semua = new javax.swing.JButton();
        tanggal_diambil = new com.toedter.calendar.JDateChooser();
        jam_diambil = new javax.swing.JSpinner();
        menit_diambil = new javax.swing.JSpinner();
        tanggal_kembali = new com.toedter.calendar.JDateChooser();
        jam_kembali = new javax.swing.JSpinner();
        menit_kembali = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        combo_pelanggan = new javax.swing.JComboBox();
        combo_motor = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Status Sewa");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Pelanggan");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Motor");

        tabel_sewa.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tabel_sewa);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setText("Sewa Damarent");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(618, 618, 618)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Menit");

        btn_tambah.setText("Tambah");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Tanggal Kembali");

        combo_status.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ada" }));
        combo_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_statusActionPerformed(evt);
            }
        });

        btn_tambah1.setText("Simpan");
        btn_tambah1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah1ActionPerformed(evt);
            }
        });

        btn_tambah2.setText("Ubah");
        btn_tambah2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah2ActionPerformed(evt);
            }
        });

        btn_tambah3.setText("Hapus");
        btn_tambah3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah3ActionPerformed(evt);
            }
        });

        btn_tambah4.setText("Batal");
        btn_tambah4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah4ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Cari");

        txt_cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_cariActionPerformed(evt);
            }
        });

        combo_kategori.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nama", "Plat Nomor", "Status" }));
        combo_kategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_kategoriActionPerformed(evt);
            }
        });

        btn_cari.setText("Cari");
        btn_cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cariActionPerformed(evt);
            }
        });

        btn_tampil_semua.setText("Tampilkan Semua Data");
        btn_tampil_semua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tampil_semuaActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Tanggal Ambil");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("Jam");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Jam");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Menit");

        combo_pelanggan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        combo_motor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator2)
            .addComponent(jSeparator3)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(combo_pelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combo_motor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 163, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tanggal_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tanggal_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jam_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(16, 16, 16)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(menit_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(10, 10, 10)
                                .addComponent(jam_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(16, 16, 16)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(menit_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(142, 142, 142)
                        .addComponent(jLabel6)
                        .addGap(51, 51, 51)
                        .addComponent(combo_status, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81))))
            .addGroup(layout.createSequentialGroup()
                .addGap(385, 385, 385)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(combo_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_tampil_semua, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_tambah1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_tambah2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_tambah3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_tambah4, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(508, 508, 508))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jam_diambil, menit_diambil});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jam_kembali, menit_kembali});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {combo_motor, combo_pelanggan});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(combo_status, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jam_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel10))
                                        .addGap(15, 15, 15))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(menit_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel12))
                                        .addGap(13, 13, 13)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jam_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(menit_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel11)
                                        .addComponent(jLabel4))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(tanggal_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(tanggal_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(combo_pelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(combo_motor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))))))
                .addGap(19, 19, 19)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cari)
                    .addComponent(btn_tampil_semua, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combo_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_tambah1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_tambah2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_tambah3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_tambah4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_cari, btn_tampil_semua, combo_kategori, jLabel8, txt_cari});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {combo_motor, combo_pelanggan, combo_status, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jam_diambil, jam_kembali, menit_diambil, menit_kembali, tanggal_diambil});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void combo_kategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_kategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_kategoriActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void combo_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_statusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_statusActionPerformed

    private void btn_tambah1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_tambah1ActionPerformed

    private void btn_tambah2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_tambah2ActionPerformed

    private void btn_tambah3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_tambah3ActionPerformed

    private void btn_tambah4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_tambah4ActionPerformed

    private void btn_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cariActionPerformed

    private void btn_tampil_semuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampil_semuaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_tampil_semuaActionPerformed

    private void txt_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cariActionPerformed

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
            java.util.logging.Logger.getLogger(sewa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(sewa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(sewa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(sewa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new sewa().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cari;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_tambah1;
    private javax.swing.JButton btn_tambah2;
    private javax.swing.JButton btn_tambah3;
    private javax.swing.JButton btn_tambah4;
    private javax.swing.JButton btn_tampil_semua;
    private javax.swing.JComboBox combo_kategori;
    private javax.swing.JComboBox combo_motor;
    private javax.swing.JComboBox combo_pelanggan;
    private javax.swing.JComboBox combo_status;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSpinner jam_diambil;
    private javax.swing.JSpinner jam_kembali;
    private javax.swing.JSpinner menit_diambil;
    private javax.swing.JSpinner menit_kembali;
    private javax.swing.JTable tabel_sewa;
    private com.toedter.calendar.JDateChooser tanggal_diambil;
    private com.toedter.calendar.JDateChooser tanggal_kembali;
    private javax.swing.JTextField txt_cari;
    // End of variables declaration//GEN-END:variables
}
