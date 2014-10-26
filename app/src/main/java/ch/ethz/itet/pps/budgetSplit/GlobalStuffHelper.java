package ch.ethz.itet.pps.budgetSplit;

import android.app.Application;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Chrissy on 26.10.2014.
 */
public class GlobalStuffHelper extends Application {
    // Counter Variable for virtual Contacts
    static int virtualCounter = 1;
    // Project Uris for Listview in Main Activity
    static ArrayList<Uri> projects = new ArrayList<Uri>();

    public static int getCounter() {
        return virtualCounter;
    }

    public static void raiseCounterByOne() {
        virtualCounter++;
    }

    public static void addUri(Uri uri) {

        projects.add(uri);
    }

    public static Uri getUriAtPosition(int index) {

        return (Uri) projects.get(index);

    }

    public static void deleteUriAtPosition(int index) {

        projects.remove(index);

    }
}
