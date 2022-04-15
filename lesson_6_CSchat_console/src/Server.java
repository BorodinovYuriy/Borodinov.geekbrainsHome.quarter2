
/**
 * -Порядок запуска: сервер-"ВКЛ"-клиент...
 * -Порядок отключения: произвольный, но если первым выключить клиент - возникнут ошибки,
 * и сервер приёдется перезапустить (ВЫКЛ/ВКЛ) с выводом ошибок...
 * -Клиент ещё не готов, но работоспособен: если запустить без сервера - exit только "насильно"
 * -Метод serverShutdown() нужно правильно реализовать...
 * --------------------------------------------------------------
 * -Подскажите пожалуйста, как реализовать правильно отключения сервера и клиента, что бы
 * при разрыве соединения сервер выполнял serverShutdown() и правильно завершал работу;
 * так же у клиента void windowClosing(WindowEvent e) хотелось бы иметь возможность закрыть окно,
 * когда клиент запущен отдельно.????
 * Клиент будет ещё дорабатываться...
 *
 * P.S. Потдсажите пожалуйста, если выполнять всё это в javafx, как там можно реализовать два GUI
 * (клиента и сервера) в одном проекте(т.е. не когда в клиенте реализуем и сервер), или нужно 2 проекта???
 *
 * !!!!!!!!!-"....Есть одна особенность,
 * которую нужно учитывать: клиент или сервер может написать несколько сообщений подряд.
 * Такую ситуацию необходимо корректно обработать."
 * -Я не понял этой части задания(т.е - а что имелось ввиду?...):(
 *
 *
 *
 * */
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {
    private JTextArea chatAreaJTA;
    private JTextField msgInputJTF;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket = null;
    private final int SERVER_PORT = 8189;
    public Server(){
        prepareGUI();
    }
    public void prepareGUI(){
        setBounds(600, 300, 500, 500);
        setTitle("Сервер");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        chatAreaJTA = new JTextArea();
        chatAreaJTA.setEditable(false);
        chatAreaJTA.setLineWrap(true);
        chatAreaJTA.setBackground(Color.lightGray);
        add(new JScrollPane(chatAreaJTA), BorderLayout.CENTER);

        JPanel allPanel = new JPanel(new FlowLayout());
        msgInputJTF= new JTextField();
        msgInputJTF.setEnabled(false);

        JButton send = new JButton("Отправить");
        send.setEnabled(false);
        JButton serverOn = new JButton("ВКЛ");
        //serverOn.setEnabled(true);
        JButton serverOff = new JButton("ВЫКЛ");
        serverOff.setEnabled(false);

        allPanel.add(send);
        allPanel.add(serverOn);
        allPanel.add(serverOff);

        add(allPanel,BorderLayout.NORTH);
        add(msgInputJTF,BorderLayout.SOUTH);

        msgInputJTF.addActionListener(e -> sendMessage());
        send.addActionListener(e -> sendMessage());
        serverOn.addActionListener(e -> {
            serverOff.setEnabled(true);
            serverOn.setEnabled(false);
            send.setEnabled(true);
            serverStart();
        });
        serverOff.addActionListener(e -> {
            serverOff.setEnabled(false);
            serverOn.setEnabled(true);
            send.setEnabled(false);
            msgInputJTF.setEnabled(false);
            serverShutdown();
        });

        // Настраиваем действие на закрытие окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
            }
        });

        setVisible(true);
    }
    public void serverStart(){
        Thread thread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
                System.out.println("\n"+"Сервер запущен, ожидаем подключения...");
                chatAreaJTA.append("\n"+"Сервер запущен, ожидаем подключения...");
                socket = serverSocket.accept();
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                chatAreaJTA.append("\n"+"!!!"+"Клиент подключился"+"\n");
                System.out.println("Клиент подключился");
                msgInputJTF.setEnabled(true);

                while (true) {
                    String str = in.readUTF();
                    if(str.equals("/end")){
                        break;
                    }
                    chatAreaJTA.append("-Клиент: "+ str +"\n");
                }
                System.out.println("\n"+"!!!"+"Отключение клиента");
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    socket.close();
                    in.close();
                    out.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

    }
    public void sendMessage(){
        if (!msgInputJTF.getText().trim().isEmpty()) {
            try {
                out.writeUTF(msgInputJTF.getText());
                chatAreaJTA.append("-" + msgInputJTF.getText()+"\n");
                msgInputJTF.setText("");
                msgInputJTF.grabFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void serverShutdown() {
            try {
                socket.close();
                in.close();
                out.close();
            }catch (NullPointerException e){
                System.out.println("Null point exception");
                e.printStackTrace();
            }catch (IOException e){
                System.out.println("IOException");
                e.printStackTrace();
            }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(Server::new);

    }
}

