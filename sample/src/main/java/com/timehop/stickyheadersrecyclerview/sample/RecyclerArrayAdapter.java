package com.timehop.stickyheadersrecyclerview.sample;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


public abstract class RecyclerArrayAdapter<M, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private ArrayList<Pair<Long, M>> items = new ArrayList<Pair<Long, M>>();
    private long idCounter = 0;

    public RecyclerArrayAdapter() {
        setHasStableIds(true);
    }

    public void add(M object) {
        items.add(new Pair<Long, M>(idCounter++, object));
        notifyDataSetChanged();
    }

    public void add(int index, M object) {
        items.add(index, new Pair<Long, M>(idCounter++, object));
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends M> collection) {
        if (collection != null) {
            for (M x : collection) {
                items.add(new Pair<Long, M>(idCounter++, x));
            }
            notifyDataSetChanged();
        }
    }

    public void addAll(M... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(M object) {
        int foundIndex = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).second == object) {
                foundIndex = i;
                break;
            }
        }

        if (foundIndex < 0) {
            return;
        }

        items.remove(foundIndex);
//        notifyItemRemoved(foundIndex);
        notifyDataSetChanged();
    }

    public void move(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        Pair<Long, M> item = items.remove(fromPosition);
        items.add(toPosition, item);
        notifyItemMoved(fromPosition, toPosition);
    }

    public M getItem(int position) {
        return items.get(position).second;
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).first;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
