/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatappfn01;

import java.util.ArrayList;

/**
 *
 * @author Ali El-Derawi @ Momen Zaqout
 */
public abstract class NAMES {

    public static ArrayList<String> names_arl = new ArrayList<>();
    public static ArrayList<String> names_arl_group = new ArrayList<>();
    
    public static void add(String name) {
        int x = 1;
        for (String na : names_arl) {
            if (na.equals(name)) {
                x = 0;
            }
        }
        if (x == 1) {
            names_arl.add(name);
        }
    }
     public static void addGroup(String name) {
        int x = 1;
        for (String na : names_arl_group) {
            if (na.equals(name)) {
                x = 0;
            }
        }
        if (x == 1) {
            names_arl_group.add(name);
        }
}
}
