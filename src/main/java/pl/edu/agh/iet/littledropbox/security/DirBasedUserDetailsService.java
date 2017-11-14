package pl.edu.agh.iet.littledropbox.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by janwojcik5 on 2017-04-25.
 *
 */
public class DirBasedUserDetailsService implements UserDetailsService {

    private String baseDir;
    private boolean useBaseDirInResources=true;

    public void setBaseDir(String baseDir) {
        this.baseDir=baseDir;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setUseBaseDirInResources(boolean useBaseDirInResources) {
        this.useBaseDirInResources=useBaseDirInResources;
    }

    public boolean getUseBaseDirInResources() {
        return useBaseDirInResources;
    }


    public UserDetails loadUserByUsername(String username) {
        String passwordFile;
        if(useBaseDirInResources) {
            passwordFile=this.getClass().getResource("/userDirectories/"+username+"/passwordFile.txt").getFile();
        } else {
            passwordFile=baseDir+"/"+username+"/passwordFile.txt";
        }
        BufferedReader reader;
        UserDetails user=null;
        try {
            reader = new BufferedReader(new FileReader(passwordFile));
            String password=reader.readLine();
            String grantedAuthorities=reader.readLine(); //list of roles,separated by commas
            String []grantedAuthoritiesArray=StringUtils.tokenizeToStringArray(grantedAuthorities,",");
            List<GrantedAuthority> grantedAuthorityList=new LinkedList<GrantedAuthority>();
            for(String grantedAuthority:grantedAuthoritiesArray) {
                grantedAuthorityList.add(new SimpleGrantedAuthority(grantedAuthority));
            }
            user=User.withUsername(username).password(password).authorities(grantedAuthorityList).build();//
        } catch(IOException e) {
            e.printStackTrace();
        }
        return user;
    }


}
