/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurpop3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Epulapp
 */
class Communication implements Runnable {

    public static final String AUTORISATION = "AUTHORIZATION";
    public static final String TRANSACTION = "TRANSACTION";
    public static final String FIN = "END";
    public static final String DEBUT = "";

    private Socket socket;
    private OutputStream output;
    private InputStream input;
    private String commande;
    private String etat;
    private String reponse;
    private String user;
    private String pass;
    private boolean connect = false;
    private int nbConnect = 0;
    private int nbMessagesNonLu = 0;
    private int tailleMessagesNonLu = 0;
    private ArrayList<Integer> toDelete;
    private MailsFile mailFile;

    public Communication(Socket s) {
        this.socket = s;
        this.etat = this.AUTORISATION;
        toDelete = new ArrayList<>();
        mailFile = new MailsFile();
    }

    @Override
    public void run() {
        Utilitaire.print("\n Communication ouverte", Utilitaire.ANSI_GREEN);

        boolean close = false;
        int maxIter = 1000000000;
        int i = 0;
        boolean firstTime = true;
        
                long time = 0;
        String timbre = null;
        do {
            try {
                output = socket.getOutputStream();
                input = socket.getInputStream();
            } catch (IOException ex) {
                Utilitaire.print("\n Erreur : création flux entrée/sortie ", Utilitaire.ANSI_RED);
                reponse = "Erreur : création flux entrée/sortie";
                Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
            }

            BufferedReader buf = new BufferedReader(new InputStreamReader(input));
            String s = new String();

            try {

                if (!firstTime && buf.ready()) {
                    String requete = new String();

                    /* while ((s = buf.readLine()) != null || s.length() > 0) {
                     requete = requete + s + "\n";
                     System.err.println(requete);
                     break;
                     }*/
                    requete = buf.readLine();
                    System.err.println(requete);

                    commande = requete.split(" ")[0];

                    int n = -1;
                    String[] tabRequete = requete.split(" ");

                    nbMessagesNonLu = mailFile.getNombreMails(); // A remplacer par le compte du nb de msg
                    tailleMessagesNonLu = mailFile.getTailleMails();

                    switch (commande) {
                        case "APOP":
                            if (etat == this.AUTORISATION) {
                                String user = null;
                                String somme = null;
                                if(tabRequete.length==3){
                                    user = tabRequete[1];
                                somme = tabRequete[2];
                                }
                                
                                if (user!= null && somme !=null && user.equals("demo@polytech.fr") && somme.equals(getSomme("demo", timbre))) { //Remplacer par le controle de validité des connexion
                                    Utilitaire.print("Utilisateur Authentifié", Utilitaire.ANSI_CYAN);
                                    connect = true;
                                    etat = this.TRANSACTION;
                                    reponse = "+OK maildrop has " + nbMessagesNonLu + " messages ("+tailleMessagesNonLu+" octets)";
                                } else {
                                    nbConnect++;
                                         Utilitaire.print("Authentification échouée ", Utilitaire.ANSI_RED);
                                }
                                if (!connect && nbConnect >= 3) {
                                    reponse = "-ERR tentatives de connexion épuissé";
                                    close = true;
                                }
                            }
                            break;
                        case "STAT":
                            if (etat == this.TRANSACTION) {
                                reponse = "+OK " + nbMessagesNonLu + " " + tailleMessagesNonLu;
                            }
                            break;
                        case "RETR":
                            if (etat == this.TRANSACTION) {
                                if (tabRequete.length > 1) {
                                    n = Integer.parseInt(tabRequete[1]);
                                }
                                if (n != -1 && !toDelete.contains(n)) {
                                    Mail m = mailFile.getMailByNb(n);
                                    if (m != null) {
                                        reponse = "+OK " + m.getTaille() + " octets \r\n";
                                        reponse += m.getMessage();
                                    } else {
                                        reponse = "-ERR" + " invalid message number";
                                    }
                                } else {
                                    reponse = "-ERR" + " invalid message number";
                                }
                            }
                            break;
                        case "DELE":
                            if (etat == this.TRANSACTION) {
                                if (tabRequete.length > 1) {
                                    n = Integer.parseInt(tabRequete[1]);
                                }
                                if (n != -1 && !toDelete.contains(n) && n < nbMessagesNonLu) {
                                    toDelete.add(n);
                                    reponse = "+OK " + "message " + n + " deleted";
                                } else if (toDelete.contains(n)) {
                                    reponse = "-ERR " + " message " + n + " already deleted";
                                } else {
                                    reponse = "-ERR " + " invalid message number";
                                }
                            }
                            break;
                        case "QUIT":
                            if (etat == this.AUTORISATION) {
                                reponse = "+OK" + " POP3 server signing off";

                            } else if (etat == this.TRANSACTION) {
                                int m = 0;
                                if ((m = deleteMsg()) == 0) {
                                    reponse = "+OK" + " messages deleted";
                                } else {
                                    reponse = "-ERR " + m + " messages " + "not removed";
                                }
                                mailFile.ecrireFichier();
                                etat = this.AUTORISATION;
                            }
                            break;
                        default:
                            System.err.println("Commande invalide");
                            break;
                    }
                }
                if (firstTime) {
                    firstTime = false;
                    time = System.currentTimeMillis();
                    timbre = "<" + time + "@serveurPOP3.fr" + ">";
                    reponse = "+OK POP3 server ready "+timbre;
                    
                    System.out.println(getSomme("mdpDemo", timbre));
                }

                if (reponse.length() > 0) {
                    System.out.println(reponse);
                    output.write(reponse.getBytes());
                    output.write(new String("\r\n").getBytes());
                    output.flush();
                    reponse = "";
                }

                i++;
                if (i == maxIter) {
                    close = true;
                }
            } catch (IOException ex) {
                Utilitaire.print("\n Erreur : emmission ", Utilitaire.ANSI_RED);
                Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (!close);
        try {
            socket.close();
            Utilitaire.print("\n Connection close ", Utilitaire.ANSI_GREEN);
        } catch (IOException ex) {
            Utilitaire.print("\n Erreur : fermeture de conexion à échoué ", Utilitaire.ANSI_RED);
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int deleteMsg() {
        ArrayList<Mail> m = new ArrayList<>();
        int nbMessagesBeforeDel = mailFile.getNombreMails();
        int nbMessagesToDel = toDelete.size();
        for (int i = 0; i < toDelete.size(); i++) {
            m.add(mailFile.getMailByNb(toDelete.get(i)));
        }
        mailFile.removeAll(m);
       // boolean ok = true;
        //  if(mailFile.getNombreMails() != nbMessagesBeforeDel - nbMessagesToDel) ok = false;
        toDelete = new ArrayList<>();

        return nbMessagesBeforeDel - mailFile.getNombreMails() - nbMessagesToDel;
    }
    
    public static String getSomme(String mdp, String timbre) {
        String somme = null;
        MessageDigest md;
        try {
            String message = timbre + mdp;
            md = MessageDigest.getInstance("md5");
            md.update(message.getBytes());

            byte[] digest = null;
            digest = md.digest();
           // System.err.println(bytesToHex(digest));

            somme = bytesToHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Erreur lors de la génération de la somme de controle");
        }
        return somme;
    }

    public static String bytesToHex(byte[] b) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
            'b', 'c', 'd', 'e', 'f'};
        StringBuffer buffer = new StringBuffer();
        for (int j = 0; j < b.length; j++) {
            buffer.append(hexDigits[(b[j] >> 4) & 0x0f]);
            buffer.append(hexDigits[b[j] & 0x0f]);
        }
        return buffer.toString();
    }
}
