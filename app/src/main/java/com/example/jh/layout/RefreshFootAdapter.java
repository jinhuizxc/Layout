package com.example.jh.layout;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jinhui on 2018/4/9.
 * email: 1004260403@qq.com
 */

public class RefreshFootAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 上拉加载更多
    public static final int SATUS_PULLUP_LOAD_MORE = 0;
    // 正在加载中
    public static final int SATUS_LOADING_MORE = 1;
    public static final int SATUS_UP_LOADING_MORE = 2;
    // 上拉加载更多状态-默认为0
    private int load_more_status = 0;
    private LayoutInflater mInflater;
    private List<String> mTitles = null;
    private static final int TYPE_ITEM = 0; // 普通Item View
    private static final int TYPE_FOOTER = 1; // 底部FootView
    private static final int TYPE_FOOTER_EMPTY = 2; // 底部空白View
    private static int pagesize;
    private int eview_height = 1;
    private long TimeFlag;// 回弹时间
    private RecyclerView parent;
    private boolean loadmare;// 判断当前是可加载更多
    private boolean loading;// 判断是否正在加载
    private int startY, nowY;// 滑动判断

    //构造函数 处理滑动监听 更新等功能
    public RefreshFootAdapter(Context context, RecyclerView parent,
                              final LinearLayoutManager linearLayoutManager, int pagesize,
                              final Runnable onloadmore) {
        this.parent = parent;
        this.mInflater = LayoutInflater.from(context);
        this.mTitles = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            int index = i + 1;
            mTitles.add("item" + index);
        }
        this.pagesize = pagesize;

        parent.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                // TODO Auto-generated method stub

                switch (ev.getAction()) {

                    case MotionEvent.ACTION_MOVE:

                        nowY = (int) ev.getY();

                        if (RefreshFootAdapter.this.getItemCount() == linearLayoutManager
                                .findLastVisibleItemPosition() + 1) {
                            if (startY == 0) {// 按下
                                startY = nowY;
                            }
                            int changeY = nowY - startY;
                            RefreshFootAdapter.this
                                    .notifyEmptyView((int) (-changeY / 1.3f));

                            if (loading) {
                                return false;
                            }
                            RefreshFootAdapter.this
                                    .changeMoreStatus(RefreshFootAdapter.this.SATUS_UP_LOADING_MORE);

                            loadmare = true;
                        } else {
                            loadmare = false;
                            if (loading) {
                                return false;
                            }
                            RefreshFootAdapter.this
                                    .changeMoreStatus(RefreshFootAdapter.this.SATUS_PULLUP_LOAD_MORE);
                            // 普通的滑动
                            startY = 0;
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        RefreshFootAdapter.this.resetEmptyView();
                        if (loadmare) {
                            if (loading) {
                                return false;
                            } else {
                                RefreshFootAdapter.this
                                        .changeMoreStatus(RefreshFootAdapter.this.SATUS_LOADING_MORE);
                                if (onloadmore != null && !loading) {
                                    loading = true;
                                    onloadmore.run();
                                }
                            }
                        }
                        startY = 0;
                        break;
                    default:

                        break;
                }
                return false;
            }
        });
    }

    /**
     * item显示类型
     *
     * @param parent
     * @param viewType
     * @return
     */
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // 进行判断显示类型，来创建返回不同的View
        if (viewType == TYPE_ITEM) {
            View view = mInflater.inflate(R.layout.item_recycler_layout,
                    parent, false);
            // 这边可以做一些属性设置，甚至事件监听绑定
            // view.setBackgroundColor(Color.RED);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View foot_view = mInflater.inflate(
                    R.layout.recycler_load_more_layout, parent, false);
            // 这边可以做一些属性设置，甚至事件监听绑定
            // view.setBackgroundColor(Color.RED);
            return new FootViewHolder(foot_view);
        } else if (viewType == TYPE_FOOTER_EMPTY) {
            View foot_view_empty = mInflater.inflate(
                    R.layout.recycler_load_more_layout_empty, parent, false);
            // 这边可以做一些属性设置，甚至事件监听绑定
            // view.setBackgroundColor(Color.RED);
            return new FootEmptyHolder(foot_view_empty);
        }
        return null;
    }

    /**
     * 数据的绑定显示
     *
     * @param holder
     * @param position
     */
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).item_tv.setText(mTitles.get(position));
            holder.itemView.setTag(position);
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (load_more_status) {
                case SATUS_PULLUP_LOAD_MORE:
                    footViewHolder.foot_view_item_tv.setText("上拉加载更多...");
                    break;
                case SATUS_LOADING_MORE:
                    footViewHolder.foot_view_item_tv.setText("正在加载更多数据...");
                    break;
                case SATUS_UP_LOADING_MORE:
                    footViewHolder.foot_view_item_tv.setText("松开加载更多数据...");
                    break;
            }
        } else if (holder instanceof FootEmptyHolder) {
            FootEmptyHolder footViewHolder = (FootEmptyHolder) holder;
            footViewHolder.empty.setLayoutParams(new ViewGroup.LayoutParams(
                    111, eview_height));

        }
    }

    /**
     * 进行判断是普通Item视图还是FootView视图
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER_EMPTY;
        } else if (position + 2 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    //如果是页数的倍数 itemcount+2
    @Override
    public int getItemCount() {
        if (mTitles.size() % pagesize != 0) {
            return mTitles.size();
        } else {
            return mTitles.size() + 2;
        }
        // return mTitles.size()+1;
    }

    // 自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView item_tv;

        public ItemViewHolder(View view) {
            super(view);
            item_tv = (TextView) view.findViewById(R.id.item_tv);
        }
    }

    /**
     * 底部FootView布局
     */
    public static class FootViewHolder extends RecyclerView.ViewHolder {

        private TextView foot_view_item_tv;

        public FootViewHolder(View view) {
            super(view);
            foot_view_item_tv = (TextView) view
                    .findViewById(R.id.foot_view_item_tv);
        }
    }

    //空白项
    public static class FootEmptyHolder extends RecyclerView.ViewHolder {
        private View empty;

        public FootEmptyHolder(View view) {
            super(view);
            empty = view.findViewById(R.id.empty);
        }
    }

    // 添加数据
    public void addItem(List<String> newDatas) {
        // mTitles.add(position, data);
        // notifyItemInserted(position);
        newDatas.addAll(mTitles);
        mTitles.removeAll(mTitles);
        mTitles.addAll(newDatas);
        notifyDataSetChanged();
    }

    //更新添加数据
    public void addMoreItem(List<String> newDatas) {
        mTitles.addAll(newDatas);
        notifyDataSetChanged();
    }

    //更新空白项高度
    private void notifyEmptyView(int height) {
        this.eview_height = height;
        notifyItemChanged(getItemCount() - 1);
    }

    //空白回弹 伪回弹动画
    private void resetEmptyView() {
        final int dx = eview_height;
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                final int time = 500;
                final long startTime = new Date().getTime();
                TimeFlag = startTime;
                long nowTime = new Date().getTime();
                while (startTime + time > nowTime && TimeFlag == startTime) {
                    nowTime = new Date().getTime();
                    final int dt = (int) (nowTime - startTime);
                    parent.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            eview_height = eview_height * (time - dt) / time;
                            notifyDataSetChanged();
                        }
                    });

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                parent.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        eview_height = 0;
                        notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    //停止加载更多 重置loading状态和显示文本
    public void stopLoadMore() {

        notifyDataSetChanged();
        loading = false;
        RefreshFootAdapter.this
                .changeMoreStatus(RefreshFootAdapter.this.SATUS_PULLUP_LOAD_MORE);
    }

    //改变加载条状态
    private void changeMoreStatus(int status) {
        if (loading) {
            return;
        }
        load_more_status = status;
        notifyDataSetChanged();
    }
}
