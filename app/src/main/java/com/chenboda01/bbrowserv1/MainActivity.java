package com.chenboda01.bbrowserv1;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.webkit.*;
import android.content.SharedPreferences;
import java.net.URLEncoder;
import java.util.LinkedHashSet;
import java.util.Set;

public class MainActivity extends Activity {
    private WebView webView;
    private EditText address;
    private LinearLayout bookmarksBar;
    private TextView status;
    private SharedPreferences prefs;
    private final String HOME = "https://www.google.com";
    private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}

    protected void onCreate(Bundle b){
        super.onCreate(b);
        prefs=getSharedPreferences("bbrowser",MODE_PRIVATE);
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Color.rgb(7,19,30));
        LinearLayout top=new LinearLayout(this);top.setOrientation(LinearLayout.HORIZONTAL);top.setGravity(Gravity.CENTER_VERTICAL);top.setPadding(dp(8),dp(8),dp(8),dp(5));
        TextView logo=new TextView(this);logo.setText("🌐");logo.setTextSize(24);logo.setGravity(Gravity.CENTER);top.addView(logo,new LinearLayout.LayoutParams(dp(42),dp(42)));
        address=new EditText(this);address.setSingleLine(true);address.setTextColor(Color.WHITE);address.setHintTextColor(Color.rgb(160,190,210));address.setHint("Search or type website");address.setTextSize(15);address.setPadding(dp(12),0,dp(12),0);address.setImeOptions(EditorInfo.IME_ACTION_GO);address.setBackgroundColor(Color.rgb(19,40,56));address.setOnEditorActionListener((v,a,e)->{if(a==EditorInfo.IME_ACTION_GO||(e!=null&&e.getKeyCode()==KeyEvent.KEYCODE_ENTER)){go(address.getText().toString());return true;}return false;});
        top.addView(address,new LinearLayout.LayoutParams(0,dp(42),1));Button goBtn=btn("Go");goBtn.setOnClickListener(v->go(address.getText().toString()));top.addView(goBtn);root.addView(top);
        LinearLayout controls=new LinearLayout(this);controls.setOrientation(LinearLayout.HORIZONTAL);controls.setPadding(dp(8),0,dp(8),dp(5));
        Button back=btn("←");back.setOnClickListener(v->{if(webView.canGoBack())webView.goBack();});Button forward=btn("→");forward.setOnClickListener(v->{if(webView.canGoForward())webView.goForward();});Button reload=btn("Reload");reload.setOnClickListener(v->webView.reload());Button home=btn("Home");home.setOnClickListener(v->go(HOME));Button bm=btn("★");bm.setOnClickListener(v->addBookmark());
        controls.addView(back);controls.addView(forward);controls.addView(reload);controls.addView(home);controls.addView(bm);root.addView(controls);
        bookmarksBar=new LinearLayout(this);bookmarksBar.setOrientation(LinearLayout.HORIZONTAL);bookmarksBar.setPadding(dp(8),0,dp(8),dp(5));HorizontalScrollView hsv=new HorizontalScrollView(this);hsv.addView(bookmarksBar);root.addView(hsv,new LinearLayout.LayoutParams(-1,dp(48)));
        status=new TextView(this);status.setTextColor(Color.rgb(168,199,217));status.setTextSize(12);status.setPadding(dp(10),0,dp(10),dp(4));status.setText("B-Browser V1");root.addView(status);
        webView=new WebView(this);WebSettings s=webView.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setDatabaseEnabled(true);s.setLoadWithOverviewMode(true);s.setUseWideViewPort(true);s.setBuiltInZoomControls(true);s.setDisplayZoomControls(false);s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.setWebChromeClient(new WebChromeClient(){public void onProgressChanged(WebView v,int p){status.setText(p<100?"Loading "+p+"%":"Ready");}});
        webView.setWebViewClient(new WebViewClient(){public void onPageFinished(WebView v,String url){address.setText(url);prefs.edit().putString("last_url",url).apply();}});
        root.addView(webView,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);renderBookmarks();go(prefs.getString("last_url",HOME));
    }
    private Button btn(String t){Button b=new Button(this);b.setText(t);b.setTextColor(Color.WHITE);b.setTextSize(12);b.setAllCaps(false);b.setBackgroundColor(Color.rgb(11,132,255));b.setMinWidth(dp(46));return b;}
    private void go(String raw){if(raw==null)return;raw=raw.trim();if(raw.length()==0)return;String url;if(raw.startsWith("http://")||raw.startsWith("https://"))url=raw;else if(raw.contains(".")&&!raw.contains(" "))url="https://"+raw;else{try{url="https://www.google.com/search?q="+URLEncoder.encode(raw,"UTF-8");}catch(Exception e){url=HOME;}}webView.loadUrl(url);}
    private void addBookmark(){String url=webView.getUrl();String title=webView.getTitle();if(url==null)return;Set<String> set=new LinkedHashSet<>(prefs.getStringSet("bookmarks",new LinkedHashSet<String>()));set.add((title==null?url:title)+"||"+url);prefs.edit().putStringSet("bookmarks",set).apply();renderBookmarks();Toast.makeText(this,"Bookmarked",Toast.LENGTH_SHORT).show();}
    private void renderBookmarks(){bookmarksBar.removeAllViews();Button clear=btn("Clear");clear.setOnClickListener(v->{prefs.edit().remove("bookmarks").apply();renderBookmarks();});bookmarksBar.addView(clear);Set<String> set=prefs.getStringSet("bookmarks",new LinkedHashSet<String>());for(String item:set){String[] p=item.split("\\|\\|",2);String title=p.length>0?p[0]:"Link";String url=p.length>1?p[1]:item;Button b=btn(title.length()>14?title.substring(0,14):title);b.setOnClickListener(v->go(url));bookmarksBar.addView(b);}}
    public void onBackPressed(){if(webView.canGoBack())webView.goBack();else super.onBackPressed();}
}
