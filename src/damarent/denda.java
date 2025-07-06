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

/**
 *
 * @author sauki
 */
public class denda extends javax.swing.JFrame {
    koneksi dbsetting;
    String driver,database,user,pass;
    Object tabel;
    /**
     * Creates new form denda
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
        
        txt_total_denda.setVisible(false);
        label_total_denda.setVisible(false);
        txt_total_telat.setVisible(false);
        label_total_telat.setVisible(false);
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
                "Keterlambatan",
                "Bayar Kerusakan",
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
        tableMode1.setRowCount(0); 
        Connection kon = null;
        Statement stt = null;
        ResultSet res = null;
        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            stt = kon.createStatement();
            String SQL = "SELECT " +
                         "    d.id_denda, d.id_sewa, p.nama_pelanggan, d.jenis_denda, " + // Used aliases for clarity
                         "    COALESCE(d.total_terlambat, 0) AS total_terlambat, " + 
                         "    COALESCE(d.total_kerusakan, 0) AS total_kerusakan, " +
                         "    d.jumlah_denda, d.keterangan_denda " +
                         "FROM denda d " + // Alias denda as 'd'
                         "JOIN sewa s ON d.id_sewa = s.id_sewa " + // Alias sewa as 's'
                         "JOIN pelanggan p ON s.id_pelanggan = p.id_pelanggan " + // Alias pelanggan as 'p'
                         "ORDER BY d.id_denda DESC"; // Use alias in ORDER BY
            res = stt.executeQuery(SQL);
            while (res.next()) {
                Object[] data = new Object[8]; 
                data[0] = res.getString(1);
                data[1] = res.getString(2);
                data[2] = res.getString(3); 
                data[3] = res.getString(4); 
                data[4] = res.getString(5); 
                data[5] = res.getString(6); 
                data[6] = res.getString(7); 
                data[7] = res.getString(8); 
                tableMode1.addRow(data);
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
        txt_total_denda.setText("");
        txt_total_telat.setValue(0); 
        if (combo_penyewa.getItemCount() > 0) combo_penyewa.setSelectedIndex(0);
        if (combo_jenis.getItemCount() > 0) combo_jenis.setSelectedIndex(0);
        combo_jenisActionPerformed(null);
    }
    
    int row = -1; 
    public void tampil_field()
    {
        row = tabel_denda.getSelectedRow();
        if (row >= 0) {
            String idSewaDisplay = tableMode1.getValueAt(row, 1).toString();
            String namaPenyewa = tableMode1.getValueAt(row, 2).toString(); 
            String jenisDenda = tableMode1.getValueAt(row, 3).toString();
            String jumlahDendaTelatStr = tableMode1.getValueAt(row, 4).toString(); 
            String jumlahDendaRusakStr = tableMode1.getValueAt(row, 5).toString(); 
            String keteranganDenda = tableMode1.getValueAt(row, 7).toString(); // Keterangan Denda

            for (int i = 0; i < combo_penyewa.getItemCount(); i++) {
                String itemText = (String) combo_penyewa.getItemAt(i);
                if (itemText.startsWith(idSewaDisplay + " - ")) {
                    combo_penyewa.setSelectedIndex(i);
                    break;
                }
            }

            combo_jenis.setSelectedItem(jenisDenda);

            combo_jenisActionPerformed(null); 

            if (jenisDenda.equals("Terlambat")) {
                try {
                    txt_total_telat.setValue(Integer.parseInt(jumlahDendaTelatStr));
                } catch (NumberFormatException ex) {
                    txt_total_telat.setValue(0); 
                }
                txt_total_denda.setText("");
            } else if (jenisDenda.equals("Kerusakan")) {
                try {
                    txt_total_denda.setText(jumlahDendaRusakStr); 
                } catch (Exception ex) {
                    txt_total_denda.setText("0"); 
                }
                txt_total_telat.setValue(0);
            }

            txt_keterangan.setText(keteranganDenda);

            btn_ubah.setEnabled(true);
            btn_hapus.setEnabled(true);
            btn_simpan.setEnabled(false);
            btn_tambah.setEnabled(true);
        } else {
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
        txt_keterangan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        txt_total_denda = new javax.swing.JTextField();
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
        label_total_denda = new javax.swing.JLabel();
        label_total_telat = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JButton();
        btn_ubah = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        btn_batal = new javax.swing.JButton();
        btn_simpan = new javax.swing.JButton();
        txt_total_telat = new javax.swing.JSpinner();

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

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Keterangan");

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
            .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 1222, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(447, 447, 447)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        btn_cari.setText("Cari");
        btn_cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cariActionPerformed(evt);
            }
        });

        label_total_denda.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        label_total_denda.setText("Total Denda");

        label_total_telat.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        label_total_telat.setText("Total Telat");

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
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
                        .addComponent(btn_sort, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                        .addGap(11, 11, 11))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(combo_penyewa, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(combo_jenis, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_total_denda)
                            .addComponent(label_total_telat))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_total_telat, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_total_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(90, 90, 90)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(250, 250, 250)
                .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_ubah, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 2, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_total_telat)
                                    .addComponent(txt_total_telat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_total_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_denda))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(combo_penyewa, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(combo_jenis, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_ubah, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {combo_jenis, combo_penyewa});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {label_total_denda, label_total_telat, txt_total_denda, txt_total_telat});

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
        // TODO add your handling code here:
        String jenisDenda = (String) combo_jenis.getSelectedItem();
        
        if (jenisDenda == "Terlambat") {
            txt_total_denda.setVisible(false);
            label_total_denda.setVisible(false);
            
            txt_total_telat.setVisible(true);
            label_total_telat.setVisible(true);
        }
        else if (jenisDenda == "Kerusakan") {
            txt_total_telat.setVisible(false);
            label_total_telat.setVisible(false);
            
            txt_total_denda.setVisible(true);
            label_total_denda.setVisible(true);
        }
        else {
            txt_total_telat.setVisible(false);
            label_total_telat.setVisible(false);
            
            txt_total_denda.setVisible(false);
            label_total_denda.setVisible(false);
        }
    }//GEN-LAST:event_combo_jenisActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_ubahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ubahActionPerformed
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Pilih data denda yang akan diubah dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idDendaToUpdate = tableMode1.getValueAt(row, 0).toString();

        String selectedPenyewa = (String) combo_penyewa.getSelectedItem();
        String jenisDenda = (String) combo_jenis.getSelectedItem();
        String keteranganDenda = txt_keterangan.getText();

        double jumlahDendaFinal = 0.0;
        double totalTerlambat = 0.0;
        double totalKerusakan = 0.0;

        if (jenisDenda != null && jenisDenda.equals("Terlambat")) {
            Object telatValue = txt_total_telat.getValue();
            if (telatValue == null || (Integer)telatValue <= 0) {
                JOptionPane.showMessageDialog(null, "Jumlah denda terlambat harus diisi dengan angka lebih dari 0.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            totalTerlambat = ((Integer) telatValue).doubleValue();
            jumlahDendaFinal = totalTerlambat;
            totalKerusakan = 0.0;
        } else if (jenisDenda != null && jenisDenda.equals("Kerusakan")) {
            String jumlahKerusakanStr = txt_total_denda.getText(); // This is a JTextField
            if (jumlahKerusakanStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Jumlah denda kerusakan tidak boleh kosong.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                totalKerusakan = Double.parseDouble(jumlahKerusakanStr);
                if (totalKerusakan <= 0) {
                     JOptionPane.showMessageDialog(null, "Jumlah denda kerusakan harus lebih dari 0.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
                     return;
                }
                jumlahDendaFinal = totalKerusakan; 
                totalTerlambat = 0.0; 
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Jumlah denda kerusakan tidak valid (not a number).", "Error Input", JOptionPane.ERROR_MESSAGE);
                txt_total_denda.requestFocus();
                return;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih jenis denda terlebih dahulu.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer idSewa = penggunaMap.get(selectedPenyewa);
        if (idSewa == null || idSewa == 0) {
            JOptionPane.showMessageDialog(null, "Pilih penyewa yang valid dari daftar.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection kon = null;
        Statement stt = null;

        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            stt = kon.createStatement();

            String SQL = "UPDATE denda SET "
                       + "id_sewa = " + idSewa + ", " 
                       + "jenis_denda = '" + jenisDenda + "', "
                       + "total_terlambat = " + (totalTerlambat > 0 ? totalTerlambat : "NULL") + ", "
                       + "total_kerusakan = " + (totalKerusakan > 0 ? totalKerusakan : "NULL") + ", "
                       + "jumlah_denda = " + jumlahDendaFinal + ", "
                       + "keterangan_denda = '" + keteranganDenda + "' "
                       + "WHERE id_denda = '" + idDendaToUpdate + "'";

            stt.executeUpdate(SQL); 
            JOptionPane.showMessageDialog(null, "Data denda berhasil diubah!");

            String namaPenyewaDiperbarui = "";
            for (java.util.Map.Entry<String, Integer> entry : penggunaMap.entrySet()) {
                if (entry.getValue().equals(idSewa)) {
                    namaPenyewaDiperbarui = entry.getKey().substring(entry.getKey().indexOf(" - ") + 3);
                    break;
                }
            }
            tableMode1.setValueAt(idSewa.toString(), row, 1);
            tableMode1.setValueAt(namaPenyewaDiperbarui, row, 2);
            tableMode1.setValueAt(jenisDenda, row, 3);
            tableMode1.setValueAt((totalTerlambat > 0 ? totalTerlambat : ""), row, 4); 
            tableMode1.setValueAt((totalKerusakan > 0 ? totalKerusakan : ""), row, 5); 
            tableMode1.setValueAt(jumlahDendaFinal, row, 6);
            tableMode1.setValueAt(keteranganDenda, row, 7);

            btn_ubah.setEnabled(false);
            btn_hapus.setEnabled(false);
            btn_simpan.setEnabled(true); 
            btn_tambah.setEnabled(true); 
            
            membersihkan_teks();
            
        }
        catch (Exception ex)
        {
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
        String selectedPenyewa = (String) combo_penyewa.getSelectedItem();
        String jenisDenda = (String) combo_jenis.getSelectedItem();
        String keteranganDenda = txt_keterangan.getText();
        
        double jumlahDendaFinal = 0.0;
        int totalTerlambat = 0;
        double totalKerusakan = 0.0;

        if (selectedPenyewa == null || selectedPenyewa.equals("Pilih Penyewa") || 
            jenisDenda == null || jenisDenda.equals("Jenis Denda") || 
            keteranganDenda.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Semua field wajib diisi!", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (jenisDenda.equals("Terlambat")) {
            Object telatValue = txt_total_telat.getValue();
            if (telatValue == null || !(telatValue instanceof Integer) || ((Integer)telatValue) <= 0) {
                JOptionPane.showMessageDialog(null, "Jumlah denda terlambat harus diisi dengan angka bulat lebih dari 0.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            totalTerlambat = ((Integer) telatValue);
            jumlahDendaFinal = totalTerlambat; 
            totalKerusakan = 0.0; 
        } else if (jenisDenda.equals("Kerusakan")) {
            String kerusakanStr = txt_total_denda.getText();
            if (kerusakanStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Jumlah denda kerusakan tidak boleh kosong.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                totalKerusakan = Double.parseDouble(kerusakanStr);
                if (totalKerusakan <= 0) {
                     JOptionPane.showMessageDialog(null, "Jumlah denda kerusakan harus lebih dari 0.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
                     return;
                }
                jumlahDendaFinal = totalKerusakan;
                totalTerlambat = 0; 
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Jumlah denda kerusakan tidak valid (bukan angka).", "Error Input", JOptionPane.ERROR_MESSAGE);
                txt_total_denda.requestFocus();
                return;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih jenis denda yang valid.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!penggunaMap.containsKey(selectedPenyewa)) {
            JOptionPane.showMessageDialog(null, "Penyewa tidak ditemukan dalam daftar. Pilih penyewa dari combo box yang tersedia.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Integer idSewa = penggunaMap.get(selectedPenyewa);
        if (idSewa == null || idSewa == 0) {
            JOptionPane.showMessageDialog(null, "ID Sewa tidak valid. Silahkan pilih penyewa yang benar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection kon = null;
        Statement stt = null;
        ResultSet rs = null;

        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            stt = kon.createStatement();

            String SQL = "INSERT INTO denda (id_sewa, jenis_denda, total_terlambat, total_kerusakan, jumlah_denda, keterangan_denda) " +
                         "VALUES (" + idSewa + ", " + 
                         "'" + jenisDenda + "', " +
                         totalTerlambat + ", " +    
                         totalKerusakan + ", " +     
                         jumlahDendaFinal + ", " +  
                         "'" + keteranganDenda + "')";

            stt.executeUpdate(SQL, Statement.RETURN_GENERATED_KEYS);
            rs = stt.getGeneratedKeys();

            String generatedIdDenda = null;
            if (rs.next()) {
                generatedIdDenda = rs.getString(1); 
            }

            JOptionPane.showMessageDialog(null, "Data denda berhasil disimpan!");

            String namaPelangganTersimpan = "";
            if (idSewa != 0) { 
                 for (java.util.Map.Entry<String, Integer> entry : penggunaMap.entrySet()) {
                    if (entry.getValue().equals(idSewa)) {
                        int dashIndex = entry.getKey().indexOf(" - ");
                        if (dashIndex != -1) {
                            namaPelangganTersimpan = entry.getKey().substring(dashIndex + 3);
                        } else {
                             namaPelangganTersimpan = entry.getKey();
                        }
                        break;
                    }
                }
            }


            Object[] newRowData = new Object[8];
            newRowData[0] = generatedIdDenda;
            newRowData[1] = idSewa; 
            newRowData[2] = namaPelangganTersimpan; 
            newRowData[3] = jenisDenda;
            newRowData[4] = (totalTerlambat > 0 ? totalTerlambat : ""); 
            newRowData[5] = (totalKerusakan > 0 ? totalKerusakan : ""); 
            newRowData[6] = jumlahDendaFinal;
            newRowData[7] = keteranganDenda;
            
            tableMode1.addRow(newRowData);

            int lastRowIndex = tableMode1.getRowCount() - 1;
            if (lastRowIndex >= 0) {
                tabel_denda.setRowSelectionInterval(lastRowIndex, lastRowIndex);
                tabel_denda.scrollRectToVisible(tabel_denda.getCellRect(lastRowIndex, 0, true));
            }
            
            membersihkan_teks();
            btn_simpan.setEnabled(true); 
            btn_ubah.setEnabled(false);   
            btn_hapus.setEnabled(false);
            
        }
        catch (Exception ex)
        {
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
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JLabel label_total_denda;
    private javax.swing.JLabel label_total_telat;
    private javax.swing.JTable tabel_denda;
    private javax.swing.JTextField txt_cari;
    private javax.swing.JTextField txt_keterangan;
    private javax.swing.JTextField txt_total_denda;
    private javax.swing.JSpinner txt_total_telat;
    // End of variables declaration//GEN-END:variables
}
