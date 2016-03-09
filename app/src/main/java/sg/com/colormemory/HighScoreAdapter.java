package sg.com.colormemory;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import sg.com.colormemory.entity.HighScore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anky on 09-03-2016.
 */
public class HighScoreAdapter extends RecyclerView.Adapter<HighScoreAdapter.ViewHolder> {

    Context context;
    List<HighScore> highscores = new ArrayList<>();
    public HighScoreAdapter(Context context, List<HighScore> scores){
        this.context = context;
        this.highscores = scores;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        HighScore highScore = highscores.get(position);
        holder.name.setText(highScore.getName());
        holder.score.setText(highScore.getScore());
    }


    @Override
    public int getItemCount() {
        return highscores.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgThumbnail;
        public TextView name;
        public TextView score;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.nameHg);
            score = (TextView)itemView.findViewById(R.id.scoreHg);

        }

    }
    public void refreshData(){
        notifyDataSetChanged();
    }
}
