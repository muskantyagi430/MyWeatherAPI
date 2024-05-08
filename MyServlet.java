package MyPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		//API setup
		String api="e7ad82d30660c0cb9f691ee0c5b13c5d";
		//get the city from the form input
		String city=request.getParameter("city");
		//create the url for the openWeathermap api request
		String apiurl="https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+api;
		//API integration hmari url bn chuki h hme connection chhiye jha p ye url pass ho ske
		URL url=new URL(apiurl);
		HttpURLConnection connection=(HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		// Reading the data from network
		InputStream inputstream=connection.getInputStream(); 
		InputStreamReader reader=new InputStreamReader(inputstream);
		//want to store in string (but string is immutable that can"t change that why i make Stringbuilder so that changes can apply
		StringBuilder responseContent=new StringBuilder();
		//for take input from the user ,we will create scanner object
		Scanner scanner=new Scanner(reader);
		while(scanner.hasNext()) {
			responseContent.append(scanner.nextLine());
			
		}
		scanner.close();
		//type casting parsing the data into JSON
		Gson gson=new Gson();
		JsonObject jsonobject=gson.fromJson(responseContent.toString(),JsonObject.class);
		System.out.println(jsonobject);

        //Date & Time
        long dateTimestamp = jsonobject.get("dt").getAsLong() * 1000;
        String date = new Date(dateTimestamp).toString();
        
        //Temperature
        double temperatureKelvin = jsonobject.getAsJsonObject("main").get("temp").getAsDouble();
        int temperatureCelsius = (int) (temperatureKelvin - 273.15);
       
        //Humidity
        int humidity = jsonobject.getAsJsonObject("main").get("humidity").getAsInt();
        
        //Wind Speed
        double windSpeed = jsonobject.getAsJsonObject("wind").get("speed").getAsDouble();
        
        //Weather Condition
        String weatherCondition = jsonobject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
        // Set the data as request attributes (for sending to the jsp page)
        request.setAttribute("date", date);
        request.setAttribute("city", city);
        request.setAttribute("temperature", temperatureCelsius);
        request.setAttribute("weatherCondition", weatherCondition); 
        request.setAttribute("humidity", humidity);    
        request.setAttribute("windSpeed", windSpeed);
        request.setAttribute("weatherData", responseContent.toString());
        connection.disconnect();
        //forwarding the  request to the wereather.jsp page for the rendering
      // if (jsonobject.has("cod") && jsonobject.get("cod").getAsInt() == 200) {
      //  request.getRequestDispatcher("index.jsp").forward(request,response);}else {
        //	
        //	request.setAttribute("error", "Invalid city. Please enter a valid city name.");
          //  request.getRequestDispatcher("index.html").forward(request, response);
       // }
     // Check if the response from the API indicates an error
        if (jsonobject.has("cod")) {
            int statusCode = jsonobject.get("cod").getAsInt();
            if (statusCode == 200) {
                // Data is valid, proceed with processing and forwarding
                // Date & Time, Temperature, Humidity, Wind Speed, Weather Condition
                // Set request attributes
                request.setAttribute("date", date);
                request.setAttribute("city", city);
                request.setAttribute("temperature", temperatureCelsius);
                request.setAttribute("weatherCondition", weatherCondition); 
                request.setAttribute("humidity", humidity);    
                request.setAttribute("windSpeed", windSpeed);
                request.setAttribute("weatherData", responseContent.toString());
                
                // Forward the request to the weather.jsp page for rendering
                request.getRequestDispatcher("index.jsp").forward(request,response);
            } else {
                // Data is invalid, redirect back to the input form page (index.jsp)
                // Include an error message to display on the form
                String errorMessage = "Invalid city name. Please enter a valid city name.";
                request.setAttribute("errorMessage", errorMessage);
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        } else {
            // Handle unexpected API response format
            // Redirect back to the input form page (index.jsp) with a general error message
            String errorMessage = "Error: Unexpected API response format. Please try again later.";
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }

        
        
		
		
	}

}
