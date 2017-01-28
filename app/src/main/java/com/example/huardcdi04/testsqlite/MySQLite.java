package com.example.huardcdi04.testsqlite;

/**
 * Created by huard.cdi04 on 21/12/2016.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class MySQLite extends SQLiteOpenHelper {

    private final Context mycontext;
    private static MySQLite sInstance;

    private static final int DATABASE_VERSION = 1; // l'incrément appelle onUpgrade(), décrément => onDowngrade()
    private String DATABASE_PATH; // chemin défini dans le constructeur
    private static final String DATABASE_NAME = "db.sqlite";//nom par default
    private SQLiteDatabase maBase;

    public static synchronized MySQLite getInstance(Context context) {
        if (sInstance == null) { sInstance = new MySQLite(context); }
        return sInstance;
    }

    public static synchronized MySQLite getInstance(Context context, String baseName) {
        if (sInstance == null) { sInstance = new MySQLite(context, baseName); }
        return sInstance;
    }

    // Constructeur
    private MySQLite(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mycontext=context;
        String filesDir = context.getFilesDir().getPath(); // /data/data/com.package.nom/files/
        DATABASE_PATH = filesDir.substring(0, filesDir.lastIndexOf("/")) + "/databases/"; // /data/data/com.package.nom/databases/

        // Si la bdd n'existe pas dans le dossier de l'app
        if (!checkdatabase()) {
            // copy db de 'assets' vers DATABASE_PATH
            copydatabase();
        }
        //recuperation de la base en mode lecture seul, on a pas besoin de faire d'écriture
        maBase = this.getReadableDatabase();
    }

    // Surcharge du Constructeur pour remplacer la base plutot que celle par default
    private MySQLite(Context context, String baseName) {

        super(context, baseName, null, DATABASE_VERSION);
        this.mycontext=context;
        String filesDir = context.getFilesDir().getPath(); // /data/data/com.package.nom/files/
        DATABASE_PATH = filesDir.substring(0, filesDir.lastIndexOf("/")) + "/databases/"; // /data/data/com.package.nom/databases/

        // Si la bdd n'existe pas dans le dossier de l'app
        if (!checkdatabase()) {
            // copy db de 'assets' vers DATABASE_PATH
            copydatabase(baseName);
        }
        //recuperation de la base en mode lecture seul, on a pas besoin de faire d'écriture
        maBase = this.getReadableDatabase();
    }

    /**
     * verification de l'existance de la BDD
     * @return
     */
    private boolean checkdatabase() {
        // retourne true/false si la bdd existe dans le dossier de l'app
        File dbfile = new File(DATABASE_PATH + DATABASE_NAME);
        return dbfile.exists();
    }

    /**
     * On copie la base de "assets" vers "/data/data/com.package.nom/databases"
     * ceci est fait au premier lancement de l'application
     */
    private void copydatabase() {

        final String outFileName = DATABASE_PATH + DATABASE_NAME;

        InputStream myInput;
        try {
            // Ouvre la bdd de 'assets' en lecture
            myInput = mycontext.getAssets().open(DATABASE_NAME);

            // dossier de destination
            File pathFile = new File(DATABASE_PATH);
            if(!pathFile.exists()) {
                if(!pathFile.mkdirs()) {
                    Toast.makeText(mycontext, "Erreur : copydatabase(), pathFile.mkdirs()", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Ouverture en écriture du fichier bdd de destination
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfert de inputfile vers outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Fermeture
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mycontext, "Erreur : copydatabase()", Toast.LENGTH_SHORT).show();
        }

        // on greffe le numéro de version
        try{
            SQLiteDatabase checkdb = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            checkdb.setVersion(DATABASE_VERSION);
        }
        catch(SQLiteException e) {
            // bdd n'existe pas
            Log.e("SQLExeption",e.getMessage());
        }

    } // copydatabase()

    /**
     * surchage de la fonction copydatabase pour le cas ou on change le nom de la base
     * On copie la base de "assets" vers "/data/data/com.package.nom/databases"
     * ceci est fait au premier lancement de l'application
     * @param baseName le nom de la base
     */
    private void copydatabase(String baseName) {

        final String outFileName = DATABASE_PATH + baseName;

        InputStream myInput;
        try {
            // Ouvre la bdd de 'assets' en lecture
            myInput = mycontext.getAssets().open(baseName);

            // dossier de destination
            File pathFile = new File(DATABASE_PATH);
            if(!pathFile.exists()) {
                if(!pathFile.mkdirs()) {
                    Toast.makeText(mycontext, "Erreur : copydatabase(), pathFile.mkdirs()", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Ouverture en écriture du fichier bdd de destination
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfert de inputfile vers outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Fermeture
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mycontext, "Erreur : copydatabase()", Toast.LENGTH_SHORT).show();
        }

        // on greffe le numéro de version
        try{
            SQLiteDatabase checkdb = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            checkdb.setVersion(DATABASE_VERSION);
        }
        catch(SQLiteException e) {
            // bdd n'existe pas
            Log.e("SQLExeption",e.getMessage());
        }

    } // copydatabase()

    /**
     * recuperation de de la base
     * @return
     */
    public SQLiteDatabase getDataBase(){
        return this.maBase;
    }

    /**
     * envoie une requette SQL a la base
     * @param query
     * @return un curseur
     */
    public Cursor sendQuery(String query){
        return this.getDataBase().rawQuery(query,null);
    }

    /**
     * Override de close pour fermer la BDD de notre class
     */
    @Override
    public void close(){
        super.close();
        this.maBase.close();
    }

    /**
     * TODO à commenter, peu utile?
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.maBase = db;
    }

    /**
     * TODO à commenter, peu utile?
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion){
            //Log.d("debug", "onUpgrade() : oldVersion=" + oldVersion + ",newVersion=" + newVersion);
            mycontext.deleteDatabase(DATABASE_NAME);
            copydatabase();
        }
    } // onUpgrade

} // class MySQLite
