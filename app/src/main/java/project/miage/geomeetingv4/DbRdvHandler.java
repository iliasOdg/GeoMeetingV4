package project.miage.geomeetingv4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import project.miage.geomeetingv4.Contact;
import project.miage.geomeetingv4.RendezVous;

import java.util.ArrayList;
import java.util.Iterator;

public class DbRdvHandler extends SQLiteOpenHelper {

    public static final String RDV_DATABASE_NAME = "DbRdv.db";
    public static  final int DATABASE_VERSION = 1;

    //Table des Rdv
    public static final String RDV_TABLE_NAME = "MesRdv";
    public static final String RDV_KEY = "id_rdv";
    public static final String RDV_DATE = "date";
    public static final String RDV_HEURE = "heure";
    public static final String RDV_LATITUDE = "latitude";
    public static final String RDV_LONGITUDE = "longitude";

    public static final String RDV_TABLE_CREATE =
            "CREATE TABLE " + RDV_TABLE_NAME + " (" +
                    RDV_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    RDV_DATE + " TEXT," +
                    RDV_HEURE + " TEXT," +
                    RDV_LATITUDE + " TEXT," +
                    RDV_LONGITUDE + " TEXT);";

    public static final String RDV_TABLE_DROP = "DROP TABLE IF EXISTS " + RDV_TABLE_NAME + ";";


    //Table des destinataires
    public static final String DST_TABLE_NAME = "MesDst";
    public static final String DST_KEY = "id";
    public static final String DST_PHONE = "phone";
    public static final String DST_NOM = "nom";
    public static final String DST_ID_RDV = "id_rdv_target";
    public static final String DST_ACCORD = "accord_rdv";

    public static final String DST_TABLE_CREATE =
            "CREATE TABLE " + DST_TABLE_NAME + " (" +
                    DST_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DST_PHONE + " TEXT," +
                    DST_NOM + " TEXT," +
                    DST_ACCORD + " TEXT," +
                    DST_ID_RDV + " TEXT);";

    public static final String DST_TABLE_DROP = "DROP TABLE IF EXISTS " + DST_TABLE_NAME + ";";


    public DbRdvHandler(Context context) {
        super(context, RDV_DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RDV_TABLE_CREATE);
        db.execSQL(DST_TABLE_CREATE);
        Log.i("DATABASE", "OnCreate invonked");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(RDV_TABLE_DROP);
        db.execSQL(DST_TABLE_DROP);
        onCreate(db);
    }

    public void ajouterRdv(project.miage.geomeetingv2.RendezVous rv) {

        ContentValues values = new ContentValues();

        values.put(RDV_DATE, rv.getDate());
        values.put(RDV_HEURE, rv.getHeure());
        values.put(RDV_LATITUDE, "" + rv.getLieu().latitude + "");
        values.put(RDV_LONGITUDE, "" + rv.getLieu().longitude + "");

        this.getWritableDatabase().insert(RDV_TABLE_NAME, null, values);
        this.getWritableDatabase().close();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT MAX(" + RDV_KEY + ") from " + RDV_TABLE_NAME +";";

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String res = cursor.getString(0);

        Iterator it = rv.getDestinataires().iterator();
        int cpt = 0;

        ContentValues vals = new ContentValues();

        while(it.hasNext()) {
            Contact c = rv.getDestinataires().get(cpt);
            vals.put(DST_PHONE, c.getTelephone());
            vals.put(DST_NOM, c.getNom());
            vals.put(DST_ID_RDV, res);
            vals.put(DST_ACCORD,"0");
            this.getWritableDatabase().insert(DST_TABLE_NAME, null, vals);
            cpt++;
            it.next();
        }

        cursor.close();
        //Log.i("DATABASE RDV", rv.getDestinataires().toString());
    }

    public RendezVous chercher(int id) {

        String sql_rdv = "SELECT  * FROM " + RDV_TABLE_NAME + " WHERE "
                + RDV_KEY + " = " + id;

        String sql_dst = "SELECT  * FROM " + DST_TABLE_NAME + " WHERE "
                + DST_ID_RDV + " = " + id;

        Cursor c = this.getReadableDatabase().rawQuery(sql_rdv, null);
        Cursor d = this.getReadableDatabase().rawQuery(sql_dst, null);

        ArrayList<Contact> lists = new ArrayList<>();
        RendezVous res = new RendezVous(lists ,"00/00/0000","0",new LatLng(0,0));
        if (c.getCount() == 0 || d.getCount() ==0) return res;

        c.moveToFirst();
        d.moveToFirst();

        ArrayList<Contact> list = new ArrayList<>();

        while(d.moveToNext()) {
            Contact ctc = new Contact(d.getString(1),d.getString(2));
            list.add(ctc);
        }

        String date = c.getString(1);
        String heure = c.getString(2);
        LatLng lieu = new LatLng(Double.parseDouble(c.getString(3)),Double.parseDouble(c.getString(4)));
        double id_rdv = Double.parseDouble(c.getString(0));

        c.close();
        d.close();
        return new RendezVous(list,date,heure,lieu,id_rdv);

    }

    public void supprimer(int id) {
        RendezVous rdv = this.chercher(id);
        this.getWritableDatabase().delete(RDV_TABLE_NAME,RDV_KEY + " = ?",new String[] {"" + rdv.getId() + ""});
        this.getWritableDatabase().delete(DST_TABLE_NAME,DST_ID_RDV + " = ?",new String[] {"" + rdv.getId() + ""});
    }

    public Boolean modifier(int id) {
        RendezVous rdv = this.chercher(id);

        if(rdv.getId() < 0) return false;

        ContentValues values = new ContentValues();
        values.put(DST_ACCORD,"1");

        this.getWritableDatabase().update(DST_TABLE_NAME,values,DST_ID_RDV + " = ?",new String[] {"" + rdv.getId() + ""});

        return true;

    }

}
