package com.example.tweakbase;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;
import android.util.Log;
	

/**
 * This code was adapted from: 
 * http://androidexample.com/Upload_File_To_Server_-_Android_Example/index.php?view=article_discription&aid=83&aaid=106
 * 
 * It uploads database files to http://whester.com/tweakbase/uploads
 */
public class HttpFileUpload implements Runnable{
	final String TAG = "HttpFileUpload";
        URL connectURL;
        String responseString;
        String Title;
        String Description;
        byte[ ] dataToServer;
        FileInputStream fileInputStream = null;

        HttpFileUpload(String urlString, String vTitle, String vDesc){
                try{
                        connectURL = new URL(urlString);
                        Title= vTitle;
                        Description = vDesc;
                }catch(Exception ex){
                    Log.e("HttpFileUpload","URL Malformatted");
                }
        }
	
        void Send_Now(FileInputStream fStream){
                fileInputStream = fStream;
                Sending();
        }
	
        void Sending(){
                String iFileName = Title;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                try
                {
                        Log.d(TAG,"Starting Http File Sending to URL");
	
                        // Open a HTTP connection to the URL
                        HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();
	
                        // Allow Inputs
                        conn.setDoInput(true);
	
                        // Allow Outputs
                        conn.setDoOutput(true);
	
                        // Don't use a cached copy.
                        conn.setUseCaches(false);
	
                        // Use a post method.
                        conn.setRequestMethod("POST");
	
                        conn.setRequestProperty("Connection", "Keep-Alive");
	
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	
                        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"title\""+ lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(Title);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
	                        
                        dos.writeBytes("Content-Disposition: form-data; name=\"description\""+ lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(Description);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
	                        
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + iFileName +"\"" + lineEnd);
                        dos.writeBytes(lineEnd);
	
                        Log.d(TAG,"Headers are written");
	
                        // create a buffer of maximum size
                        int bytesAvailable = fileInputStream.available();
	                        
                        int maxBufferSize = 1024;
                        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        byte[ ] buffer = new byte[bufferSize];
	
                        // read file and write it into form...
                        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	
                        while (bytesRead > 0)
                        {
                                dos.write(buffer, 0, bufferSize);
                                bytesAvailable = fileInputStream.available();
                                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                                bytesRead = fileInputStream.read(buffer, 0,bufferSize);
                        }
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	
                        // close streams
                        fileInputStream.close();
	                        
                        dos.flush();
	                        
                        Log.d(TAG,"File Sent, Response: "+String.valueOf(conn.getResponseCode()));
	                         
                        InputStream is = conn.getInputStream();
	                        
                        // retrieve the response from server
                        int ch;
	
                        StringBuffer b =new StringBuffer();
                        while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
                        String s=b.toString();
                        Log.i("Response",s);
                        dos.close();
                }
                catch (MalformedURLException ex)
                {
                        Log.e(TAG, "URL error: " + ex.getMessage(), ex);
                }
	
                catch (IOException ioe)
                {
                        Log.e(TAG, "IO error: " + ioe.getMessage(), ioe);
                }
        }
	
        @Override
        public void run() {
                // TODO Auto-generated method stub
        }
        
        public static void UploadFile(String filePath){
        	try {
        	    // Set your file path here
        	    FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + "/" + filePath);       	  

        	    // Set your server page url (and the file title/description)
        	    HttpFileUpload hfu = new HttpFileUpload("http://whester.com/tweakbase/upload.php", filePath,"SQLite DB");
        	    
        	    hfu.Send_Now(fstrm);
        	    
        	  } catch (FileNotFoundException e) {
        	    Log.e("HttpFileUpload", e.getMessage());
        	  }
        	}
}
