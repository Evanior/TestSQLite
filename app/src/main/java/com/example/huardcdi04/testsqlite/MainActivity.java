package com.example.huardcdi04.testsqlite;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.sqlite.*;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MySQLite monSQL = MySQLite.getInstance(this);
        Cursor monCurseur = monSQL.sendQuery("SELECT str FROM test WHERE 1");

        TextView monText = (TextView) findViewById(R.id.monText);
        Spinner monSpinner = (Spinner) findViewById(R.id.monSpinner);

        String tabStr[] = lectureCurseur(monCurseur,"str",5);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,tabStr);
        // Specify the layout to use when the list of choices appears
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        monSpinner.setAdapter(adapter);

        monCurseur.close();
        monSQL.close();
    }

    /**
     * lit la colonne demander d'un curseur
     * @param unCurseur le curseur à lire
     * @param colonne le nom de la colonne à récuperer
     * @param taille du tableau de string (par default 100)
     * @return un tableau de string (à rentrer dans un adapter par exemple)
     */
    public String[] lectureCurseur(Cursor unCurseur, String colonne, int taille){
        if(taille <= 0){
            taille = 100;
        }
        String[] unTablString = new String[taille];
        Arrays.fill(unTablString,"");//remplir le tableau de chaine vide
        int pos = 0;
        if(unCurseur.moveToFirst()){
            do{
                unTablString[pos] = unCurseur.getString(unCurseur.getColumnIndex(colonne));
                pos++;
            }while (unCurseur.moveToNext());
        }
        return unTablString;
    }
}
