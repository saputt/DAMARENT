/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package damarent;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;


/**
 *
 * @author sauki
 */
public class Damarent {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FlatMacDarkLaf.setup();

        menu_utama utama = new menu_utama();
        utama.setVisible(true);
    }
    
}
