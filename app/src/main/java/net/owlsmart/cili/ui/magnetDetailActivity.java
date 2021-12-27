package net.owlsmart.cili.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.androidx.PageMenu.IndicatorView;
import com.androidx.PageMenu.PageMenuLayout;
import com.androidx.PageMenu.ScreenUtil;
import com.androidx.PageMenu.holder.AbstractHolder;
import com.androidx.PageMenu.holder.PageMenuViewHolderCreator;


import net.owlsmart.cili.R;
import net.owlsmart.cili.util.CodeCreator;
import net.owlsmart.cili.util.ModelHomeEntrance;
import net.owlsmart.cili.util.SystemUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class magnetDetailActivity extends AppCompatActivity {

    List<ModelHomeEntrance> homeEntrances;
    IndicatorView entranceIndicatorView;
    PageMenuLayout<ModelHomeEntrance> mPageMenuLayout;
    private ImageView iv_back;
    private TextView hash,files,size,convert,keywords,title1;
    private EditText link;
    private ImageView iv_icon,iv_add;
    private LinearLayout ll_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnet_detail);

        //顶部框颜色
        SystemUtil.setStatusBarColor(this, this.getResources().getColor(R.color.top_background_color));
        SystemUtil.setAndroidNativeLightStatusBar(this,true);
        //拿到返回的参数
        Intent intent=getIntent();
        String title=intent.getStringExtra("title");
        String href=intent.getStringExtra("href");

        //视图

        iv_add= (ImageView) findViewById(R.id.iv_add) ;
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinQQGroup("WZXjv7I736JKIJ48YTlIfCuLvHlWTx0a");
            }
        });

        hash = (TextView)findViewById(R.id.hash) ;
        title1 = (TextView)findViewById(R.id.title) ;
        files = (TextView)findViewById(R.id.files) ;
        size = (TextView)findViewById(R.id.size) ;
        convert = (TextView)findViewById(R.id.convert) ;
        keywords = (TextView)findViewById(R.id.keywords) ;
        entranceIndicatorView = (IndicatorView)findViewById(R.id.main_home_entrance_indicator);
        mPageMenuLayout = (PageMenuLayout)findViewById(R.id.pagemenu);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        link = (EditText) findViewById(R.id.link) ;
        ll_icon = (LinearLayout) findViewById(R.id.ll_icon) ;

        title1.setText(title);
        //返回按钮
        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //System.out.print("href:::"+href);

        try {
            okhttp_text("http:"+href);
        }catch(Exception e){
            okhttp_text(href);
        }
        //屏幕适配绑定，一定要写，否则加载不出来
        ScreenUtil.init(magnetDetailActivity.this);
        //加载可滑动的导航栏
        initData();
        init();
    }

    /*
     * 加入QQ群
     * */
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public void okhttp_text(String href) {

        //创建okHttpclient的对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建Request,Builder
        Request.Builder builder = new Request.Builder();
        //使用Request.Builder对象,调用Url方法,传入网路路径
        Request.Builder url = builder.url(href);
        //使用Request.Builder对象,调用builder方法构件request对象
        Request request = url.build();
        //创建一个Call对象,参数就request对象
        Call call = okHttpClient.newCall(request);
        //使用call对象,调用enqueue方法,请求加入调度(异步加载)

        call.enqueue(new Callback() {
            //当请求失败的时候,调用此方法
            @Override
            public void onFailure(Call call, IOException e) {
                //andler.sendEmptyMessage(FALL);
            }

            //当请求成功的时候,调用此方法
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String source = response.body().string();
               // JsoupDoc(source);
                Message msg = new Message();
                msg.what = 10;
                Bundle bundle = new Bundle();
                bundle.putString("result", source);//往Bundle中存放数据
                msg.setData(bundle);
                Thandler.sendMessage(msg);
            }
        });

    }



    private void JsoupDoc(String source){
        // System.out.println(source);
        //解析和遍历文档，创建干净的解析
        Document doc = Jsoup.parse(source);
        //标题已经传进来了
        Element magnetLink = doc.getElementById("magnetLink");
        System.out.println(magnetLink.text().toString());//拿到磁力
        Elements elements = doc.getElementsByClass("detail data-list");
        System.out.println(elements.text().toString());

        //拆分
        String Torrentdata=elements.text().toString();

        String Files = Torrentdata.replace("Number of Files:","$");
        String Size = Files.replace("Content Size:","$");
        String Convert = Size.replace("Convert On:","$");
        String Keywords = Convert.replace("Keywords:","$");
        String Link = Keywords.replace("Magnet Link:","$");
        String Torrent = Link.replace("Torrent Hash:","$");

        Message msg = new Message();
        msg.what = 16;
        Bundle bundle = new Bundle();
        bundle.putString("magnetLink", magnetLink.text().toString());//磁力
        bundle.putString("Torrent", Torrent);//数组
        msg.setData(bundle);
        Thandler.sendMessage(msg);//用activity中的handler发送消息
    }


    private Handler Thandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    String result = msg.getData().getString("result");//接受msg传递过来的参数
                    System.out.println("Handler::::"+result);
                    //这里操作更新ui
                    JsoupDoc(result);
                    break;

                case 16:
                    String Torrent = msg.getData().getString("Torrent");//接受msg传递过来的参数
                    String magnetLink = msg.getData().getString("magnetLink");

                    String Torrentspit[] = Torrent.split("\\$");

                    System.out.println("Torrent Hash:"+Torrentspit[1]);
                    System.out.println("Number of Files:"+Torrentspit[2]);
                    System.out.println("Content Size:"+Torrentspit[3]);
                    System.out.println("Convert On:"+Torrentspit[4]);
                    System.out.println("Keywords:"+Torrentspit[5]);
                    System.out.println("Link:"+Torrentspit[6]);

                    hash.setText(Torrentspit[1]);
                    files.setText(Torrentspit[2]);
                    size.setText(Torrentspit[3]);
                    convert.setText(Torrentspit[4]);
                    keywords.setText(Torrentspit[5]);
                    link.setText(magnetLink);

                    break;


            }
        }
    };




    //初始化数据
    private void initData() {
        homeEntrances = new ArrayList<>();
        //第1页显示
        homeEntrances.add(new ModelHomeEntrance("复制", R.drawable.ic_copy));
        homeEntrances.add(new ModelHomeEntrance("二维码", R.drawable.ic_qrcode));
        homeEntrances.add(new ModelHomeEntrance("转发", R.drawable.ic_share));
        homeEntrances.add(new ModelHomeEntrance("下载", R.drawable.ic_xunlei));

    }
    private void init() {
        mPageMenuLayout.setPageDatas(homeEntrances, new PageMenuViewHolderCreator() {
            @Override
            public AbstractHolder createHolder(View itemView) {
                return new AbstractHolder<ModelHomeEntrance>(itemView) {
                    private TextView entranceNameTextView;
                    private ImageView entranceIconImageView;

                    @Override
                    protected void initView(View itemView) {
                        entranceIconImageView = itemView.findViewById(R.id.entrance_image);
                        entranceNameTextView = itemView.findViewById(R.id.entrance_name);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) ((float) ScreenUtil.getScreenWidth() / 4.0f));
                        itemView.setLayoutParams(layoutParams);
                    }

                    @Override
                    public void bindView(RecyclerView.ViewHolder holder, final ModelHomeEntrance data, int pos) {
                        entranceNameTextView.setText(data.getName());
                        entranceIconImageView.setImageResource(data.getImage());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String clickName =   data.getName();
                                if(clickName.equals("复制")){
                                    if(copy(link.getText().toString())){

                                        Toast.makeText(magnetDetailActivity.this, "已经复制到剪贴板", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(magnetDetailActivity.this, "遇到了点问题，试试别的方式吧～", Toast.LENGTH_SHORT).show();

                                    }
                                }else if(clickName.equals("转发")){
                                    shareText(link.getText().toString());
                                }else if(clickName.equals("二维码")){
                                    //shareText(link.getText().toString());

                                    Bitmap bitmap = null;
                                    //可对图片进行修改
                                    Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                                    bitmap = CodeCreator.createQRCode(link.getText().toString(), 400, 400, logo);
                                    iv_icon.setImageBitmap(bitmap);

                                    if(ll_icon.getVisibility()==View.VISIBLE){
                                        ll_icon.setVisibility(View.GONE);
                                    }else {
                                        ll_icon.setVisibility(View.VISIBLE);
                                    }


                                }else if(clickName.equals("下载")){
                                    try{
                                        //尝试通过链接唤起手机迅雷
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.getText().toString()));
                                        intent.addCategory("android.intent.category.DEFAULT");
                                        startActivity(intent);
                                    }catch(Exception e){
                                        Toast.makeText(magnetDetailActivity.this, "没有安装", Toast.LENGTH_LONG).show();
                                    }
                                }

                            }
                        });
                    }
                };
            }

            @Override
            public int getLayoutId() {
                return R.layout.item_home_entrance;
            }
        });
        entranceIndicatorView.setIndicatorCount(mPageMenuLayout.getPageCount());
        mPageMenuLayout.setOnPageListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                entranceIndicatorView.setCurrentIndicator(position);
            }
        });
    }

    /**
     * 复制内容到剪切板
     *
     * @param copyStr
     * @return
     */
    private boolean copy(String copyStr) {
        try {
            //获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //分享文字
    public void shareText(String text) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("text/plain");

        //设置分享列表的标题，并且每次都显示分享列表
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }


}