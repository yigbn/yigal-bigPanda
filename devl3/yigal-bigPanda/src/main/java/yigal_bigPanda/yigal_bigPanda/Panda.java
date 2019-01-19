package yigal_bigPanda.yigal_bigPanda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import io.reactivex.Observable;

public class Panda 
{
	private static JSONParser parser = new JSONParser();
	private static Map<String,Integer> cntEvents = new HashMap<>();
	private static Map<String, Integer> cntData = new HashMap<>(); 
	
	public static void main(String [ ]args) throws IOException, InterruptedException
	{
		//check input 
		if ( args.length < 1)
		{
			System.out.println("please specify the splitter path");
			System.exit(1);
		}
		
		// start the http server and answer the required APIs
	    HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
	    server.createContext("/cntEvents", new CntEventsHandler());
	    server.createContext("/cntWords", new cntWordsHandler());
	    server.setExecutor(null); // creates a default executor
	    server.start();


	    // create an rx observer to emmit string events from the splitter output
		Observable<String> handleInputObservable = Observable.create(emitter -> {
		    try {
				Process proc = Runtime.getRuntime().exec(args[0]);
				BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				while (true)
				{
					String line = in.readLine();
					emitter.onNext(line);
				}
		        //emitter.onComplete();
		    } catch (Exception e) {
		        emitter.onError(e);
		    }
		});
		
		// subscribe to the events and update the stats
		handleInputObservable.subscribe(t ->
				{
					JSONObject jsonObject = null;
					try
					{
						jsonObject = (JSONObject)parser.parse(t);
					}
					catch (ParseException e) {
						// just ignore corrupted input json 
					}
					if ( jsonObject != null) // if it's null parsing yield meaningless results
					{
						String eventType = (String)jsonObject.get("event_type");
						String data = (String)jsonObject.get("data");
						if ( eventType != null)
						{
							if ( !cntEvents.containsKey(eventType)) 
								cntEvents.put(eventType, 1);
							else
								cntEvents.put(eventType, cntEvents.get(eventType) + 1 );
						}
						if ( data!= null)
						{
							if ( !cntData.containsKey(data)) 
								cntData.put(data, 1);
							else
								cntData.put(data, cntData.get(data) + 1 );
						}
					}
				}
		);
		
	}
	
    static class CntEventsHandler implements HttpHandler 
    {
	    public void handle(HttpExchange t) throws IOException 
	    {
	    	String eventType = t.getRequestURI().getQuery();
	        Integer cnt = cntEvents.containsKey(eventType) ? cntEvents.get(eventType) : 0;
            byte [] response = cnt.toString().getBytes();

	        Headers headers = t.getResponseHeaders();
	        headers.add("Content-Type", "text/plain");
	        t.sendResponseHeaders(200, response.length);
	        OutputStream os = t.getResponseBody();
	        os.write(response);
	        os.close();

	    }
    }
    
    static class cntWordsHandler implements HttpHandler 
    {
	    public void handle(HttpExchange t) throws IOException 
	    {
	    	String data = t.getRequestURI().getQuery();
	        Integer cnt = cntData.containsKey(data) ? cntData.get(data) : 0;
            byte [] response = cnt.toString().getBytes();

	        Headers headers = t.getResponseHeaders();
	        headers.add("Content-Type", "text/plain");
	        t.sendResponseHeaders(200, response.length);
	        OutputStream os = t.getResponseBody();
	        os.write(response);
	        os.close();
	    }
    }


}