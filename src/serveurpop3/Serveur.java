/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurpop3;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author Epulapp
 */
public class Serveur implements Runnable {

    private SSLServerSocket socketServeur;
    private SSLSocket socket;
    private Thread thread;

    public Serveur(int port) {
        try {
            SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            socketServeur = (SSLServerSocket)factory.createServerSocket(port);
            socketServeur.setEnabledCipherSuites(
                    Arrays.stream(factory.getSupportedCipherSuites())
                        .filter(s -> s.contains("anon"))
                        .toArray(size -> new String[size])
            );
            Utilitaire.print("\n Le serveur écoute sur le port " + socketServeur.getLocalPort(), Utilitaire.ANSI_GREEN);

        } catch (Exception ex) {//Erreur création du serverSocket
            Utilitaire.print("\n Erreur à la création du serverSocket ", Utilitaire.ANSI_RED);
            Logger.getLogger(Serveur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                socket = (SSLSocket)socketServeur.accept();
                thread = new Thread(new Communication(socket));
                thread.start();
            } catch (IOException ex) {//Erreur du accept
                //System.err.println("\n Erreur serveur (accept)");
                Utilitaire.print("\n Erreur serveur (accept)", Utilitaire.ANSI_RED);
            }
        }
    }
}