package com.linorz.mygecko.customview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Collections;
import java.util.List;

/**
 * Created by linorz on 2017/8/7.
 */

public class LinorzRecyclerView extends RecyclerView {
    private Adapter adapter;
    private int LINEAR = 1, GRID = 2, STAGGERED = 3;
    private int LAYOUTMANAGER;
    private boolean isLoading = false;
    private LoadMore mLoadMore;

    public LinorzRecyclerView(Context context) {
        super(context);
    }

    public LinorzRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinorzRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setItemTouchHelper(List datas,boolean canUse, DrawOver drawOver) {
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            int fromPosition;
            int toPosition;

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position
                fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(datas, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(datas, i, i - 1);
                    }
                }
                getAdapter().notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(ViewHolder viewHolder, int direction) {
            }

            //长按选中Item的时候开始调用
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            //手指松开的时候还原
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(0);
                if (drawOver != null)
                    drawOver.drawOver();
            }

            //重写拖拽不可用
            @Override
            public boolean isLongPressDragEnabled() {
                return canUse;
            }


        });
        mItemTouchHelper.attachToRecyclerView(this);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        this.adapter = adapter;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout instanceof GridLayoutManager) {
            LAYOUTMANAGER = GRID;
        } else if (layout instanceof LinearLayoutManager) {
            LAYOUTMANAGER = LINEAR;
        } else if (layout instanceof StaggeredGridLayoutManager) {
            LAYOUTMANAGER = STAGGERED;
        }
    }

    public void setLoadMore(LoadMore loadMore) {
        this.mLoadMore = loadMore;
    }

    /**
     * 获得最大的位置
     */
    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    private int getLastVisiblePosition() {
        int position;
        if (LAYOUTMANAGER == LINEAR) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (LAYOUTMANAGER == STAGGERED) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else if (LAYOUTMANAGER == GRID) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    public void endLoading() {
        isLoading = false;
    }

    /**
     * 配置显示图片，需要设置这几个参数，快速滑动时，暂停图片加载
     *
     * @param imageLoader   ImageLoader实例对象
     * @param pauseOnScroll
     * @param pauseOnFling
     */
    public void setOnPauseListenerParams(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
        super.addOnScrollListener(new AutoLoadListener(imageLoader, pauseOnScroll, pauseOnFling) {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && !isLoading) {
                    int lastVisiblePosition = getLastVisiblePosition();
                    if (lastVisiblePosition == adapter.getItemCount()) {
                        isLoading = true;
                        mLoadMore.loadMore();
                    }
                }
            }
        });

    }


    private class AutoLoadListener extends OnScrollListener {

        private ImageLoader imageLoader;
        private final boolean pauseOnScroll;
        private final boolean pauseOnFling;

        public AutoLoadListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
            super();
            this.pauseOnScroll = pauseOnScroll;
            this.pauseOnFling = pauseOnFling;
            this.imageLoader = imageLoader;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (imageLoader != null) {
                switch (newState) {
                    case 0:
                        imageLoader.resume();
                        break;
                    case 1:
                        if (pauseOnScroll) imageLoader.pause();
                        else imageLoader.resume();
                        break;
                    case 2:
                        if (pauseOnFling) imageLoader.pause();
                        else imageLoader.resume();
                        break;
                }
            }
        }
    }

    public interface LoadMore {
        void loadMore();
    }

    public interface DrawOver {
        void drawOver();
    }
}
