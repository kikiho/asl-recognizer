package hforh.oranges.aslrecognizer;

/**
 * Created by Owner on 2/11/2018.
 */

public class SignedWordVideo {
    private String videoLink;
    private String wordName;

    public SignedWordVideo(String link, String word){
        videoLink = link;
        wordName = word;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }
}
