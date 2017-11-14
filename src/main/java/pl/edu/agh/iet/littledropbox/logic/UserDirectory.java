package pl.edu.agh.iet.littledropbox.logic;

import java.util.List;

/**
 * Created by janwojcik5 on 2017-05-03.
 * Object to be marshalled as json by JACKSON
 */
public class UserDirectory extends UserFile {

    List<UserFile> children;

    public List<UserFile> getChildren() {
        return children;
    }

    public void setChildren(List<UserFile> children) {
        this.children = children;
    }

    @Override
    public void print() {
        for(UserFile child:children) {
            child.print();
        }
    }
}
