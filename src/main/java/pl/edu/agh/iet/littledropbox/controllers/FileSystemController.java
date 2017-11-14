package pl.edu.agh.iet.littledropbox.controllers;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.iet.littledropbox.logic.FileFactory;
import pl.edu.agh.iet.littledropbox.logic.UserDirectory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Enumeration;

/**
 * Created by janwojcik5 on 2017-05-03.
 */
@RestController
public class FileSystemController {

    @RequestMapping(value="/uploadfile",method=RequestMethod.POST)
    public void uploadFile(
            @RequestParam(value="filename") String fileName,
            @RequestParam(value="file")MultipartFile file,
            Principal principal,HttpServletRequest request) {
        /*
        Enumeration<String> headers=request.getHeaderNames();
        while(headers.hasMoreElements()) {
            String header=headers.nextElement();
            System.out.println(header);
            System.out.println(request.getHeader(header));
        }
        */
        System.out.println("User "+principal.getName()+" uploads file "+fileName);
        InputStream inputStream=null;
        FileOutputStream outputStream=null;
        try {
            inputStream =file.getInputStream();
            outputStream = new FileOutputStream(this.getClass().getResource("/userDirectories/"+principal.getName()+"/files").getFile()+"/"+fileName);
            //System.out.println(outputStream);
            byte[] buffer=new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch(IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    @RequestMapping(value="/downloadfile",method=RequestMethod.POST)
    public void downloadFile(@RequestParam(value="filename") String fileName,
                             Principal principal,
                             HttpServletResponse response) {
        try(InputStream inputStream=new FileInputStream(this.getClass().getResource("/userDirectories/"+principal.getName()+"/files").getFile()+"/"+fileName);
            OutputStream outputStream=response.getOutputStream()) {
            byte buffer[]=new byte[1024];
            int length;
            while((length=inputStream.read(buffer))>0) {
                outputStream.write(buffer,0,length);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

    }


    @RequestMapping(value="/createdir",method=RequestMethod.POST)
    public void createDir(
            @RequestParam(value="dirpath") String dirName,
            Principal principal,
            HttpServletResponse response) {
        System.out.println("User "+principal.getName()+" creates directory under path "+dirName);
        try {
            Files.createDirectory(Paths.get(StringUtils.trimLeadingCharacter(this.getClass().getResource("/userDirectories/"+principal.getName()+"/files").getFile()+"/"+dirName,'/')));
        } catch(IOException e) {
            e.printStackTrace();
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch(IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @RequestMapping(value="/deletefile",method=RequestMethod.POST)
    public void deleteFile(
            @RequestParam(value="filename") String fileName,
            HttpServletResponse response,
            Principal principal) {
        System.out.println("User "+principal.getName()+" deletes file "+fileName);
        Path fileToDelete= Paths.get(StringUtils.trimLeadingCharacter(this.getClass().getResource("/userDirectories/"+principal.getName()+"/files").getFile()+"/"+fileName,'/'));
        try {
            Files.delete(fileToDelete);
        } catch(IOException e) {
            e.printStackTrace();
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch(IOException e1) {
                e.printStackTrace();
            }
        }

    }

    @RequestMapping(value="/seestructure",method=RequestMethod.GET)
    public UserDirectory seeStructure(Principal principal,HttpServletRequest request,HttpServletResponse response) {
        String userName=principal.getName();
        System.out.println("User "+userName+" requested for directory structure");
        /*Enumeration<String> headers=request.getHeaderNames();
        while(headers.hasMoreElements()) {
            System.out.println(headers.nextElement());
        }
        System.out.println(request.getHeader("accept"));*/
        UserDirectory root= FileFactory.getDirectory(userName);
        root.print();
        return root;
    }
}
