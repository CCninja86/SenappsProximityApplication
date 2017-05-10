package nz.james.senappsproximityapplication;

import android.graphics.Bitmap;

/**
 * Created by james on 10/05/2017.
 */

public class Globals {

    private static Globals instance;
    private Bitmap splashImageBitmap;

    private Globals(){

    }

    public static synchronized Globals getInstance(){
        if(instance == null){
            instance = new Globals();
        }

        return instance;
    }

    public Bitmap getSplashImageBitmap() {
        return splashImageBitmap;
    }

    public void setSplashImageBitmap(Bitmap splashImageBitmap) {
        this.splashImageBitmap = splashImageBitmap;
    }
}
