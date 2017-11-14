package pl.edu.agh.iet.littledropbox.logic;

/**
 * Created by janwojcik5 on 2017-05-03.
 * Object to be marshalled as json by JACKSON
 */
public class UserFile {

    //path relative to files directory
    String path;
    long size;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path=path;
    }

    public void print() {
        System.out.println(path);
    }
}
