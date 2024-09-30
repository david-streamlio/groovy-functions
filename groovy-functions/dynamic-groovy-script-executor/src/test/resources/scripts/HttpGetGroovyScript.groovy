package scripts

import java.net.HttpURLConnection
import java.net.URL

def getUrlContents(String urlString) {
    URL url = new URL(urlString)
    HttpURLConnection connection = (HttpURLConnection) url.openConnection()
    connection.setRequestMethod("GET")

    // Set request headers (optional)
    connection.setRequestProperty("User-Agent", "Groovy Script")

    // Response code check
    int responseCode = connection.getResponseCode()
    println "Response Code: $responseCode"

    if (responseCode == HttpURLConnection.HTTP_OK) {
        // Reading the response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))
        String line
        StringBuilder response = new StringBuilder()

        while ((line = reader.readLine()) != null) {
            response.append(line)
        }
        reader.close()

        // Return the response content
        return response.toString()
    } else {
        return "GET request failed"
    }
}

def url = "https://jsonplaceholder.typicode.com/posts/1" // Example API endpoint
def response = getUrlContents(url)
return "Response Content:\n$response"
