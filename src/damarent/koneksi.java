/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package damarent;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author sauki
 */
public class koneksi {
    public Properties mypanel, myLanguage;
    private String strNamePanel;
    public koneksi() {
    
    }
    public String SettingPanel(String nmPanel){
       try {
            mypanel = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("database.ini");

            if (input == null) {
                throw new RuntimeException("File database.ini TIDAK DITEMUKAN di dalam paket aplikasi (classpath). " +
                                           "Pastikan ada di src/resources/ di proyek NetBeans Anda.");
            }
            
            mypanel.load(input); // Memuat properti dari InputStream
            input.close(); 

            strNamePanel = mypanel.getProperty(nmPanel);
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error membaca konfigurasi database:\n" + e.getMessage() +
                    "\nMohon hubungi pengembang.",
                    "Error Konfigurasi Aplikasi",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println(e.getMessage());
            System.exit(0); 
        }
        return strNamePanel;
    }  
}
