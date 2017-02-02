/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurpop3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Epulapp
 */
public class Serveur implements Runnable {

    private ServerSocket socketServeur;
    private Socket socket;
    private Thread thread;

    public Serveur(int port) {
        try {

            socketServeur = new ServerSocket(port);
            Utilitaire.print("\n Le serveur écoute sur le port " + socketServeur.getLocalPort(), Utilitaire.ANSI_GREEN);

        } catch (IOException ex) {//Erreur création du serverSocket
            Utilitaire.print("\n Erreur à la création du serverSocket ", Utilitaire.ANSI_RED);
            Logger.getLogger(Serveur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                socket = socketServeur.accept();
                thread = new Thread(new Communication(socket));
                thread.start();
            } catch (IOException ex) {//Erreur du accept
                //System.err.println("\n Erreur serveur (accept)");
                Utilitaire.print("\n Erreur serveur (accept)", Utilitaire.ANSI_RED);
            }
        }
    }
}