package DJ;

public class Track {
    private String fullName;
    private String platformName;

    public Track(String fullName, String platformName) {
        this.fullName = fullName;
        this.platformName = platformName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPlatform() {
        return platformName;
    }

    public boolean equals(Track track) {
        return getFullName().equals(track.getFullName());
    }
}
