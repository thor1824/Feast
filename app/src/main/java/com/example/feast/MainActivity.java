package com.example.feast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLetsFeast = (Button) findViewById(R.id.btnLetsFeast);

        btnLetsFeast.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(v.getContext(), "i want toast", Toast.LENGTH_LONG).show();
           }
        });
    }
}
