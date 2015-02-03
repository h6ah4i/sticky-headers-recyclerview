package com.timehop.stickyheadersrecyclerview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.Touch;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;


public class MainActivity extends Activity {
    RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
    RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    SampleArrayHeadersAdapter mAdapter;
    RecyclerView.Adapter mWrappedAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        mAdapter = new SampleArrayHeadersAdapter();
        String[] animals = getResources().getStringArray(R.array.animals);
        mAdapter.addAll(animals);

        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mAdapter);
        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mWrappedAdapter);

        recyclerView.setAdapter(mWrappedAdapter);

        mRecyclerViewTouchActionGuardManager.attachRecyclerView(recyclerView);
        mRecyclerViewDragDropManager.attachRecyclerView(recyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(recyclerView);

        int orientation = LinearLayoutManager.VERTICAL;
//        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            orientation = LinearLayoutManager.VERTICAL;
//        } else {
//            orientation = LinearLayoutManager.HORIZONTAL;
//        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this, orientation, false));
        StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
//        recyclerView.addItemDecoration(new DividerDecoration(this));
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));
        recyclerView.addItemDecoration(headersDecor);
        StickyRecyclerHeadersTouchListener touchListener =
                new StickyRecyclerHeadersTouchListener(recyclerView, headersDecor);
        touchListener.setOnHeaderClickListener(
                new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
                    @Override
                    public void onHeaderClick(View header, int position, long headerId) {
                        Toast.makeText(MainActivity.this, "Header position: " + position + ", id: " + headerId,
                                Toast.LENGTH_SHORT).show();
                    }
                });
        recyclerView.addOnItemTouchListener(touchListener);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapter.remove(mAdapter.getItem(position));
            }
        }));
    }

    private static class SampleArrayHeadersAdapter extends RecyclerArrayAdapter<String, SampleArrayHeadersAdapter.NormalViewHolder>
            implements StickyRecyclerHeadersAdapter<SampleArrayHeadersAdapter.HeaderViewHolder>,
            DraggableItemAdapter<SampleArrayHeadersAdapter.NormalViewHolder>, SwipeableItemAdapter<SampleArrayHeadersAdapter.NormalViewHolder> {


        public static class HeaderViewHolder extends RecyclerView.ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        public static class NormalViewHolder extends AbstractDraggableSwipeableItemViewHolder {
            public ViewGroup mContainer;
            public View mDragHandle;
            public TextView mTextView;

            public NormalViewHolder(View itemView) {
                super(itemView);

                mContainer = (ViewGroup) itemView.findViewById(R.id.container);
                mDragHandle = itemView.findViewById(R.id.drag_handle);
                mTextView = (TextView) ((itemView.getId() == R.id.text) ? itemView : itemView.findViewById(R.id.text));
            }

            @Override
            public View getSwipeableContainerView() {
                return mContainer;
            }
        }

        @Override
        public NormalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_item, parent, false);
            return new NormalViewHolder(view);
        }

        @Override
        public void onBindViewHolder(NormalViewHolder holder, int position) {
            holder.mTextView.setText(getItem(position));

            final int dragState = holder.getDragStateFlags();
            final int swipeState = holder.getSwipeStateFlags();
            if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0) ||
                ((swipeState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0)) {
                if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
                    holder.mContainer.setBackgroundColor(0x80ff0000);
                } else if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0) {
                    holder.mContainer.setBackgroundColor(0x40ff0000);
                } else if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_IS_ACTIVE) != 0) {
                    holder.mContainer.setBackgroundColor(0xffffffff);
                } else if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_SWIPING) != 0) {
                    holder.mContainer.setBackgroundColor(0xfff0f0ff);
                } else {
                    holder.mContainer.setBackgroundColor(0xffffffff);
                }
            }
        }

        @Override
        public long getHeaderId(int position) {
            return getItem(position).charAt(0);
        }

        @Override
        public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_header, parent, false);
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView;
            textView.setText(String.valueOf(getItem(position).charAt(0)));
        }

        @Override
        public boolean onCheckCanStartDrag(NormalViewHolder holder, int x, int y) {
            if (holder.mContainer == null || holder.mDragHandle == null) {
                return false;
            }

            // x, y --- relative from the itemView's top-left
            final View containerView = holder.mContainer;
            final View dragHandleView = holder.mDragHandle;

            final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
            final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

            return hitTest(dragHandleView, x - offsetX, y - offsetY);
        }

        @Override
        public void onMoveItem(int fromPosition, int toPosition) {
            move(fromPosition, toPosition);
        }

        @Override
        public int onGetSwipeReactionType(NormalViewHolder holder, int x, int y) {
            boolean canSwipe = false;

            if (holder.mContainer != null) {
                if (!onCheckCanStartDrag(holder, x, y)) {
                    canSwipe = true;
                }
            }

            if (canSwipe) {
                return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT |
                        RecyclerViewSwipeManager.REACTION_CAN_SWIPE_RIGHT;
            } else {
                return RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_LEFT |
                        RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_RIGHT;
            }
        }

        @Override
        public void onSetSwipeBackground(NormalViewHolder holder, int type) {
            switch (type) {
                case RecyclerViewSwipeManager.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                    holder.itemView.setBackgroundColor(0x00000000);
                    break;
                case RecyclerViewSwipeManager.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                    holder.itemView.setBackgroundColor(0xff008800);
                    break;
                case RecyclerViewSwipeManager.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                    holder.itemView.setBackgroundColor(0xffcc0000);
                    break;
            }
        }

        @Override
        public int onSwipeItem(NormalViewHolder holder, int result) {
            switch (result) {
                case RecyclerViewSwipeManager.RESULT_SWIPED_LEFT:
                case RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT:
                    return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;
                default:
                    return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
            }
        }

        @Override
        public void onPerformAfterSwipeReaction(NormalViewHolder holder, int result, int reaction) {
            if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM) {
                remove(getItem(holder.getPosition()));
            }
        }

        public static boolean hitTest(View v, int x, int y) {
            final int tx = (int) (ViewCompat.getTranslationX(v) + 0.5f);
            final int ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
            final int left = v.getLeft() + tx;
            final int right = v.getRight() + tx;
            final int top = v.getTop() + ty;
            final int bottom = v.getBottom() + ty;

            return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
        }
    }
}
