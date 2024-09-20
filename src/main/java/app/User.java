package app;

public class User {
    private int id;
    private String username;
    private String password;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    public int getId(){
        return this.id;
    }
    public void setId(int id){this.id = id;}
    public String getUser(){
        return this.username;
    }
    public void setUser(String username){
        this.username = username;
    }
    public String getPassword(){
        return this.password;
    }
    public void setPassword(String pwd){
        this.password = pwd;
    }
}
