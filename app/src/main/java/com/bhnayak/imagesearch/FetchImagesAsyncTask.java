package com.bhnayak.imagesearch;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class FetchImagesAsyncTask extends AsyncTask<String, Void, ArrayList<ImageData>> {
    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;
    private static final String API_KEY = "&api_key=3e7cc266ae2b0e0d78e279ce8e361736";
    private static final String FORMAT = "&format=json&nojsoncallback=1&safe_search=1&per_page=50";
    private String mUrl;
    private IFetchImagesCompletionHandler mCompletionHandler = null;


    public void setCompletionHandler( IFetchImagesCompletionHandler completionHandler )
    {
        mCompletionHandler = completionHandler;
    }

    @Override
    protected ArrayList<ImageData> doInBackground(String... params) {
        mUrl = params[0];
        String strUrl = mUrl + API_KEY + FORMAT ;
        ArrayList<ImageData> messages = null;
        try {
            //Create a URL object holding our url
            URL url = new URL(strUrl);
            //Create a connection
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //Connect to our url
            connection.connect();
            //Create a new InputStreamReader
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            messages = parseInputStream( streamReader );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }

    private ArrayList<ImageData> parseInputStream(InputStreamReader streamReader) {
        JsonReader jsonReader = new JsonReader(streamReader);
        ArrayList<ImageData> images = null;
        try
        {
            jsonReader.beginObject();
            int imageCount = 0;
            String photos = jsonReader.nextName();
            jsonReader.beginObject();
            while( jsonReader.hasNext() )
            {
                String name = jsonReader.nextName();
                switch (name) {
                    case "perpage":
                        imageCount = jsonReader.nextInt();
                        break;
                    case "photo":
                        images = parseImageData(jsonReader, imageCount);
                        break;
                    case "page":
                        String page = jsonReader.nextString();
                        break;
                    default:
                        jsonReader.skipValue();

                }
            }
            jsonReader.endObject();
            while( jsonReader.hasNext() )
            {
                jsonReader.skipValue();
            }
            jsonReader.endObject();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally {
            //Close our Json reader
            try {
                jsonReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return images;
    }

    private ArrayList<ImageData> parseImageData(JsonReader jsonReader, int count) {
        ArrayList<ImageData> images = new ArrayList<ImageData>();
        try
        {
            jsonReader.beginArray();
            while( jsonReader.hasNext())
            {
                jsonReader.beginObject();
                ImageData imageData = new ImageData();
                while( jsonReader.hasNext())
                {
                    String name = jsonReader.nextName();
                    switch (name) {
                        case "farm":
                            imageData.farm = jsonReader.nextString();
                            break;
                        case "server":
                            imageData.server = jsonReader.nextString();
                            break;
                        case "id":
                            imageData.id = jsonReader.nextString();
                            break;
                        case "secret":
                            imageData.secret = jsonReader.nextString();
                            break;
                        default:
                            jsonReader.skipValue();
                            break;
                    }
                }
                jsonReader.endObject();
                images.add(imageData);
            }
            jsonReader.endArray();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return images;
    }

    protected void onPostExecute(ArrayList<ImageData> result) {
        super.onPostExecute(result);
        if( mCompletionHandler != null )
        {
            mCompletionHandler.onCompleted( result );
        }
    }
}
