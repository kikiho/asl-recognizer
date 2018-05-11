package hforh.oranges.aslrecognizer;

/**
 * Created by Owner on 2/11/2018.
 */

public class SignedWordYoutubeID {
    private String videoID;
    private String wordName;

    public SignedWordYoutubeID(String id, String word){
        videoID = id;
        wordName = word;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoLink) {
        this.videoID = videoLink;
    }

    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }
}
