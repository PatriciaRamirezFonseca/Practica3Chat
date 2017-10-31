import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {

    public static void main(String[] args) {

        MarcoServidor mimarco = new MarcoServidor();

        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}

class MarcoServidor extends JFrame implements Runnable {

    private JTextArea areatexto;

    public MarcoServidor() {
        setBounds(120, 300, 280, 350);
        JPanel milamina = new JPanel();
        milamina.setLayout(new BorderLayout());

        areatexto = new JTextArea();
        milamina.add(areatexto, BorderLayout.CENTER);

        add(milamina);
        setVisible(true);

        Thread mihilo = new Thread(this);
        mihilo.start();

    }

    @Override
    public void run() {

        try {

            ServerSocket servidor = new ServerSocket(9999);
            String nick, ip, mensaje;
            ArrayList<String> listaip = new ArrayList<String>();
            PaqueteEnvio paquete_recibido;

            while (true) {

                Socket miSocket = servidor.accept();

                ObjectInputStream paquete_datos = new ObjectInputStream(miSocket.getInputStream());
                paquete_recibido = (PaqueteEnvio) paquete_datos.readObject();

                nick = paquete_recibido.getNick();
                ip = paquete_recibido.getIp();
                mensaje = paquete_recibido.getMensaje();

                if (!mensaje.equals(" online")) {

                    areatexto.append("\n" + nick + ": " + mensaje + " para :" + ip);

                    Socket enviaDestinario = new Socket(ip, 9090);

                    ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinario.getOutputStream());
                    paqueteReenvio.writeObject(paquete_recibido);

                    paqueteReenvio.close();
                    enviaDestinario.close();
                    miSocket.close();
                } else {
                    //detectar a los clientes online

                    InetAddress localizacion = miSocket.getInetAddress();
                    String ipRemota = localizacion.getHostAddress();
                    System.out.println("Online " + ipRemota);
                    
                    listaip.add(ipRemota);
                    paquete_recibido.setIps(listaip);
                    for (String z : listaip) {
                        System.out.println("Array: " + z);
                    }

                    //-------------------
                }
            }

        } catch (IOException ex) {
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
