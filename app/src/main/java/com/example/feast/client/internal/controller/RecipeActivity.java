package com.example.feast.client.internal.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

import com.example.feast.R;
import com.example.feast.client.internal.model.Model;
import com.example.feast.core.entities.UserRecipe;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {

    private UserRecipe mainRecipe;
    private UserRecipe UpdatedRecipe;

    ArrayList<View> views;

    private ImageButton btnAddIng;
    private TableLayout tableIng;
    Button btnEdit;
    private Model model;
    private EditText etName;
    private EditText etTime;
    private boolean edit;
    int test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        model = Model.getInstance();
        edit = false;
        views = new ArrayList<>();
        tableIng = findViewById(R.id.table_ing);
        btnEdit = findViewById(R.id.button_edit);
        btnAddIng = findViewById(R.id.button_add_ing);
        views.add(btnAddIng);
        btnAddIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient();
            }
        });
        etName = findViewById(R.id.et_ur_name);
        views.add(etName);
        etTime = findViewById(R.id.et_ur_time);
        views.add(etTime);
        UserRecipe ur = (UserRecipe) getIntent().getSerializableExtra("ur");
        setRecipe(ur);
        switchActivation(edit);

        Button btnEdit = findViewById(R.id.button_edit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEdit();
            }
        });
    }

    private void addIngredient() {
        EditText etName = new EditText(this);
        views.add(etName);
        etName.setEnabled(edit);
        etName.setText("" + test++);
        EditText etAmount = new EditText(this);
        views.add(etAmount);
        etAmount.setEnabled(edit);
        ImageButton btnDeleteIng = new ImageButton(this);
        views.add(btnDeleteIng);
        btnDeleteIng.setImageResource(R.drawable.delete_icon);
        btnDeleteIng.setBackgroundResource(R.color.transparant);
        btnDeleteIng.setEnabled(edit);
        TableRow tr = new TableRow(this);
        tr.addView(etName);
        tr.addView(etAmount);
        tr.addView(btnDeleteIng);
        Log.d("recipe", "addIngredient: " + tableIng.getChildCount());
        tableIng.addView(tr, tableIng.getChildCount() >= 2 ? tableIng.getChildCount() - 1 : 0);
    }

    private void setRecipe(UserRecipe ur) {
        etName.setText(ur.getName());
        etTime.setText("" + ur.getEstimatedTime());
    }

    private void onUpdate() {
        edit = !edit;
        switchActivation(edit);
        btnEdit.setText("Edit");
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEdit();
            }
        });

    }

    private void onEdit() {
        edit = !edit;
        switchActivation(edit);
        btnEdit.setText("Save");
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEdit();
            }
        });
    }

    private void onCancel() {

    }

    private void switchActivation(boolean edit) {
        for (View v : views) {
            v.setEnabled(edit);
            v.setAlpha(1);

        }
    }


}
