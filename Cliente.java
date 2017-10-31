import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.swing.*;
import java.net.*;
import java.util.ArrayList;

public class Cliente {

    public static void main(String[] args) {

        MarcoCliente mimarco = new MarcoCliente();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}

class MarcoCliente extends JFrame {

    public MarcoCliente() {
        LaminaMarcoCliente milamina = new LaminaMarcoCliente();

        setBounds(600, 300, 300, 380);
        setVisible(true);
        add(milamina);
        addWindowListener(new EnvioOnline());

    }
}
//envio de señal online-----------------------

class EnvioOnline extends WindowAdapter {

    @Override
    public void windowOpened(WindowEvent e) {
        try {
            Socket misocket = new Socket("192.168.0.5", 9999);
            PaqueteEnvio datos = new PaqueteEnvio();
            datos.setMensaje(" online");

            ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());

            paquete_datos.writeObject(datos);
            misocket.close();

        } catch (Exception ex) {

        }
    }
}
//--------------------------------------------

class LaminaMarcoCliente extends JPanel implements Runnable {

    private JTextField campo1;
    private JComboBox ip;
    private JLabel nick;
    private JButton miboton;
    private JTextArea areaChat;

    public LaminaMarcoCliente() {
        String nick_usuario = JOptionPane.showInputDialog("nick: ");

        JLabel n_nick = new JLabel("nick: ");
        nick = new JLabel(nick_usuario);

        JLabel texto = new JLabel(" Online");
        ip = new JComboBox();
//        ip.addItem("usuario 1");
//        ip.addItem("usuario 2");
//        ip.addItem("usuario 3");
//        ip.addItem("192.168.0.5");
//        ip.addItem("192.168.0.5");

        EnviarTexto et = new EnviarTexto();

        campo1 = new JTextField(20);
        miboton = new JButton("Enviar");
        areaChat = new JTextArea(15, 22);

        miboton.addActionListener(et);

        add(n_nick);
        add(nick);
        add(texto);
        add(ip);
        add(areaChat);
        add(campo1);
        add(miboton);
        Thread mihilo = new Thread(this);
        mihilo.start();

    }

    @Override
    public void run() {
        try {
            ServerSocket servidor_cliente = new ServerSocket(9090);
            Socket cliente;
            PaqueteEnvio paqueteRecibido;
            while (true) {
                cliente = servidor_cliente.accept();
                ObjectInputStream flujoentrada = new ObjectInputStream(cliente.getInputStream());
                paqueteRecibido = (PaqueteEnvio) flujoentrada.readObject();

                if (paqueteRecibido.getMensaje().equals(" online")) {
                    areaChat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());

                } else {
                   // areaChat.append("\n" + paqueteRecibido.getIp());
                   ArrayList<String> IpsMenu= new ArrayList<>();
                   IpsMenu=paqueteRecibido.getIps();
                   ip.removeAllItems();
                   
                   for(String z:IpsMenu){
                       
                       ip.addItem(z);
                   }
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private class EnviarTexto implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            areaChat.append("\n" + "yo: " + campo1.getText());
            try {
                Socket miSocket = new Socket("192.168.0.5", 9999);
                PaqueteEnvio dato = new PaqueteEnvio();
                dato.setNick(nick.getText());
                dato.setIp((String) ip.getSelectedItem());
                dato.setMensaje(campo1.getText());

                ObjectOutputStream packeteDatos = new ObjectOutputStream(miSocket.getOutputStream());
                packeteDatos.writeObject(dato);
                miSocket.close();

            } catch (IOException ex) {

            }
        }
    }
}

class PaqueteEnvio implements Serializable {

    private String nick, ip, mensaje;

    public ArrayList<String> getIps() {
        return Ips;
    }

    public void setIps(ArrayList<String> Ips) {
        this.Ips = Ips;
    }
    private ArrayList<String> Ips;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
