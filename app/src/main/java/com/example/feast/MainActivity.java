package com.example.feast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.varunest.sparkbutton.SparkButton;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    Spinner mainSpinner;
    SparkButton sparkButton;
    String valueFromSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainSpinner = findViewById(R.id.spinnerMain);
        sparkButton = findViewById(R.id.spark_button);
        sparkButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                goToNextActivity();
            }
        });
        String[] spinnerValues = getResources().getStringArray(R.array.minutesForSpinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainSpinner.setAdapter(adapter);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinnerMain) {
           this.valueFromSpinner = parent.getItemAtPosition(position).toString();

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void goToNextActivity() {
        Intent intent = new Intent(this, DisplayRecipe.class);
        mainSpinner = findViewById(R.id.spinnerMain);
        String message = mainSpinner.getSelectedItem().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}
