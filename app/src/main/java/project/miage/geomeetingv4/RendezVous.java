package project.miage.geomeetingv4;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Ilias on 07/11/2018.
 */

public class RendezVous {

    private ArrayList<Contact> destinataires;
    private String Date;
    private String heure;
    private LatLng lieu;
    private double id;

    public RendezVous(){}

    public RendezVous(ArrayList<Contact> destinataires, String date, String heure, LatLng lieu) {
        this.destinataires = destinataires;
        Date = date;
        this.heure = heure;
        this.lieu = lieu;
        this.id = -1;
    }

    public RendezVous(ArrayList<Contact> destinataires, String date, String heure, LatLng lieu, double id) {
        this.destinataires = destinataires;
        Date = date;
        this.heure = heure;
        this.lieu = lieu;
        this.id = id;
    }

    public ArrayList<Contact> getDestinataires() {
        return destinataires;
    }

    public String getDestString() {
        Iterator it = this.destinataires.iterator();
        String g = "";
        while(it.hasNext()) {
            g = g + it.next().toString();
        }
        return g;
    }

    @Override
    public String toString() {
        return "RendezVous{" +
                "destinataires=" + destinataires + //this.getDestString()
                ", Date='" + Date + '\'' +
                ", heure='" + heure + '\'' +
                ", lieu=" + lieu +
                ", id=" + id +
                '}';
    }

    public double getId() {
        return  this.id;
    }

    public void setDestinataires(ArrayList<Contact> destinataires) {
        this.destinataires = destinataires;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public LatLng getLieu() {
        return lieu;
    }

    public void setLieu(LatLng lieu) {
        this.lieu = lieu;
    }
}
