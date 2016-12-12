package experimental.abondar.org.miwok2;

/**
 * Created by abondar on 12/10/16.
 */
public class Word {
    private String miwokTranslation;
    private String defaultTranslation;

    private Integer imageResourceId;

    private boolean isImageSet = false;

    private Integer audioResourceId;

    public Word(String miwokTranslation, String defaultTranslation, Integer imageResourceId, Integer audioResourceId) {
        this.miwokTranslation = miwokTranslation;
        this.defaultTranslation = defaultTranslation;
        this.imageResourceId = imageResourceId;
        this.isImageSet = true;
        this.audioResourceId = audioResourceId;
    }


    public Word(String miwokTranslation, String defaultTranslation, Integer audioResourceId) {
        this.miwokTranslation = miwokTranslation;
        this.defaultTranslation = defaultTranslation;
        this.audioResourceId = audioResourceId;
    }

    public String getMiwokTranslation() {
        return miwokTranslation;
    }

    public void setMiwokTranslation(String miwokTranslation) {
        this.miwokTranslation = miwokTranslation;
    }

    public String getDefaultTranslation() {
        return defaultTranslation;
    }

    public void setDefaultTranslation(String defaultTranslation) {
        this.defaultTranslation = defaultTranslation;
    }

    public Integer getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(Integer imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public boolean hasImage() {
        return isImageSet;
    }

    public Integer getAudioResourceId() {
        return audioResourceId;
    }

    public void setAudioResourceId(Integer audioResourceId) {
        this.audioResourceId = audioResourceId;
    }
}
