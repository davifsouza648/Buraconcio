package io.github.poo.game.Objects;

public class Player {

    private String username;
    private double stars;
    private int id;


    public Player(String username){
        this.username = username;
    }

    public String getUsername(){

        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {

        return id;
    }

    public double getStars(){

        return stars;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

}
