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
import java.sql.PreparedStatement;
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
        
        SpinnerModel hourModel = new SpinnerNumberModel(0, 0, 23, 1); 
        jam_diambil.setModel(hourModel);

        SpinnerModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1); 
        menit_diambil.setModel(minuteModel);

        SpinnerModel durasiHariModel = new SpinnerNumberModel(1, 1, 365, 1); 
        jumlah_hari.setModel(durasiHariModel);
     
        aturWaktuSekarang();
        
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
                "ID Pelanggan",
                "Nama Pelanggan", 
                "Merk Motor", 
                "Model Motor",   
                "Plat Nomor",   
                "Tanggal Peminjaman",
                "Tanggal Target Kembali",  
                "Harga Sewa/Hari",
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

        try (Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement()) {

            String SQL = "SELECT " +
                         "    s.id_sewa, " +
                         "    p.id_pelanggan, " +
                         "    p.nama_pelanggan, " +
                         "    m.merk, " +
                         "    m.model, " +
                         "    m.plat_nomor, " +
                         "    s.tanggal_peminjaman, " +
                         "    s.tanggal_kembali, " +
                         "    m.harga_sewa, " +
                         "    s.harga_sewa_awal, " +
                         "    s.status_sewa " +
                         "FROM " +
                         "    sewa s " +
                         "JOIN " +
                         "    pelanggan p ON s.id_pelanggan = p.id_pelanggan " +
                         "JOIN " +
                         "    motor m ON s.id_motor = m.id_motor " +
                         "ORDER BY " +
                         "    s.tanggal_peminjaman DESC";

            ResultSet res = stt.executeQuery(SQL);

            while (res.next()) {
                Object[] data = new Object[11];

                data[0] = res.getInt("id_sewa");
                data[1] = res.getInt("id_pelanggan");
                data[2] = res.getString("nama_pelanggan");
                data[3] = res.getString("merk");
                data[4] = res.getString("model");
                data[5] = res.getString("plat_nomor");
                data[6] = res.getTimestamp("tanggal_peminjaman");
                data[7] = res.getTimestamp("tanggal_kembali");
                data[8] = res.getDouble("harga_sewa");
                data[9] = res.getDouble("harga_sewa_awal");
                data[10] = res.getString("status_sewa");

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
    
    private void membersihkan_teks() {
        tanggal_diambil.setDate(new Date()); 
        jam_diambil.setValue(0);
        menit_diambil.setValue(0);
        jumlah_hari.setValue(1); 
        combo_penyewa.setSelectedIndex(0); 
        combo_motor.setSelectedIndex(0);   
    }
    
    private void aktifkan_teks() {
        tanggal_diambil.setEnabled(true);
        jam_diambil.setEnabled(true);
        menit_diambil.setEnabled(true);
        jumlah_hari.setEnabled(true);
        combo_penyewa.setEnabled(true);
        combo_motor.setEnabled(true);
    }
    
    private void nonaktif_teks() {
        tanggal_diambil.setEnabled(false);
        jam_diambil.setEnabled(false);
        menit_diambil.setEnabled(false);
        jumlah_hari.setEnabled(false);
        combo_penyewa.setEnabled(false);
        combo_motor.setEnabled(false);
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

        try {
            String idSewa = tableMode1.getValueAt(row, 0).toString();
            String idPelanggan = tableMode1.getValueAt(row, 1).toString();
            String namaPelanggan = tableMode1.getValueAt(row, 2).toString();
            String merkMotor = tableMode1.getValueAt(row, 3).toString();
            String modelMotor = tableMode1.getValueAt(row, 4).toString();
            String platNomor = tableMode1.getValueAt(row, 5).toString();
            Timestamp tanggalPeminjaman = (Timestamp) tableMode1.getValueAt(row, 6);
            Timestamp tanggalTargetKembali = (Timestamp) tableMode1.getValueAt(row, 7);

            double hargaSewaHari = (Double) tableMode1.getValueAt(row, 8);

            double hargaSewaAwal = (Double) tableMode1.getValueAt(row, 9);

            String statusSewa = tableMode1.getValueAt(row, 10).toString();

            int durasiHari = 0;
            if (hargaSewaHari > 0) { 
                durasiHari = (int) Math.round(hargaSewaAwal / hargaSewaHari);
            }

            if (tanggalPeminjaman != null) {
                tanggal_diambil.setDate(new Date(tanggalPeminjaman.getTime()));

                Calendar cal = Calendar.getInstance();
                cal.setTime(tanggalPeminjaman);
                jam_diambil.setValue(cal.get(Calendar.HOUR_OF_DAY));
                menit_diambil.setValue(cal.get(Calendar.MINUTE));
            } else {
                tanggal_diambil.setDate(null);
                jam_diambil.setValue(0);
                menit_diambil.setValue(0);
            }

            jumlah_hari.setValue(durasiHari);

            String displayPelanggan = idPelanggan + " - " + namaPelanggan;
            if (((DefaultComboBoxModel)combo_penyewa.getModel()).getIndexOf(displayPelanggan) != -1) {
                combo_penyewa.setSelectedItem(displayPelanggan);
            } else if (combo_penyewa.getItemCount() > 0) {
                combo_penyewa.setSelectedIndex(0);
            }

            String displayMotor = merkMotor + " - " + modelMotor + " (" + platNomor + ")";
            if (((DefaultComboBoxModel)combo_motor.getModel()).getIndexOf(displayMotor) != -1) {
                combo_motor.setSelectedItem(displayMotor);
            } else if (combo_motor.getItemCount() > 0) {
                combo_motor.setSelectedIndex(0);
            }

            aktifkan_teks();
            btn_ubah.setEnabled(true);
            btn_hapus.setEnabled(true);
            btn_simpan.setEnabled(false);
            btn_batal.setEnabled(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan detail data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void aturWaktuSekarang() {
        Calendar kalender = Calendar.getInstance();

        tanggal_diambil.setDate(kalender.getTime());

        int jamSekarang = kalender.get(Calendar.HOUR_OF_DAY);
        jam_diambil.setValue(jamSekarang);

        int menitSekarang = kalender.get(Calendar.MINUTE);
        menit_diambil.setValue(menitSekarang);
    }
    
    private void sorting(){
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
                     "    s.id_sewa, " +
                     "    p.id_pelanggan, " +
                     "    p.nama_pelanggan, " +
                     "    m.merk, " +
                     "    m.model, " +
                     "    m.plat_nomor, " +
                     "    s.tanggal_peminjaman, " +
                     "    s.tanggal_kembali, " +
                     "    m.harga_sewa, " + 
                     "    s.harga_sewa_awal, " +
                     "    s.status_sewa " +
                     "FROM " +
                     "    sewa s " +
                     "JOIN " +
                     "    pelanggan p ON s.id_pelanggan = p.id_pelanggan " +
                     "JOIN " +
                     "    motor m ON s.id_motor = m.id_motor " +
                     "ORDER BY " +
                     "    s.tanggal_peminjaman " + sortOrder;
            
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                Object[] data = new Object[10];
                data[0] = res.getString(1);
                data[1] = res.getString(2);
                data[2] = res.getString(3);
                data[3] = res.getString(4);
                data[4] = res.getString(5);
                data[5] = res.getString(6);
                data[6] = res.getString(7);
                data[7] = res.getString(8);
                data[8] = res.getString(9);
                data[9] = res.getString(10);
                data[9] = res.getString(10);

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
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_sewa = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JButton();
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

        jLabel7.setText("jLabel7");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

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

        combo_kategori.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nama", "Merk Motor", "Model Motor", "Plat Nomor", "Status Sewa" }));
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

        combo_sort.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Termahal", "Termurah" }));
        combo_sort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_sortActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Sort");

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 379, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jumlah_hari, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(combo_sort, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
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
                                    .addComponent(combo_penyewa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(combo_motor)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(11, 11, 11)))
                        .addGap(18, 18, 18))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jumlah_hari, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cari)
                    .addComponent(btn_tampil_semua, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combo_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combo_sort, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
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

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_cari, btn_tampil_semua, combo_kategori, combo_sort, jLabel12, jLabel8, txt_cari});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {combo_motor, combo_penyewa, jLabel2, jLabel3});

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
        row = -1; 
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        Date tanggalPeminjamanDate = tanggal_diambil.getDate();

        aturWaktuSekarang(); 
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

        String statusSewaDB = "aktif";

        double hargaSewaPerHari = 0.0;
        try (Connection kon = DriverManager.getConnection(database, user, pass);
             Statement stt = kon.createStatement()) {
            String sqlHargaMotor = "SELECT harga_sewa FROM motor WHERE id_motor = " + idMotor;
            ResultSet rsHarga = stt.executeQuery(sqlHargaMotor);
            if (rsHarga.next()) {
                hargaSewaPerHari = rsHarga.getDouble("harga_sewa");
            }
        } catch (Exception ex) {
            System.err.println("Gagal mengambil harga motor: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Gagal mengambil harga motor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double biayaSewaAwal = hargaSewaPerHari * jumlahHari;

        try (Connection kon = DriverManager.getConnection(database, user, pass)) {
            String SQL_INSERT = "INSERT INTO sewa (id_pelanggan, id_motor, tanggal_peminjaman, tanggal_kembali, harga_sewa_awal, status_sewa) VALUES (?, ?, ?, ?, ?, ?)";

            try (java.sql.PreparedStatement pst = kon.prepareStatement(SQL_INSERT)) {
                pst.setInt(1, idPelanggan);
                pst.setInt(2, idMotor);
                pst.setTimestamp(3, tanggalPeminjamanTimestamp);
                pst.setTimestamp(4, tanggalTargetKembaliTimestamp);
                pst.setDouble(5, biayaSewaAwal);
                pst.setString(6, statusSewaDB);

                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Data sewa berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    settableload(); 
                    membersihkan_teks();
                    nonaktif_teks();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan data sewa.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            System.err.println("Error saat menyimpan data sewa: " + ex.getMessage());
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_ubahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ubahActionPerformed
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data sewa yang akan diubah dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idSewaToUpdate = tableMode1.getValueAt(row, 0).toString();

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


        double hargaSewaPerHari = 0.0;
        Connection konTemp = null;
        Statement sttTemp = null;
        try {
            Class.forName(driver);
            konTemp = DriverManager.getConnection(database, user, pass);
            sttTemp = konTemp.createStatement();
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
            String SQL_UPDATE = "UPDATE sewa SET " +
                                "`id_pelanggan`=?, " +
                                "`id_motor`=?, " +
                                "`tanggal_peminjaman`=?, " +
                                "`tanggal_kembali`=?, " +
                                "`harga_sewa_awal`=?, " +             
                                "`status_sewa`=? " +
                                "WHERE `id_sewa`=?";

            pst = kon.prepareStatement(SQL_UPDATE);
            pst.setInt(1, idPelanggan);
            pst.setInt(2, idMotor);
            pst.setTimestamp(3, tanggalPeminjamanTimestamp);
            pst.setTimestamp(4, tanggalTargetKembaliTimestamp);
            pst.setDouble(5, biayaSewaAwal);
            pst.setString(6, idSewaToUpdate);

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data sewa berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                settableload(); 
                membersihkan_teks(); 
                nonaktif_teks(); 
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

        String idSewaToDelete = tableMode1.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data sewa ini? \n(Semua denda terkait juga akan dihapus).", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Class.forName(driver);
                Connection kon = DriverManager.getConnection(database, user, pass);
                kon.setAutoCommit(false); 

                Statement stt = kon.createStatement();

                String SQL_DELETE_SEWA = "DELETE FROM sewa WHERE id_sewa = '" + idSewaToDelete + "'";
                int rowsAffected = stt.executeUpdate(SQL_DELETE_SEWA);

                if (rowsAffected > 0) {
                    kon.commit(); 
                    JOptionPane.showMessageDialog(null, "Data sewa dan denda terkait berhasil dihapus!");
                    settableload(); 
                    membersihkan_teks();
                    nonaktif_teks();
                } else {
                    kon.rollback(); 
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
        String keyword = txt_cari.getText();
        if (keyword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kotak pencarian tidak boleh kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txt_cari.requestFocus();
            return;
        }

        tableMode1.setRowCount(0);
        String kategori = (String) combo_kategori.getSelectedItem();
        String searchColumn;

        switch (kategori) {
            case "Nama":
                searchColumn = "pelanggan.nama_pelanggan";
                break;
            case "Merk Motor":
                searchColumn = "motor.merk";
                break;
            case "Model Motor":
                searchColumn = "motor.model";
                break;
            case "Plat Nomor":
                searchColumn = "motor.plat_nomor";
                break;
            case "Status Sewa":
                searchColumn = "sewa.status_sewa";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Kategori pencarian tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        String sql = "SELECT " +
                 "  sewa.id_sewa, pelanggan.nama_pelanggan, motor.merk, motor.model, motor.plat_nomor, " +
                 "  sewa.tanggal_peminjaman, sewa.tanggal_kembali, motor.harga_sewa, " +
                 "  sewa.harga_sewa_awal, sewa.status_sewa " +
                 "FROM sewa " +
                 "JOIN pelanggan ON sewa.id_pelanggan = pelanggan.id_pelanggan " +
                 "JOIN motor ON sewa.id_motor = motor.id_motor " +
                 "WHERE " + searchColumn + " LIKE ?";

        try (Connection kon = DriverManager.getConnection(database, user, pass);
             PreparedStatement pst = kon.prepareStatement(sql)) {

            pst.setString(1, "%" + keyword + "%"); 

            try (ResultSet res = pst.executeQuery()) {
                if (!res.isBeforeFirst()) { 
                    JOptionPane.showMessageDialog(this, "Data tidak ditemukan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    while (res.next()) {
                        Object[] rowData = {
                            res.getInt("id_sewa"),
                            res.getString("nama_pelanggan"),
                            res.getString("merk"),
                            res.getString("model"),
                            res.getString("plat_nomor"),
                            res.getTimestamp("tanggal_peminjaman"),
                            res.getTimestamp("tanggal_kembali"),
                            res.getDouble("harga_sewa"),
                            res.getDouble("harga_sewa_awal"),
                            res.getString("status_sewa")
                        };
                        tableMode1.addRow(rowData);
                    }
                }
            }

        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),"error",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }//GEN-LAST:event_btn_cariActionPerformed

    private void btn_tampil_semuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampil_semuaActionPerformed
        // TODO add your handling code here:
        settableload();
    }//GEN-LAST:event_btn_tampil_semuaActionPerformed

    private void txt_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cariActionPerformed

    private void combo_penyewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_penyewaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_penyewaActionPerformed

    private void combo_sortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_sortActionPerformed
        // TODO add your handling code here:
        sorting();
    }//GEN-LAST:event_combo_sortActionPerformed

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
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_tampil_semua;
    private javax.swing.JButton btn_ubah;
    private javax.swing.JComboBox combo_kategori;
    private javax.swing.JComboBox combo_motor;
    private javax.swing.JComboBox combo_penyewa;
    private javax.swing.JComboBox combo_sort;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
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
