package org.example;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Queue;

public class MyFrame extends JFrame implements ActionListener {
        private final JButton selectButton;
        private final JButton start;
        private final JRadioButton compressButton;
        private final JRadioButton decompressButton;
        private String filePath;
        private char choice = 'c';
        private final Object lock = new Object();
    MyFrame(){

        //the main frame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLayout(new BorderLayout(10, 0));
        this.setResizable(false);

        //title for the frame
        JLabel title = new JLabel();
        title.setText("Huffman Encoder");
        title.setFont(new Font("MV Boli", Font.BOLD, 30));
        title.setForeground(Color.RED);
        title.setPreferredSize(new Dimension(100, 70));
        title.setVerticalAlignment(JLabel.CENTER);
        title.setHorizontalAlignment(JLabel.CENTER);


        //the middle of the frame
        JPanel content = new JPanel();
        content.setBackground(Color.gray);
        content.setPreferredSize(new Dimension(100, 100));
        content.setLayout(new BorderLayout());

        //add question for the user
        JLabel question = new JLabel("Choose Operation!");
        question.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        question.setPreferredSize(new Dimension(100, 100));
        question.setHorizontalAlignment(JLabel.CENTER);
        question.setVerticalAlignment(JLabel.CENTER);

        // Compress Button
        compressButton = new JRadioButton("Compress");
        compressButton.setFont(new Font(null,Font.PLAIN,30));
        compressButton.setPreferredSize(new Dimension(250, 100));
        compressButton.setHorizontalAlignment(JRadioButton.CENTER);
        compressButton.setSelected(true);
        compressButton.addActionListener(this);

        //Decompress Button
        decompressButton = new JRadioButton("Decompress");
        decompressButton.setFont(new Font(null,Font.PLAIN,30));
        decompressButton.setPreferredSize(new Dimension(250, 100));
        decompressButton.setHorizontalAlignment(JRadioButton.CENTER);
        decompressButton.addActionListener(this);

        // Group both radio buttons into 1 group
        ButtonGroup operations = new ButtonGroup();
        operations.add(compressButton);
        operations.add(decompressButton);

        //button to select the file
        selectButton = new JButton("Select File");
        selectButton.addActionListener(this);
        selectButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        selectButton.setFocusable(false);

        //add the button in the middle of the page
        JPanel selectButtonPanel = new JPanel();
        selectButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center align the button
        selectButtonPanel.add(selectButton); // Add the button to the panel

         // Add all the components of the middle of the frame
        content.add(question, BorderLayout.NORTH);
        content.add(compressButton, BorderLayout.WEST);
        content.add(decompressButton, BorderLayout.EAST);
        content.add(selectButtonPanel, BorderLayout.SOUTH);

        //start button
        start = new JButton("Start");
        start.setPreferredSize(new Dimension(100, 70));
        start.addActionListener(this);
        start.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        start.setFocusable(false);
        start.setVisible(false);
        //add all the components to the frame
        this.add(title, BorderLayout.NORTH);
        this.add(start, BorderLayout.SOUTH);
        this.add(content, BorderLayout.CENTER);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==selectButton) {
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showOpenDialog(null); //select file to open
            if (response == JFileChooser.APPROVE_OPTION) {
                start.setVisible(true);
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                setFilePath(file.getPath());
            }
        }
        if(actionEvent.getSource() == compressButton){
            System.out.println("Compression");
            this.setChoice('c');
        }

        if(actionEvent.getSource() == decompressButton){
            System.out.println("Decompression");
            this.setChoice('d');
        }

        if(actionEvent.getSource() == start){
            this.dispose();
            synchronized (lock) {
                lock.notify(); // Notify the waiting thread (main method)
            }
        }
    }

    // Add a method to wait for the start button to be clicked
    public void waitForStart() throws InterruptedException {
        synchronized (lock) {
            lock.wait(); // Wait for notification from the start button
        }
    }

    void setFilePath(String path){
        this.filePath = path;
    }

    String getFilePath(){
        return filePath;
    }

    void setChoice(char c){
        this.choice=c;
    }

    char getChoice(){
        return this.choice;
    }
}
