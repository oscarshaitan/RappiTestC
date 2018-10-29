package Retro;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetroYoutubeList {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<RetroYoutube> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<RetroYoutube> getResults() {
        return results;
    }

    public void setResults(List<RetroYoutube> results) {
        this.results = results;
    }

}