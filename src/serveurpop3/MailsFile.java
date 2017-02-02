/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurpop3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Epulapp
 */
public class MailsFile {

    private ArrayList<Mail> listeMail;
    String nomFichier = "mail.txt";

    public MailsFile() {
        listeMail = new ArrayList<Mail>();
        

        byte[] mailsFichier = lireFichier(nomFichier);
        String mails = new String(mailsFichier);
        String[] tableauMails = mails.split("\\r?\\n\\.\\r?\\n");

        for (int i = 0; i < tableauMails.length; i++) {
            String[] mailExplode = tableauMails[i].split("\\r?\\n\\r?\\n");
            try {
                Mail m = new Mail(mailExplode[0], mailExplode[1]);

                listeMail.add(m);
            } catch (Exception oException) {
                for (String elem : mailExplode) {
                    System.out.println(elem);
                }
            }
            
        }
    }

    public byte[] lireFichier(String nomFichier) {

        byte[] buf = null; // Tableau de byte pour stocker les data
        int fisRead; // Valeur lu

        File f; // Fichier
        FileInputStream fis = null; // Flux entrant
        try {
            f = new File("C:\\Temp\\" + nomFichier); // On ouvre le fichier
            fis = new FileInputStream(f); // On ouvre le flux
            buf = new byte[(int) f.length()]; // On initialise la taille du buffer

            for (int i = 0; i < f.length(); i++) { // On parcourt l'ensemble du fichier
                fisRead = fis.read(); // On lit la valeur
                if (fisRead == -1) { // Si -1, fin du fichier
                    break;
                }
                buf[i] = (byte) fisRead; // On met la valeur en byte dans notre tableau
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Erreur : Le fichier n'existe pas.");
        } catch (IOException ex) {
            System.err.println("Erreur : Impossible de lire dans le fichier.");
        } finally {
            try {
                fis.close(); // On ferme le flux
            } catch (IOException ex) {
                System.err.println("Erreur : Le fichier ne se ferme pas.");
            }
        }

        return buf;
    }

    public int getNombreMails() {
        return listeMail.size();
    }

    public int getTailleMails() {

        int tailleTotale = 0;

        for (int i = 0; i < this.getNombreMails(); i++) {
            tailleTotale += listeMail.get(i).getTaille();
        }

        return tailleTotale;
    }

    public Mail getMailByNb(int n) {
        if (n >= listeMail.size()) {
            return null;
        }
        return listeMail.get(n);
    }

    public void rmvMailByNb(int n) {
        listeMail.remove(n);
    }

    public void ecrireFichier() {
        
        String nouveauFichierMail = "";
        byte[] data = null;
        for(Mail m : listeMail){
            nouveauFichierMail += m.getHeader();
            nouveauFichierMail += "\r\n\r\n";
            nouveauFichierMail += m.getCorps();
            nouveauFichierMail += "\r\n.\r\n";
        }
        
        data = nouveauFichierMail.getBytes();
        
        FileOutputStream fos = null; // Flux sortant
        try {
            fos = new FileOutputStream("C:\\Temp\\" + nomFichier); // On ouvre le flux
            fos.write(data, 0, data.length); // On écrit dans le fichier
        } catch (FileNotFoundException ex) {
            System.err.println("Erreur : Le fichier n'existe pas.");
        } catch (IOException ex) {
            System.err.println("Erreur : Impossible d'écrire dans le fichier.");
        } finally {
            try {
                fos.close(); // On ferme le flux
            } catch (IOException ex) {
                System.err.println("Erreur : Le fichier ne se ferme pas.");
            }
        }
    }
    
    public int indexOfMail(Mail m){
        return listeMail.indexOf(m);
    }
    public ArrayList<Mail> getListMails(){
        return listeMail;
    }

    void removeAll(ArrayList<Mail> m) {
        listeMail.removeAll(m);
    }
}
