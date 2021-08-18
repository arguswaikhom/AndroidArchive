package com.squadx.crown.pocketcomic;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ScheduledExecutorService;

public class ContentMainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, onBackPressedInterface {

    @Override
    public void onBackPressedWebView() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            MainActivity.canGoBack = true;
        } else {
            MainActivity.canGoBack = false;
            getActivity().onBackPressed();
        }
    }

    public static final String LOG_TAG = ContentMainFragment.class.getSimpleName();

    private String link;
    private int mProgress;
    private String mCurrentUrl;
    private WebView mWebView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeLayout;
    private ScheduledExecutorService scheduler;
    private ContentListViewNavigationDrawer mCurrentObject;
    private String dummyURL = "http://172.217.167.164";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_main, container, false);
        link = dummyURL;

        mWebView = rootView.findViewById(R.id.web_view);
        progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        swipeLayout = rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.setBackgroundColor(getResources().getColor(R.color.webViewBackground));

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mCurrentObject = bundle.getParcelable(MainActivity.CLASS_NAME_MAIN_ACTIVITY);
            link = mCurrentObject.getLink();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mCurrentObject.getTitle());
        }

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);
                mProgress = progress;
                if (isProgressDone()) {
                    //setTitle(getString(R.string.app_name));
                    progressBar.setVisibility(View.GONE);
                }

            }
        });
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mCurrentUrl = url;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mCurrentUrl = url;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getScheme().equals("market")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        Activity host = (Activity) view.getContext();
                        host.startActivity(intent);
                        return true;
                    } catch (ActivityNotFoundException e) {
                        Uri uri = Uri.parse(url);
                        view.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
                        return false;
                    }

                }
                return false;
            }
        });

        mWebView.loadUrl(link);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onPause", (Class[]) null)
                    .invoke(mWebView, (Object[]) null);

        } catch (ClassNotFoundException e) {
            Log.v(LOG_TAG, "ClassNotFoundException: ", e);
        } catch (NoSuchMethodException e) {
            Log.v(LOG_TAG, "NoSuchMethodException: ", e);
        } catch (InvocationTargetException e) {
            Log.v(LOG_TAG, "InvocationTargetException: ", e);
        } catch (IllegalAccessException e) {
            Log.v(LOG_TAG, "IllegalAccessException: ", e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onRefresh() {
        mWebView.loadUrl(mCurrentUrl);
        if (isProgressDone()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(false);
                }
            }, 1000);
        }
    }

    private boolean isProgressDone() {
        return mProgress == 100;
    }
}