package com.example.kevinchristianson.shoppinglist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.example.kevinchristianson.shoppinglist.MainActivity;
import com.example.kevinchristianson.shoppinglist.R;
import com.example.kevinchristianson.shoppinglist.data.Item;

import static com.example.kevinchristianson.shoppinglist.MainActivity.getContext;
import static com.example.kevinchristianson.shoppinglist.MainActivity.getRealmInstance;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView name;
        public CheckBox bought;
        public TextView price;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.ivIcon);
            name = (TextView) itemView.findViewById(R.id.tvName);
            bought = (CheckBox) itemView.findViewById(R.id.cbBought);
            price = (TextView) itemView.findViewById(R.id.tvPrice);
        }
    }

    private List<Item> itemList;
    private Context context;
    private int lastPosition = -1;
    private static float total;

    public ItemsAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
        total = 0;
        for (Item item: itemList) {
            if (!item.isBought()) { total += item.getPrice(); }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Item item = itemList.get(position);
        viewHolder.name.setText(item.getName());
        viewHolder.price.setText(Float.toString(item.getPrice()));
        viewHolder.icon.setImageResource(item.getType().getIcon());
        viewHolder.bought.setChecked(item.isBought());
        setAnimation(viewHolder.itemView, position);
        View.OnTouchListener touchListener = createOnTouchListener(viewHolder);
        viewHolder.icon.setOnTouchListener(touchListener);
        viewHolder.price.setOnTouchListener(touchListener);
        viewHolder.bought.setChecked(item.isBought());
        setBoughtOnClick(viewHolder.bought, item);
    }

    private void setBoughtOnClick (View bought, final Item item) {
        bought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.isBought()) {
                    getRealmInstance().beginTransaction();
                    item.setBought(false);
                    getRealmInstance().commitTransaction();
                    total += item.getPrice();
                }
                else{
                    getRealmInstance().beginTransaction();
                    item.setBought(true);
                    getRealmInstance().commitTransaction();
                    total -= item.getPrice();
                }
                ((MainActivity)getContext()).updateTotal();
            }
        });
    }

    private View.OnTouchListener createOnTouchListener(final RecyclerView.ViewHolder viewHolder) {
        return new View.OnTouchListener() {
            private static final int MAX_CLICK_DURATION = 200;
            private long startClickTime;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if(clickDuration < MAX_CLICK_DURATION) {
                            ((MainActivity) context).showEditActivity(
                                    itemList.get(viewHolder.getAdapterPosition()).getId(),
                                    viewHolder.getAdapterPosition());
                        }
                    }
                }
                return true;
            }
        };
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void addItem(Item item) {
        itemList.add(item);
        notifyDataSetChanged();
        if (!item.isBought()) { total += item.getPrice(); }
    }

    public void updateItem(int index, Item item) {
        if (!itemList.get(index).isBought()) {
            total -= itemList.get(index).getPrice();
        }
        if (!item.isBought()) { total += item.getPrice(); }
        itemList.set(index, item);
        notifyItemChanged(index);
    }

    public void removeItem(int index) {
        if (!itemList.get(index).isBought()) { total -= itemList.get(index).getPrice(); }
        ((MainActivity)context).deleteItem(itemList.get(index));
        itemList.remove(index);
        notifyItemRemoved(index);
    }

    public void swapItems(int oldPosition, int newPosition) {
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(itemList, i, i + 1);
            }
        } else {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(itemList, i, i - 1);
            }
        }
        notifyItemMoved(oldPosition, newPosition);
    }

    public void removeAll() {
        total = 0;
        itemList.clear();
        notifyDataSetChanged();
    }

    public Item getItem(int i) {
        return itemList.get(i);
    }

    public float getTotal() {
        return total;
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}