package com.techov8.engineerguys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FullSolutionActivity extends AppCompatActivity {

    private TextView question, solution;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_solution);

        question = findViewById(R.id.fullquestiontxt);
        solution = findViewById(R.id.fullSolutiontxt);
        image = findViewById(R.id.solutionImage);


        Intent intent = getIntent();

        String img = intent.getStringExtra("image");
        String ques = intent.getStringExtra("question");
        String sol = intent.getStringExtra("solution");
        solution.setText(sol);
        question.setText(ques);
        try {
            if (img != null)
                Picasso.get().load(img).into(image);
        } catch (Exception e) {
            e.printStackTrace();
        }


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullSolutionActivity.this, FullImageView.class);
                intent.putExtra("image", img);

                startActivity(intent);
            }
        });
    }
}