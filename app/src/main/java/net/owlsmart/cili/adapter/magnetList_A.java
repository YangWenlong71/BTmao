package net.owlsmart.cili.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


import net.owlsmart.cili.R;
import net.owlsmart.cili.model.magnetList_C;

import java.util.ArrayList;


public class magnetList_A extends BaseAdapter implements ListAdapter {


        private ArrayList<magnetList_C> magnetList_cs;
        private int id;
        private Context context;
        private LayoutInflater inflater;

        public magnetList_A(int sub_item, Context context, ArrayList<magnetList_C> magnetList_cs) {
            this.magnetList_cs = magnetList_cs;
            this.context = context;
            this.id = sub_item;
            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return magnetList_cs.size();
        }

        @Override
        public Object getItem(int i) {
            return magnetList_cs.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("WrongConstant")
        @Override

        public View getView(int i, View view, ViewGroup viewGroup) {

            TextView title = null;
            TextView href = null;
            TextView block = null;
            ViewHolder viewHolder;
            if (view == null) {
                view = inflater.inflate(id, null);
                title = (TextView) view.findViewById(R.id.title);
                href = (TextView) view.findViewById(R.id.href);
                block = (TextView) view.findViewById(R.id.block);
                view.setTag(new ViewHolder(title,href, block));
            } else {
                ViewHolder viewHolder1 = (ViewHolder) view.getTag(); // 重新获取ViewHolder
                title = viewHolder1.title;
                href = viewHolder1.href;
                block = viewHolder1.block;
            }
            magnetList_C cc = (magnetList_C) magnetList_cs.get(i); // 获取当前项的实例

           // title.setText(cc.getTitle().toString());//对象为空
            /*
             * 设置部分字体红色
             * */
            TextToHtml(title,cc.getTitle().split("#")[0],cc.getTitle().split("#")[1]);

            href.setText(cc.getHref().toString());
            block.setText(cc.getBlock().toString());
            return view;

        }


        private final class ViewHolder {

            TextView title = null;
            TextView href = null;
            TextView block = null;
            TextView item_id = null;
            //href,title,block;

            public ViewHolder(TextView title, TextView href, TextView block) {
                this.block = block;
                this.title = title;
                this.href = href;
                this.item_id = item_id;
            }

        }

    /*
     * 设置部分字体红色
     * */
    private void TextToHtml(TextView tv_name,String str,String keyword){

        //String keyword="普罗米修斯";
        String middle =  str.replace(keyword,"</font><font color='#e93323'>"+keyword+"</font><font color='#2c2c2c'>");
        String ultimately = "<font color='#2c2c2c'>"+middle+"</font>";
        tv_name.setText(Html.fromHtml(ultimately));
    }


}
