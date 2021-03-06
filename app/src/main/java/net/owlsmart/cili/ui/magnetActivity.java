package net.owlsmart.cili.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.owlsmart.cili.R;
import net.owlsmart.cili.adapter.magnetList_A;
import net.owlsmart.cili.model.magnetList_C;
import net.owlsmart.cili.util.NoScrollGridView;
import net.owlsmart.cili.util.SystemUtil;
import net.owlsmart.cili.view.AutoNewLineLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class magnetActivity extends AppCompatActivity {
    private ImageView iv_search;
    private magnetList_C magnetList_c;
    private List<magnetList_C> magnetList_cList = new ArrayList<magnetList_C>();
    private List<String> rem = new ArrayList<String>();
    private magnetList_A magnetList_a;
    private NoScrollGridView lv_magentlist;
    private EditText et_search;
    private TextView tv_qq;

    private AutoNewLineLayout anl_tags;


    private ImageView iv_clear;

    private String btsowUrl = "https://btsow.com/search/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnet);

        //???????????????
        SystemUtil.setStatusBarColor(this, this.getResources().getColor(R.color.top_background_color));
        SystemUtil.setAndroidNativeLightStatusBar(this,true);
        //?????????????????????
        Intent intent=getIntent();
        String homeSearch=intent.getStringExtra("search");
        iv_clear= (ImageView) findViewById(R.id.iv_clear);

        anl_tags = (AutoNewLineLayout)findViewById(R.id.anl_tags);
        lv_magentlist = (NoScrollGridView)findViewById(R.id.lv_magentlist);
        et_search=  (EditText) findViewById(R.id.et_search);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    // TODO: 2018/6/27 0027 ???????????????????????????
                    //??????edittext?????????
                    anl_tags.setVisibility(View.GONE);
                    lv_magentlist.setVisibility(View.VISIBLE);
                    String et = et_search.getText().toString();
                    okhttp_text(et);
                    return true;
                }
                return false;
            }
        });
//????????????????????????
        iv_search =  (ImageView) findViewById(R.id.iv_search);

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????edittext?????????
                String et = et_search.getText().toString();
                if (!et.isEmpty()) {
                    okhttp_text(et);
                    anl_tags.setVisibility(View.GONE);
                    lv_magentlist.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(magnetActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                    anl_tags.setVisibility(View.VISIBLE);
                    lv_magentlist.setVisibility(View.GONE);
                }
            }
        });
        tv_qq = (TextView) findViewById(R.id.tv_qq);
//        tv_qq.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                joinQQGroup("WZXjv7I736JKIJ48YTlIfCuLvHlWTx0a");
//            }
//        });
        okhttp_home();
        IvClearLister();

        timer_start();
    }

    private void IvClearLister(){

        //????????????
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
                anl_tags.setVisibility(View.VISIBLE);
                lv_magentlist.setVisibility(View.GONE);
            }
        });
        //?????????????????????
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //??????????????????????????????
                if(et_search.getText().toString().equals("")){
                    iv_clear.setVisibility(View.INVISIBLE);
                }else {
                    iv_clear.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                //??????????????????????????????
                if(et_search.getText().toString().equals("")){
                    iv_clear.setVisibility(View.INVISIBLE);
                }else {
                    iv_clear.setVisibility(View.VISIBLE);
                }
            }
        });

    }







    public void okhttp_text(String search) {

        magnetList_cList.clear();

        //??????okHttpclient?????????
        OkHttpClient okHttpClient = new OkHttpClient();
        //??????Request,Builder
        Request.Builder builder = new Request.Builder();
        //??????Request.Builder??????,??????Url??????,??????????????????
        Request.Builder url = builder.url(btsowUrl+search);
        //??????Request.Builder??????,??????builder????????????request??????
        Request request = url.build();
        //????????????Call??????,?????????request??????
        Call call = okHttpClient.newCall(request);
        //??????call??????,??????enqueue??????,??????????????????(????????????)

        call.enqueue(new Callback() {
            //????????????????????????,???????????????
            @Override
            public void onFailure(Call call, IOException e) {
                //andler.sendEmptyMessage(FALL);
            }

            //????????????????????????,???????????????
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String source = response.body().string();

                System.out.print(source);

                //?????????????????????????????????????????????
                Document list = Jsoup.parse(source);
                Elements listElement = list.getElementsByClass("data-list");

                //?????????class="row"
                Document row = Jsoup.parse(listElement.html());
                Elements rowElement = row.getElementsByClass("row");

                Elements links = listElement.select("a[href]");
                for (Element idt : links){
                    System.out.println("href : "+ idt.attr("href"));//??????href
                    System.out.println("title :"+ idt.attr("title"));//????????????
                    System.out.println("block :"+ idt.text());
                    magnetList_c =new magnetList_C();
                    //??????????????????
                    magnetList_c.setHref(idt.attr("href").toString());
                    magnetList_c.setTitle(idt.attr("title").toString()+"#"+et_search.getText().toString());
                    magnetList_c.setBlock(idt.text().toString().replace(idt.attr("title").toString(),""));
                    magnetList_cList.add(magnetList_c);
                }
                Message msg = new Message();
                msg.what = 16;
                Thandler.sendMessage(msg);//???activity??????handler????????????

               // System.out.println(rowElement.html());
            }
        });

    }


    /*
    *
    * ????????????
    * */

    public void okhttp_home() {

        //??????okHttpclient?????????
        OkHttpClient okHttpClient = new OkHttpClient();
        //??????Request,Builder
        Request.Builder builder = new Request.Builder();
        //??????Request.Builder??????,??????Url??????,??????????????????
        Request.Builder url = builder.url(btsowUrl);
        //??????Request.Builder??????,??????builder????????????request??????
        Request request = url.build();
        //????????????Call??????,?????????request??????
        Call call = okHttpClient.newCall(request);
        //??????call??????,??????enqueue??????,??????????????????(????????????)

        call.enqueue(new Callback() {
            //????????????????????????,???????????????
            @Override
            public void onFailure(Call call, IOException e) {
                //andler.sendEmptyMessage(FALL);
            }

            //????????????????????????,???????????????
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String source = response.body().string();

                System.out.print(source);

                //?????????????????????????????????????????????
                Document list = Jsoup.parse(source);
                Elements listElement = list.getElementsByClass("tags-box hidden-xs");

                Elements links = listElement.select("a[href]");
                for (Element idt : links){
                    //System.out.println("href : "+ idt.attr("href"));//??????href
                    System.out.println("text :"+ idt.text().split("\\.")[1]);

                    rem.add(idt.text().split("\\.")[1]);
                }
                Message msg = new Message();
                msg.what = 20;
                Thandler.sendMessage(msg);//???activity??????handler????????????
                // System.out.println(rowElement.html());
            }
        });

    }


    private Handler Thandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 16:
                    clickData((ArrayList<magnetList_C>) magnetList_cList);
                    break;
                case 20:
                    remData((ArrayList<String>) rem);
                    break;

            }
        }
    };

    public void remData(ArrayList<String> remlist) {
        Iterator<String> it = remlist.iterator();
        while (it.hasNext()) {
            String next = it.next();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10,10,0,10);//4???????????????????????????????????????



            final TextView bt1 = new TextView(magnetActivity.this);
            bt1.setBackgroundResource(R.drawable.radius_border);
            bt1.setElevation(3);
            bt1.setText(next);
            bt1.setLayoutParams(layoutParams);
            bt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   okhttp_text(bt1.getText().toString());
                    anl_tags.setVisibility(View.GONE);
                    lv_magentlist.setVisibility(View.VISIBLE);
                   et_search.setText(bt1.getText().toString());
                }
            });
            anl_tags.addView(bt1);
        }
    }

    public void clickData(ArrayList<magnetList_C> magnetList_cList) {

        magnetList_a = new magnetList_A(R.layout.item_magnet, magnetActivity.this, magnetList_cList);


        lv_magentlist.setAdapter(magnetList_a);
        lv_magentlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final TextView href = (TextView) view.findViewById(R.id.href);//??????
                final TextView title = (TextView) view.findViewById(R.id.title);//??????
                // ????????????
                Intent intent=new Intent();
                intent.putExtra("href", href.getText().toString());//????????????,""
                intent.putExtra("title", title.getText().toString());//????????????,""
                intent.setClass(magnetActivity.this, magnetDetailActivity.class);//?????????????????????
                magnetActivity.this.startActivity(intent);
            }
        });
    }

    private int timer_flag =3;
    private void timer_start()
    {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub


                timer_flag--;
                if(timer_flag < 0)
                {
                    timer_flag = 3;
                    timer.cancel();
                    Message msg =new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);

                }
            }
        }, 1, 1000);
    }


    private Handler handler =new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(msg.what == 1)
            {
                tv_qq.setVisibility(View.GONE);
            }

        }
    };



}