package pl.edu.agh.iet.littledropbox.logic;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by janwojcik5 on 2017-05-06.
 */
public class FileFactory {

    //return fully populated directory classes to be marshalled into json
    public static UserDirectory getDirectory(String username,String path) {
        URL resource=FileFactory.class.getResource("/userDirectories/"+username+"/files"+path);
        //System.out.println(resource);
        Path baseDir= Paths.get(StringUtils.trimLeadingCharacter(FileFactory.class.getResource("/userDirectories/"+username+"/files"+path).getFile(),'/'));
        LittleDropboxFileVisitor visitor=new LittleDropboxFileVisitor();
        try {
            Files.walkFileTree(baseDir,visitor);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return visitor.getStructureToReturn();
    }


    public static UserDirectory getDirectory(String username) {
        return getDirectory(username,"");
    }

    public static UserFile getFile(String username,String path) {
        Path file=Paths.get(FileFactory.class.getResource("/userDirectories/"+username+"/files"+path).getFile());
        UserFile fileToReturn=new UserFile();
        fileToReturn.setPath(file.toString());
        fileToReturn.setSize(file.toFile().length());
        return fileToReturn;
    }

    private static class LittleDropboxFileVisitor extends SimpleFileVisitor<Path> {

        private LinkedList<UserDirectory> currentPath=new LinkedList<UserDirectory>();
        private UserDirectory structureToReturn;

        @Override
        public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs) {
            UserDirectory directoryToAdd=new UserDirectory();
            if(currentPath.isEmpty())
                directoryToAdd.setPath(dir.toString());
            else {
                //trim path to main directory in resources
                String absolutePath=dir.toString();
                String relativePath=absolutePath.substring(currentPath.getFirst().getPath().length()+1);
                directoryToAdd.setPath(relativePath);
            }
            directoryToAdd.setSize(attrs.size());
            directoryToAdd.setChildren(new LinkedList<UserFile>());
            if(!currentPath.isEmpty())
                currentPath.getLast().getChildren().add(directoryToAdd);
            currentPath.addLast(directoryToAdd);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if(currentPath.size()==1) {
                structureToReturn=currentPath.getFirst();
                structureToReturn.setPath("");
            }
            currentPath.removeLast();
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            UserFile fileToAdd=new UserFile();

            //trim path to lead directory in resources
            String absolutePath=file.toString();
            String relativePath=absolutePath.substring(currentPath.getFirst().getPath().length()+1);

            fileToAdd.setPath(relativePath);
            fileToAdd.setSize(attrs.size());
            currentPath.getLast().getChildren().add(fileToAdd);
            return FileVisitResult.CONTINUE;
        }

        public LinkedList<UserDirectory> getCurrentPath() {
            return currentPath;
        }
        public UserDirectory getStructureToReturn() {return structureToReturn;}
    }
}
