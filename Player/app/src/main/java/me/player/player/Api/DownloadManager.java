package me.player.player.Api;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.apache.http.util.ByteArrayBuffer;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import me.player.player.AppState;
import me.player.player.Entities.ImageFromServer;
import me.player.player.Util;

/**
 * Created by stevenstewart on 9/12/14.
 */
public class DownloadManager
{
    private static DownloadManager downloadManager;
    private DownloadManager()
    {

    }
    public static DownloadManager getInstance()
    {
        if (downloadManager == null)
        {
            downloadManager = new DownloadManager();
        }
        return downloadManager;
    }

    private final int maxNumberOfImagesToKeep = 50;
    public ArrayList<ImageFromServer> imagesFromServer = new ArrayList<ImageFromServer>();
    private boolean downloading = false;
    private Object imageListLock = new Object();
    //private int adapterIndexRangeStart = 0;
    //private int adapterIndexRangeEnd = 20;
    public DownloadListener downloadListener;

    private void alertListenerOfDownloadCompletion(final ImageFromServer imageFromServer)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() //UI thread - because this callback will likely alter the UI
        {
            @Override
            public void run()
            {
                if(downloadListener != null)
                    downloadListener.onDownloadComplete(imageFromServer);
            }
        });
    }
    public ImageFromServer addImageToBeDownloaded(ImageFromServer imageFromServer)
    {
        if(AppState.getInstance().tooManyItemsqueuedForDownload)
            return null;

        synchronized (imageListLock)
        {
            if(!imagesFromServer.contains(imageFromServer)) //Check again because multiple requests might have been sent while waiting for the lock.
            {
                //Check to see if an image with the same url is already in the list. If so, both feed items should point to that image.
                //Since we are already running through the list, use this opportunity to alter the importance of all the current server images. When a new image is added, all the others should move one place closer to being recycled in the queue.
                for (int i = 0; i < imagesFromServer.size(); i++)
                {
                    ImageFromServer tempImageFromServer = imagesFromServer.get(i);
                    tempImageFromServer.importance++;
                    if (tempImageFromServer.url.equals(imageFromServer.url))
                    {
                        //Log.d("player", "Image Repeated!");
                        return tempImageFromServer;
                    }
                }
                imageFromServer.downloadFailed = false;
                imageFromServer.skipNextDownloadAttempt = false;
                imagesFromServer.add(imageFromServer);
                if(imagesFromServer.size() > (maxNumberOfImagesToKeep+50)) //Only keep a certain number of extra images in the download queue before setting the flag that should keep other threads from adding more images to be queued.
                    AppState.getInstance().tooManyItemsqueuedForDownload = true;

                //Log.d("player","imagesFromServer size: " + imagesFromServer.size());

                //Log.d("player", "Image added to download queue. url:" + imageFromServer.url);
            }
        }
        if (!downloading)
        {
            //The separate thread for image downloading starts here. Many can start, but most should end just after adding the image to the queue because downloading is already taking place. The attemptDownload method restarts itself if there is another image to download after completing the first.
            new Thread(new Runnable()
            {
                public void run()
                {
                    attemptImageDownload();
                }
            }).start();
        }
        return null; //This alerts the caller that there was not an image with this url already loaded.
    }

    private void attemptImageDownload()
    {
        //Log.d("player","Attempt download starting");
        downloading = true;
        ImageFromServer imageToDownload = null;
        synchronized (imageListLock)
        {
            for (int i = 0; i < imagesFromServer.size(); i++)
            {
                ImageFromServer imageFromServer = imagesFromServer.get(i);
                if (!imageFromServer.downloaded && !imageFromServer.downloadFailed && !imageFromServer.skipNextDownloadAttempt) //It hasn't been downloaded nor has it failed to download previously.
                {
                    //Log.d("player", "Found image in list to download");
                    imageToDownload = imageFromServer;
                    i = imagesFromServer.size();
                }
            }
            if(imageToDownload == null) //If none are found, check the list once more for skipped downloads so we can try them agian
            {
                for (int i = 0; i < imagesFromServer.size(); i++)
                {
                    ImageFromServer imageFromServer = imagesFromServer.get(i);
                    if (!imageFromServer.downloaded && !imageFromServer.downloadFailed) //It hasn't been downloaded nor has it failed completely, but was skipped for some reason.
                    {
                        //Log.d("player", "Found previously failed image in list to download");
                        imageToDownload = imageFromServer;
                        i = imagesFromServer.size();
                    }
                }
            }
        }

        if(imageToDownload != null)
        {
            try
            {
                //Log.d("player","Starting image download");
                downloadImageInListview(imageToDownload);
                attemptImageDownload(); //Restart this method. The last iteration, when no more downloadable images are found, will run the else case, clean up the list, and let the thread end for the moment.
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            //Log.d("player","No more images found to download. Ending download thread.");
            cleanUpImageList();
            downloading = false;
        }

    }

    public static Drawable downloadImageDirectly(String urlPar, boolean circleCropImage) //Method for trying to get a drawable from an image url without considering any sort of UI input that might cancel this download.
    {
        try
        {
            AppState appState = AppState.getInstance();
            boolean downloadCancelled = false;
            ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(0);

            URL url = new URL(urlPar);
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            int bufferSize = 50;
            byte[] buffer = new byte[bufferSize];
            int bytesRead = 0;
            int totalBytesReceived = 0;
            long startTime = System.currentTimeMillis();
            long timeElapsed = 0;
            long maxFileSize = Util.powForNaturalNumbers(2,20); //No images over a megabyte

            while(bytesRead >= 0 && !downloadCancelled) //bytesRead returns -1 when the stream is finished.
            {
                //Get the data
                bytesRead = inputStream.read(buffer, 0, bufferSize);
                if (bytesRead > 0)
                    for (int i = 0; i < bytesRead; i++)
                        byteArrayBuffer.append(buffer[i]);

                //look for fail conditions
                //---------------------------------------------------------------------------------------------------
                totalBytesReceived += bytesRead;
                timeElapsed = System.currentTimeMillis() - startTime;
                //max file size exceeded
                if(totalBytesReceived > maxFileSize)
                {
                    downloadCancelled = true;
                    Log.d("player","Download failed! File size exceeded limit!");
                }
                else if(timeElapsed>appState.millisBeforeNoDataFail && totalBytesReceived==0) //to much time taken to receive first byte
                {
                    downloadCancelled = true;
                    Log.d("player","Download cancelled! No bytes received!");
                }
                else if(timeElapsed>appState.millisBeforeIncompleteDataFail) //to much time taken to receive all data
                {
                    downloadCancelled = true;
                    Log.d("player","Download cancelled! Could not download image in the specified time!");
                }
            }

            if(downloadCancelled)
            {
                return null;
            }
            else
            {
                byte[] imageData = byteArrayBuffer.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                Drawable drawable;

                if(circleCropImage)
                {
                    Bitmap circleCroppedBitmap = Util.getCircleCroppedBitmap(bitmap);
                    drawable = new BitmapDrawable(AppState.getInstance().mainActivity.getResources(), circleCroppedBitmap);
                }
                else
                {
                    drawable = new BitmapDrawable(AppState.getInstance().mainActivity.getResources(), bitmap);
                }
                return drawable;
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.d("player","DIRECT DOWNLOAD FAILED WITH EXCEPTION");
            return null;
        }
    }

    private void downloadImageInListview(ImageFromServer imageToDownload) //Method for downloading images for the current listview adapter.
    {
        try
        {
            AppState appState = AppState.getInstance();
            boolean downloadCancelled = false;
            ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(0);

            if((AppState.getInstance().adapterIndexRangeStart-1) <= imageToDownload.mostRecentAdapterIndex && imageToDownload.mostRecentAdapterIndex <= AppState.getInstance().adapterIndexRangeEnd)
            {
                URL url = new URL(imageToDownload.url);
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                int bufferSize = 50;
                byte[] buffer = new byte[bufferSize];
                int bytesRead = 0;
                int totalBytesReceived = 0;
                long startTime = System.currentTimeMillis();
                long timeElapsed = 0;
                long maxFileSize = Util.powForNaturalNumbers(2,19); //No images over a half-megabyte

                while(bytesRead >= 0 && !downloadCancelled) //bytesRead returns -1 when the stream is finished.
                {
                    //Check range again and again throughout the download process
                    if((appState.adapterIndexRangeStart-1) <= imageToDownload.mostRecentAdapterIndex && imageToDownload.mostRecentAdapterIndex <= appState.adapterIndexRangeEnd)
                    {
                        //Get the data
                        bytesRead = inputStream.read(buffer, 0, bufferSize);
                        if (bytesRead > 0)
                            for (int i = 0; i < bytesRead; i++)
                                byteArrayBuffer.append(buffer[i]);
                    }
                    else //Cancel this download if the users scroll to where the image would appear off screen.
                    {
                        downloadCancelled = true;
                        imageToDownload.downloadFailed = true;
                        //Log.d("player","********** Download cancelled! User scrolled this image off screen!");
                    }

                    //look for fail conditions
                    //---------------------------------------------------------------------------------------------------
                    totalBytesReceived += bytesRead;
                    timeElapsed = System.currentTimeMillis() - startTime;
                    //max file size exceeded
                    if(totalBytesReceived > maxFileSize)
                    {
                        downloadCancelled = true;
                        imageToDownload.downloadFailed = true; //Do not attempt to download this image again.
                        Log.d("player","Download failed! File size exceeded limit!");
                    }
                    else if(timeElapsed>appState.millisBeforeNoDataFail && totalBytesReceived==0) //to much time taken to receive first byte
                    {
                        downloadCancelled = true;
                        Log.d("player","Download cancelled! No bytes received!");
                    }
                    else if(timeElapsed>appState.millisBeforeIncompleteDataFail) //to much time taken to receive all data
                    {
                        downloadCancelled = true;
                        Log.d("player","Download cancelled! Could not download image in the specified time!");
                    }

                }
            }
            else //Cancel this download if the users scroll to where the image would appear off screen.
            {
                downloadCancelled = true;
                imageToDownload.downloadFailed = true;
                //Log.d("player","********** Download cancelled! User scrolled this image off screen!");
            }

            if(downloadCancelled) //We will just let this request stay in the queue and try again later
            {
                imageToDownload.downloadFailCount++;
                imageToDownload.skipNextDownloadAttempt = true;
                imageToDownload.drawable = null;
            }
            else
            {
                byte[] imageData = byteArrayBuffer.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                if(imageToDownload.isAvatar)
                {
                    Bitmap circleCroppedBitmap = Util.getCircleCroppedBitmap(bitmap);
                    imageToDownload.drawable = new BitmapDrawable(AppState.getInstance().mainActivity.getResources(), circleCroppedBitmap);
                }
                else
                {
                    imageToDownload.drawable = new BitmapDrawable(AppState.getInstance().mainActivity.getResources(), bitmap);
                }

                imageToDownload.downloaded = true;
                alertListenerOfDownloadCompletion(imageToDownload);
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            imageToDownload.downloadFailed = true;
            Log.d("player","DOWNLOAD FAILED WITH EXCEPTION");
        }
    }
    private void cleanUpImageList()
    {
        synchronized (imageListLock)
        {
            //Log.d("player","Image list cleanup process starting.");

            //Sort list according to importance
            Collections.sort(imagesFromServer,imageSorter);

            int listIndex = 0;
            for (Iterator<ImageFromServer> iterator = imagesFromServer.iterator(); iterator.hasNext(); )
            {
                ImageFromServer imageFromServer = iterator.next();
                //Log.d("player","Index: " + listIndex + " Importance: " + imageFromServer.importance);
                if(imageFromServer.downloadFailed || listIndex > maxNumberOfImagesToKeep)
                {
                    //Log.d("player","Image being removed from cache...");
                    imageFromServer.downloadFailed = false;
                    imageFromServer.downloaded = false;
                    imageFromServer.skipNextDownloadAttempt = false;
                    imageFromServer.drawable = null;
                    iterator.remove();
                }
                listIndex++;
            }

            AppState.getInstance().tooManyItemsqueuedForDownload = false; //Ath this point we have trimmed the list down to the max size.
        }
    }

    Comparator<ImageFromServer> imageSorter = new Comparator<ImageFromServer>()
    {
        @Override
        public int compare(ImageFromServer lhs, ImageFromServer rhs)
        {
            if(lhs.importance < rhs.importance)
                return -1;
            else if(lhs.importance > rhs.importance)
                return 1;
            else
                return 0;
        }
    };

    public interface DownloadListener
    {
        void onDownloadComplete(ImageFromServer imageFromServer);
    }
}
