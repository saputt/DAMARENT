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
import java.util.HashMap; 
import java.util.Map;    
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

        // Inisialisasi HashMap DULU
        sewaAktifMap = new HashMap<>();
        sewaBiayaAwalMap = new HashMap<>();
        sewaTargetKembaliMap = new HashMap<>();
        sewaPeminjamanMap = new HashMap<>();

        SpinnerModel hourModel = new SpinnerNumberModel(0, 0, 23, 1);
        jam_dikembalikan.setModel(hourModel);

        SpinnerModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1); 
        menit_dikembalikan.setModel(minuteModel);

        txt_tanggal_kembali_seharusnya.setEditable(false);
        txt_tanggal_peminjaman.setEditable(false);
        txt_total_bayar.setEditable(false);
        txt_jumlah_denda.setEditable(false);

        // Load data KE MAPS
        loadPenyewaToComboBox(); 

        // Set table model dan load data tabel
        table_selesai.setModel(tableMode1); 
        settableload(); 

        // BARU SET TANGGAL, SETELAH MAPS DAN TABLE TERISI
        // Ini akan memicu PropertyChange, tapi sekarang maps sudah terisi
        tanggal_dikembalikan.setDate(new Date()); 

        // Panggil updateCalculatedFields() sebagai langkah TERAKHIR di konstruktor
        updateCalculatedFields(); 
    }
    
    private javax.swing.table.DefaultTableModel tableMode1=getDefaultTabelModel();
    private javax.swing.table.DefaultTableModel getDefaultTabelModel()
    {
        return new javax.swing.table.DefaultTableModel
        (
            new Object[][] {},
            new String[]
            {
                "ID",                  // Index 0 (id_selesai)
                "ID Sewa",             // Index 1 (id_sewa) <- BARU UNTUK MENGHINDARI FK ERROR PADA UBAH
                "Nama Pelanggan",      // Index 2
                "Plat Nomor",          // Index 3
                "Tgl Peminjaman",      // Index 4
                "Tgl Target Kembali",  // Index 5
                "Tgl Kembali Aktual",  // Index 6
                "Ttl Denda",           // Index 7 (Double)
                "Rincian Denda",       // Index 8 (String)
                "Jml Jam Telat",       // Index 9 (Integer)
                "Ttl Biaya",           // Index 10 (Double)
                "Jml Dibayar",         // Index 11 (Double)
                "Stt Bayar"            // Index 12 (String)
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
        tableMode1.setRowCount(0); 
        Connection kon = null;
        Statement stt = null;
        ResultSet res = null;

        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            stt = kon.createStatement();

            String SQL = "SELECT "
                        + "    sl.id_selesai, sl.id_sewa, p.nama_pelanggan, m.plat_nomor, " // <<< TAMBAHKAN sl.id_sewa di SELECT
                        + "    s.tanggal_peminjaman, s.tanggal_kembali, sl.tanggal_kembali_aktual, "
                        + "    sl.total_denda, sl.rincian_denda, sl.total_biaya, sl.jumlah_sudah_dibayar, "
                        + "    sl.status_pembayaran, "
                        + "    sl.jumlah_jam_telat " 
                        + "FROM selesai sl "
                        + "JOIN sewa s ON sl.id_sewa = s.id_sewa "
                        + "JOIN pelanggan p ON s.id_pelanggan = p.id_pelanggan "
                        + "JOIN motor m ON s.id_motor = m.id_motor "
                        + "ORDER BY sl.tanggal_kembali_aktual DESC";

            res = stt.executeQuery(SQL);

            while (res.next()) {
                Object[] data = new Object[13]; 

                data[0] = res.getString("id_selesai");
                data[1] = res.getInt("id_sewa"); // <<< ISI ID SEWA DARI HASIL QUERY
                data[2] = res.getString("nama_pelanggan");
                data[3] = res.getString("plat_nomor");
                data[4] = res.getTimestamp("tanggal_peminjaman");
                data[5] = res.getTimestamp("tanggal_kembali");
                data[6] = res.getTimestamp("tanggal_kembali_aktual");
                data[7] = res.getDouble("total_denda");        // Index 7: Ttl Denda (Double)
                data[8] = res.getString("rincian_denda");      // Index 8: Rincian Denda (String)
                data[9] = res.getInt("jumlah_jam_telat");      // Index 9: Jml Jam Telat (Integer)
                data[10] = res.getDouble("total_biaya");       // Index 10: Ttl Biaya (Double)
                data[11] = res.getDouble("jumlah_sudah_dibayar"); // Index 11: Jml Dibayar (Double)
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
    
    private void aktifkan_teks() {
        combo_penyewa.setEnabled(true);
        tanggal_dikembalikan.setEnabled(true);
        jam_dikembalikan.setEnabled(true);
        txt_rincian_denda.setEnabled(true);
        txt_jumlah_denda2.setEnabled(true);
        menit_dikembalikan.setEnabled(true);
        txt_jumlah_bayar.setEnabled(true);
    }
    
    private void nonaktif_teks() {
        combo_penyewa.setEnabled(false);
        tanggal_dikembalikan.setEnabled(false);
        jam_dikembalikan.setEnabled(false);
        txt_rincian_denda.setEnabled(false);
        txt_jumlah_denda2.setEnabled(false);
        menit_dikembalikan.setEnabled(false);
        txt_jumlah_bayar.setEnabled(false);
    }
    
    private void membersihkan_teks() {
        combo_penyewa.setSelectedIndex(0); 
        tanggal_dikembalikan.setDate(new Date()); 
        jam_dikembalikan.setValue(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        menit_dikembalikan.setValue(Calendar.getInstance().get(Calendar.MINUTE));
        txt_jumlah_bayar.setText("");
        txt_rincian_denda.setText("");
        txt_jumlah_denda2.setText("");
        txt_cari.setText(""); 
        txt_tanggal_kembali_seharusnya.setText(""); 
        txt_total_bayar.setText("0.0"); 
    }
    
    int row = -1; 
    public void tampil_field() {
        row = table_selesai.getSelectedRow();
        if (row >= 0) {
            try {
                String idSelesaiFromTable = tableMode1.getValueAt(row, 0).toString(); // ID Selesai
                int idSewaFromTable = (Integer) tableMode1.getValueAt(row, 1);      // <<< AMBIL ID SEWA DARI TABEL
                String namaPelangganFromTable = tableMode1.getValueAt(row, 2).toString(); // Nama Pelanggan
                String platNomorFromTable = tableMode1.getValueAt(row, 3).toString(); // Plat Nomor (kolom 3) -> Sesuaikan indeks
                Timestamp tglPeminjamanTs = (Timestamp) tableMode1.getValueAt(row, 4); // Tgl Peminjaman (kolom 4)
                Timestamp tglTargetKembaliTs = (Timestamp) tableMode1.getValueAt(row, 5); // Tgl Target Kembali (kolom 5)
                Timestamp tglKembaliAktualTs = (Timestamp) tableMode1.getValueAt(row, 6); // Tgl Kembali Aktual (kolom 6)

                double totalDendaTable = (Double) tableMode1.getValueAt(row, 7);       // Index 7: Ttl Denda (Double)
                String rincianDendaTable = tableMode1.getValueAt(row, 8).toString();   // Index 8: Rincian Denda (String)
                int jumlahJamTelatTable = (Integer) tableMode1.getValueAt(row, 9);     // Index 9: Jml Jam Telat (Integer)
                double totalBiayaTable = (Double) tableMode1.getValueAt(row, 10);      // Index 10: Ttl Biaya (Double)
                double jumlahSudahDibayar = (Double) tableMode1.getValueAt(row, 11);   // Index 11: Jml Dibayar (Double)
                String statusPembayaranTable = tableMode1.getValueAt(row, 12).toString(); // Index 12: Stt Bayar (String)


                // Set combo_penyewa berdasarkan ID Sewa yang ditemukan di tabel
                // Ini penting agar semua map (sewaBiayaAwalMap, dll) terisi dengan data yang benar
                String displayStringForComboBox = null;
                // Cari item yang sesuai dengan idSewaFromTable di sewaAktifMap
                for (Map.Entry<String, Integer> entry : sewaAktifMap.entrySet()) {
                    if (entry.getValue().equals(idSewaFromTable)) {
                        displayStringForComboBox = entry.getKey();
                        break;
                    }
                }
                if (displayStringForComboBox != null) {
                    combo_penyewa.setSelectedItem(displayStringForComboBox);
                } else {
                    // Jika id_sewa dari tabel selesai tidak ada di sewaAktifMap (karena sudah selesai),
                    // buat item sementara agar combo box menampilkan nama yang benar.
                    // Ini tidak akan memicu perhitungan baru karena idSewaMap tidak diupdate.
                    // Atau, lebih baik, load *semua* sewa (aktif/non-aktif) ke combo_penyewa jika diperlukan untuk ubah.
                    // Untuk saat ini, kita akan mencoba membuat string display sementara
                    String tempDisplayString = idSewaFromTable + " - " + namaPelangganFromTable + " (" + platNomorFromTable + ")";
                    DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) combo_penyewa.getModel();
                    if(model.getIndexOf(tempDisplayString) == -1) { // Hanya tambahkan jika belum ada
                         model.addElement(tempDisplayString);
                    }
                    combo_penyewa.setSelectedItem(tempDisplayString);
                    // Penting: jika sewa sudah tidak aktif, pastikan Anda bisa mendapatkan detailnya dari map
                    // atau dari database, karena sewaBiayaAwalMap, dll. hanya berisi sewa 'aktif'.
                    // Untuk ubah, kita perlu data sewa asli, bukan hanya yang 'aktif'.
                    // Ini adalah kompleksitas yang muncul ketika combo box hanya memuat subset data.
                }

                txt_jumlah_bayar.setText(String.format("%.2f", jumlahSudahDibayar));
                txt_jumlah_denda2.setText(String.format("%.2f", totalDendaTable)); // Ini adalah input denda manual
                txt_rincian_denda.setText(rincianDendaTable); // Set rincian denda yang ada di tabel

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm"); // Perbaikan SimpleDateFormat
                txt_tanggal_peminjaman.setText(tglPeminjamanTs != null ? dateFormat.format(tglPeminjamanTs) : "N/A");
                txt_tanggal_kembali_seharusnya.setText(tglTargetKembaliTs != null ? dateFormat.format(tglTargetKembaliTs) : "N/A");
                
                if (tglKembaliAktualTs != null) {
                    tanggal_dikembalikan.setDate(new Date(tglKembaliAktualTs.getTime()));
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(tglKembaliAktualTs.getTime());
                    jam_dikembalikan.setValue(cal.get(Calendar.HOUR_OF_DAY));
                    menit_dikembalikan.setValue(cal.get(Calendar.MINUTE));
                } else {
                    tanggal_dikembalikan.setDate(null);
                    jam_dikembalikan.setValue(0);
                    menit_dikembalikan.setValue(0);
                }
                
                aktifkan_teks();
                updateCalculatedFields(); // Panggil ini untuk memperbarui total denda/biaya berdasarkan input field saat ini
                
                btn_simpan.setEnabled(false); // Setelah tampil field, biasanya mode adalah ubah/hapus
                btn_ubah.setEnabled(true);   
                btn_hapus.setEnabled(true);  
                btn_batal.setEnabled(true);

            } catch (ClassCastException e) { 
                JOptionPane.showMessageDialog(this, "Kesalahan tipe data saat menampilkan field: " + e.getMessage() + ". Pastikan data di tabel cocok dengan format kolom.", "Error Tipe Data", JOptionPane.ERROR_MESSAGE);
                System.err.println("Error di tampil_field (ClassCastException): " + e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error menampilkan data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Error di tampil_field (General Exception): " + e.getMessage());
            }
        } else {
            membersihkan_teks();
            nonaktif_teks();
            btn_simpan.setEnabled(true); // Jika tidak ada baris dipilih, aktifkan simpan
            btn_ubah.setEnabled(false);
            btn_hapus.setEnabled(false);
            btn_batal.setEnabled(false); 
        }
    }
    
    private void loadPenyewaToComboBox() {
        combo_penyewa.removeAllItems(); 
        sewaAktifMap.clear();
        sewaBiayaAwalMap.clear();
        sewaTargetKembaliMap.clear();

        javax.swing.DefaultComboBoxModel<String> model = new javax.swing.DefaultComboBoxModel<>();

        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();

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
                         "    s.status_sewa = 'aktif' " + 
                         "ORDER BY s.tanggal_peminjaman DESC";

            ResultSet res = stt.executeQuery(SQL);

            String defaultItemText = "Pilih Sewa Aktif";
            model.addElement(defaultItemText);
            sewaAktifMap.put(defaultItemText, 0); 

            while (res.next()) {
                int idSewa = res.getInt("id_sewa");
                String namaPelanggan = res.getString("nama_pelanggan");
                String platNomor = res.getString("plat_nomor");
                double biayaSewaAwal = res.getDouble("harga_sewa_awal");
                Timestamp tanggalTargetKembali = res.getTimestamp("tanggal_kembali");
                Timestamp tanggalPeminjaman = res.getTimestamp("tanggal_peminjaman");

                String displayString = idSewa + " - " + namaPelanggan + " (" + platNomor + ")";
                model.addElement(displayString);
                sewaAktifMap.put(displayString, idSewa);
                sewaBiayaAwalMap.put(idSewa, biayaSewaAwal);
                sewaTargetKembaliMap.put(idSewa, tanggalTargetKembali);
                sewaPeminjamanMap.put(idSewa, tanggalPeminjaman);
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

     private void updateCalculatedFields() {
         String selectedDisplayString = (String) combo_penyewa.getSelectedItem();
        int idSewa = 0;
        if (selectedDisplayString != null && sewaAktifMap.containsKey(selectedDisplayString)) {
            idSewa = sewaAktifMap.get(selectedDisplayString);
        }

        if (selectedDisplayString != null && sewaAktifMap.containsKey(selectedDisplayString)) {
            idSewa = sewaAktifMap.get(selectedDisplayString);
        }
        
        if (idSewa <= 0) {
            // --- GUNAKAN resetCalculatedFields() di sini, dan pastikan tidak ada setText untuk txt_jumlah_denda2 di dalamnya ---
            resetCalculatedFieldsExcludingManualDenda(); // Metode baru atau modifikasi resetCalculatedFields
            return; 
        }
        
        if (idSewa > 0) {
            Timestamp tanggalTargetKembali = sewaTargetKembaliMap.get(idSewa);
            Timestamp tanggalPeminjaman = sewaPeminjamanMap.get(idSewa);
            double biayaSewaAwal = sewaBiayaAwalMap.get(idSewa);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
            if (tanggalTargetKembali != null) {
                txt_tanggal_kembali_seharusnya.setText(dateFormat.format(tanggalTargetKembali));
            } else {
                txt_tanggal_kembali_seharusnya.setText("N/A");
            }
            if (tanggalPeminjaman != null) {
                txt_tanggal_peminjaman.setText(dateFormat.format(tanggalPeminjaman));
            } else {
                txt_tanggal_peminjaman.setText("N/A");
            }

            Date tglKembaliActualDate = tanggal_dikembalikan.getDate();
            int jamAktual = (Integer) jam_dikembalikan.getValue();
            int menitAktual = (Integer) menit_dikembalikan.getValue();

            double dendaTelat = 0.0;
            double dendaKerusakan = 0.0;
            long jumlahJamTelat = 0;
            
            // Get dendaKerusakan from txt_jumlah_denda2
            try {
                String denda2Text = txt_jumlah_denda2.getText();
                if (!denda2Text.isEmpty()) {
                    dendaKerusakan = Double.parseDouble(denda2Text);
                }
            } catch (NumberFormatException ex) {
                // Handle case where txt_jumlah_denda2 is not a valid number
                // You might want to show an error message or set dendaKerusakan to 0
                dendaKerusakan = 0.0; 
                // Optionally, clear the invalid text or highlight the field
                // txt_jumlah_denda2.setText("0.0"); 
                System.err.println("Invalid number format in txt_jumlah_denda2: " + ex.getMessage());
            }

            if (tglKembaliActualDate != null && tanggalTargetKembali != null) {
                // Only calculate dendaTelat if actual return date is available and target return date exists
                dendaTelat = hitungDendaTelat(tanggalTargetKembali, tglKembaliActualDate, jamAktual, menitAktual, biayaSewaAwal);
            }
            
            double totalDendaYangDitampilkan = dendaTelat + dendaKerusakan; // Sum of both denda types
            double totalBiayaYangHarusDibayar = biayaSewaAwal + totalDendaYangDitampilkan;

            txt_jumlah_denda.setText(String.format("%.2f", totalDendaYangDitampilkan));
            txt_total_bayar.setText(String.format("%.2f", totalBiayaYangHarusDibayar));

        } else {
            // Reset fields if no valid sewa is selected
            txt_total_bayar.setText("0.0");
            txt_jumlah_denda.setText("0.0"); // Reset total denda
            txt_tanggal_kembali_seharusnya.setText("");
            txt_tanggal_peminjaman.setText("");
            txt_rincian_denda.setText(""); // Clear rincian denda
            txt_jumlah_denda2.setText(""); // Clear manual denda
        }
    }

    private void resetCalculatedFieldsExcludingManualDenda() {
        if (txt_total_bayar != null) txt_total_bayar.setText("0.00");
        if (txt_jumlah_denda != null) txt_jumlah_denda.setText("0.00");
        if (txt_tanggal_kembali_seharusnya != null) txt_tanggal_kembali_seharusnya.setText("");
        if (txt_tanggal_peminjaman != null) txt_tanggal_peminjaman.setText("");
        if (txt_rincian_denda != null) txt_rincian_denda.setText(""); // Rincian denda ini juga digenerate, jadi bisa direset
        // JANGAN reset txt_jumlah_denda2 di sini
        // If you want it to revert to the initial value from DB when a row is clicked,
        // that should be handled in tampil_field().
    }
     
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

        if (calAktual.compareTo(calTarget) <= 0) {
            return 0.0;
        }

        long diffHours = 0; 
        if (calAktual.compareTo(calTarget) > 0) { // Jika aktual lebih lambat dari target
        long diffMillis = calAktual.getTimeInMillis() - calTarget.getTimeInMillis();
        diffHours = (long) Math.ceil((double)diffMillis / (1000.0 * 60 * 60)); // Bulatkan ke atas ke jam terdekat
    }
        
        long diffMillis = calAktual.getTimeInMillis() - calTarget.getTimeInMillis();
        double diffHoursFraction = (double)diffMillis / (1000.0 * 60 * 60); 

        double denda = 0.0;
        double dendaPerJam = 10000.0;
        if (diffHours > 4) { // Contoh logika denda per hari jika > 4 jam
            long totalDaysCeiled = (long) Math.ceil(diffHours / 24.0);
            denda = totalDaysCeiled * hargaSewaAwal;
        } else {
            denda = diffHours * dendaPerJam; // Denda per jam
        }
        
        return denda;
    }

    private void sorting(){
        if (combo_sort == null || tableMode1 == null) {
            System.err.println("Komponen sorting tidak diinisialisasi.");
            return;
        }
        
        String sort = (String) combo_sort.getSelectedItem();
        tableMode1.setRowCount(0);
        String sortOrder = "";
        // Jika sorting Termurah/Termahal, kemungkinan besar ingin mengurutkan 'total_biaya'
        String orderByColumn = "sl.tanggal_kembali_aktual"; // Default
        if ("Termurah".equals(sort)) {
            sortOrder = "ASC";
            orderByColumn = "sl.total_biaya"; // Urutkan berdasarkan total_biaya
        } else if ("Termahal".equals(sort)) {
            sortOrder = "DESC";
            orderByColumn = "sl.total_biaya"; // Urutkan berdasarkan total_biaya
        }
        try {
            Class.forName(driver);
            try (Connection kon = DriverManager.getConnection(database, user, pass);
                 Statement stt = kon.createStatement();
                 ResultSet res = stt.executeQuery("SELECT " +
                        "    sl.id_selesai, sl.id_sewa, p.nama_pelanggan, m.plat_nomor, " +
                        "    s.tanggal_peminjaman, s.tanggal_kembali, sl.tanggal_kembali_aktual, " +
                        "    sl.total_denda, sl.rincian_denda, sl.total_biaya, sl.jumlah_sudah_dibayar, " +
                        "    sl.status_pembayaran, sl.jumlah_jam_telat " +
                        "FROM selesai sl " +
                        "JOIN sewa s ON sl.id_sewa = s.id_sewa " +
                        "JOIN pelanggan p ON s.id_pelanggan = p.id_pelanggan " +
                        "JOIN motor m ON s.id_motor = m.id_motor " +
                        "ORDER BY " + orderByColumn + " " + sortOrder)) // Gunakan orderByColumn
            {
                while(res.next()) {
                    Object[] data = new Object[13]; // Ukuran array 13 sesuai getDefaultTabelModel()
                    data[0] = res.getString("id_selesai");
                    data[1] = res.getInt("id_sewa");
                    data[2] = res.getString("nama_pelanggan");
                    data[3] = res.getString("plat_nomor"); // Perbaiki, ini harusnya plat_nomor
                    data[4] = res.getTimestamp("tanggal_peminjaman"); // Ini harusnya tgl_peminjaman
                    data[5] = res.getTimestamp("tanggal_kembali"); // Ini harusnya tgl_target_kembali
                    data[6] = res.getTimestamp("tanggal_kembali_aktual"); // Ini harusnya tgl_kembali_aktual
                    data[7] = res.getDouble("total_denda");
                    data[8] = res.getString("rincian_denda");
                    data[9] = res.getInt("jumlah_jam_telat");
                    data[10] = res.getDouble("total_biaya");
                    data[11] = res.getDouble("jumlah_sudah_dibayar");
                    data[12] = res.getString("status_pembayaran");
                    tableMode1.addRow(data);
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
    }
    
    private void updateSewaStatus(int idSewa, Connection kon) throws SQLException {
        String SQL = "UPDATE sewa SET status_sewa = 'selesai' WHERE id_sewa = ?";
        try (PreparedStatement pst = kon.prepareStatement(SQL)) {
            pst.setInt(1, idSewa); // Set parameter id_sewa
            pst.executeUpdate(); // Jalankan query UPDATE
            System.out.println("Status sewa dengan ID " + idSewa + " berhasil diperbarui menjadi 'selesai'.");
        }
    }
    
    private boolean isSewaAlreadySelesai(int idSewa, Connection kon) throws SQLException {
        String SQL = "SELECT COUNT(*) FROM selesai WHERE id_sewa = ?";
        try (PreparedStatement pst = kon.prepareStatement(SQL)) {
            pst.setInt(1, idSewa);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
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
        txt_cari = new javax.swing.JTextField();
        combo_sort = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
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
        jLabel12 = new javax.swing.JLabel();
        txt_jumlah_bayar = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txt_tanggal_kembali_seharusnya = new javax.swing.JTextField();
        txt_total_bayar = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel18 = new javax.swing.JLabel();
        txt_jumlah_denda = new javax.swing.JTextField();
        txt_tanggal_peminjaman = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txt_rincian_denda = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txt_jumlah_denda2 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();

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
        table_selesai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_selesaiMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                table_selesaiMouseEntered(evt);
            }
        });
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

        txt_cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_cariActionPerformed(evt);
            }
        });

        combo_sort.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Termurah", "Termahal" }));
        combo_sort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_sortActionPerformed(evt);
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
                .addGap(705, 705, 705)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Penyewa");

        combo_penyewa.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        combo_penyewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_penyewaActionPerformed(evt);
            }
        });

        menit_dikembalikan.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                menit_dikembalikanStateChanged(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Menit");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Jam");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Tanggal");

        tanggal_dikembalikan.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tanggal_dikembalikanPropertyChange(evt);
            }
        });

        jam_dikembalikan.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jam_dikembalikanStateChanged(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Jam Kembali Aktual");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Jumlah Bayar");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setText("Tanggal Kembali Seharusnya");

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel17.setText("Total Bayar");

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel18.setText("Total Denda");

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel19.setText("Tanggal Peminjaman");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setText("Rincian Denda");

        txt_jumlah_denda2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_jumlah_denda2KeyReleased(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setText("Jumlah Denda");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel15.setText("(Optional)");

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel20.setText("(Optional)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator2)
            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel19)
                        .addGap(18, 18, 18)
                        .addComponent(txt_tanggal_peminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(jLabel16)
                        .addGap(18, 18, 18)
                        .addComponent(txt_tanggal_kembali_seharusnya, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(75, 75, 75)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_jumlah_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_total_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_cari)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_tampil, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(combo_sort, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(620, 620, 620)
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
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txt_jumlah_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(61, 61, 61)
                                        .addComponent(combo_penyewa, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(146, 146, 146)
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
                                    .addComponent(jLabel11)
                                    .addComponent(menit_dikembalikan, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel14)
                                                .addComponent(jLabel15))
                                            .addGap(273, 273, 273))
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(134, 134, 134)
                                            .addComponent(txt_jumlah_denda2, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel13)
                                            .addComponent(jLabel20))
                                        .addGap(18, 18, 18)
                                        .addComponent(txt_rincian_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap())
            .addComponent(jSeparator1)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_batal, btn_hapus, btn_simpan, btn_tambah, btn_ubah});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_tanggal_peminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_tanggal_kembali_seharusnya, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jumlah_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_total_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel15))
                                    .addComponent(txt_jumlah_denda2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(combo_penyewa, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 32, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_jumlah_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel12))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(txt_rincian_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel5)
                                        .addComponent(jLabel11))
                                    .addGap(3, 3, 3)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(menit_dikembalikan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jam_dikembalikan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(tanggal_dikembalikan, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cari)
                    .addComponent(btn_tampil)
                    .addComponent(combo_sort, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan)
                    .addComponent(btn_ubah)
                    .addComponent(btn_hapus)
                    .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_batal))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_cari, btn_tampil, combo_sort, jLabel10, jLabel8, txt_cari});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_batal, btn_hapus, btn_simpan, btn_tambah, btn_ubah});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {combo_penyewa, jLabel2});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jam_dikembalikan, menit_dikembalikan, tanggal_dikembalikan});

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
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diubah terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil ID Selesai dari baris yang dipilih di tabel (Kolom "ID")
        String idSelesaiToUpdate = tableMode1.getValueAt(row, 0).toString();

        // --- AMBIL ID SEWA LANGSUNG DARI TABEL (BUKAN DARI COMBO BOX) ---
        int idSewa = (Integer) tableMode1.getValueAt(row, 1); 
        // --- END AMBIL ID SEWA ---

        // Ambil Tanggal Kembali Aktual
        Date tglKembaliActualDate = tanggal_dikembalikan.getDate();
        if (tglKembaliActualDate == null) {
            JOptionPane.showMessageDialog(this, "Tanggal kembali aktual tidak boleh kosong.", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Calendar calAktual = Calendar.getInstance();
        calAktual.setTime(tglKembaliActualDate);
        calAktual.set(Calendar.HOUR_OF_DAY, (Integer) jam_dikembalikan.getValue());
        calAktual.set(Calendar.MINUTE, (Integer) menit_dikembalikan.getValue());
        calAktual.set(Calendar.SECOND, 0);
        calAktual.set(Calendar.MILLISECOND, 0);
        Timestamp tanggalKembaliAktual = new Timestamp(calAktual.getTimeInMillis());

        // Ambil nilai denda, biaya, dan status pembayaran
        double totalDenda = 0.0;
        double jumlahSudahDibayar = 0.0;
        double totalBiaya = 0.0;
        String rincianDenda = txt_rincian_denda.getText().trim(); // Ambil dari field yang digenerate
        String statusPembayaran = ""; 
        long jumlahJamTelat = 0; // Variabel baru untuk disimpan

        try {
            totalDenda = Double.parseDouble(txt_jumlah_denda.getText().replace(",", ".")); 
            jumlahSudahDibayar = Double.parseDouble(txt_jumlah_bayar.getText().replace(",", "."));
            totalBiaya = Double.parseDouble(txt_total_bayar.getText().replace(",", "."));

            // Logika untuk menentukan status pembayaran
            if (jumlahSudahDibayar >= totalBiaya) {
                statusPembayaran = "Lunas";
            } else if (jumlahSudahDibayar > 0 && jumlahSudahDibayar < totalBiaya) {
                statusPembayaran = "Belum Lunas";
            } else {
                statusPembayaran = "Belum Bayar";
            }
            
            // Hitung ulang jumlah_jam_telat untuk disimpan
            // Anda perlu mendapatkan tanggalTargetKembali dari suatu tempat.
            // Pilihan terbaik adalah dari sewaTargetKembaliMap menggunakan idSewa,
            // atau jika tidak ada di map (karena sewa sudah selesai), Anda bisa query DB.
            Timestamp tanggalTargetKembali = sewaTargetKembaliMap.get(idSewa); 

            if (tglKembaliActualDate != null && tanggalTargetKembali != null) {
                Calendar targetCal = Calendar.getInstance();
                targetCal.setTimeInMillis(tanggalTargetKembali.getTime());

                Calendar currentActualCal = Calendar.getInstance();
                currentActualCal.setTime(tglKembaliActualDate);
                currentActualCal.set(Calendar.HOUR_OF_DAY, (Integer) jam_dikembalikan.getValue());
                currentActualCal.set(Calendar.MINUTE, (Integer) menit_dikembalikan.getValue());
                currentActualCal.set(Calendar.SECOND, 0);
                currentActualCal.set(Calendar.MILLISECOND, 0);

                if (currentActualCal.compareTo(targetCal) > 0) {
                    long diffMillis = currentActualCal.getTimeInMillis() - targetCal.getTimeInMillis();
                    jumlahJamTelat = (long) Math.ceil((double)diffMillis / (1000.0 * 60 * 60));
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format angka untuk denda, jumlah dibayar, atau total biaya tidak valid.", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection kon = null;
        PreparedStatement pst = null;

        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);

            String SQL = "UPDATE selesai SET "
                    + "id_sewa = ?, " // Ini akan menggunakan idSewa yang diambil langsung dari tabel
                    + "tanggal_kembali_aktual = ?, "
                    + "total_denda = ?, "
                    + "rincian_denda = ?, "
                    + "total_biaya = ?, "
                    + "jumlah_sudah_dibayar = ?, "
                    + "status_pembayaran = ?, "
                    + "jumlah_jam_telat = ? " 
                    + "WHERE id_selesai = ?";

            pst = kon.prepareStatement(SQL);
            pst.setInt(1, idSewa); // Menggunakan idSewa dari tabel
            pst.setTimestamp(2, tanggalKembaliAktual);
            pst.setDouble(3, totalDenda);
            pst.setString(4, rincianDenda); 
            pst.setDouble(5, totalBiaya);
            pst.setDouble(6, jumlahSudahDibayar);
            pst.setString(7, statusPembayaran);
            pst.setLong(8, jumlahJamTelat); 
            pst.setString(9, idSelesaiToUpdate); // Kondisi WHERE

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Data selesai berhasil diubah!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                settableload(); // Muat ulang tabel untuk menampilkan perubahan
                membersihkan_teks(); // Bersihkan field setelah berhasil ubah
                nonaktif_teks(); // Nonaktifkan field
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah data selesai. Data tidak ditemukan atau tidak ada perubahan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }

        }catch (Exception ex)
            {
                System.err.println(ex.getMessage());
            }
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

        // Validasi: Pastikan sewa aktif telah dipilih (bukan item default atau invalid)
        if (idSewa <= 0) {
            JOptionPane.showMessageDialog(this, "Pilih data sewa aktif yang ingin diselesaikan.", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date tglKembaliActualDate = tanggal_dikembalikan.getDate();
        if (tglKembaliActualDate == null) {
            JOptionPane.showMessageDialog(this, "Tanggal kembali aktual tidak boleh kosong.", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Calendar calAktual = Calendar.getInstance();
        calAktual.setTime(tglKembaliActualDate);
        calAktual.set(Calendar.HOUR_OF_DAY, (Integer) jam_dikembalikan.getValue());
        calAktual.set(Calendar.MINUTE, (Integer) menit_dikembalikan.getValue());
        calAktual.set(Calendar.SECOND, 0);
        calAktual.set(Calendar.MILLISECOND, 0);
        Timestamp tanggalKembaliAktual = new Timestamp(calAktual.getTimeInMillis());

        double totalDenda = 0.0;
        double jumlahSudahDibayar = 0.0;
        double totalBiaya = 0.0;
        String rincianDenda = txt_rincian_denda.getText().trim(); // Ambil dari field yang sudah digenerate
        String statusPembayaran = "";
        long jumlahJamTelat = 0; // Variabel baru untuk disimpan

        // Konversi teks angka ke double dan tangani kesalahan format
        try {
            totalDenda = Double.parseDouble(txt_jumlah_denda.getText().replace(",", ".")); 
            jumlahSudahDibayar = Double.parseDouble(txt_jumlah_bayar.getText().replace(",", "."));
            totalBiaya = Double.parseDouble(txt_total_bayar.getText().replace(",", "."));

            // Logika untuk menentukan status pembayaran
            if (jumlahSudahDibayar >= totalBiaya) {
                statusPembayaran = "Lunas";
            } else if (jumlahSudahDibayar > 0 && jumlahSudahDibayar < totalBiaya) {
                statusPembayaran = "Belum Lunas";
            } else { // jumlahSudahDibayar <= 0
                statusPembayaran = "Belum Bayar";
            }
            
            // Hitung ulang jumlah_jam_telat untuk disimpan ke database
            Timestamp tanggalTargetKembali = sewaTargetKembaliMap.get(idSewa);
            if (tglKembaliActualDate != null && tanggalTargetKembali != null) {
                Calendar targetCal = Calendar.getInstance();
                targetCal.setTimeInMillis(tanggalTargetKembali.getTime());

                Calendar currentActualCal = Calendar.getInstance();
                currentActualCal.setTime(tglKembaliActualDate);
                currentActualCal.set(Calendar.HOUR_OF_DAY, (Integer) jam_dikembalikan.getValue());
                currentActualCal.set(Calendar.MINUTE, (Integer) menit_dikembalikan.getValue());
                currentActualCal.set(Calendar.SECOND, 0);
                currentActualCal.set(Calendar.MILLISECOND, 0);

                if (currentActualCal.compareTo(targetCal) > 0) {
                    long diffMillis = currentActualCal.getTimeInMillis() - targetCal.getTimeInMillis();
                    jumlahJamTelat = (long) Math.ceil((double)diffMillis / (1000.0 * 60 * 60));
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format angka untuk denda, jumlah dibayar, atau total biaya tidak valid. Pastikan hanya angka (dan titik desimal) yang dimasukkan.", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Koneksi dan Eksekusi Query INSERT
        Connection kon = null;
        PreparedStatement pst = null;

        try {
            Class.forName(driver); // Pastikan driver database sudah dimuat
            kon = DriverManager.getConnection(database, user, pass);

            // Cek apakah data sewa sudah diselesaikan untuk idSewa ini
            if (isSewaAlreadySelesai(idSewa, kon)) {
                JOptionPane.showMessageDialog(this, "Sewa ini sudah diselesaikan sebelumnya. Gunakan fitur Ubah jika ingin mengupdate data.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return; // Batalkan operasi simpan
            }

            // SQL INSERT statement (TAMBAHKAN 'jumlah_jam_telat')
            String SQL = "INSERT INTO selesai ("
                    + "id_sewa, tanggal_kembali_aktual, total_denda, rincian_denda, "
                    + "total_biaya, jumlah_sudah_dibayar, status_pembayaran, jumlah_jam_telat" // <<< TAMBAHKAN KOLOM BARU
                    + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; // <<< TAMBAHKAN '?' UNTUK KOLOM BARU

            pst = kon.prepareStatement(SQL);
            pst.setInt(1, idSewa);
            pst.setTimestamp(2, tanggalKembaliAktual);
            pst.setDouble(3, totalDenda);
            pst.setString(4, rincianDenda); 
            pst.setDouble(5, totalBiaya);
            pst.setDouble(6, jumlahSudahDibayar);
            pst.setString(7, statusPembayaran);
            pst.setLong(8, jumlahJamTelat); // <<< SET PARAMETER UNTUK KOLOM BARU

            int affectedRows = pst.executeUpdate(); // Jalankan query INSERT

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Data selesai berhasil disimpan!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                
                // Setelah berhasil disimpan ke tabel 'selesai', perbarui status di tabel 'sewa'
                updateSewaStatus(idSewa, kon); // Panggil metode untuk memperbarui status sewa
                
                settableload(); // Muat ulang tabel untuk menampilkan data baru
                membersihkan_teks(); // Bersihkan field form
                nonaktif_teks(); // Nonaktifkan field input
                loadPenyewaToComboBox(); // Muat ulang combo box agar sewa yang baru selesai tidak muncul lagi
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data selesai.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }

        }catch (Exception ex)
        {
            System.err.println(ex.getMessage());
        }
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Pilih data denda yang akan dihapus dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        Connection kon = null;
        Statement stt = null;

        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database,user,pass);
            stt = kon.createStatement();
 
            String SQL = "DELETE FROM selesai " + "WHERE id_selesai = '" + tableMode1.getValueAt(row, 0).toString() + "'";

            stt.executeUpdate(SQL);
            tableMode1.removeRow(row);

            JOptionPane.showMessageDialog(null, "Data denda berhasil dihapus!");
            membersihkan_teks();
  
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

    private void txt_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cariActionPerformed

    private void combo_sortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_sortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_sortActionPerformed

    private void combo_penyewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_penyewaActionPerformed
        // TODO add your handling code here:
        updateCalculatedFields();
    }//GEN-LAST:event_combo_penyewaActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        menu_utama utama = new menu_utama();
        utama.setVisible(true);
    }//GEN-LAST:event_formWindowClosed

    private void table_selesaiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_selesaiMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount()== 1)
        {
            tampil_field();
        }
    }//GEN-LAST:event_table_selesaiMouseClicked

    private void table_selesaiMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_selesaiMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_table_selesaiMouseEntered

    private void tanggal_dikembalikanPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tanggal_dikembalikanPropertyChange
        // TODO add your handling code here:
        if ("date".equals(evt.getPropertyName())) {
            updateCalculatedFields();
        }
    }//GEN-LAST:event_tanggal_dikembalikanPropertyChange

    private void jam_dikembalikanStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jam_dikembalikanStateChanged
        // TODO add your handling code here:
        updateCalculatedFields();
    }//GEN-LAST:event_jam_dikembalikanStateChanged

    private void menit_dikembalikanStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_menit_dikembalikanStateChanged
        // TODO add your handling code here:
        updateCalculatedFields();
    }//GEN-LAST:event_menit_dikembalikanStateChanged

    private void txt_jumlah_denda2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jumlah_denda2KeyReleased
        // TODO add your handling code here:
        updateCalculatedFields();
    }//GEN-LAST:event_txt_jumlah_denda2KeyReleased

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

    private HashMap<String, Integer> sewaAktifMap;
    private HashMap<Integer, Double> sewaBiayaAwalMap;
    private HashMap<Integer, Timestamp> sewaTargetKembaliMap;
    private HashMap<Integer, Timestamp> sewaPeminjamanMap;
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
    private javax.swing.JComboBox combo_penyewa;
    private javax.swing.JComboBox combo_sort;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
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
    private javax.swing.JTextField txt_cari;
    private javax.swing.JTextField txt_jumlah_bayar;
    private javax.swing.JTextField txt_jumlah_denda;
    private javax.swing.JTextField txt_jumlah_denda2;
    private javax.swing.JTextField txt_rincian_denda;
    private javax.swing.JTextField txt_tanggal_kembali_seharusnya;
    private javax.swing.JTextField txt_tanggal_peminjaman;
    private javax.swing.JTextField txt_total_bayar;
    // End of variables declaration//GEN-END:variables
}
