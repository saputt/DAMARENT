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
import com.toedter.calendar.JDateChooser; 
import java.awt.*; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.Calendar;
import java.sql.Timestamp; 
import java.text.SimpleDateFormat; 

/**
 *
 * @author sauki
 */

public class sewa extends javax.swing.JFrame {
    koneksi dbsetting;
    String driver,database,user,pass;
    Object tabel;
    
    /**
     */
    public sewa() {
        initComponents();
        
        dbsetting = new koneksi();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");
        
        SpinnerModel hourModel = new SpinnerNumberModel(0, 0, 23, 1); // initial, min, max, step
        jam_diambil.setModel(hourModel);

        // Konfigurasi JSpinner untuk Menit (0-59)
        SpinnerModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1); // initial, min, max, step
        menit_diambil.setModel(minuteModel);

        // Konfigurasi JSpinner untuk Jumlah Hari (misal minimal 1 hari)
        SpinnerModel durasiHariModel = new SpinnerNumberModel(1, 1, 365, 1); // initial, min, max, step
        jumlah_hari.setModel(durasiHariModel);
        
        penggunaMap = new java.util.LinkedHashMap<>(); 
        loadPenggunaToComboBox();
        motorMap = new java.util.LinkedHashMap<>();
        loadMotorToComboBox();
        tabel_sewa.setModel(tableMode1); 
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
                "ID Sewa",
                "Nama Pelanggan", 
                "Merk Motor", 
                "Model Motor",   
                "Plat Nomor",   
                "Tanggal Peminjaman",
                "Tanggal Target Kembali",  
                "Durasi Sewa Hari",
                "Harga Sewa Awal",
                "Status Sewa",
            }
        )
        
        {
            boolean[] canEdit = new boolean[]
            {
                 false, false, false, false, false, false, false, false, false, false         
            };
            public boolean isCellEditable(int rowIndex, int columnIndex) 
            {
                return canEdit[columnIndex];
            }
        };
    }
    
    private void settableload()
    {
        tableMode1.setRowCount(0); 

        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();

            String SQL = "SELECT " +
                         "    id_sewa, " +
                         "    pelanggan.nama_pelanggan, " +
                         "    motor.merk, " +
                         "    motor.model, " +
                         "    motor.plat_nomor, " +
                         "    tanggal_peminjaman, " +
                         "    tanggal_kembali, " + 
                         "    durasi_sewa_hari, " +
                         "    harga_sewa_awal, " +
                         "    status_sewa " +
                         "FROM " +
                         "    sewa " +
                         "JOIN " +
                         "    pelanggan ON sewa.id_pelanggan = pelanggan.id_pelanggan " +
                         "JOIN " +
                         "    motor ON sewa.id_motor = motor.id_motor " +
                         "ORDER BY " +
                         "    sewa.tanggal_peminjaman DESC";

            ResultSet res = stt.executeQuery(SQL);

            while (res.next()) {
                Object[] data = new Object[10];
                data[0] = res.getString(1);
                data[1] = res.getString(2);
                data[2] = res.getString(3);
                data[3] = res.getString(4);
                data[4] = res.getString(5);
                data[5] = res.getTimestamp(6);
                data[6] = res.getTimestamp(7);
                data[7] = res.getInt(8);
                data[8] = res.getDouble(9);
                data[9] = res.getString(10);
                tableMode1.addRow(data);
            }

        } catch(Exception ex){
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null,
                        ex.getMessage(),"error",
                        JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    
    private void setInitialButtonStates() {
        btn_tambah.setEnabled(true);
        btn_simpan.setEnabled(false); // Simpan nonaktif sampai "Tambah" diklik
        btn_ubah.setEnabled(false);
        btn_hapus.setEnabled(false);
        btn_batal.setEnabled(false); // Batal nonaktif sampai ada operasi yang berjalan
    }
    
    // Metode untuk mengosongkan semua field input
    private void membersihkan_teks() {
        tanggal_diambil.setDate(new Date()); // Atur ke tanggal saat ini
        jam_diambil.setValue(0);
        menit_diambil.setValue(0);
        jumlah_hari.setValue(1); // Set durasi kembali ke 1 hari
        combo_penyewa.setSelectedIndex(0); // Pilih item default "Pilih Pelanggan"
        combo_motor.setSelectedIndex(0);   // Pilih item default "Pilih Motor"
        combo_status.setSelectedItem("Disewa"); // Set status default
        // Jika ada txt_id_sewa, kosongkan juga: txt_id_sewa.setText("");
    }
    
    // Metode untuk mengaktifkan field input
    private void aktifkan_teks() {
        tanggal_diambil.setEnabled(true);
        jam_diambil.setEnabled(true);
        menit_diambil.setEnabled(true);
        jumlah_hari.setEnabled(true);
        combo_penyewa.setEnabled(true);
        combo_motor.setEnabled(true);
        combo_status.setEnabled(true);
    }
    
    // Metode untuk menonaktifkan field input
    private void nonaktif_teks() {
        tanggal_diambil.setEnabled(false);
        jam_diambil.setEnabled(false);
        menit_diambil.setEnabled(false);
        jumlah_hari.setEnabled(false);
        combo_penyewa.setEnabled(false);
        combo_motor.setEnabled(false);
        combo_status.setEnabled(false);
    }
    
    private void loadPenggunaToComboBox() {
        combo_penyewa.removeAllItems();
        penggunaMap.clear();

        javax.swing.DefaultComboBoxModel<String> model = new javax.swing.DefaultComboBoxModel<>();

        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();

            String SQL = "SELECT id_pelanggan, nama_pelanggan FROM pelanggan ORDER BY nama_pelanggan ASC";
            ResultSet res = stt.executeQuery(SQL);

            String defaultItemText = "Pilih Pelanggan";
            model.addElement(defaultItemText);
            penggunaMap.put(defaultItemText, 0); 

            while (res.next()) {
                int id = res.getInt("id_pelanggan");
                String nama = res.getString("nama_pelanggan");
                String displayString = id + " - " + nama;
                model.addElement(displayString);
                penggunaMap.put(displayString, id);   
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
    
    private void loadMotorToComboBox() {
        combo_motor.removeAllItems();
        motorMap.clear();

        javax.swing.DefaultComboBoxModel<String> Model = new javax.swing.DefaultComboBoxModel<>();

        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();

            String SQL = "SELECT id_motor, merk, model, plat_nomor FROM motor ORDER BY merk, model ASC";
            ResultSet res = stt.executeQuery(SQL);

            String defaultItemText = "Pilih Motor";
            Model.addElement(defaultItemText);
            motorMap.put(defaultItemText, 0); 

            while (res.next()) {
                int id = res.getInt("id_motor");
                String merk = res.getString("merk");
                String modelMotor = res.getString("model");
                String platNomor = res.getString("plat_nomor"); 
             
                String displayString = merk + " - " + modelMotor + " (" + platNomor + ")";
           
                Model.addElement(displayString); 
                motorMap.put(displayString, id); 
            }

            combo_motor.setModel(Model); 

        }catch(Exception ex){
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null,
                        ex.getMessage(),"error",
                        JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    
    int row = -1;
    private void tampil_field() {
        row = tabel_sewa.getSelectedRow();
        if (row == -1) {
            return;
        }

        // Ambil data dari tabel model (indeks kolom disesuaikan)
        String idSewa = tableMode1.getValueAt(row, 0).toString();
        String namaPelanggan = tableMode1.getValueAt(row, 1).toString();
        String merkMotor = tableMode1.getValueAt(row, 2).toString();
        String modelMotor = tableMode1.getValueAt(row, 3).toString();
        String platNomor = tableMode1.getValueAt(row, 4).toString();
        Timestamp tanggalPeminjaman = (Timestamp) tableMode1.getValueAt(row, 5);
        Timestamp tanggalTargetKembali = (Timestamp) tableMode1.getValueAt(row, 6);
        int durasiSewaHari = (int) tableMode1.getValueAt(row, 7);
        // === BARIS INI TIDAK AKAN ERROR LAGI DENGAN getColumnClass() DI ATAS ===
        Double hargaSewaAwal = (Double) tableMode1.getValueAt(row, 8); 
        String statusSewa = tableMode1.getValueAt(row, 9).toString();

        // Mengisi JDateChooser
        if (tanggalPeminjaman != null) {
            tanggal_diambil.setDate(new Date(tanggalPeminjaman.getTime()));
            
            // Mengisi JSpinner jam dan menit
            Calendar cal = Calendar.getInstance();
            cal.setTime(tanggalPeminjaman);
            jam_diambil.setValue(cal.get(Calendar.HOUR_OF_DAY));
            menit_diambil.setValue(cal.get(Calendar.MINUTE));
        } else {
            tanggal_diambil.setDate(null);
            jam_diambil.setValue(0);
            menit_diambil.setValue(0);
        }

        // Mengisi JSpinner Durasi Hari
        jumlah_hari.setValue(durasiSewaHari);

        // Mengisi ComboBox Pelanggan
        boolean pelangganFound = false;
        for (java.util.Map.Entry<String, Integer> entry : penggunaMap.entrySet()) {
            if (entry.getKey().contains(namaPelanggan)) {
                combo_penyewa.setSelectedItem(entry.getKey());
                pelangganFound = true;
                break;
            }
        }
        if (!pelangganFound && combo_penyewa.getItemCount() > 0) {
            combo_penyewa.setSelectedIndex(0);
        }

        // Mengisi ComboBox Motor
        String displayMotor = merkMotor + " - " + modelMotor + " (" + platNomor + ")";
        boolean motorFound = false;
        // Coba cari di motorMap yang sudah dimuat (motor 'tersedia')
        if (motorMap.containsKey(displayMotor)) {
            combo_motor.setSelectedItem(displayMotor);
            motorFound = true;
        } else {
            // Jika tidak ditemukan di motor yang 'tersedia', coba cari di daftar lengkap motor
            // (termasuk yang 'disewa' atau 'perawatan') untuk tujuan menampilkan data yang sudah ada.
            try (Connection kon = DriverManager.getConnection(database, user, pass);
                 Statement stt = kon.createStatement();
                 ResultSet res = stt.executeQuery("SELECT id_motor, merk, model, plat_nomor FROM motor WHERE plat_nomor = '" + platNomor + "'")) {
                if (res.next()) {
                    String fullDisplayString = res.getString("merk") + " - " + res.getString("model") + " (" + res.getString("plat_nomor") + ")";
                    // Jika item ini belum ada di combobox, tambahkan sementara
                    if (!motorMap.containsKey(fullDisplayString)) {
                        ((DefaultComboBoxModel<String>) combo_motor.getModel()).addElement(fullDisplayString);
                        motorMap.put(fullDisplayString, res.getInt("id_motor"));
                    }
                    combo_motor.setSelectedItem(fullDisplayString);
                    motorFound = true;
                }
            } catch (SQLException e) {
                System.err.println("Error memuat motor spesifik: " + e.getMessage());
            }
        }
        
        if (!motorFound && combo_motor.getItemCount() > 0) {
             JOptionPane.showMessageDialog(this, "Motor dengan plat nomor " + platNomor + " mungkin tidak lagi 'tersedia' atau tidak ditemukan dalam daftar pilihan. Pastikan motor tersedia jika ingin mengubah ke motor lain.", "Info", JOptionPane.INFORMATION_MESSAGE);
             combo_motor.setSelectedIndex(0);
        } else if (!motorFound) { // Jika combobox kosong
             JOptionPane.showMessageDialog(this, "Tidak ada motor yang tersedia atau motor ini tidak ditemukan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }


        // Mengisi ComboBox Status Sewa
        if (statusSewa.equalsIgnoreCase("aktif")) {
            combo_status.setSelectedItem("Disewa");
        } else if (statusSewa.equalsIgnoreCase("selesai")) {
            combo_status.setSelectedItem("Selesai");
        } else {
            combo_status.setSelectedItem(statusSewa);
        }
        
        aktifkan_teks();
        btn_ubah.setEnabled(true);
        btn_hapus.setEnabled(true);
        btn_simpan.setEnabled(false);
        btn_batal.setEnabled(true);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_sewa = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JButton();
        combo_status = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        btn_simpan = new javax.swing.JButton();
        btn_ubah = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        btn_batal = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txt_cari = new javax.swing.JTextField();
        combo_kategori = new javax.swing.JComboBox();
        btn_cari = new javax.swing.JButton();
        btn_tampil_semua = new javax.swing.JButton();
        tanggal_diambil = new com.toedter.calendar.JDateChooser();
        combo_penyewa = new javax.swing.JComboBox();
        combo_motor = new javax.swing.JComboBox();
        jam_diambil = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        menit_diambil = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        jumlah_hari = new javax.swing.JSpinner();
        combo_sort = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        btn_sort = new javax.swing.JButton();

        jLabel7.setText("jLabel7");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

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
        tabel_sewa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_sewaMouseClicked(evt);
            }
        });
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
                .addGap(659, 659, 659)
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

        btn_tambah.setText("Tambah");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        combo_status.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "aktif", "Selesai" }));
        combo_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_statusActionPerformed(evt);
            }
        });

        btn_simpan.setText("Simpan");
        btn_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpanActionPerformed(evt);
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

        combo_penyewa.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        combo_penyewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_penyewaActionPerformed(evt);
            }
        });

        combo_motor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Tanggal");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Jam");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Menit");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Jumlah Hari");

        combo_sort.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Termahal", "Termurah", " " }));
        combo_sort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_sortActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Sort");

        btn_sort.setText("Sort");
        btn_sort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sortActionPerformed(evt);
            }
        });

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
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(combo_penyewa, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combo_motor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(248, 248, 248)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tanggal_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jam_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(menit_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(combo_status, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jumlah_hari, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combo_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_tampil_semua, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(317, 317, 317)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(combo_sort, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_sort, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(547, 547, 547)
                .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_ubah, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {combo_motor, combo_penyewa});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(menit_diambil)
                                .addComponent(jam_diambil))
                            .addComponent(tanggal_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(combo_penyewa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(combo_status, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(combo_motor)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jumlah_hari, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9)))))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cari)
                    .addComponent(btn_tampil_semua, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combo_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combo_sort, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(btn_sort, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 583, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_ubah, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_cari, btn_sort, btn_tampil_semua, combo_kategori, combo_sort, jLabel12, jLabel8, txt_cari});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {combo_motor, combo_penyewa, combo_status, jLabel2, jLabel3, jLabel6});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jam_diambil, menit_diambil, tanggal_diambil});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel9, jumlah_hari});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, jLabel4, jLabel5});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void combo_kategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_kategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_kategoriActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        // TODO add your handling code here:
        membersihkan_teks();
        aktifkan_teks();
        btn_simpan.setEnabled(true);
        btn_ubah.setEnabled(false);
        btn_hapus.setEnabled(false);
        btn_batal.setEnabled(true);
        tabel_sewa.clearSelection();
        row = -1; // Reset row selection
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void combo_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_statusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_statusActionPerformed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        Date tanggalPeminjamanDate = tanggal_diambil.getDate();

        if (tanggalPeminjamanDate == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih tanggal peminjaman.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int jam = (Integer) jam_diambil.getValue();
        int menit = (Integer) menit_diambil.getValue();
        int jumlahHari = (Integer) jumlah_hari.getValue();

        Calendar calPeminjaman = Calendar.getInstance();
        calPeminjaman.setTime(tanggalPeminjamanDate);
        calPeminjaman.set(Calendar.HOUR_OF_DAY, jam);
        calPeminjaman.set(Calendar.MINUTE, menit);
        calPeminjaman.set(Calendar.SECOND, 0);
        calPeminjaman.set(Calendar.MILLISECOND, 0);
        java.sql.Timestamp tanggalPeminjamanTimestamp = new java.sql.Timestamp(calPeminjaman.getTimeInMillis());

        Calendar calTargetKembali = (Calendar) calPeminjaman.clone();
        calTargetKembali.add(Calendar.DAY_OF_YEAR, jumlahHari);
        java.sql.Timestamp tanggalTargetKembaliTimestamp = new java.sql.Timestamp(calTargetKembali.getTimeInMillis());

        String selectedPelangganDisplay = (String) combo_penyewa.getSelectedItem();
        if (selectedPelangganDisplay == null || selectedPelangganDisplay.equals("Pilih Pelanggan")) {
            JOptionPane.showMessageDialog(this, "Silakan pilih pelanggan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idPelanggan = penggunaMap.get(selectedPelangganDisplay);

        String selectedMotorDisplay = (String) combo_motor.getSelectedItem();
        if (selectedMotorDisplay == null || selectedMotorDisplay.equals("Pilih Motor")) {
            JOptionPane.showMessageDialog(this, "Silakan pilih motor.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idMotor = motorMap.get(selectedMotorDisplay);

        String statusSewaCombo = (String) combo_status.getSelectedItem();
        String statusSewaDB;
        if (statusSewaCombo.equalsIgnoreCase("Disewa")) {
            statusSewaDB = "aktif";
        } else if (statusSewaCombo.equalsIgnoreCase("Selesai")) {
            statusSewaDB = "selesai";
        } else {
            statusSewaDB = statusSewaCombo; // Fallback jika ada status lain
        }

        double hargaSewaPerHari = 0.0;
        Connection kon = null;
        Statement stt = null;
        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            stt = kon.createStatement();
            // === PERBAIKAN NAMA KOLOM HARGA SEWA ===
            String sqlHargaMotor = "SELECT harga_sewa FROM motor WHERE id_motor = " + idMotor;
            ResultSet rsHarga = stt.executeQuery(sqlHargaMotor);
            if (rsHarga.next()) {
                hargaSewaPerHari = rsHarga.getDouble("harga_sewa");
            }
            rsHarga.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error mengambil harga sewa motor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            try { if (stt != null) stt.close(); } catch (SQLException e) { /* ignore */ }
            try { if (kon != null) kon.close(); } catch (SQLException e) { /* ignore */ }
        }
        double biayaSewaAwal = hargaSewaPerHari * jumlahHari;

        // === PERBAIKAN SYNTAX SQL INSERT ===
        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            String SQL_INSERT = "INSERT INTO sewa (id_pelanggan, id_motor, tanggal_peminjaman, tanggal_kembali, durasi_sewa_hari, harga_sewa_awal, status_sewa) VALUES (?, ?, ?, ?, ?, ?, ?)";
            java.sql.PreparedStatement pst = kon.prepareStatement(SQL_INSERT);

            pst.setInt(1, idPelanggan);
            pst.setInt(2, idMotor);
            pst.setTimestamp(3, tanggalPeminjamanTimestamp);
            pst.setTimestamp(4, tanggalTargetKembaliTimestamp);
            pst.setInt(5, jumlahHari);
            pst.setDouble(6, biayaSewaAwal);
            pst.setString(7, statusSewaCombo);

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data sewa berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                settableload();
                membersihkan_teks(); // Bersihkan form
                nonaktif_teks(); // Nonaktifkan field
                setInitialButtonStates(); // Atur ulang status tombol
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data sewa.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            pst.close();
            kon.close();
        } catch (SQLException ex) {
            System.err.println("SQL Error di btn_simpanActionPerformed(): " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            System.err.println("Driver not found di btn_simpanActionPerformed(): " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Driver database tidak ditemukan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_ubahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ubahActionPerformed
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data sewa yang akan diubah dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Ambil ID Sewa dari baris yang dipilih di tabel
        String idSewaToUpdate = tableMode1.getValueAt(row, 0).toString();

        // 2. Ambil data terbaru dari komponen input form
        Date tanggalPeminjamanDate = tanggal_diambil.getDate();
        if (tanggalPeminjamanDate == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih tanggal peminjaman.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int jam = (Integer) jam_diambil.getValue();
        int menit = (Integer) menit_diambil.getValue();
        int jumlahHari = (Integer) jumlah_hari.getValue();

        Calendar calPeminjaman = Calendar.getInstance();
        calPeminjaman.setTime(tanggalPeminjamanDate);
        calPeminjaman.set(Calendar.HOUR_OF_DAY, jam);
        calPeminjaman.set(Calendar.MINUTE, menit);
        calPeminjaman.set(Calendar.SECOND, 0);
        calPeminjaman.set(Calendar.MILLISECOND, 0);
        java.sql.Timestamp tanggalPeminjamanTimestamp = new java.sql.Timestamp(calPeminjaman.getTimeInMillis());

        Calendar calTargetKembali = (Calendar) calPeminjaman.clone();
        calTargetKembali.add(Calendar.DAY_OF_YEAR, jumlahHari);
        java.sql.Timestamp tanggalTargetKembaliTimestamp = new java.sql.Timestamp(calTargetKembali.getTimeInMillis());

        String selectedPelangganDisplay = (String) combo_penyewa.getSelectedItem();
        if (selectedPelangganDisplay == null || selectedPelangganDisplay.equals("Pilih Pelanggan")) {
            JOptionPane.showMessageDialog(this, "Silakan pilih pelanggan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idPelanggan = penggunaMap.get(selectedPelangganDisplay);

        String selectedMotorDisplay = (String) combo_motor.getSelectedItem();
        if (selectedMotorDisplay == null || selectedMotorDisplay.equals("Pilih Motor")) {
            JOptionPane.showMessageDialog(this, "Silakan pilih motor.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idMotor = motorMap.get(selectedMotorDisplay);

        String statusSewaCombo = (String) combo_status.getSelectedItem();

        // Ambil harga sewa per hari dari database untuk menghitung ulang biaya_sewa_awal
        double hargaSewaPerHari = 0.0;
        Connection konTemp = null;
        Statement sttTemp = null;
        try {
            Class.forName(driver);
            konTemp = DriverManager.getConnection(database, user, pass);
            sttTemp = konTemp.createStatement();
            // === PERBAIKAN NAMA KOLOM HARGA SEWA ===
            String sqlHargaMotor = "SELECT harga_sewa FROM motor WHERE id_motor = " + idMotor;
            ResultSet rsHarga = sttTemp.executeQuery(sqlHargaMotor);
            if (rsHarga.next()) {
                hargaSewaPerHari = rsHarga.getDouble("harga_sewa");
            }
            rsHarga.close();
        }catch (Exception ex){
            System.err.println(ex.getMessage());
        }
        double biayaSewaAwal = hargaSewaPerHari * jumlahHari;

        java.sql.PreparedStatement pst = null;
        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);

            // === PERBAIKAN SQL UPDATE QUERY ===
            String SQL_UPDATE = "UPDATE sewa SET " +
                                "`id_pelanggan`=?, " +
                                "`id_motor`=?, " +
                                "`tanggal_peminjaman`=?, " +
                                "`tanggal_kembali`=?, " + // Nama kolom yang benar
                                "`durasi_sewa_hari`=?, " +
                                "`harga_sewa_awal`=?, " +              // Kolom baru
                                "`status_sewa`=? " +
                                "WHERE `id_sewa`=?";

            pst = kon.prepareStatement(SQL_UPDATE);
            pst.setInt(1, idPelanggan);
            pst.setInt(2, idMotor);
            pst.setTimestamp(3, tanggalPeminjamanTimestamp);
            pst.setTimestamp(4, tanggalTargetKembaliTimestamp);
            pst.setInt(5, jumlahHari);
            pst.setDouble(6, biayaSewaAwal);
            pst.setString(7, statusSewaCombo);
            pst.setString(8, idSewaToUpdate);

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data sewa berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                settableload(); // Refresh tabel dengan data terbaru
                membersihkan_teks(); // Bersihkan form
                nonaktif_teks(); // Nonaktifkan field
                setInitialButtonStates(); // Atur ulang status tombol
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah data sewa. ID Sewa tidak ditemukan atau tidak ada perubahan.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex){
            System.err.println(ex.getMessage());
        }
    }//GEN-LAST:event_btn_ubahActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Pilih data sewa yang akan dihapus dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil ID Sewa dari baris yang dipilih
        String idSewaToDelete = tableMode1.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data sewa ini? \n(Semua denda terkait juga akan dihapus).", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Class.forName(driver);
                Connection kon = DriverManager.getConnection(database, user, pass);
                // Menggunakan transaksi untuk memastikan kedua operasi (hapus denda & hapus sewa) berhasil atau gagal bersamaan
                kon.setAutoCommit(false); // Nonaktifkan auto-commit

                Statement stt = kon.createStatement();

                // 1. Hapus denda terkait terlebih dahulu
                String SQL_DELETE_DENDA = "DELETE FROM denda WHERE id_sewa = '" + idSewaToDelete + "'";
                stt.executeUpdate(SQL_DELETE_DENDA);
                // System.out.println("Denda terkait sewa ID " + idSewaToDelete + " berhasil dihapus."); // Untuk debugging

                // 2. Kemudian, hapus transaksi sewa
                String SQL_DELETE_SEWA = "DELETE FROM sewa WHERE id_sewa = '" + idSewaToDelete + "'";
                int rowsAffected = stt.executeUpdate(SQL_DELETE_SEWA);

                if (rowsAffected > 0) {
                    kon.commit(); // Commit transaksi jika kedua operasi berhasil
                    JOptionPane.showMessageDialog(null, "Data sewa dan denda terkait berhasil dihapus!");
                    settableload(); // Refresh tabel
                    membersihkan_teks();
                    nonaktif_teks();
                    setInitialButtonStates();
                } else {
                    kon.rollback(); // Rollback jika ada masalah pada operasi sewa
                    JOptionPane.showMessageDialog(null, "Gagal menghapus data sewa. ID Sewa tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex){
                System.err.println(ex.getMessage());
            }
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_batalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_batalActionPerformed

    private void btn_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cariActionPerformed

    private void btn_tampil_semuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampil_semuaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_tampil_semuaActionPerformed

    private void txt_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cariActionPerformed

    private void combo_penyewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_penyewaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_penyewaActionPerformed

    private void combo_sortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_sortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_sortActionPerformed

    private void btn_sortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_sortActionPerformed
        // TODO add your handling code here:
        String sort = (String) combo_sort.getSelectedItem();
        tableMode1.setRowCount(0);
        String sortOrder = "";
        if (sort=="Termurah") {
            sortOrder = "ASC";
        } else if (sort=="Termahal") {
            sortOrder = "DESC";
        }
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(
                database,
                user,
                pass
            );
            Statement stt=kon.createStatement();

            String SQL = "SELECT " +
                         "    id_sewa, " +
                         "    pelanggan.nama_pelanggan, " +
                         "    motor.merk, " +
                         "    motor.model, " +
                         "    motor.plat_nomor, " +
                         "    tanggal_peminjaman, " +
                         "    tanggal_kembali, " + 
                         "    durasi_sewa_hari, " +
                         "    status_sewa, " +
                         "FROM " +
                         "    sewa " +
                         "JOIN " +
                         "    pelanggan ON sewa.id_pelanggan = pelanggan.id_pelanggan " +
                         "JOIN " +
                         "    motor ON sewa.id_motor = motor.id_motor " +
                         "ORDER BY " +
                         "    sewa.total_keseluruhan " + sortOrder;

            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                Object[] data = new Object[9];
                data[0] = res.getString(1);
                data[1] = res.getString(2);
                data[2] = res.getString(3);
                data[3] = res.getString(4);
                data[4] = res.getString(5);
                data[5] = res.getString(6);
                data[6] = res.getString(7);
                data[7] = res.getString(8);
                data[8] = res.getString(9);

                tableMode1.addRow(data);
            }
            res.close();
            stt.close();
            kon.close();
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null,
                ex.getMessage(),"error",
                JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }//GEN-LAST:event_btn_sortActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        menu_utama utama = new menu_utama();
        utama.setVisible(true);
    }//GEN-LAST:event_formWindowClosed

    private void tabel_sewaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_sewaMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount()== 1)
        {
            tampil_field();
        }
    }//GEN-LAST:event_tabel_sewaMouseClicked

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
    private java.util.Map<String, Integer> penggunaMap;
    private java.util.Map<String, Integer> motorMap;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_batal;
    private javax.swing.JButton btn_cari;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton btn_sort;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_tampil_semua;
    private javax.swing.JButton btn_ubah;
    private javax.swing.JComboBox combo_kategori;
    private javax.swing.JComboBox combo_motor;
    private javax.swing.JComboBox combo_penyewa;
    private javax.swing.JComboBox combo_sort;
    private javax.swing.JComboBox combo_status;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSpinner jam_diambil;
    private javax.swing.JSpinner jumlah_hari;
    private javax.swing.JSpinner menit_diambil;
    private javax.swing.JTable tabel_sewa;
    private com.toedter.calendar.JDateChooser tanggal_diambil;
    private javax.swing.JTextField txt_cari;
    // End of variables declaration//GEN-END:variables
}
