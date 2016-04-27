package hr.droidcon.conference.hack.objects;

public class StaticHelper {
    public static Object object;
    public static boolean changed = false;
    public static boolean hasDataChanged(){
        if(changed){
            changed = false;
            return true;
        }
        return false;
    }
}
