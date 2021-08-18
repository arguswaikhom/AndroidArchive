package com.projectreachout.DummyUpload.DownloadImage;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projectreachout.AppController;
import com.projectreachout.Article.AddArticle.OnUploadCompleted;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.projectreachout.GeneralStatic.getDomainUrl;

public class DownloadTask extends AsyncTask<Void, Void, Bitmap> implements OnUploadCompleted {
    private static final String TAG = DownloadTask.class.getName();

    private DummyArticle dummyArticle;
    private Context context;

    public DownloadTask(Context context, DummyArticle dummyArticle) {
        this.context = context;
        this.dummyArticle = dummyArticle;
    }

    protected Bitmap doInBackground(Void... voids) {
        URL url = stringToURL(getDomainUrl() + dummyArticle.getImage());
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            File file = saveImageToInternalStorage(result);
            uploadArticle(Uri.fromFile(file));

        } else {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private File saveImageToInternalStorage(Bitmap bitmap) {
        ContextWrapper wrapper = new ContextWrapper(context);
        File file = wrapper.getDir("Images", MODE_PRIVATE);
        file = new File(file, dummyArticle.getEmail() + "_" + new Date().getTime() + ".jpg");

        try {
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Uri savedImageURI = Uri.parse(file.getAbsolutePath());
        //return savedImageURI;
        return file;
    }

    private void uploadArticle(Uri uri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Article");
        final StorageReference imgStrRef = storageReference.child(uri.getLastPathSegment());
        UploadTask uploadTask = imgStrRef.putFile(uri);
        uploadTask.continueWithTask((Task<UploadTask.TaskSnapshot> task) -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return imgStrRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                Toast.makeText(context, "uploaded: " + downloadUri, Toast.LENGTH_SHORT).show();

                this.onUploadCompleted(downloadUri.toString(), "");
            } else {
                Log.v("abc", task.getException().getMessage());
                Toast.makeText(context, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private URL stringToURL(String urlString) {
        try {
            URL url = new URL(urlString);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onUploadCompleted(String imageUrl, String description) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dummyArticle.getTime_stamp());


            Map<String, Object> article = new HashMap<>();
            article.put("image_url", imageUrl);
            article.put("description", dummyArticle.getDesc());
            article.put("time_stamp", new Timestamp(date));
            article.put("email", dummyArticle.getEmail());

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("BufferArticle")
                    .document(date.getTime() + "")
                    .set(article)
                    .addOnSuccessListener(aVoid -> {
                        Log.v("abc", "article: " + dummyArticle.getArticle_id());
                        Toast.makeText(context, "Upload Completed", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Log.w("abc", "Error adding document: " + dummyArticle.getArticle_id()));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}