/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package serveurpop3;

/**
 *
 * @author Epulapp
 */
public class Mail {
    
    private String header = null;
    private String corps = null;
    private int taille = 0;

    public Mail(String h, String c){
        this.header = h;
        this.corps = c;
        this.taille = h.length() + c.length();
    }
    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the corps
     */
    public String getCorps() {
        return corps;
    }
    
    public String getMessage(){
        return getHeader() + "\r\n\r\n" + getCorps();
    }

    /**
     * @param corps the corps to set
     */
    public void setCorps(String corps) {
        this.corps = corps;
    }

    /**
     * @return the taille
     */
    public int getTaille() {
        return taille;
    }

    /**
     * @param taille the taille to set
     */
    public void setTaille(int taille) {
        this.taille = taille;
    }
    
}