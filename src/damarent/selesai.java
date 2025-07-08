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
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import java.util.HashMap; // Pastikan import ini ada
import java.util.Map;     // Pastikan import ini ada
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author sauki
 */
public class selesai extends javax.swing.JFrame {
    koneksi dbsetting;
    String driver,database,user,pass;
    Object tabel;
    
    /**
     * Creates new form 
     */
    public selesai() {
        initComponents();
        
        dbsetting = new koneksi();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");
        
        tanggal_dikembalikan.setDate(new Date());
        
        sewaAktifMap = new HashMap<>(); // Menggunakan HashMap sebagai implementasi konkret
        sewaBiayaAwalMap = new HashMap<>();
        sewaTargetKembaliMap = new HashMap<>();
        
        SpinnerModel hourModel = new SpinnerNumberModel(0, 0, 23, 1); // initial, min, max, step
        jam_dikembalikan.setModel(hourModel);

        // Konfigurasi JSpinner untuk Menit (0-59)
        SpinnerModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1); // initial, min, max, step
        menit_dikembalikan.setModel(minuteModel);
        
        txt_tanggal_kembali_seharusnya.setEnabled(false);
        txt_jumlah_harus_dibayarkan.setEnabled(false);
        
        loadPenyewaToComboBox();
        table_selesai.setModel(tableMode1); 
        settableload(); 
    }
    
    private javax.swing.table.DefaultTableModel tableMode1=getDefaultTabelModel();
    private javax.swing.table.DefaultTableModel getDefaultTabelModel()
    {
        return new javax.swing.table.DefaultTableModel
        (
            new Object[][] {},
            new String[]
            {
                "ID Selesai",             // Kolom dari tabel selesai
                "ID Sewa",                // Kolom dari tabel selesai
                "Nama Pelanggan",         // Join dari tabel pelanggan
                "Merk Motor",             // Join dari tabel motor
                "Plat Nomor",             // Join dari tabel motor
                "Tgl Peminjaman",         // Join dari tabel sewa
                "Tgl Target Kembali",     // Join dari tabel sewa
                "Tgl Kembali Aktual",     // Kolom dari tabel selesai
                "Kondisi Motor",          // Kolom dari tabel selesai
                "Total Denda",            // Kolom dari tabel selesai
                "Total Biaya Akhir",      // Kolom dari tabel selesai
                "Jml Dibayar",            // Kolom dari tabel selesai
                "Status Bayar"            // Kolom dari tabel selesai
            }
        )
        
        {
            boolean[] canEdit = new boolean[]
            {
                 false, false, false, false, false, false, false, false, false, false, false, false, false 
            };
            public boolean isCellEditable(int rowIndex, int columnIndex) 
            {
                return canEdit[columnIndex];
            }
        };
    }
    
    private void settableload()
    {
        tableMode1.setRowCount(0); // Pastikan tabel dikosongkan terlebih dahulu

        Connection kon = null;
        Statement stt = null;
        ResultSet res = null;

        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            stt = kon.createStatement();

            // === PERBAIKAN SQL QUERY UNTUK MEMUAT DATA DARI TABEL SELESAI ===
            // Pastikan alias tabel digunakan untuk menghindari ambiguitas kolom
            String SQL = "SELECT " +
                         "    sl.id_selesai, " +                  // 0
                         "    sl.id_sewa, " +                     // 1
                         "    p.nama_pelanggan, " +               // 2
                         "    m.merk, " +                         // 3
                         "    m.plat_nomor, " +                   // 4
                         "    s.tanggal_peminjaman, " +           // 5
                         "    s.tanggal_kembali, " +  // 6
                         "    sl.tanggal_kembali_aktual, " + // 7
                         "    sl.kondisi_motor_kembali, " +       // 8
                         "    sl.total_denda, " + // 9
                         "    sl.total_biaya, " +           // 10
                         "    sl.jumlah_sudah_dibayar, " +   // 11
                         "    sl.status_pembayaran " +            // 12
                         "FROM " +
                         "    selesai sl " +                      // Alias 'sl' untuk tabel selesai
                         "JOIN " +
                         "    sewa s ON sl.id_sewa = s.id_sewa " + // Alias 's' untuk tabel sewa
                         "JOIN " +
                         "    pelanggan p ON s.id_pelanggan = p.id_pelanggan " + // Alias 'p'
                         "JOIN " +
                         "    motor m ON s.id_motor = m.id_motor " + // Alias 'm'
                         "ORDER BY " +
                         "    sl.tanggal_kembali_aktual DESC"; // Urutkan berdasarkan tanggal selesai

            res = stt.executeQuery(SQL);

            while (res.next()) {
                Object[] data = new Object[13]; // === UKURAN ARRAY HARUS 13 KOLOM ===

                // Mengambil data menggunakan nama kolom (lebih aman & mudah dibaca)
                data[0] = res.getString("id_selesai");
                data[1] = res.getString("id_sewa");
                data[2] = res.getString("nama_pelanggan");
                data[3] = res.getString("merk");
                data[4] = res.getString("plat_nomor");
                data[5] = res.getTimestamp("tanggal_peminjaman");
                data[6] = res.getTimestamp("tanggal_kembali");
                data[7] = res.getTimestamp("tanggal_kembali_aktual");
                data[8] = res.getString("kondisi_motor_kembali");
                data[9] = res.getDouble("total_denda");
                data[10] = res.getDouble("total_biaya");
                data[11] = res.getDouble("jumlah_sudah_dibayar");
                data[12] = res.getString("status_pembayaran");

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
    
    int row = -1;
     private void aktifkan_teks() {
        combo_penyewa.setEnabled(true);
        combo_kondisi.setEnabled(true);
        tanggal_dikembalikan.setEnabled(true);
        jam_dikembalikan.setEnabled(true);
        menit_dikembalikan.setEnabled(true);
        txt_jumlah_dibayarkan.setEnabled(true);
        combo_penyewa1.setEnabled(true);
    }
    
    private void nonaktif_teks() {
        combo_penyewa.setEnabled(false);
        combo_kondisi.setEnabled(false);
        tanggal_dikembalikan.setEnabled(false);
        jam_dikembalikan.setEnabled(false);
        menit_dikembalikan.setEnabled(false);
        txt_jumlah_dibayarkan.setEnabled(false);
        combo_penyewa1.setEnabled(false);
    }
    
    private void clear_teks() {
        combo_penyewa.setSelectedIndex(0); 
        tanggal_dikembalikan.setDate(new Date()); 
        jam_dikembalikan.setValue(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        menit_dikembalikan.setValue(Calendar.getInstance().get(Calendar.MINUTE));
        combo_kondisi.setSelectedIndex(0);
        txt_jumlah_dibayarkan.setText("");
        txt_jumlahdenda1.setText("0.0"); // Reset total denda yang ditampilkan
        txt_tanggal_kembali_seharusnya.setText(""); // Reset field tanggal seharusnya
        txt_jumlah_harus_dibayarkan.setText("0.0"); // Reset field jumlah harus dibayarkan
        combo_penyewa1.setSelectedIndex(0);
    }
    
    private void loadPenyewaToComboBox() {
        combo_penyewa.removeAllItems(); // combo_penyewa akan kita gunakan untuk memilih sewa aktif
        sewaAktifMap.clear();
        sewaBiayaAwalMap.clear();
        sewaTargetKembaliMap.clear();

        javax.swing.DefaultComboBoxModel<String> model = new javax.swing.DefaultComboBoxModel<>();

        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();

            // Ambil sewa yang statusnya 'aktif'
            String SQL = "SELECT " +
                         "    s.id_sewa, " +
                         "    p.nama_pelanggan, " +
                         "    m.plat_nomor, " +
                         "    s.tanggal_peminjaman, " +
                         "    s.tanggal_kembali, " +
                         "    s.harga_sewa_awal " +
                         "FROM " +
                         "    sewa s " +
                         "JOIN " +
                         "    pelanggan p ON s.id_pelanggan = p.id_pelanggan " +
                         "JOIN " +
                         "    motor m ON s.id_motor = m.id_motor " +
                         "WHERE " +
                         "    s.status_sewa = 'aktif' " + // HANYA SEWA YANG AKTIF
                         "ORDER BY s.tanggal_peminjaman DESC";

            ResultSet res = stt.executeQuery(SQL);

            String defaultItemText = "Pilih Sewa Aktif";
            model.addElement(defaultItemText);
            sewaAktifMap.put(defaultItemText, 0); // ID 0 untuk "Pilih Sewa Aktif"

            while (res.next()) {
                int idSewa = res.getInt("id_sewa");
                String namaPelanggan = res.getString("nama_pelanggan");
                String platNomor = res.getString("plat_nomor");
                double biayaSewaAwal = res.getDouble("harga_sewa_awal");
                Timestamp tanggalTargetKembali = res.getTimestamp("tanggal_kembali");

                String displayString = idSewa + " - " + namaPelanggan + " (" + platNomor + ")";
                model.addElement(displayString);
                sewaAktifMap.put(displayString, idSewa);
                sewaBiayaAwalMap.put(idSewa, biayaSewaAwal);
                sewaTargetKembaliMap.put(idSewa, tanggalTargetKembali);
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
    
    // --- Handler ketika item di combo_penyewa dipilih atau tanggal/jam/kondisi berubah ---
     private void updateCalculatedFields() {
        String selectedDisplayString = (String) combo_penyewa.getSelectedItem();
        int idSewa = 0;
        if (selectedDisplayString != null && sewaAktifMap.containsKey(selectedDisplayString)) {
            idSewa = sewaAktifMap.get(selectedDisplayString);
        }

        if (idSewa > 0) {
            Timestamp tanggalTargetKembali = sewaTargetKembaliMap.get(idSewa);
            double biayaSewaAwal = sewaBiayaAwalMap.get(idSewa);

            // === Tampilkan Tanggal Seharusnya Dikembalikan ===
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm"); // Format yang lebih lengkap
            if (tanggalTargetKembali != null) {
                txt_tanggal_kembali_seharusnya.setText(dateFormat.format(tanggalTargetKembali));
            } else {
                txt_tanggal_kembali_seharusnya.setText("N/A");
            }

            Date tglKembaliActualDate = tanggal_dikembalikan.getDate();
            int jamAktual = (Integer) jam_dikembalikan.getValue();
            int menitAktual = (Integer) menit_dikembalikan.getValue();

            double dendaTelat = 0.0;
            double dendaKerusakan = 0.0;
            double totalBiayaYangHarusDibayar = 0.0; // Ini yang akan ditampilkan di txt_jumlah_harus_dibayarkan

            // Hanya hitung denda jika tanggal aktual sudah diisi
            if (tglKembaliActualDate != null) { 
                dendaTelat = hitungDendaTelat(tanggalTargetKembali, tglKembaliActualDate, jamAktual, menitAktual, biayaSewaAwal); // Passing biayaSewaAwal
                
                String kondisiMotor = (String) combo_kondisi.getSelectedItem();
                if (kondisiMotor != null) {
                    switch (kondisiMotor) {
                        case "Lecet": dendaKerusakan = 50000.0; break;
                        case "Rusak Ringan": dendaKerusakan = 200000.0; break;
                        case "Rusak Berat": dendaKerusakan = 1000000.0; break;
                        default: dendaKerusakan = 0.0; break;
                    }
                }
                totalBiayaYangHarusDibayar = biayaSewaAwal + dendaTelat + dendaKerusakan;
            } else {
                // Jika tanggal aktual belum diisi, hanya tampilkan harga_sewa_awal
                totalBiayaYangHarusDibayar = biayaSewaAwal;
            }

            double totalDendaYangDitampilkan = dendaTelat + dendaKerusakan;
            
            txt_jumlahdenda1.setText(String.format("%.2f", totalDendaYangDitampilkan)); // Tampilkan TOTAL denda
            txt_jumlah_harus_dibayarkan.setText(String.format("%.2f", totalBiayaYangHarusDibayar)); // Update JML HARUS DIBAYARKAN

        } else {
            // Reset tampilan jika "Pilih Sewa Aktif" dipilih
            txt_jumlahdenda1.setText("0.0");
            txt_jumlah_harus_dibayarkan.setText("0.0");
            txt_tanggal_kembali_seharusnya.setText("");
        }
    }
    
    // --- REVISI FUNGSI hitungDendaTelat() ---
    private double hitungDendaTelat(Timestamp tanggalTargetKembali, Date tanggalAktual, int jamAktual, int menitAktual, double hargaSewaAwal) {
        if (tanggalTargetKembali == null || tanggalAktual == null) {
            return 0.0;
        }

        Calendar calTarget = Calendar.getInstance();
        calTarget.setTimeInMillis(tanggalTargetKembali.getTime());

        Calendar calAktual = Calendar.getInstance();
        calAktual.setTime(tanggalAktual);
        calAktual.set(Calendar.HOUR_OF_DAY, jamAktual);
        calAktual.set(Calendar.MINUTE, menitAktual);
        calAktual.set(Calendar.SECOND, 0);
        calAktual.set(Calendar.MILLISECOND, 0);

        // Jika waktu aktual lebih awal atau sama dengan waktu target, tidak ada denda
        if (calAktual.compareTo(calTarget) <= 0) {
            return 0.0;
        }

        long diffMillis = calAktual.getTimeInMillis() - calTarget.getTimeInMillis();
        // Gunakan diffHoursFraction untuk perhitungan denda progresif
        double diffHoursFraction = (double)diffMillis / (1000.0 * 60 * 60); 

        double denda = 0.0;
        double dendaPerJam = 10000.0; // Denda Rp 10.000 per jam

        // Aturan denda progresif:
        // Jika telat lebih dari 4 jam, maka denda dihitung berdasarkan pembulatan ke atas per 24 jam (hari penuh),
        // menggunakan hargaSewaAwal sebagai tarif per harinya.
        // Jika telat 4 jam atau kurang, denda dihitung per jam (dibulatkan ke atas).
        
        if (diffHoursFraction > 4.0) {
            // Hitung berapa total hari penuh yang dibulatkan ke atas.
            // Contoh: 4.1 jam -> ceil(4.1/24) = ceil(0.17) = 1 hari
            // Contoh: 24.1 jam -> ceil(24.1/24) = ceil(1.004) = 2 hari
            long totalDaysCeiled = (long) Math.ceil(diffHoursFraction / 24.0);
            denda = totalDaysCeiled * hargaSewaAwal; // Denda dihitung per hari penuh
        } else {
            // Jika telat 4 jam atau kurang, denda per jam (dibulatkan ke atas ke jam terdekat)
            denda = Math.ceil(diffHoursFraction) * dendaPerJam;
        }
        
        return denda;
    }

    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        btn_cari = new javax.swing.JButton();
        btn_tampil = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_selesai = new javax.swing.JTable();
        btn_tambah = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        btn_ubah = new javax.swing.JButton();
        btn_batal = new javax.swing.JButton();
        btn_simpan = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        btn_hapus = new javax.swing.JButton();
        txt_jumlahdenda1 = new javax.swing.JTextField();
        combo_urut = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btn_urut1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        combo_penyewa = new javax.swing.JComboBox();
        menit_dikembalikan = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        tanggal_dikembalikan = new com.toedter.calendar.JDateChooser();
        jam_dikembalikan = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        combo_penyewa1 = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txt_jumlah_dibayarkan = new javax.swing.JTextField();
        combo_kondisi = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        txt_tanggal_kembali_seharusnya = new javax.swing.JTextField();
        txt_jumlah_harus_dibayarkan = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

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

        table_selesai.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(table_selesai);

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

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("Cari");

        btn_hapus.setText("Hapus");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        txt_jumlahdenda1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_jumlahdenda1ActionPerformed(evt);
            }
        });

        combo_urut.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nama", "Total Bayar" }));
        combo_urut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_urutActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Urutkan");

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel1.setText("Tabel Akhir");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(616, 616, 616)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btn_urut1.setText("Urutkan");
        btn_urut1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_urut1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Penyewa");

        combo_penyewa.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        combo_penyewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_penyewaActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Menit");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Jam");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Tanggal");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Jam Kembali Aktual");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Kondisi Motor");

        combo_penyewa1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cash", "Transfer", " " }));
        combo_penyewa1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_penyewa1ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Metode Pembayaran");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Jumlah Bayar");

        combo_kondisi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Baik", "Lecet", "Rusak Ringan", "Rusak Berat", " ", " " }));
        combo_kondisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_kondisiActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setText("Jam Kembali Seharusnya");

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel17.setText("Jumlah yang harus diabayarkan");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(527, 527, 527)
                        .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_simpan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_ubah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_hapus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_batal)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txt_jumlahdenda1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_cari)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_tampil, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(combo_urut, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_urut1)))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(jLabel16)
                .addGap(65, 65, 65)
                .addComponent(txt_tanggal_kembali_seharusnya, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                .addComponent(txt_jumlah_harus_dibayarkan, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61))
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(combo_penyewa1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(combo_penyewa, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(144, 144, 144)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tanggal_dikembalikan, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jam_dikembalikan, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(menit_dikembalikan, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(21, 21, 21)
                        .addComponent(txt_jumlah_dibayarkan, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(combo_kondisi, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_batal, btn_hapus, btn_simpan, btn_tambah, btn_ubah});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_tanggal_kembali_seharusnya, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jumlah_harus_dibayarkan, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(combo_penyewa, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel12)
                            .addComponent(txt_jumlah_dibayarkan, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(combo_penyewa1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combo_kondisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(33, 33, 33))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel11))
                                .addGap(3, 3, 3)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(menit_dikembalikan)
                                    .addComponent(jam_dikembalikan)
                                    .addComponent(tanggal_dikembalikan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jumlahdenda1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cari)
                    .addComponent(btn_tampil)
                    .addComponent(combo_urut, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(btn_urut1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan)
                    .addComponent(btn_ubah)
                    .addComponent(btn_hapus)
                    .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_batal))
                .addGap(21, 21, 21))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_cari, btn_tampil, btn_urut1, combo_urut, jLabel10, jLabel8, txt_jumlahdenda1});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_batal, btn_hapus, btn_simpan, btn_tambah, btn_ubah});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel12, jLabel7});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {combo_kondisi, combo_penyewa, combo_penyewa1, jLabel2, jLabel9});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cariActionPerformed

    private void btn_tampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampilActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_tampilActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        // TODO add your handling code here:
        aktifkan_teks();
        btn_simpan.setEnabled(true);
        btn_ubah.setEnabled(false);
        btn_hapus.setEnabled(false);
        btn_batal.setEnabled(true);
        table_selesai.clearSelection();
        row = -1; // Reset row selection
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_ubahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ubahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_ubahActionPerformed

    private void btn_batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_batalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_batalActionPerformed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        String selectedDisplayString = (String) combo_penyewa.getSelectedItem();
        int idSewa = 0;
        if (selectedDisplayString != null && sewaAktifMap.containsKey(selectedDisplayString)) {
            idSewa = sewaAktifMap.get(selectedDisplayString);
        }

        if (idSewa <= 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih Sewa Aktif terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date tglKembaliDate = tanggal_dikembalikan.getDate();
        if (tglKembaliDate == null) {
            JOptionPane.showMessageDialog(this, "Tanggal kembali aktual harus diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int jam = (Integer) jam_dikembalikan.getValue();
        int menit = (Integer) menit_dikembalikan.getValue();

        Calendar calAktual = Calendar.getInstance();
        calAktual.setTime(tglKembaliDate);
        calAktual.set(Calendar.HOUR_OF_DAY, jam);
        calAktual.set(Calendar.MINUTE, menit);
        calAktual.set(Calendar.SECOND, 0);
        calAktual.set(Calendar.MILLISECOND, 0);
        Timestamp tanggalKembaliAktual = new Timestamp(calAktual.getTimeInMillis());

        String kondisiMotor = (String) combo_kondisi.getSelectedItem();
        if (kondisiMotor == null || kondisiMotor.equals("--- Pilih Kondisi ---")) {
             JOptionPane.showMessageDialog(this, "Kondisi motor harus dipilih.", "Peringatan", JOptionPane.WARNING_MESSAGE);
             return;
        }
        
        double jumlahDibayar = 0.0;
        try {
            jumlahDibayar = Double.parseDouble(txt_jumlah_dibayarkan.getText()); 
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format Jumlah Dibayarkan tidak valid.", "Error Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String metodePembayaran = (String) combo_penyewa1.getSelectedItem();
        if (metodePembayaran == null || metodePembayaran.equals("--- Pilih Metode ---") || metodePembayaran.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Metode pembayaran harus dipilih.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- RE-CALCULATE Denda dan Total Biaya Akhir TEPAT SEBELUM INSERT ---
        double biayaSewaAwal = sewaBiayaAwalMap.get(idSewa); 
        Timestamp tanggalTargetKembali = sewaTargetKembaliMap.get(idSewa);

        double dendaTelatFinal = hitungDendaTelat(tanggalTargetKembali, tglKembaliDate, jam, menit, biayaSewaAwal);
        
        double dendaKerusakanFinal = 0.0;
        if (kondisiMotor != null) {
            switch (kondisiMotor) {
                case "Lecet": dendaKerusakanFinal = 50000.0; break;
                case "Rusak Ringan": dendaKerusakanFinal = 200000.0; break;
                case "Rusak Berat": dendaKerusakanFinal = 1000000.0; break;
                default: dendaKerusakanFinal = 0.0; break;
            }
        }
        
        double totalDendaFinal = dendaTelatFinal + dendaKerusakanFinal;
        double totalBiayaAkhirFinal = biayaSewaAwal + totalDendaFinal; // Ini yang akan dimasukkan ke DB

        String statusPembayaran = "";
        if (jumlahDibayar >= totalBiayaAkhirFinal) {
            statusPembayaran = "Lunas";
        } else if (jumlahDibayar > 0 && jumlahDibayar < totalBiayaAkhirFinal) {
            statusPembayaran = "Kurang Bayar";
        } else {
            statusPembayaran = "Belum Bayar";
        }

        Connection kon = null;
        PreparedStatement psInsertSelesai = null;
        PreparedStatement psUpdateSewa = null;
        PreparedStatement psInsertDendaTelat = null;
        PreparedStatement psInsertDendaKerusakan = null;

        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            kon.setAutoCommit(false);

            // INSERT ke tabel 'selesai'
            String SQL_INSERT_SELESAI = "INSERT INTO selesai (id_sewa, tanggal_kembali_aktual, kondisi_motor_kembali, total_denda, total_biaya, jumlah_sudah_dibayar, status_pembayaran, metode_pembayaran) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            psInsertSelesai = kon.prepareStatement(SQL_INSERT_SELESAI);
            psInsertSelesai.setInt(1, idSewa);
            psInsertSelesai.setTimestamp(2, tanggalKembaliAktual);
            psInsertSelesai.setString(3, kondisiMotor);
            psInsertSelesai.setDouble(4, totalDendaFinal);      
            psInsertSelesai.setDouble(5, totalBiayaAkhirFinal); 
            psInsertSelesai.setDouble(6, jumlahDibayar);
            psInsertSelesai.setString(7, statusPembayaran);
            psInsertSelesai.setString(8, metodePembayaran);

            int rowsInsertedSelesai = psInsertSelesai.executeUpdate();

            // INSERT ke tabel 'denda' jika ada denda telat
            if (dendaTelatFinal > 0) {
                String SQL_INSERT_DENDA_TELAT = "INSERT INTO denda (id_sewa, jenis_denda, jumlah_denda, keterangan_denda) VALUES (?, ?, ?, ?)";
                psInsertDendaTelat = kon.prepareStatement(SQL_INSERT_DENDA_TELAT);
                psInsertDendaTelat.setInt(1, idSewa);
                psInsertDendaTelat.setString(2, "Terlambat");
                psInsertDendaTelat.setDouble(3, dendaTelatFinal);
                psInsertDendaTelat.setString(4, "Denda keterlambatan pengembalian motor.");
                psInsertDendaTelat.executeUpdate();
            }

            // INSERT ke tabel 'denda' jika ada denda kerusakan
            if (dendaKerusakanFinal > 0 && !kondisiMotor.equals("Baik")) {
                String SQL_INSERT_DENDA_KERUSAKAN = "INSERT INTO denda (id_sewa, jenis_denda, jumlah_denda, keterangan_denda) VALUES (?, ?, ?, ?)";
                psInsertDendaKerusakan = kon.prepareStatement(SQL_INSERT_DENDA_KERUSAKAN);
                psInsertDendaKerusakan.setInt(1, idSewa);
                psInsertDendaKerusakan.setString(2, "Kerusakan");
                psInsertDendaKerusakan.setDouble(3, dendaKerusakanFinal);
                psInsertDendaKerusakan.setString(4, "Denda kerusakan motor: " + kondisiMotor);
                psInsertDendaKerusakan.executeUpdate();
            }

            // UPDATE status_sewa di tabel 'sewa'
            String SQL_UPDATE_SEWA = "UPDATE sewa SET status_sewa = 'selesai' WHERE id_sewa = ?";
            psUpdateSewa = kon.prepareStatement(SQL_UPDATE_SEWA);
            psUpdateSewa.setInt(1, idSewa);
            int rowsUpdatedSewa = psUpdateSewa.executeUpdate();

            if (rowsInsertedSelesai > 0 && rowsUpdatedSewa > 0) {
                kon.commit();
                JOptionPane.showMessageDialog(this, "Transaksi selesai berhasil dicatat!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clear_teks();
                nonaktif_teks();
                btn_simpan.setEnabled(false);
                btn_batal.setEnabled(false);
                loadPenyewaToComboBox();
                settableload();
            } else {
                kon.rollback();
                JOptionPane.showMessageDialog(this, "Gagal mencatat transaksi selesai atau memperbarui status sewa.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            try {
                if (kon != null) kon.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error saat rollback: " + rollbackEx.getMessage());
            }
            System.err.println("SQL Error di btn_simpanActionPerformed(): " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Terjadi kesalahan database: " + ex.getMessage(), "Error Database",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            System.err.println("Driver Error di btn_simpanActionPerformed(): " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Driver database tidak ditemukan: " + ex.getMessage(), "Error Driver",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Pastikan Jumlah Dibayarkan diisi dengan angka yang valid.", "Error Input", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            System.err.println("General Error di btn_simpanActionPerformed(): " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Terjadi kesalahan tak terduga: " + ex.getMessage(), "Error Umum",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (psInsertSelesai != null) psInsertSelesai.close();
                if (psUpdateSewa != null) psUpdateSewa.close();
                if (psInsertDendaTelat != null) psInsertDendaTelat.close();
                if (psInsertDendaKerusakan != null) psInsertDendaKerusakan.close();
                if (kon != null) kon.close();
            } catch (SQLException ex) {
                System.err.println("Error closing resources in btn_simpanActionPerformed(): " + ex.getMessage());
            }
        }
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void txt_jumlahdenda1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_jumlahdenda1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_jumlahdenda1ActionPerformed

    private void combo_urutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_urutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_urutActionPerformed

    private void btn_urut1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_urut1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_urut1ActionPerformed

    private void combo_penyewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_penyewaActionPerformed
        // TODO add your handling code here:
        updateCalculatedFields();
    }//GEN-LAST:event_combo_penyewaActionPerformed

    private void combo_penyewa1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_penyewa1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_penyewa1ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        menu_utama utama = new menu_utama();
        utama.setVisible(true);
    }//GEN-LAST:event_formWindowClosed

    private void combo_kondisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_kondisiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_kondisiActionPerformed

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
            java.util.logging.Logger.getLogger(selesai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(selesai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(selesai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(selesai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new selesai().setVisible(true);
            }
        });
    }
    private Map<String, Integer> sewaAktifMap;
    private Map<Integer, Double> sewaBiayaAwalMap;
    private Map<Integer, Timestamp> sewaTargetKembaliMap;
    private javax.swing.JLabel jLabelJumlahHarusDibayarkan;
    private java.util.Map<String, Integer> penggunaMap;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_batal;
    private javax.swing.JButton btn_cari;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_tampil;
    private javax.swing.JButton btn_ubah;
    private javax.swing.JButton btn_urut1;
    private javax.swing.JComboBox combo_kondisi;
    private javax.swing.JComboBox combo_penyewa;
    private javax.swing.JComboBox combo_penyewa1;
    private javax.swing.JComboBox combo_urut;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
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
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSpinner jam_dikembalikan;
    private javax.swing.JSpinner menit_dikembalikan;
    private javax.swing.JTable table_selesai;
    private com.toedter.calendar.JDateChooser tanggal_dikembalikan;
    private javax.swing.JTextField txt_jumlah_dibayarkan;
    private javax.swing.JTextField txt_jumlah_harus_dibayarkan;
    private javax.swing.JTextField txt_jumlahdenda1;
    private javax.swing.JTextField txt_tanggal_kembali_seharusnya;
    // End of variables declaration//GEN-END:variables
}
