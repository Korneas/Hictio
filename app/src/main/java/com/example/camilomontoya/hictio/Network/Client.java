package com.example.camilomontoya.hictio.Network;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observer;

public class Client implements Runnable {

    private Socket s;
    private static Client ref;
    private Observer boss;

    //ADDRESS
    private final static String ADDRESS = "192.168.1.112";
    private static final int PORT = 5000;
    private static boolean online;

    private Client() {
        s = null;
    }

    public static Client getInstance() {
        if (ref == null) {
            ref = new Client();
        }
        return ref;
    }

    public void startConection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (s == null) {
                    try {
                        Log.d("Cliente", "Iniciando conexion");
                        s = new Socket(InetAddress.getByName(ADDRESS), PORT);
                        //Conection request
                        send("conect");
                        online = true;
                        new Thread(ref).start();
                        Log.d("Cliente", "Conectado");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    public void run() {
        while (online) {
            try {
                receive();
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
                boss.update(null, "offline");
            }
        }
    }

    /**
     * Metodo para recibir todos los objetos que envie el servidor y notificarlos al Observer
     */
    private void receive() {
        try {
            DataInputStream in = new DataInputStream(s.getInputStream());
            String str = in.readUTF();
            Log.d("ClienteMensaje", "Llego mensaje");
            boss.update(null, str);

            if (str.contains("offline")) {
                online = false;
                forceDisconection();
            }

        } catch (IOException e) {
            online = false;
            boss.update(null, "offline");
        }
    }

    /**
     * Metodo para enviar objetos al servidor
     *
     * @param str String
     * @throws IOException
     */
    public void send(final String str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (s != null) {
                    try {
                        DataOutputStream out = new DataOutputStream(s.getOutputStream());
                        out.writeUTF(str);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        online = false;
                        Log.d("Cliente", "Conexion terminada");
                    }
                }
            }
        }).start();
    }

    private void forceDisconection() {
        if (s != null) {
            try {
                s.close();
                s = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Cliente", "Ya estas desconectado");
        }
    }

    public void setObserver(Observer boss) {
        this.boss = boss;
    }


}
