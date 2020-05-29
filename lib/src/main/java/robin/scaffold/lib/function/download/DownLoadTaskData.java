package robin.scaffold.lib.function.download;

import java.io.File;

public class DownLoadTaskData {
    private int id;

    private String url;

    private File mFile;

    private long length;

    private long initSize = -1;

    /**
     *
     * @param id
     * @param url
     * @param file
     * @param length
     */
    public DownLoadTaskData(int id,
                            String url,
                            File file,
                            long length) {
        this.id = id;
        this.url = url;
        mFile = file;
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getInitalSize() {
        if(initSize < 0) {
            if(mFile == null)
                return initSize = 0;
            return initSize = mFile.length();
        }
       return initSize;
    }


    public String getFilePath() {
        if(mFile == null)
            return "";
        return mFile.getAbsolutePath();
    }
}
