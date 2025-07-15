package bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MovieBot extends TelegramLongPollingBot {

	private static final String BOT_USERNAME = "MovieFinder_bot";
	private static final String BOT_TOKEN = "7983506850:AAEQdXeswVgBLNI2FmS-uVYJObFxJRrL5ps";
	private final String TMDB_API_KEY = "ff3020874ac1a94a5bb1cdca59853723";

	@Override
	public String getBotUsername() {
		// TODO Auto-generated method stub
		return BOT_USERNAME;
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return BOT_TOKEN;
	}

	 @Override
	    public void onUpdateReceived(Update update) {
	        if (update.hasMessage() && update.getMessage().hasText()) {
	            String message = update.getMessage().getText();
	            String chatId = update.getMessage().getChatId().toString();

	            if (message.startsWith("/movie")) {
	                String query = message.replace("/movie", "").trim();
	                if (query.isEmpty()) {
	                    sendText(chatId, "Please provide a movie name.");
	                    return;
	                }
	                String info = fetchMovieInfo(query);
	                sendText(chatId, info);
	            } else if (message.equals("/start")) {
	                sendText(chatId, "Hi! Send /movie <movie name> to get movie details.");
	            }
	        }
	    }
	 
	 private void sendText(String chatId, String text) {
	        SendMessage message = new SendMessage();
	        message.setChatId(chatId);
	        message.setText(text);
	        try {
	            execute(message);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
	private String fetchMovieInfo(String movieName) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + TMDB_API_KEY + "&query=" + movieName;

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String jsonData = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
            JsonArray results = jsonObject.getAsJsonArray("results");

            if (results.size() == 0) {
                return "Movie not found.";
            }

            JsonObject movie = results.get(0).getAsJsonObject();
            System.out.println("Movie is : "+movie);
            String title = movie.get("title").getAsString();
            String overview = movie.get("overview").getAsString();
            String rating = movie.get("vote_average").getAsString();
            boolean isAdult = movie.get("adult").getAsBoolean();
            String release = movie.has("release_date") ? movie.get("release_date").getAsString() : "N/A";
            
            String contentLabel = isAdult ? "🔞 *Adult Content*" : "✅ Suitable for all audiences";
            
            return "🎬 " + title + " (" + release + ")\n⭐ Rating: " + rating + "/10\n"+ contentLabel + "\n\n" + overview;

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to fetch movie info.";
        }
    }
	
	public static void main(String[] args) throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new MovieBot());
    }

}
