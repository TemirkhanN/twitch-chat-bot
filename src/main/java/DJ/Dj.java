package DJ;

public interface Dj {
    public Track getCurrentTrack();

    public void skipCurrentTrack();

    public boolean isPlaying(Track track);
}
