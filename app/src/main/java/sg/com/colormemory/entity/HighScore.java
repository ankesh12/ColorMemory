package sg.com.colormemory.entity;

/**
 * Created by anky on 08-03-2016.
 */
public class HighScore {

    private long id;
    private String name;
    private String score;

    public HighScore(String name, String score){
        this.name = name;
        this.score = score;
    }

    public HighScore(){
        this.name = null;
        this.score = null;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
