/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

/**
 *
 * @author ElangovanV
 */
public class UserPassParam {
    private String username;
    private String password;
    private int position;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserPassParam(String username, String password, int position) {
        this.username = username;
        this.password = password;
        this.position = position;
    }

    public String toString() {
        return "UserPassParam{" + "username=" + username + ", password=" + password + ", position=" + position + '}';
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserPassParam other = (UserPassParam) obj;
        if (this.position != other.position) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.position;
        return hash;
    }
        
}
