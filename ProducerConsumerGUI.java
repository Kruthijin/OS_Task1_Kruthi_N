import javax.swing.*;
import java.awt.*;

public class ProducerConsumerGUI extends JFrame {
    static final int SIZE = 5;
    private final int[] buffer = new int[SIZE];
    private int in = 0, out = 0, count = 0;

    private final JTextArea output = new JTextArea(18, 55);
    private final JLabel status = new JLabel("Ready");
    private final JPanel bufferPanel = new JPanel(new GridLayout(1, SIZE, 8, 8));

    public ProducerConsumerGUI() {
        setTitle("Producer-Consumer - Multithreading");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Shared Circular Buffer (Capacity = 5)"), BorderLayout.WEST);
        top.add(status, BorderLayout.EAST);

        for (int i = 0; i < SIZE; i++) {
            JLabel cell = new JLabel("Empty", SwingConstants.CENTER);
            cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            cell.setName("cell" + i);
            bufferPanel.add(cell);
        }

        JButton start = new JButton("START");
        start.addActionListener(e -> startThreads());

        add(top, BorderLayout.NORTH);
        add(bufferPanel, BorderLayout.CENTER);
        add(new JScrollPane(output), BorderLayout.SOUTH);
        add(start, BorderLayout.WEST);

        setSize(760, 500);
        setLocationRelativeTo(null);
        refreshBuffer();
    }

    private JLabel cell(int index) {
        return (JLabel) bufferPanel.getComponent(index);
    }

    private synchronized void refreshBuffer() {
        for (int i = 0; i < SIZE; i++) {
            cell(i).setText("Empty");
        }
        int p = out;
        for (int k = 0; k < count; k++) {
            cell(p).setText(String.valueOf(buffer[p]));
            p = (p + 1) % SIZE;
        }
        status.setText("Items in buffer: " + count + "/" + SIZE);
    }

    private synchronized void produce(int value) throws InterruptedException {
        while (count == SIZE) {
            output.append("Producer waiting: buffer is FULL\n");
            wait();
        }
        buffer[in] = value;
        in = (in + 1) % SIZE;
        count++;
        output.append("Producer -> " + value + " | Buffer: " + count + "/" + SIZE + "\n");
        SwingUtilities.invokeLater(this::refreshBuffer);
        notifyAll();
    }

    private synchronized int consume() throws InterruptedException {
        while (count == 0) {
            output.append("Consumer waiting: buffer is EMPTY\n");
            wait();
        }
        int value = buffer[out];
        out = (out + 1) % SIZE;
        count--;
        output.append("Consumer <- " + value + " | Buffer: " + count + "/" + SIZE + "\n");
        SwingUtilities.invokeLater(this::refreshBuffer);
        notifyAll();
        return value;
    }

    private void startThreads() {
        output.setText("");
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    produce(i);
                    Thread.sleep(300);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    consume();
                    Thread.sleep(500);
                }
                SwingUtilities.invokeLater(() -> status.setText("Completed"));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producer.start();
        consumer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProducerConsumerGUI().setVisible(true));
    }
}
