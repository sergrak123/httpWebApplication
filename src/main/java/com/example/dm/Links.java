package com.example.dm;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Links {

    public ArrayList<String> URLs = new ArrayList<>();
    public ArrayList<String> brokenURLs = new ArrayList<>();
    public String srcLink;
    public String internalAbsoluteLinks;
    int count = 0;
    private String[] files = {"ico","css", "jpg"};

    public Links(String link){
        srcLink = link;
    }

    public String parseHTML(String sourceLink) throws IOException {

        String html = null;
        try {
            String url = "http://" + sourceLink;

            URLConnection connection = new URL(url).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");

            html = scanner.next();
            scanner.close();
        }
        catch (Exception ex){
            ex.printStackTrace();

        }
        return html;
    }

    public void getURLs(String html) throws IOException {

        String regex = "<a\\s+(?:[^>]*?\\s+)?href=\"(.*?)\"";
        Matcher matcher = Pattern.compile(regex).matcher(html);
        while (matcher.find() && count < 2000){
            String link = matcher.group(1);

            //Определяем внутр или внешн
            if(getInternal(link)!=null){

                String internalLink = getInternal(link);
                if(!URLs.contains(internalLink)){

                    URLs.add(internalLink);
                    count++;
                    //добавить проверку на 400+
                    if( isValid(internalLink) && !isFile(internalLink) )/*&& parseHTMLWHTTP(interalLink)!= null*/{
                        String content = parseHTMLWHTTP(internalLink);
                        getURLs(content);
                    }
                }
            }
    }

    public String getInternal(String link) throws MalformedURLException {

        String finalLink  = null;
        //если абсолютная ссылка, то проверяем на внутр.
        if(link.startsWith("https://") || link.startsWith("http://")){

            URL url = new URL(link);
            String host = url.getHost();
            if(Objects.equals(host,srcLink) || Objects.equals(host,"www."+srcLink )){
                finalLink = link;
            }
        }
        //если относительная ссылка -> внутр / #
        else if(link.startsWith("/")){
            finalLink ="http://"+ srcLink + link;
        }
        else
            finalLink  = null;

        return finalLink;

    }

    private boolean isFile(String link) {
        for(String file : files){
            if(link.endsWith(file)){
                return true;
            }

        }
        return false;
    }

    public boolean isValid(String internalLink) throws IOException {

        URL url = new URL(internalLink);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "href");
        connection.setConnectTimeout(10000);
        if (connection.getResponseCode()!= HttpURLConnection.HTTP_OK){
            if(connection.getResponseCode()>= 400){
                brokenURLs.add(internalLink);
            }
            return false;
        }
        else
            return true;
    }
        
        

    public StringBuffer getBySocket() throws IOException {

        String data = URLEncoder.encode("key1", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("value1", StandardCharsets.UTF_8);

        Socket socket = new Socket(srcLink, 80);

        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        wr.write("GET / HTTP/1.0\r\n");
        wr.write("Host:" + srcLink + "\r\n");
        wr.write("\r\n");
        wr.write(data);
        wr.flush();

        BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = rd.readLine()) != null) {
            response.append(inputLine);
        }
        wr.close();
        rd.close();
        return response;
    }
        
    public String parseHTMLWHTTP(String sourceLink) throws IOException {

        String html = null;
        try {
            String url = sourceLink;

            URLConnection connection = new URL(url).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");

            html = scanner.next();
            scanner.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return html;
    }    
        
        

}
