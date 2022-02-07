import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InterruptedIOException;


public class api {

    public static void main(String[] args) throws IOException, InterruptedException {

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

        if (response.statusCode() == 200){
            System.out.println("Connection status: " + response.statusCode() + " OK!");
        }else {
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

        //loop for JSON values parsing and writing to to file

        for (int i = 0; i < articles_arr.length(); i++){
            var author = articles_arr.getJSONObject(i).get("author");
            var title = articles_arr.getJSONObject(i).get("title");
            var description = articles_arr.getJSONObject(i).get("description");
            var line = (title + ":" + description + ":" + author);
            news_fileWriter.write(line + "\n");
        }

        //closing the file
        news_fileWriter.close();

        }



    }