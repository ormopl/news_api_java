import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;


public class api implements ActionListener {

    JFrame frame;
    JButton button;
    JLabel label;
    JPanel panel;
    JTextArea textAera;
    public int status = 0;
    public int writerStatus = 0;
    public String line;


    public api() {
        //configuring the GUI
        frame = new JFrame();
        button = new JButton("Download!");
        button.setSize(new Dimension(10, 10));
        button.addActionListener(this);
        label = new JLabel("Click the button to download news...");
        textAera = new JTextArea(5, 20);
        textAera.setEditable(false);

        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(button);
        panel.add(label);
        panel.add(textAera);

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("News API Downloader");
        frame.setSize(400, 600);
        frame.pack();
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        new api();
    }


    @Override //button and labels behaviour
    public void actionPerformed (ActionEvent e){
        try {
            getAPI();
            label.setText("Connection Status: " + status);
            if (writerStatus == 1) {
                label.setText("news.txt file created!");
            }else{
                label.setText("Nothing Created");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void getAPI() throws IOException, InterruptedException { //old part of code, writer status and textfield output added
        //basic configuration
        String API_KEY = "0f85faabd2084b8db9f708dd6270e532";
        String category = "business";
        String country = "pl";
        String URI_link = "https://newsapi.org/v2/top-headlines?country=%s&category=%s&apiKey=%s";

        //connection configuration
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(URI_link, country, category, API_KEY)))
                .GET()
                .build();

        //printing something on the screen

        System.out.println("News API downloader ");
        System.out.println("Hardcoded category: " + category);
        System.out.println("Hardcoded country: " + country);
        System.out.println("Hardcoded API Key: " + API_KEY);

        //connection establishing and data download
        HttpResponse<Void> response = client.send(request,
                HttpResponse.BodyHandlers.discarding());
        HttpResponse<String> response_string = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        String content = response_string.body();

        if (response.statusCode() == 200) {

            System.out.println("Connection status: " + response.statusCode() + " OK!");
            status = response.statusCode();
        } else {
            System.out.println("Connection status: " + response.statusCode() + ". Stopping.");
            System.exit(0);
        }

        //parsing JSON
        JSONObject json_content = new JSONObject(content);
        JSONArray articles_arr = json_content.getJSONArray("articles");

        //creating text file
        File news_file = new File("news.txt");
        news_file.createNewFile();
        FileWriter news_fileWriter = new FileWriter("news.txt");
        System.out.println("news.txt file created!");

        //loop for JSON values parsing and writing to file

        for (int i = 0; i < articles_arr.length(); i++) {
            var author = articles_arr.getJSONObject(i).get("author");
            var title = articles_arr.getJSONObject(i).get("title");
            var description = articles_arr.getJSONObject(i).get("description");
            line = (title + ":" + description + ":" + author);
            textAera.append(line + "\n");
            news_fileWriter.write(line + "\n");

        }

        //closing the file
        news_fileWriter.close();
        writerStatus = 1;

    }
}
