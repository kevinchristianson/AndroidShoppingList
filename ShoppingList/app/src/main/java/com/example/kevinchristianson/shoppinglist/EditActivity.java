package com.example.kevinchristianson.shoppinglist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.kevinchristianson.shoppinglist.adapter.NothingSelectedSpinnerAdapter;
import com.example.kevinchristianson.shoppinglist.data.Item;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;


public class EditActivity extends AppCompatActivity {

    public static final String KEY_PLACE = "KEY_PLACE";

    @BindView(R.id.etName)
    EditText name;
    @BindView(R.id.etDescription)
    EditText description;
    @BindView(R.id.etPrice)
    EditText price;
    @BindView(R.id.dropDownList)
    Spinner typeList;
    @BindView(R.id.cbBoughtEdit)
    CheckBox bought;
    private Item itemToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        setupUI();

        if (getIntent().getSerializableExtra(MainActivity.KEY_EDIT) != null) {
            initEdit();
        } else {
            initCreate();
        }
    }

    private void initCreate() {
        getRealm().beginTransaction();
        itemToEdit = getRealm().createObject(Item.class, UUID.randomUUID().toString());
        getRealm().commitTransaction();
    }

    private void initEdit() {
        String itemID = getIntent().getStringExtra(MainActivity.KEY_EDIT);
        itemToEdit = getRealm().where(Item.class).equalTo("id", itemID).findFirst();
        name.setText(itemToEdit.getName());
        description.setText(itemToEdit.getDescription());
        price.setText(Float.toString(itemToEdit.getPrice()));
        typeList.setSelection(itemToEdit.getType().getValue() + 1);
        bought.setChecked(itemToEdit.isBought());
    }

    private void setupUI() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.itemtypes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeList.setPrompt("Select type of item");
        typeList.setAdapter(new NothingSelectedSpinnerAdapter(adapter,
                R.layout.contact_spinner_row_nothing_selected, this));

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePlace();
            }
        });
    }

    public Realm getRealm() {
        return ((MainApplication) getApplication()).getRealmPlaces();
    }

    private void savePlace() {
        if (name.getText() == null || name.getText().toString().equals("")) {
            name.setHintTextColor(Color.RED);
            return;
        }
        if (price.getText() == null || price.getText().toString().equals("")) {
            price.setHintTextColor(Color.RED);
            return;
        }
        try {
            Float.parseFloat(price.getText().toString());
        }
        catch (NumberFormatException n) {
            price.setText("");
            price.setHintTextColor(Color.RED);
            return;
        }
        Intent intentResult = new Intent();

        getRealm().beginTransaction();
        itemToEdit.setName(name.getText().toString());
        itemToEdit.setDescription(description.getText().toString());
        itemToEdit.setPrice(Float.parseFloat(price.getText().toString()));
        itemToEdit.setType(typeList.getSelectedItemPosition() - 1);
        itemToEdit.setBought(bought.isChecked());
        getRealm().commitTransaction();

        intentResult.putExtra(KEY_PLACE, itemToEdit.getId());
        setResult(RESULT_OK, intentResult);
        finish();
    }
}