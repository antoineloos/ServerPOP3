/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurpop3;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 *
 * @author Epulapp
 */
public class Utilitaire {

    public static String scanner(int portDeb, int portFin) {
        String usedPort = "";
        DatagramSocket ds = null;
        int total = 0;
        for (int i = portDeb; i <= portFin; i++) {
            try {
                ds = new DatagramSocket(i);
            } catch (SocketException e) {
                usedPort += i + "; ";
                total++;
            } finally {
                ds.close();
            }
        }
        return usedPort += "Total used: " + total;
    }

    public static synchronized void print(String str) {
        System.out.print(str);
    }
    public static synchronized void print(String str, String color) {
        System.out.print(color + str + ANSI_RESET);
    }
    
    public static synchronized void println(String str) {
        System.err.println(str);
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

}
