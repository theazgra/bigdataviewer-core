package azgracompress;

public class ViewerCompressionOptions {
    private boolean enabled = false;
    private int compressFromMipmapLevel = 0;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enable) {
        this.enabled = enable;
    }

    public int getCompressFromMipmapLevel() {
        return compressFromMipmapLevel;
    }

    public void setCompressFromMipmapLevel(final int compressFrom) {
        this.compressFromMipmapLevel = compressFrom;
    }
}
