package com.example.kevinchristianson.shoppinglist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import com.example.kevinchristianson.shoppinglist.adapter.ItemsAdapter;
import com.example.kevinchristianson.shoppinglist.data.Item;
import com.example.kevinchristianson.shoppinglist.touch.ItemsListTouchHelperCallback;


public class MainActivity extends AppCompatActivity {


    public static final int REQUEST_NEW= 101;
    public static final int REQUEST_EDIT = 102;
    public static final String KEY_EDIT = "KEY_EDIT";
    private static Context context;
    private static Realm realm;

    private ItemsAdapter itemsAdapter;
    private CoordinatorLayout layoutContent;
    private TextView total;
    private int placeToEditPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((MainApplication) getApplication()).openRealm();

        setUpAdapter();
        setUpTouch();
        setUpFabs();

        layoutContent = (CoordinatorLayout) findViewById(R.id.layoutContent);
        total = (TextView) findViewById(R.id.tvTotal);
        setUpToolBar();
        updateTotal();
        context = this;
        realm = getRealm();
    }

    public static Realm getRealmInstance() {
        return realm;
    }

    public static Context getContext() {
        return context;
    }

    public Realm getRealm() {
        return ((MainApplication)getApplication()).getRealmPlaces();
    }


    private void setUpAdapter() {
        RealmResults<Item> allItems = getRealm().where(Item.class).findAll();
        Item itemsArray[] = new Item[allItems.size()];
        List<Item> itemsResult = new ArrayList<>(Arrays.asList(allItems.toArray(itemsArray)));

        itemsAdapter = new ItemsAdapter(itemsResult, this);
    }

    private void setUpFabs() {
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateActivity();
            }
        });
        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.warning)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getRealm().beginTransaction();
                                itemsAdapter.removeAll();
                                getRealm().deleteAll();
                                getRealm().commitTransaction();
                                updateTotal();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.show();
            }
        });
    }

    private void setUpTouch() {
        RecyclerView recyclerViewItems = (RecyclerView) findViewById(R.id.recycleList);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(itemsAdapter);

        ItemsListTouchHelperCallback touchHelperCallback = new ItemsListTouchHelperCallback(
                itemsAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(
                touchHelperCallback);
        touchHelper.attachToRecyclerView(recyclerViewItems);
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void updateTotal() {
        total.setText(getResources().getString(R.string.total_cost, String.format("%.02f", itemsAdapter.getTotal())));
    }

    private void showCreateActivity() {
        Intent intentStart = new Intent(MainActivity.this, EditActivity.class);
        startActivityForResult(intentStart, REQUEST_NEW);
    }

    public void showEditActivity(String placeID, int position) {
        Intent intentStart = new Intent(MainActivity.this, EditActivity.class);
        placeToEditPosition = position;

        intentStart.putExtra(KEY_EDIT, placeID);
        startActivityForResult(intentStart, REQUEST_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                String itemID  = data.getStringExtra(EditActivity.KEY_PLACE);
                Item item = getRealm().where(Item.class).equalTo("id", itemID).findFirst();

                if (requestCode == REQUEST_NEW) {
                    itemsAdapter.addItem(item);
                    updateTotal();
                    showSnackBarMessage(getString(R.string.txt_place_added));
                } else if (requestCode == REQUEST_EDIT) {
                    itemsAdapter.updateItem(placeToEditPosition, item);
                    updateTotal();
                    showSnackBarMessage(getString(R.string.txt_place_edited));
                }
                break;
            case RESULT_CANCELED:
                showSnackBarMessage(getString(R.string.txt_add_cancel));
                break;
        }
    }

    public void deleteItem(Item item) {
        updateTotal();
        getRealm().beginTransaction();
        item.deleteFromRealm();
        getRealm().commitTransaction();
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(layoutContent,
                message,
                Snackbar.LENGTH_LONG
        ).setAction(R.string.action_hide, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //...
            }
        }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MainApplication)getApplication()).closeRealm();
    }
}
