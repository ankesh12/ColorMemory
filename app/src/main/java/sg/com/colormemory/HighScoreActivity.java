package sg.com.colormemory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import sg.com.colormemory.entity.HighScore;

import java.util.ArrayList;
import java.util.List;

public class HighScoreActivity extends AppCompatActivity {

    HighScoreAdapter adapter;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    List<HighScore> highScores = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        highScores = HomeActivity.datasource.getAllComments();

        recyclerView = (RecyclerView) findViewById(R.id.buddy_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new HighScoreAdapter(this,highScores);
        recyclerView.setAdapter(adapter);
    }
}
