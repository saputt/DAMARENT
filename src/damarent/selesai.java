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

        sewaAktifMap = new HashMap<>();
        sewaBiayaAwalMap = new HashMap<>();
        sewaTargetKembaliMap = new HashMap<>();
        sewaPeminjamanMap = new HashMap<>();
        sewaHargaMotorMap = new HashMap<>();

        SpinnerModel hourModel = new SpinnerNumberModel(0, 0, 23, 1);
        jam_dikembalikan.setModel(hourModel);

        SpinnerModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1); 
        menit_dikembalikan.setModel(minuteModel);

        txt_tanggal_kembali_seharusnya.setEditable(false);
        txt_tanggal_peminjaman.setEditable(false);
        txt_total_bayar.setEditable(false);
        txt_jumlah_denda.setEditable(false);

        loadPenyewaToComboBox(); 

        table_selesai.setModel(tableMode1); 
        settableload(); 

        tanggal_dikembalikan.setDate(new Date()); 

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
                "ID sls",                  
                "ID Sewa",             
                "Nama",      
                "Plat",          
                "Tgl Pmnjmn",      
                "Tgl Kembali",  
                "Tgl Kembali Aktual",  
                "Ttl Denda",           
                "Rncian Denda",      
                "Jml Jam Telat",       
                "Ttl Biaya",          
                "Jml Dibayar",        
                "Stt Bayar"           
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
                        + "    sl.id_selesai, sl.id_sewa, p.nama_pelanggan, m.plat_nomor, " 
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
                data[1] = res.getInt("id_sewa");
                data[2] = res.getString("nama_pelanggan");
                data[3] = res.getString("plat_nomor");
                data[4] = res.getTimestamp("tanggal_peminjaman");
                data[5] = res.getTimestamp("tanggal_kembali");
                data[6] = res.getTimestamp("tanggal_kembali_aktual");
                data[7] = res.getDouble("total_denda");       
                data[8] = res.getString("rincian_denda");     
                data[9] = res.getInt("jumlah_jam_telat");     
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
                String idSelesaiFromTable = tableMode1.getValueAt(row, 0).toString();
                int idSewaFromTable = (Integer) tableMode1.getValueAt(row, 1);      
                String namaPelangganFromTable = tableMode1.getValueAt(row, 2).toString(); 
                String platNomorFromTable = tableMode1.getValueAt(row, 3).toString(); 
                Timestamp tglPeminjamanTs = (Timestamp) tableMode1.getValueAt(row, 4); 
                Timestamp tglTargetKembaliTs = (Timestamp) tableMode1.getValueAt(row, 5); 
                Timestamp tglKembaliAktualTs = (Timestamp) tableMode1.getValueAt(row, 6);

                double totalDendaTable = (Double) tableMode1.getValueAt(row, 7);       
                String rincianDendaTable = tableMode1.getValueAt(row, 8).toString();   
                int jumlahJamTelatTable = (Integer) tableMode1.getValueAt(row, 9);     
                double totalBiayaTable = (Double) tableMode1.getValueAt(row, 10);      
                double jumlahSudahDibayar = (Double) tableMode1.getValueAt(row, 11);  
                String statusPembayaranTable = tableMode1.getValueAt(row, 12).toString(); 

                String displayStringForComboBox = null;
                for (Map.Entry<String, Integer> entry : sewaAktifMap.entrySet()) {
                    if (entry.getValue().equals(idSewaFromTable)) {
                        displayStringForComboBox = entry.getKey();
                        break;
                    }
                }
                if (displayStringForComboBox != null) {
                    combo_penyewa.setSelectedItem(displayStringForComboBox);
                } else {
                    String tempDisplayString = idSewaFromTable + " - " + namaPelangganFromTable + " (" + platNomorFromTable + ")";
                    DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) combo_penyewa.getModel();
                    if(model.getIndexOf(tempDisplayString) == -1) { // Hanya tambahkan jika belum ada
                         model.addElement(tempDisplayString);
                    }
                    combo_penyewa.setSelectedItem(tempDisplayString);
                }

                txt_jumlah_bayar.setText(String.format("%.2f", jumlahSudahDibayar));
                txt_jumlah_denda2.setText(String.format("%.2f", totalDendaTable)); 
                txt_rincian_denda.setText(rincianDendaTable); 

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
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
                updateCalculatedFields();
                
                btn_simpan.setEnabled(false); 
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
            btn_simpan.setEnabled(true); 
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
                         "    m.harga_sewa, " +
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
                Double hargaMotorharian = res.getDouble("harga_sewa");
                double biayaSewaAwal = res.getDouble("harga_sewa_awal");
                Timestamp tanggalTargetKembali = res.getTimestamp("tanggal_kembali");
                Timestamp tanggalPeminjaman = res.getTimestamp("tanggal_peminjaman");

                String displayString = idSewa + " - " + namaPelanggan + " (" + platNomor + ")";
                model.addElement(displayString);
                sewaAktifMap.put(displayString, idSewa);
                sewaBiayaAwalMap.put(idSewa, biayaSewaAwal);
                sewaTargetKembaliMap.put(idSewa, tanggalTargetKembali);
                sewaPeminjamanMap.put(idSewa, tanggalPeminjaman);
                sewaHargaMotorMap.put(idSewa, hargaMotorharian);
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

        if (idSewa <= 0) {
            resetCalculatedFieldsExcludingManualDenda();
            return;
        }

        if (idSewa > 0) {
            Timestamp tanggalTargetKembali = sewaTargetKembaliMap.get(idSewa);
            Timestamp tanggalPeminjaman = sewaPeminjamanMap.get(idSewa);
            double biayaSewaAwal = sewaBiayaAwalMap.get(idSewa);
            double hargaMotorHarian = sewaHargaMotorMap.get(idSewa); 

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

            try {
                String denda2Text = txt_jumlah_denda2.getText();
                if (!denda2Text.isEmpty()) {
                    dendaKerusakan = Double.parseDouble(denda2Text);
                }
            } catch (NumberFormatException ex) {
                dendaKerusakan = 0.0;
                System.err.println("Invalid number format in txt_jumlah_denda2: " + ex.getMessage());
            }

            if (tglKembaliActualDate != null && tanggalTargetKembali != null) {
                dendaTelat = hitungDendaTelat(tanggalTargetKembali, tglKembaliActualDate, jamAktual, menitAktual, hargaMotorHarian);
            }

            double totalDendaYangDitampilkan = dendaTelat + dendaKerusakan;
            double totalBiayaYangHarusDibayar = biayaSewaAwal + totalDendaYangDitampilkan;

            txt_jumlah_denda.setText(String.format("%.2f", totalDendaYangDitampilkan));
            txt_total_bayar.setText(String.format("%.2f", totalBiayaYangHarusDibayar));

        } else {
            txt_total_bayar.setText("0.0");
            txt_jumlah_denda.setText("0.0"); 
            txt_tanggal_kembali_seharusnya.setText("");
            txt_tanggal_peminjaman.setText("");
            txt_rincian_denda.setText("");
            txt_jumlah_denda2.setText("");
        }
    }

    private void resetCalculatedFieldsExcludingManualDenda() {
        if (txt_total_bayar != null) txt_total_bayar.setText("0.00");
        if (txt_jumlah_denda != null) txt_jumlah_denda.setText("0.00");
        if (txt_tanggal_kembali_seharusnya != null) txt_tanggal_kembali_seharusnya.setText("");
        if (txt_tanggal_peminjaman != null) txt_tanggal_peminjaman.setText("");
        if (txt_rincian_denda != null) txt_rincian_denda.setText(""); 
    }
     
    private double hitungDendaTelat(Timestamp tanggalTargetKembali, Date tanggalAktual, int jamAktual, int menitAktual, double dendaHarian) {
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

        long diffMillis = calAktual.getTimeInMillis() - calTarget.getTimeInMillis();
        long diffHours = (long) Math.ceil((double) diffMillis / (1000.0 * 60 * 60));

        double denda = 0.0;
        double dendaPerJam = 10000.0;

        if (diffHours > 4) {
            long jumlahHariTelat = (long) Math.ceil(diffHours / 24.0);
            denda = jumlahHariTelat * dendaHarian;
        } else {
            denda = diffHours * dendaPerJam;
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
        String orderByColumn = "sl.tanggal_kembali_aktual"; 
        if ("Termurah".equals(sort)) {
            sortOrder = "ASC";
            orderByColumn = "sl.total_biaya"; 
        } else if ("Termahal".equals(sort)) {
            sortOrder = "DESC";
            orderByColumn = "sl.total_biaya"; 
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
                        "ORDER BY " + orderByColumn + " " + sortOrder)) 
            {
                while(res.next()) {
                    Object[] data = new Object[13]; 
                    data[0] = res.getString("id_selesai");
                    data[1] = res.getInt("id_sewa");
                    data[2] = res.getString("nama_pelanggan");
                    data[3] = res.getString("plat_nomor");
                    data[4] = res.getTimestamp("tanggal_peminjaman"); 
                    data[5] = res.getTimestamp("tanggal_kembali"); 
                    data[6] = res.getTimestamp("tanggal_kembali_aktual"); 
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
            pst.setInt(1, idSewa); 
            pst.executeUpdate();
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
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel3 = new javax.swing.JLabel();
        btn_cari = new javax.swing.JButton();
        btn_tampil = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
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
        combo_kategori = new javax.swing.JComboBox();
        jSeparator3 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_selesai = new javax.swing.JTable();

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

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel1.setText("Tabel Selesai");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = -16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 594, 9, 569);
        jPanel1.add(jLabel1, gridBagConstraints);

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
        jLabel15.setText("Tambahan");

        combo_kategori.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nama Pelanggan", "Plat Nomor", "Status Pembayaran" }));
        combo_kategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_kategoriActionPerformed(evt);
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

        jScrollPane2.setViewportView(jScrollPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator1)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1583, Short.MAX_VALUE)
            .addComponent(jSeparator3)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_cari, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combo_kategori, 0, 113, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_cari, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                        .addGap(11, 11, 11)
                        .addComponent(btn_tampil, javax.swing.GroupLayout.PREFERRED_SIZE, 117, Short.MAX_VALUE)
                        .addGap(698, 698, 698)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combo_sort, 0, 199, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(combo_penyewa, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_jumlah_bayar, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE))
                        .addGap(111, 111, 111)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tanggal_dikembalikan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(147, 147, 147)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jam_dikembalikan)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(49, 49, 49)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(38, 38, 38))
                            .addComponent(menit_dikembalikan))
                        .addGap(130, 130, 130)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                                .addGap(21, 21, 21)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_jumlah_denda2, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                            .addComponent(txt_rincian_denda)))
                    .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_tanggal_peminjaman, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                                .addGap(49, 49, 49)
                                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txt_tanggal_kembali_seharusnya, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                                .addGap(75, 75, 75)
                                .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txt_jumlah_denda, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                                .addGap(40, 40, 40)
                                .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txt_total_bayar, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(343, 343, 343)
                                .addComponent(btn_tambah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_simpan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_ubah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_hapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_batal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(355, 355, 355)))
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txt_tanggal_peminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_jumlah_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_total_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_tanggal_kembali_seharusnya, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(combo_penyewa, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(59, 59, 59)
                                .addComponent(txt_jumlah_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(8, 8, 8))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txt_jumlah_denda2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel2))
                                        .addGap(15, 15, 15)
                                        .addComponent(txt_rincian_denda, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(65, 65, 65)
                                .addComponent(jLabel12))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel11))
                                        .addGap(3, 3, 3)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(tanggal_dikembalikan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jam_dikembalikan)
                                            .addComponent(menit_dikembalikan, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(combo_sort, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(combo_kategori, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_cari)))
                    .addComponent(btn_tampil, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_simpan)
                        .addComponent(btn_ubah)
                        .addComponent(btn_hapus)
                        .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btn_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_cari, btn_tampil, combo_kategori, combo_sort, jLabel10, jLabel8, txt_cari});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_batal, btn_hapus, btn_simpan, btn_tambah, btn_ubah});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {combo_penyewa, jLabel12, jLabel2, txt_jumlah_bayar});

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            case "Nama Pelanggan":
                searchColumn = "p.nama_pelanggan";
                break;
            case "Plat Nomor":
                searchColumn = "m.plat_nomor";
                break;
            case "Status Pembayaran":
                searchColumn = "sl.status_pembayaran";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Kategori pencarian tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
        String sql = "SELECT " +
                     "    sl.id_selesai, sl.id_sewa, p.nama_pelanggan, m.plat_nomor, " +
                     "    s.tanggal_peminjaman, s.tanggal_kembali, sl.tanggal_kembali_aktual, " +
                     "    sl.total_denda, sl.rincian_denda, sl.total_biaya, sl.jumlah_sudah_dibayar, " +
                     "    sl.status_pembayaran, sl.jumlah_jam_telat " +
                     "FROM selesai sl " +
                     "JOIN sewa s ON sl.id_sewa = s.id_sewa " +
                     "JOIN pelanggan p ON s.id_pelanggan = p.id_pelanggan " +
                     "JOIN motor m ON s.id_motor = m.id_motor " +
                     "WHERE " + searchColumn + " LIKE ?"; 

        try (Connection kon = DriverManager.getConnection(database, user, pass);
             PreparedStatement pst = kon.prepareStatement(sql)) {

            pst.setString(1, "%" + keyword + "%");

            try (ResultSet res = pst.executeQuery()) {
                boolean dataDitemukan = false;
                while (res.next()) {
                    dataDitemukan = true;
                    Object[] data = new Object[13];
                    data[0] = res.getString("id_selesai");
                    data[1] = res.getInt("id_sewa");
                    data[2] = res.getString("nama_pelanggan");
                    data[3] = res.getString("plat_nomor");
                    data[4] = res.getTimestamp("tanggal_peminjaman");
                    data[5] = res.getTimestamp("tanggal_kembali");
                    data[6] = res.getTimestamp("tanggal_kembali_aktual");
                    data[7] = res.getDouble("total_denda");
                    data[8] = res.getString("rincian_denda");
                    data[9] = res.getInt("jumlah_jam_telat");
                    data[10] = res.getDouble("total_biaya");
                    data[11] = res.getDouble("jumlah_sudah_dibayar");
                    data[12] = res.getString("status_pembayaran");
                    tableMode1.addRow(data);
                }

                if (!dataDitemukan) {
                    JOptionPane.showMessageDialog(this, "Data tidak ditemukan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (Exception ex) {
            System.err.println("Error saat melakukan pencarian: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btn_cariActionPerformed

    private void btn_tampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampilActionPerformed
        // TODO add your handling code here:
        settableload();
    }//GEN-LAST:event_btn_tampilActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        // TODO add your handling code here:
        aktifkan_teks();
        btn_simpan.setEnabled(true);
        btn_ubah.setEnabled(false);
        btn_hapus.setEnabled(false);
        btn_batal.setEnabled(true);
        table_selesai.clearSelection();
        row = -1; 
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_ubahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ubahActionPerformed
        // TODO add your handling code here:
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diubah terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idSelesaiToUpdate = tableMode1.getValueAt(row, 0).toString();

        int idSewa = (Integer) tableMode1.getValueAt(row, 1); 

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
        String rincianDenda = txt_rincian_denda.getText().trim(); 
        String statusPembayaran = ""; 
        long jumlahJamTelat = 0; 

        try {
            totalDenda = Double.parseDouble(txt_jumlah_denda.getText().replace(",", ".")); 
            jumlahSudahDibayar = Double.parseDouble(txt_jumlah_bayar.getText().replace(",", "."));
            totalBiaya = Double.parseDouble(txt_total_bayar.getText().replace(",", "."));

            if (jumlahSudahDibayar >= totalBiaya) {
                statusPembayaran = "Lunas";
            } else if (jumlahSudahDibayar > 0 && jumlahSudahDibayar < totalBiaya) {
                statusPembayaran = "Belum Lunas";
            } else {
                statusPembayaran = "Belum Bayar";
            }
            
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
                    + "id_sewa = ?, " 
                    + "tanggal_kembali_aktual = ?, "
                    + "total_denda = ?, "
                    + "rincian_denda = ?, "
                    + "total_biaya = ?, "
                    + "jumlah_sudah_dibayar = ?, "
                    + "status_pembayaran = ?, "
                    + "jumlah_jam_telat = ? " 
                    + "WHERE id_selesai = ?";

            pst = kon.prepareStatement(SQL);
            pst.setInt(1, idSewa);
            pst.setTimestamp(2, tanggalKembaliAktual);
            pst.setDouble(3, totalDenda);
            pst.setString(4, rincianDenda); 
            pst.setDouble(5, totalBiaya);
            pst.setDouble(6, jumlahSudahDibayar);
            pst.setString(7, statusPembayaran);
            pst.setLong(8, jumlahJamTelat); 
            pst.setString(9, idSelesaiToUpdate); 

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Data selesai berhasil diubah!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                settableload(); 
                membersihkan_teks(); 
                nonaktif_teks(); 
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
        String jumlahBayar = txt_jumlah_bayar.getText();
        String jumlahDendaTambahan = txt_jumlah_denda2.getText();
        Boolean lanjut = false;
        if (!jumlahBayar.matches("[0-9]+")) {
            JOptionPane.showMessageDialog(this, "harga hanya boleh berisi angka!", "Error Input", JOptionPane.ERROR_MESSAGE);
            txt_jumlah_bayar.requestFocus(); 
            txt_jumlah_bayar.setText("");
            lanjut = true;
            return; 
        }
        if (!jumlahDendaTambahan.matches("[0-9]+")) {
            JOptionPane.showMessageDialog(this, "harga hanya boleh berisi angka!", "Error Input", JOptionPane.ERROR_MESSAGE);
            txt_jumlah_denda2.requestFocus(); 
            txt_jumlah_denda2.setText("");
            lanjut = true;
            return; 
        }
        
        if(lanjut) {
            String selectedDisplayString = (String) combo_penyewa.getSelectedItem();
            int idSewa = 0;
            if (selectedDisplayString != null && sewaAktifMap.containsKey(selectedDisplayString)) {
                idSewa = sewaAktifMap.get(selectedDisplayString);
            }
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
            long jumlahJamTelat = 0; 

            try {
                totalDenda = Double.parseDouble(txt_jumlah_denda.getText().replace(",", ".")); 
                jumlahSudahDibayar = Double.parseDouble(txt_jumlah_bayar.getText().replace(",", "."));
                totalBiaya = Double.parseDouble(txt_total_bayar.getText().replace(",", "."));

                if (jumlahSudahDibayar >= totalBiaya) {
                    statusPembayaran = "Lunas";
                } else if (jumlahSudahDibayar > 0 && jumlahSudahDibayar < totalBiaya) {
                    statusPembayaran = "Belum Lunas";
                } else { 
                    statusPembayaran = "Belum Bayar";
                }

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

            Connection kon = null;
            PreparedStatement pst = null;

            try {
                Class.forName(driver);
                kon = DriverManager.getConnection(database, user, pass);

                if (isSewaAlreadySelesai(idSewa, kon)) {
                    JOptionPane.showMessageDialog(this, "Sewa ini sudah diselesaikan sebelumnya. Gunakan fitur Ubah jika ingin mengupdate data.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return; 
                }

                String SQL = "INSERT INTO selesai ("
                        + "id_sewa, tanggal_kembali_aktual, total_denda, rincian_denda, "
                        + "total_biaya, jumlah_sudah_dibayar, status_pembayaran, jumlah_jam_telat" 
                        + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                pst = kon.prepareStatement(SQL);
                pst.setInt(1, idSewa);
                pst.setTimestamp(2, tanggalKembaliAktual);
                pst.setDouble(3, totalDenda);
                pst.setString(4, rincianDenda); 
                pst.setDouble(5, totalBiaya);
                pst.setDouble(6, jumlahSudahDibayar);
                pst.setString(7, statusPembayaran);
                pst.setLong(8, jumlahJamTelat);

                int affectedRows = pst.executeUpdate(); 

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Data selesai berhasil disimpan!", "Informasi", JOptionPane.INFORMATION_MESSAGE);

                    updateSewaStatus(idSewa, kon);

                    settableload(); 
                    membersihkan_teks(); 
                    nonaktif_teks(); 
                    loadPenyewaToComboBox(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan data selesai.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }

            }catch (Exception ex)
            {
                System.err.println(ex.getMessage());
            }
        }
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Pilih data denda yang akan dihapus dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        Connection kon = null;
        Statement stt = null;
        if (confirm == JOptionPane.YES_OPTION) {
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
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void txt_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cariActionPerformed

    private void combo_sortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_sortActionPerformed
        // TODO add your handling code here:
        sorting();
    }//GEN-LAST:event_combo_sortActionPerformed

    private void combo_penyewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_penyewaActionPerformed
        // TODO add your handling code here:
        updateCalculatedFields();
    }//GEN-LAST:event_combo_penyewaActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
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

    private void combo_kategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_kategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_kategoriActionPerformed

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
    private HashMap<Integer, Double> sewaHargaMotorMap;
    private java.util.Map<String, Integer> penggunaMap;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_batal;
    private javax.swing.JButton btn_cari;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_tampil;
    private javax.swing.JButton btn_ubah;
    private javax.swing.JComboBox combo_kategori;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
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
